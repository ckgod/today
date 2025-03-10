package com.theplay.aos.fragment.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theplay.aos.api.RemoteRepository
import com.theplay.aos.api.model.*
import com.theplay.aos.fragment.mypage.MyPageBoardViewModel
import com.theplay.aos.fragment.mypage.MyPageGoodViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class MainBoardViewModel() : ViewModel() {
    private val remoteRepository: RemoteRepository =
        RemoteRepository()

    private var _postLikeResponse : MutableLiveData<PostLikeResponse> = MutableLiveData()
    val postLikeResponse get() = _postLikeResponse

    fun postLike(postId : Int) {
        CompositeDisposable().add(
            remoteRepository.postLike(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        postLikeResponse.postValue(response)
                    }, { throwable ->
                        Log.d(TAG,"throwable.localizedMessage${throwable.localizedMessage}")
                        postLikeResponse.postValue(null)
                    })
        )
    }

    private var _getLikedResponse : MutableLiveData<MainBoardResponse> = MutableLiveData()
    val getLikedResponse get() = _getLikedResponse

    fun getLikedPost(pageNumber : Int, pageSize : Int) {
        CompositeDisposable().add(
            remoteRepository.getLikedPost(pageNumber, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        getLikedResponse.postValue(response)
                    }, { throwable ->
                        Log.d(TAG,"throwable.localizedMessage${throwable.localizedMessage}")
                        getLikedResponse.postValue(null)
                    })
        )
    }

    private var _postFollowingResponse : MutableLiveData<DefaultResponse> = MutableLiveData()
    val postFollowingResponse get() = _postFollowingResponse

    fun postFollow(userId: Int) {
        CompositeDisposable().add(
            remoteRepository.postFollow(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        postFollowingResponse.postValue(response)
                    }, { throwable ->
                        if(throwable is HttpException) {
                            when(throwable.code()) {
                                409 -> { // 이미 팔로우 되어있을때
                                    postFollowingResponse.postValue(DefaultResponse(409,"이미 팔로우 중인 사용자입니다.", true))
                                }
                                else -> {
                                    postFollowingResponse.postValue(null)
                                }
                            }
                        }
                        else {
                            Log.d(TAG, "throwable.localizedMessage${throwable.localizedMessage}")
                            postFollowingResponse.postValue(null)
                        }
                    })
        )
    }

    private var _postSaveRecipeResponse : MutableLiveData<RecipeSaveResponse> = MutableLiveData()
    val postSaveRecipeResponse get() = _postSaveRecipeResponse

    fun postSaveRecipe(tagId : Int) {
        CompositeDisposable().add(
            remoteRepository.postSaveRecipe(tagId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        postSaveRecipeResponse.postValue(response)
                    }, { throwable ->
                        if(throwable is HttpException) {
                            when(throwable.code()) {
                            }
                        }
                        else {
                            Log.d(TAG, "throwable.localizedMessage${throwable.localizedMessage}")
                            postSaveRecipeResponse.postValue(null)
                        }
                    })
        )
    }

    private var _postReportResponse : MutableLiveData<DefaultResponse> = MutableLiveData()
    val postReportResponse get() = _postReportResponse

    fun postReport(reportRequest: ReportRequest) {
        CompositeDisposable().add(
            remoteRepository.postReport(reportRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        postReportResponse.postValue(response)
                    }, { throwable ->
                        Log.d(TAG, "throwable.localizedMessage${throwable.localizedMessage}")
                        postReportResponse.postValue(null)
                    })
        )
    }

    private var _deletePostResponse : MutableLiveData<DefaultResponse> = MutableLiveData()
    val deletePostResponse get() = _deletePostResponse

    fun deletePost(postId: Int) {
        CompositeDisposable().add(
            remoteRepository.deletePost(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        deletePostResponse.postValue(response)
                    }, { throwable ->
                        Log.d(TAG, "throwable.localizedMessage${throwable.localizedMessage}")
                        deletePostResponse.postValue(null)
                    })
        )
    }

    private var _getMyPostResponse : MutableLiveData<MainBoardResponse> = MutableLiveData()
    val getMyPostResponse get() = _getMyPostResponse

    fun getMyPost(pageNumber : Int, pageSize : Int) {
        CompositeDisposable().add(
            remoteRepository.getMyPost(pageNumber, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        getMyPostResponse.postValue(response)
                    }, { throwable ->
                        Log.d(TAG,"throwable.localizedMessage${throwable.localizedMessage}")
                        getMyPostResponse.postValue(null)
                    })
        )
    }

    private var _mainBoardResponse : MutableLiveData<MainBoardResponse> = MutableLiveData()
    val mainBoardResponse get() = _mainBoardResponse

    fun getMainBoard(pageNumber : Int, pageSize : Int) {
        CompositeDisposable().add(
            remoteRepository.getMainBoard(pageNumber, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mainBoardResponse.postValue(response)
                    }, { throwable ->
                        Log.d(HomeViewModel.TAG,"throwable.localizedMessage${throwable.localizedMessage}")
                        mainBoardResponse.postValue(null)
                    })
        )
    }

    companion object{
        const val TAG = "MainBoardViewModel"
    }
}