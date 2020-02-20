package com.elder.zcommonmodule.Service.Login

import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface PrivateService {


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/userCenterManage/queryAllWaterMake")
    fun getMakerList(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //个人关注列表
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/UserPersonalCenter/queryFollowList")
    fun MyFocuserList(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //个人粉丝列表
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/UserPersonalCenter/queryFansList")
    fun MyFansList(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //个人赞列表POST /UserPersonalCenter/queryFabulousList
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/UserPersonalCenter/queryFabulousList")
    fun MyLikeList(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //    POST /UserPersonalCenter/dynamicCollectionList
    //个人赞列表POST /UserPersonalCenter/queryFabulousList
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/UserPersonalCenter/dynamicCollectionList")
    fun MyCollection(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>
//    POST /SameCity/sameCityData

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/SameCity/sameCityData1")
    fun Home(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/SameCity/queryMemberRanking")
    fun QueryRanking(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/unifiedInfo/getSystemInfoList")
    fun QuerySystemNotify(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/unifiedInfo/getActivityInfoList")
    fun QueryActiveNotify(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/unifiedInfo/deleteActivityInfoList")
    fun deleteActiveNotify(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/unifiedInfo/deleteUserMessageList")
    fun deleteSystemNotify(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/unifiedInfo/getCallMeList")
    fun queryAtMeList(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("/AmoskiActivity/DynamicManage/queryCommentMeList")
    fun queryCommandMeList(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("/AmoskiActivity/unifiedInfo/getUnifiedInfoCount")
    fun getMsgCount(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>
}