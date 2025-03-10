package com.theplay.aos.fragment.write

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.gson.Gson
import com.theplay.aos.ApplicationClass
import com.theplay.aos.ApplicationClass.Companion.iconHashMap
import com.theplay.aos.ApplicationClass.Companion.userInfo
import com.theplay.aos.R
import com.theplay.aos.api.model.AddPostRequest
import com.theplay.aos.api.model.MainBoardResponse
import com.theplay.aos.base.BaseKotlinFragment
import com.theplay.aos.customview.AddDrinkDialog
import com.theplay.aos.customview.AddDrinkListener
import com.theplay.aos.customview.CustomDialogDeleteTag
import com.theplay.aos.customview.CustomDialogDeleteTagInterface
import com.theplay.aos.databinding.FragmentWriteBinding
import com.theplay.aos.fragment.home.ImageFragment
import com.theplay.aos.iadapter.DrinkAdapter
import com.theplay.aos.iadapter.DrinkAdapterInterface
import com.theplay.aos.imagepicker.ImagePicker
import com.theplay.aos.imagepicker.ImagePicker.getImages
import com.theplay.aos.imagepicker.ImagePicker.shouldResolve
import com.theplay.aos.item.DrinkItem
import com.theplay.aos.utils.DrinkUtil
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.*


