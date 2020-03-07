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


}