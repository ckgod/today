package com.theplay.aos.fragment.mypage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theplay.aos.api.RemoteRepository
import com.theplay.aos.api.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class MyPageFollowViewModel() : ViewModel() {
    private val remoteRepository: RemoteRepository =
        RemoteRepository()

    private var _followerListResponse : MutableLiveData<FollowListResponse> = MutableLiveData()
    val followerListResponse get() = _followerListResponse

    fun getFollower() {
        CompositeDisposable().add(
            remoteRepository.getFollower()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        followerListResponse.postValue(response)
                    }, { throwable ->
                        Log.d(TAG,"throwable.localizedMessage${throwable.localizedMessage}")
                        followerListResponse.postValue(null)
                    })
        )
    }

    private var _followingListResponse : MutableLiveData<FollowListResponse> = MutableLiveData()
    val followingListResponse get() = _followingListResponse

    fun getFollowing() {
        CompositeDisposable().add(
            remoteRepository.getFollowing()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        followingListResponse.postValue(response)
                    }, { throwable ->
                        Log.d(TAG,"throwable.localizedMessage${throwable.localizedMessage}")
                        followingListResponse.postValue(null)
                    })
        )
    }


    companion object{
        const val TAG = "MyPageFollowViewModel"
    }
}