class WriteFragment() : BaseKotlinFragment<FragmentWriteBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_write

    private var viewPagerAdapter: ViewPagerAdapter? = null
    private val viewModel by lazy { WriteViewModel() }

    var hasRecipe : Boolean = false

    var drinkList : MutableList<DrinkItem> = mutableListOf()
    var ingredientList : MutableList<AddPostRequest.Ingredient> = mutableListOf()
    var stepList : MutableList<AddPostRequest.Step> = mutableListOf()
    var alcoholTagList : MutableList<AddPostRequest.AlcoholTag> = mutableListOf()
    lateinit var images: List<File>
    lateinit var drinkAdapterListener : DrinkAdapterInterface


    override fun initStartView() {
        binding.btnBack.setOnClickListener {
            DrinkUtil.clearRecipeSaved()
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
        }
        binding.vpPager.isSaveEnabled = false
        binding.vpPager.isUserInputEnabled = true
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.btnPlusRecipe.setOnClickListener {
            if (drinkList.size >= 6) {
                showCustomToast("최대 6개까지만 태그할 수 있습니다.")
                return@setOnClickListener
            }
            val dialog = AddDrinkDialog(requireContext(), requireActivity()).apply {
                setDialogListener(object : AddDrinkListener{
                    override fun onComplete(item: DrinkItem) {
                        drinkList.add(item)
                        binding.rvDrinks.adapter?.notifyDataSetChanged()
                        dismiss()
                    }
                })
            }
            dialog.show()
        }
        binding.rvDrinks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvDrinks.adapter = DrinkAdapter(requireActivity(), requireContext(), drinkList).apply {
            setInterface(object : DrinkAdapterInterface{
                override fun clickDelete(icon: Int, colorType: Int, name: String, position: Int) {
                    val dialog = CustomDialogDeleteTag(requireContext(), icon, name, colorType).apply {
                        setDialogListener(object : CustomDialogDeleteTagInterface{
                            override fun onFirstClicked() { // plan 삭제
                                drinkList.removeAt(position)
                                binding.rvDrinks.adapter?.notifyDataSetChanged()
                                dismiss()
                            }

                            override fun onSecondClicked() { // plan 취소
                                dismiss()
                            }
                        })
                    }.show()
                }

                override fun clickRecipe(icon: Int, name: String, colorType: Int) {
                    requireActivity().findNavController(R.id.main_nav_host_fragment).navigate(WriteFragmentDirections.actionWriteFragmentToWriteRecipeFragment(icon, name, colorType))
                }
            })
        }
        //https://github.com/akvelon/android-image-picker
        ImagePicker.launch(this)

        binding.btnComplete.setOnClickListener {
            if(binding.etContent.text.isEmpty()) {
                showCustomToast("글을 입력해주세요")
                return@setOnClickListener
            }
            if(drinkList.isEmpty()) {
                showCustomToast("술을 하나 이상 태그해주세요")
                return@setOnClickListener
            }

            showProgress()

            val imageFileList : MutableList<MultipartBody.Part> = mutableListOf()
            for(img in images) {
                if(img.length() / 1024 > 5000) {
                    var bitmap = BitmapFactory.decodeFile(img.absolutePath)
                    var storage = requireContext().cacheDir
                    var filename = "_" + System.currentTimeMillis() + ".jpg"
                    var tempFile = File(storage, filename)
                    tempFile.createNewFile()
                    var out = FileOutputStream(tempFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out)
                    out.close()
                    val re = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), tempFile)
                    val multipartItem = MultipartBody.Part.createFormData("files", tempFile.name, re)
                    imageFileList.add(multipartItem)
                    Log.d(TAG, "image size : ${tempFile.length() / 1024}")
                }
                else {
                    val re = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), img)
                    val multipartItem = MultipartBody.Part.createFormData("files", img.name, re)
                    imageFileList.add(multipartItem)
                }
            }
            var hasRecipeYn = "N"
            if(hasRecipe == true) {
                hasRecipeYn = "Y"
            } // 흰 메인 분 노 갈
            for(drink in drinkList) {
                var reYn = "N"
                if(drink.hasRecipe) reYn = "Y"
                val iconName = iconHashMap.filterValues { it == drink.icon }.keys.elementAt(0)
                val color = ApplicationClass.colorToCodeHashMap[drink.colorType]!!
                val realIconName = DrinkUtil.convertIconFromColor(iconName, color)
                Log.d(TAG, "drink color is ${color}, icon is ${realIconName}, name is ${drink.name}")
                alcoholTagList.add(AddPostRequest.AlcoholTag(color,realIconName,drink.name,reYn))
            }
            val addPostRequest = AddPostRequest(alcoholTagList, binding.etContent.text.toString(),hasRecipeYn,ingredientList,stepList)
            val hashMap = HashMap<String, RequestBody>()
            Log.d(TAG, addPostRequest.toString())
            val requestString = Gson().toJson(addPostRequest)
            val body = RequestBody.create("application/json".toMediaTypeOrNull(), requestString)
            hashMap.put("request",body)
            viewModel.postAddPost(hashMap, imageFileList)
        }
    }


    override fun initDataBinding() {
        viewModel.getMyPostResponse.observe(this@WriteFragment, Observer {
            if(it == null) showNetworkError()
            else {
                Log.d(TAG, it.toString())
                if(it.code == 0) {
                    val tmpList : MutableList<MainBoardResponse.Content> = mutableListOf()
                    for(i in it.data.content) {
                        tmpList.add(i)
                    }
                    userInfo?.let {
                        it.data.posts += 1
                    }
                    ApplicationClass.myPostedPost = tmpList
                    removeActivity()
                }
            }
            hideProgress()
        })
        viewModel.addPostResponse.observe(this@WriteFragment, Observer {
            if(it == null) showNetworkError()
            else {
                Log.d(TAG, it.toString())
                if(it.code == 0) {
                    DrinkUtil.clearRecipeSaved()
                    viewModel.getMainBoard(0,50)
//                    removeActivity()
                }
            }
//            hideProgress()
        })
        viewModel.mainBoardResponse.observe(this@WriteFragment, Observer {
            if(it == null) showNetworkError()
            else {
                Log.d(TAG, it.toString())
                if(it.code == 0) {
                    ApplicationClass.mainBoardList = it.data.content
                    viewModel.getMyPost(0,50)
                }
            }
        })

    }

    override fun initAfterBinding() {

    }

    override fun reLoadUI() {
        if(DrinkUtil.checkSavedRecipe()) {
            for (drink in drinkList) {
                drink.hasRecipe = drink.name == DrinkUtil.DrinkName
            }
            hasRecipe = true
            ingredientList = DrinkUtil.materialList
            stepList = DrinkUtil.stepList
            binding.rvDrinks.adapter?.notifyDataSetChanged()
        }
        Log.d(TAG, "recipe exist = $hasRecipe")
    }

    fun removeActivity() {
        requireActivity().finish()
        requireActivity().overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
    }

    //            get a single image only
//            val image: File? = getSingleImageOrNull(data)
//            Log.d(TAG, image.toString())
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (shouldResolve(requestCode, resultCode)) {
            if (data?.extras?.getInt("cancelSelect") == -1) {
                DrinkUtil.clearRecipeSaved()
                removeActivity()
                return
            }
            Log.d(TAG, "onActivity Result")
            // Get a list of picked images
            images = getImages(data)
            Log.d(TAG, images.toString())
            if (images.isEmpty()) {
                showCustomToast("사진을 선택해주세요")
            } else {
                for (item in images) {
                    viewPagerAdapter?.addFragment(ImageFragment(item))
                    Log.d(TAG, "image file size : ${item.length() / 1024}")
                }
                binding.vpPager.adapter = viewPagerAdapter
                binding.wormDotsIndicator.setViewPager2(binding.vpPager)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private inner class ViewPagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        var fragments: MutableList<Fragment> = mutableListOf()

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment) {
            fragments.add(fragment)
            notifyItemInserted(fragments.size - 1)
        }
    }

    companion object {
        const val TAG = "WriteFragment"
    }
}
