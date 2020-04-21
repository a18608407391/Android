package com.elder.zcommonmodule.Service.Login

import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.lang.StringBuilder

interface LoginService {


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/memberUser/login")
    fun login(@Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/memberUser/checkVersionUpd")
    fun getVession(@Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/userCenterManage/exitLogin")
    fun exitLogin(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiActivity/memberUser/quickLogin")
    fun reLogin(@Body builder: RequestBody
    ): Observable<BaseResponse>


}