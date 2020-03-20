package com.elder.zcommonmodule.Service.Party

import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.lang.StringBuilder

interface PartyService {


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/appRidingGuideManage/activityIndex")
    fun partyHome(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    //活动详情
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/appRidingGuideManage/queryActivityDetailInfo1")
    fun partyDetail(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/appRidingGuideManage/queryOrganization")
    fun partyOrganization(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/appRidingGuideManage/queryActivitySignList")
    fun partySignList(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/appRidingGuideManage/searchActivityList")
    fun partySearch(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/appRidingGuideManage/querySubjectActivityList")
    fun partySubject(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/appRidingGuideManage/queryMotoActivityList")
    fun partyMoto(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/appRidingGuideManage/queryClockActivityList")
    fun partyClock(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/appRidingGuideManage/queryActivityAlbum")
    fun partyAlbum(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/appRidingGuideManage/queryClockActivityRankingList")
    fun partyRanking(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


}