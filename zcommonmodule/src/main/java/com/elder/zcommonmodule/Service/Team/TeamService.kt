package com.elder.zcommonmodule.Service.Team

import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface TeamService {
    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiRidingTeam/ridingTeam/createTeam")
    fun createTeam(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiRidingTeam/ridingTeam/joinTeam")
    fun JoinTeam(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiRidingTeam/ridingTeam/queryTeamInvalid")
    fun queryTeamInvalid(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>


    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiRidingTeam/ridingTeam/queryTeamRoleInfo")
    fun queryTeamRoleInfo(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

}