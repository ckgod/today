package com.theplay.aos.api

import com.theplay.aos.api.model.*
import io.reactivex.Observable
import retrofit2.http.*

class UserApi {
    interface UserApiImpl {
        @POST("/v1/sign-in")
        fun postLogin(@Body params: LoginRequest): Observable<LoginResponse>

        @POST("/v1/sign-up")
        fun postSignUp(@Body params : SignUpRequest) : Observable<SignUpResponse>

        @PUT("/v1/user/nickname")
        fun putNickName(@Body params : NickNameRequest) : Observable<DefaultResponse>

        @GET("/v1/user/notice")
        fun getNotice() : Observable<NoticeResponse>

        @GET("/v1/user/notice/{noticeId}")
        fun getNoticeDetail(@Path("noticeId") noticeId : Int):Observable<NoticeDetailResponse>

        @GET("/v1/sign-up")
        fun getRandomNick():Observable<RandomNickNameResponse>

        @GET("/v1/user/main-info")
        fun getMyPageTopInfo() : Observable<MyPageTopResponse>

        @GET("/v1/popular-recipes") // 인기 레시피 조회
        fun getHotRecipe(@Query("pageNumber") pageNumber : Int, @Query("pageSize") pageSize : Int) : Observable<HotRecipeResponse>

        @GET("/v1/main-posts")
        fun getMainBoard(@Query("pageNumber") pageNumber: Int, @Query("pageSize") pageSize: Int) : Observable<MainBoardResponse>

        @GET("/v1/user/setting")
        fun getSettingProfile() :Observable<SettingProfileResponse>

        @PUT("/v1/user/nickname")
        fun putChangeNickName(@Body params : ChangeNickRequest) : Observable<DefaultResponse>

        @PUT("/v1/user/password")
        fun putChangePw(@Body params : ChangePwRequest) : Observable<DefaultResponse>

        @GET("/v1/user/show-yn")
        fun getPrivacyYn() : Observable<PrivacyStatusResponse>


    }
}