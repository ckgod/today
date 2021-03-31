package com.theplay.aos.iadapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.theplay.aos.ApplicationClass.Companion.X_ACCESS_TOKEN
import com.theplay.aos.MainActivity
import com.theplay.aos.R
import com.theplay.aos.SplashActivity
import com.theplay.aos.customview.CustomDialogListener
import com.theplay.aos.customview.CustomDialogTwoButton
import com.theplay.aos.databinding.ItemMyPageBoardAllBinding
import com.theplay.aos.databinding.ItemRecipeColorBinding
import com.theplay.aos.databinding.ItemRecipeImageBinding
import com.theplay.aos.databinding.ItemSettingBinding
import com.theplay.aos.fragment.setting.SettingFragmentDirections
import com.theplay.aos.item.MyPageBoardAllItem
import com.theplay.aos.item.RecipeColorItem
import com.theplay.aos.item.RecipeImageItem
import com.theplay.aos.item.SettingItem
import com.theplay.aos.utils.ViewUtils


class SettingAdapter(
    private val activity: Activity,
    private val context: Context,
    private val items: MutableList<SettingItem>
) : RecyclerView.Adapter<SettingAdapter.SettingVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingVH {
        val itemBinding =
            ItemSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingVH(itemBinding)
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: SettingVH, position: Int) {
        val item: SettingItem = items[position]
        holder.bind(item)
    }

    inner class SettingVH(var binding: ItemSettingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SettingItem) {
            binding.tvName.text = item.name
            setIcon(binding, item)
            itemView.setOnClickListener {
                when (item.type) {
                    MODIFY_PROFILE -> {
                        activity.findNavController(R.id.main_nav_host_fragment).navigate(SettingFragmentDirections.actionSettingFragmentToSettingProfileFragment())
                    }
                    LOGOUT -> {
                        var dialog = CustomDialogTwoButton(
                            context,
                            context.getString(R.string.logout_title),
                            context.getString(R.string.logout_content),
                            context.getString(R.string.logout_title),
                            context.getString(R.string.cancel)
                        ).apply {
                            setDialogListener(object : CustomDialogListener {
                                override fun onFirstClicked() {
                                    var preferences: SharedPreferences = context.getSharedPreferences(X_ACCESS_TOKEN, Context.MODE_PRIVATE)
                                    var editor = preferences.edit()
                                    editor.clear()
                                    editor.apply()
                                    val nextIntent = Intent(context, SplashActivity::class.java)
                                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(nextIntent)
                                    dismiss()
                                }
                                override fun onSecondClicked() {
                                    dismiss()
                                }
                            })
                        }
                        dialog.show()
                    }
                    NOTICE -> {

                    }
                    ASK -> {

                    }
                    SET_ALARM -> {

                    }
                    QUIT_ACCOUNT -> {

                    }
                    OPEN_SOURCE -> {

                    }
                }
            }
        }
    }

    fun setIcon(binding: ItemSettingBinding, item: SettingItem) {
        when (item.type) {
            MODIFY_PROFILE -> {
                binding.ivSetting.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_profile_setting))
            }
            LOGOUT -> {
                binding.ivSetting.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_logout))
            }
            NOTICE -> {
                binding.ivSetting.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_notice))
            }
            ASK -> {
                binding.ivSetting.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_ask_the_play))
            }
            SET_ALARM -> {
                binding.ivSetting.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_alarm_setting))
            }
            QUIT_ACCOUNT -> {
                binding.ivSetting.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_quit_account))
            }
            OPEN_SOURCE -> {
                binding.ivSetting.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_open_source))
            }
        }
    }

    companion object {
        const val TAG = "SettingAdapter"
        const val MODIFY_PROFILE = 1
//        const val BLOCK_USER = 2
        const val LOGOUT = 2
        const val NOTICE = 3
        const val ASK = 4
        const val SET_ALARM = 5
        const val QUIT_ACCOUNT = 6
        const val OPEN_SOURCE = 7
    }
}
