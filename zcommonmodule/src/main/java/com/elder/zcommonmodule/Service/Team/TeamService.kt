package com.elder.zcommonmodule.Service.Team

import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface TeamService {

    @Headers("Content-type:application/json;charset=utf-8")//需要添加touch
    @POST("AmoskiRiding/ridingManage/recordRidingData")
    fun startDriver(@Header("appToken") header: String, @Body builder: RequestBody
    ): Observable<BaseResponse>

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


    @Multipart
    @POST("AmoskiActivity/ridingManage/ridingFileUpd")
    fun uploadDriverFiles(@Header("appToken") header: String, @Part builder: List<MultipartBody.Part>
                          , @Part part: MultipartBody.Part
    ): Observable<BaseResponse>

    @Multipart
    @POST("AmoskiActivity/ridingManage/uploadRidingPic")
    fun uploadDriverImages(@Header("appToken") header: String, @Part builder: List<MultipartBody.Part>
                           , @Part part: MultipartBody.Part
    ): Observable<BaseResponse>

}