package com.elder.zcommonmodule.Service.Team

import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface RoadBookService {

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiRiding/appRidingGuideManage/queryGuideList")
    fun getRoadBookGuideList(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiRiding/appRidingGuideManage/queryRouteList")
    fun getRoadBookDetail(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiRiding/appRidingGuideManage/queryGuideList")
    fun searchRoadBook(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiRiding/appRidingGuideManage/downRoadBook")
    fun downloadRoadBook(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiRiding/appRidingGuideManage/getMyRoadBook")
    fun getMyRoadBook(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>
}