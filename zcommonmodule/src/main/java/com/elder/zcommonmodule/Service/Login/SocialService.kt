package com.elder.zcommonmodule.Service.Login

import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface SocialService {
    @Multipart
    @POST("AmoskiActivity/DynamicManage/saveImgCompressFile")
    fun uploadPhoto(@Header("appToken") header: String, @Part builder: List<MultipartBody.Part>
    ): Observable<BaseResponse>

//    POST /DynamicManage/releaseDynamic

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/DynamicManage/releaseDynamic")
    fun releaseDynamics(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/DynamicManage/queryDynamicList")
    fun getDynamicList(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //收藏
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/DynamicManage/dynamicCollection")
    fun Collection(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //评论
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/DynamicManage/dynamicComment")
    fun Comment(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //评论列表
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/DynamicManage/dynamicCommentList")
    fun getCommentList(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>
    //点赞

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("/AmoskiActivity/DynamicManage/commentFabulous")
    fun commentLike(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/DynamicManage/dynamicFabulous")
    fun Like(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //点赞列表
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/DynamicManage/dynamicFabulousList")
    fun LikerList(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //关注
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/DynamicManage/dynamicFollow")
    fun Focus(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //关注列表
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/DynamicManage/dynamicFollowList")
    fun FocuserList(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //个人获取动态列表
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/UserPersonalCenter/queryDynamicList")
    fun MyDynamicsList(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //骑士首页

    //    POST /SameCity/knightHomePage
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/SameCity/knightHomePage")
    fun getCanalier(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //    POST /SameCity/queryMemberByMemberName
    //    POST /SameCity/knightHomePage
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/SameCity/queryMemberByMemberName")
    fun QueryMember(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //    POST /SameCity/getFabulousList
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/SameCity/getFabulousList")
    fun getLikeResult(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    //    POST /DynamicManage/deleteDynamic
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/DynamicManage/deleteDynamic")
    fun deleteSocial(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

}