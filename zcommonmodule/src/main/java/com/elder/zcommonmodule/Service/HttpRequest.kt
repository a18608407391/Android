package com.elder.zcommonmodule.Service

import android.util.Log
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Http.NetWorkManager
import com.elder.zcommonmodule.Service.Error.ExceptionEngine
import com.elder.zcommonmodule.Service.Error.ServerResponseError
import com.elder.zcommonmodule.Service.Login.LoginService
import com.elder.zcommonmodule.Service.Login.PrivateService
import com.elder.zcommonmodule.Service.Login.SocialService
import com.elder.zcommonmodule.Service.Party.PartyService
import com.elder.zcommonmodule.Service.Team.RoadBookService
import com.elder.zcommonmodule.Service.Team.TeamService
import com.elder.zcommonmodule.USER_TOKEN
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.PreferenceUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Bus.RxBus


class HttpRequest {
    companion object {
        val instance: HttpRequest by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpRequest()
        }
    }

    var startDriver: HttpInteface.startDriverResult? = null
    fun startDriver(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(TeamService::class.java)?.startDriver(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            startDriver?.startDriverError(it)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                startDriver?.startDriverSuccess(t)
            }

            override fun onError(e: Throwable) {
                startDriver?.startDriverError(e)
            }
        })
    }

    var CreateResult: HttpInteface.CreateTeamResult? = null
    var JoinResult: HttpInteface.JoinTeamResult? = null
    var LGresult: HttpInteface.LoginResult? = null
    var QueryRollResult: HttpInteface.QueryRollInfo? = null
    var CheckTeamResult: HttpInteface.CheckTeamStatus? = null
    var getMakerList: HttpInteface.getMakerList? = null
    var RoadBookGuideList: HttpInteface.getRoadBookList? = null
    var RoadBookDetail: HttpInteface.RoadBookDetail? = null
    var SearchRoadBook: HttpInteface.SearchRoadBook? = null
    var downLoadRoad: HttpInteface.DownLoadRoodBook? = null
    var myRoadBook: HttpInteface.getMyRoadBook? = null
    var getMsgCount: HttpInteface.getMsgNotifyCount? = null

    //个人跳转后数据
    var socialGetdynamicList: HttpInteface.SocialMyDynamicList? = null
    var privateGetFansList: HttpInteface.PrivateFansList? = null
    var privateGetFocusList: HttpInteface.PrivateFocusList? = null
    var privateLikeList: HttpInteface.PrivateLikeList? = null
    var privateRestoreList: HttpInteface.PrivateRestoreList? = null
    var deleteSocialResult: HttpInteface.deleteSocialResult? = null
    var homeResult: HttpInteface.HomeResult? = null


    fun getRbookGuideResult(result: HttpInteface.getRoadBookList) {
        this.RoadBookGuideList = result
    }


    fun getMakerListResult(result: HttpInteface.getMakerList) {
        this.getMakerList = result
    }

    fun setLoginResult(result: HttpInteface.LoginResult) {
        this.LGresult = result
    }

    fun setCheckStatusResult(result: HttpInteface.CheckTeamStatus) {
        this.CheckTeamResult = result
    }

    fun setCreateTeamResult(result: HttpInteface.CreateTeamResult) {
        this.CreateResult = result
    }

    fun setJoinTeamResult(result: HttpInteface.JoinTeamResult) {
        this.JoinResult = result
    }

    fun setQueryRollInfoResult(result: HttpInteface.QueryRollInfo) {
        this.QueryRollResult = result
    }

    fun Login(map: HashMap<String, String>) {
        NetWorkManager.instance.getOkHttpRetrofit()?.create(LoginService::class.java)?.login(NetWorkManager.instance.getBaseRequestBody(map)!!)?.map {
            return@map it
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.doOnError {
            LGresult?.LoginError(it)
        }?.onErrorResumeNext(Observable.empty())?.subscribe(object : Observer<BaseResponse> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponse) {
                LGresult?.LoginSuccess(t)
            }

            override fun onError(e: Throwable) {
                LGresult?.LoginError(e)
            }
        })
    }


    fun createTeam(map: HashMap<String, String>) {
        //joinAddr
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(TeamService::class.java)?.createTeam(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            CreateResult?.CreateTeamError(it)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                CreateResult?.CreateTeamSuccess(t)
            }

            override fun onError(e: Throwable) {
                CreateResult?.CreateTeamError(e)
            }
        })
    }


    var exit: HttpInteface.ExitLogin? = null
    fun exitLogin(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(LoginService::class.java)?.exitLogin(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            exit?.ExitLoginError(it)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                exit?.ExitLoginSuccess(t)
            }

            override fun onError(e: Throwable) {
                exit?.ExitLoginError(e)
            }
        })
    }


    fun JoinTeam(map: HashMap<String, String>) {
        //joinAddr
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(TeamService::class.java)?.JoinTeam(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            JoinResult?.JoinTeamError(it)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                JoinResult?.JoinTeamSuccess(t)
            }

            override fun onError(e: Throwable) {
                JoinResult?.JoinTeamError(e)
            }
        })
    }


    var ReLoginImpl: HttpInteface.IRelogin? = null
    fun relogin(phone: HashMap<String, String>) {
//        val aesPhone = String(StringBuilder(Base64.encodeToString(phone.tob(Charset.forName("UTF-8")), Base64.DEFAULT)))
//        var map = HashMap<String, String>()
//        map.put("phoneNumber", aesPhone)
        NetWorkManager.instance.getOkHttpRetrofit()!!.create(LoginService::class.java).reLogin(NetWorkManager.instance.getBaseReplaceRequestBody(phone)!!).map { baseResponse -> baseResponse.data.toString() }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                RxBus.default!!.post(RxBusEven.getInstance(RxBusEven.RELOGIN))
                PreferenceUtils.putString(context, USER_TOKEN, t)
                if (ReLoginImpl != null) {
                    ReLoginImpl!!.IReloginSuccess(t)
                }
            }

            //            override fun onSubscribe(d: Disposable) {
//
//            }
//
//            override fun onNext(o: Any) {
//                RxBus.default!!.post(RxBusEven.getInstance(RxBusEven.RELOGIN))
//                PreferenceUtils.putString(context, USER_TOKEN, o.toString())
//            }
//
            override fun onError(e: Throwable) {
                RxBus.default!!.post(RxBusEven.getInstance(RxBusEven.RELOGIN))
                if (ReLoginImpl != null) {
                    ReLoginImpl!!.IReloginError()
                }

            }
//
//            override fun onComplete() {
//
//            }
        })
    }

    fun checkTeamStatus(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(TeamService::class.java)?.queryTeamInvalid(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.doOnError {
            CheckTeamResult?.CheckTeamStatusError(it)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<BaseResponse> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponse) {
                CheckTeamResult?.CheckTeamStatusSucccess(t)
            }

            override fun onError(e: Throwable) {
                CheckTeamResult?.CheckTeamStatusError(e)
            }
        })
    }

    fun getManagerName(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(TeamService::class.java)?.queryTeamRoleInfo(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            QueryRollResult?.QueryRollInfoError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                QueryRollResult?.QueryRollInfoSuccess(t)
            }

            override fun onError(e: Throwable) {
                QueryRollResult?.QueryRollInfoError(e)
            }
        })
    }


    fun getMakerList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.getMakerList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            getMakerList?.getMakerListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                getMakerList?.getMakerListSuccess(t)
            }

            override fun onError(e: Throwable) {
                getMakerList?.getMakerListError(e)
            }
        })
    }

    fun getRoadBookList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(RoadBookService::class.java)?.getRoadBookGuideList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            RoadBookGuideList?.getRoadBookError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                RoadBookGuideList?.getRoadBookSuccess(t)
            }

            override fun onError(e: Throwable) {
                RoadBookGuideList?.getRoadBookError(e)
            }
        })
    }

    fun getRoadBookDetail(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(RoadBookService::class.java)?.getRoadBookDetail(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            RoadBookDetail?.getRoadBookDetailError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                RoadBookDetail?.getRoadBookDetailSuccess(t)
            }

            override fun onError(e: Throwable) {
                RoadBookDetail?.getRoadBookDetailError(e)
            }
        })
    }


    fun SearchRoadBookDetail(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(RoadBookService::class.java)?.searchRoadBook(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            SearchRoadBook?.SearchRoadBookError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                SearchRoadBook?.SearchRoadBookSuccess(t)
            }

            override fun onError(e: Throwable) {
                SearchRoadBook?.SearchRoadBookError(e)
            }
        })
    }


    fun DownLoadRoadBook(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(RoadBookService::class.java)?.downloadRoadBook(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map {
            return@map it
        }?.doOnError {
            downLoadRoad?.DownLoadRoadBookError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<BaseResponse> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponse) {
                downLoadRoad?.DownLoadRoadBookSuccess(t)
            }

            override fun onError(e: Throwable) {
                downLoadRoad?.DownLoadRoadBookError(e)
            }
        })
    }


    fun getMyRoadBook(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(RoadBookService::class.java)?.getMyRoadBook(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            myRoadBook?.getMyRoadBookkError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                myRoadBook?.getMyRoadBookSuccess(t)
            }

            override fun onError(e: Throwable) {
                myRoadBook?.getMyRoadBookkError(e)
            }
        })
    }


    var postPhotoResult: HttpInteface.SocialUploadPhoto? = null
    var DynamicListResult: HttpInteface.SocialDynamicsList? = null
    var DynamicLikeResult: HttpInteface.SocialDynamicsLike? = null
    var CommentLikeResult: HttpInteface.SocialCommentLike? = null
    var DynamicCommentResult: HttpInteface.SocialDynamicsComment? = null
    var DynamicCommentListResult: HttpInteface.SocialDynamicsCommentList? = null
    var DynamicFocusResult: HttpInteface.SocialDynamicsFocus? = null
    var DynamicCollectionResult: HttpInteface.SocialDynamicsCollection? = null
    var DynamicLikerListResult: HttpInteface.SocialDynamicsLikerList? = null
    var DynamicFocusListResult: HttpInteface.SocialDynamicsFocuserList? = null


    var uploadDriverFiles: HttpInteface.IUploadDriverFiles? = null
    var uploadDriverImages: HttpInteface.IUploadDriverImages? = null
    fun postDriverFile(part: MultipartBody, part1: MultipartBody.Part) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(TeamService::class.java)?.uploadDriverFiles(token, part.parts(), part1)?.map(ServerResponseError())?.doOnError {
            uploadDriverFiles?.UploadDriverError(it)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                uploadDriverFiles?.UploadDriverSuccess(t)
            }

            override fun onError(e: Throwable) {
                uploadDriverFiles?.UploadDriverError(e)
            }
        })
    }

    fun postDriverImage(part: MultipartBody, part1: MultipartBody.Part) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(TeamService::class.java)?.uploadDriverImages(token, part.parts(), part1)?.map(ServerResponseError())?.doOnError {
            uploadDriverImages?.UploadDriverImagesError(it)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                uploadDriverImages?.UploadDriverImagesSuccess(t)
            }

            override fun onError(e: Throwable) {
                uploadDriverImages?.UploadDriverImagesError(e)
            }
        })
    }


    fun postMuiltyPhoto(part: MultipartBody) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.uploadPhoto(token, part.parts())?.map(ServerResponseError())?.doOnError {
            postPhotoResult?.postPhotoError(it)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                postPhotoResult?.postPhotoSuccess(t)
            }

            override fun onError(e: Throwable) {
                postPhotoResult?.postPhotoError(e)
            }

        })
    }

    var resultReleaseDynamics: HttpInteface.SocialReleaseDynamics? = null

    fun postReleaseDynamics(map: HashMap<String, Any>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.releaseDynamics(token, NetWorkManager.instance.getBaseRequestBodyAny(map)!!)?.map(ServerResponseError())?.doOnError {
            Log.e("result", "错误出现位置2" + it.message)
            resultReleaseDynamics?.ResultReleaseDynamicsError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                resultReleaseDynamics?.ResultReleaseDynamicsSuccess(t)
            }

            override fun onError(e: Throwable) {
                Log.e("result", "错误出现位置1" + e.message)
                resultReleaseDynamics?.ResultReleaseDynamicsError(e)
            }
        })
    }

    fun getDynamicsLike(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.Like(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicListResult?.ResultSDListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicLikeResult?.ResultLikeSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicLikeResult?.ResultLikeError(e)
            }
        })
    }

    fun getCommentDynamicsLike(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.commentLike(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            CommentLikeResult?.ResultCommentLikeError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                CommentLikeResult?.ResultCommentLikeSuccess(t)
            }

            override fun onError(e: Throwable) {
                CommentLikeResult?.ResultCommentLikeError(e)
            }
        })
    }


    fun getDynamicsCommonList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.getCommentList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicListResult?.ResultSDListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicCommentListResult?.ResultCommentListSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicCommentListResult?.ResultCommentListError(e)
            }
        })
    }


    fun getDynamicsCommon(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.Comment(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicListResult?.ResultSDListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicCommentResult?.ResultCommentSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicCommentResult?.ResultCommentError(e)
            }
        })
    }


    fun getDynamicsCollection(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.Collection(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicListResult?.ResultSDListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicCollectionResult?.ResultCollectionSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicCollectionResult?.ResultCollectionError(e)
            }
        })
    }


    fun getDynamicsFocus(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.Focus(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicListResult?.ResultSDListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicFocusResult?.ResultFocusSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicFocusResult?.ResultFocusError(e)
            }
        })
    }


    fun getDynamicsList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.getDynamicList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            Log.e("result", "请求回来了吗2")
            DynamicListResult?.ResultSDListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                Log.e("result", "请求回来了吗")
                DynamicListResult?.ResultSDListSuccess(t)
            }

            override fun onError(e: Throwable) {
                Log.e("result", "请求回来了吗1")
                DynamicListResult?.ResultSDListError(e)
            }
        })
    }

    fun getDynamicsLikeList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.LikerList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicLikerListResult?.ResultLikerError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicLikerListResult?.ResultLikerSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicLikerListResult?.ResultLikerError(e)
            }
        })
    }


    fun getDynamicsFocusList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.FocuserList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            DynamicFocusListResult?.ResultFocuserError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                DynamicFocusListResult?.ResultFocuserSuccess(t)
            }

            override fun onError(e: Throwable) {
                DynamicFocusListResult?.ResultFocuserError(e)
            }
        })
    }


    //个人

    fun getSocialMyDynamicsList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.MyDynamicsList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            socialGetdynamicList?.ResultSocialMyDynamicError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                socialGetdynamicList?.ResultSocialMyDynamicSuccess(t)
            }

            override fun onError(e: Throwable) {
                socialGetdynamicList?.ResultSocialMyDynamicError(e)
            }
        })
    }

    fun getPrivateLikeList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.MyLikeList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            privateLikeList?.ResultPrivateLikeError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                privateLikeList?.ResultPrivateLikeSuccess(t)
            }

            override fun onError(e: Throwable) {
                privateLikeList?.ResultPrivateLikeError(e)
            }
        })
    }

    fun getPrivateFansList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.MyFansList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            privateGetFansList?.ResultPrivateFansError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                privateGetFansList?.ResultPrivateFansSuccess(t)
            }

            override fun onError(e: Throwable) {
                privateGetFansList?.ResultPrivateFansError(e)
            }
        })
    }

    fun getPrivateFocusList(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.MyFocuserList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            privateGetFocusList?.ResultPrivateFocusError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                privateGetFocusList?.ResultPrivateFocusSuccess(t)
            }

            override fun onError(e: Throwable) {
                privateGetFocusList?.ResultPrivateFocusError(e)
            }
        })
    }


    fun getPrivateCollection(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.MyCollection(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            privateRestoreList?.ResultPrivateDynamicError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }


            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                privateRestoreList?.ResultPrivateDynamicSuccess(t)
            }

            override fun onError(e: Throwable) {
                privateRestoreList?.ResultPrivateDynamicError(e)
            }
        })
    }


    fun getHome(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        Log.e("token", "$token")
        NetWorkManager
                .instance
                .getOkHttpRetrofit()?.create(PrivateService::class.java)?.Home(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
                    homeResult?.ResultHomeError(ExceptionEngine.handleException(it))
                }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: String) {
                        homeResult?.ResultHomeSuccess(t)
                    }

                    override fun onError(e: Throwable) {
                        homeResult?.ResultHomeError(e)

                    }
                })
    }


    var RankingResut: HttpInteface.getRankResult? = null

    fun QueryRanking(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.QueryRanking(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            RankingResut?.ResultRankingError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                RankingResut?.ResultRankingSuccess(t)
            }

            override fun onError(e: Throwable) {
                RankingResut?.ResultRankingError(e)
            }
        })
    }


    var canalierResult: HttpInteface.CanalierResult? = null

    fun CanalierHome(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.getCanalier(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            canalierResult?.ResultCanalierError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                canalierResult?.ResultCanalierSuccess(t)
            }

            override fun onError(e: Throwable) {
                canalierResult?.ResultCanalierError(e)
            }
        })
    }


    var queryMember: HttpInteface.SearchMember? = null

    fun SearchMember(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.QueryMember(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            queryMember?.SearchError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                queryMember?.SearchSuccess(t)
            }

            override fun onError(e: Throwable) {
                queryMember?.SearchError(e)
            }
        })
    }


    var getLikeResult: HttpInteface.GetLikeResult? = null


    fun GetLike(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.getLikeResult(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            getLikeResult?.getLikeError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                getLikeResult?.GetLikeSuccess(t)
            }

            override fun onError(e: Throwable) {
                getLikeResult?.getLikeError(e)
            }
        })
    }

    fun deleteSocial(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(SocialService::class.java)?.deleteSocial(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            deleteSocialResult?.deleteSocialError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                deleteSocialResult?.deleteSocialSuccess(t)
            }

            override fun onError(e: Throwable) {
                deleteSocialResult?.deleteSocialError(e)
            }
        })
    }


    var getSystemNotify: HttpInteface.querySystemNotifyList? = null
    var getActiveNotify: HttpInteface.queryActiveNotifyList? = null
    var deleteSystemNotify: HttpInteface.deleteSystemNotifyList? = null
    var deleteActiveNotify: HttpInteface.deleteActiveNotifyList? = null

    fun querySystemNotify(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.QuerySystemNotify(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            getSystemNotify?.SystemNotifyListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                getSystemNotify?.SystemNotifyListSuccess(t)
            }

            override fun onError(e: Throwable) {
                getSystemNotify?.SystemNotifyListError(e)
            }
        })
    }

    fun queryActiveNotify(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.QueryActiveNotify(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            getActiveNotify?.ActiveNotifyListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                getActiveNotify?.ActiveNotifyListSuccess(t)
            }

            override fun onError(e: Throwable) {
                getActiveNotify?.ActiveNotifyListError(e)
            }
        })
    }

    fun deleteActiveNotifyRequest(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.deleteActiveNotify(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            deleteActiveNotify?.ActiveNotifyListDeleteError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                deleteActiveNotify?.ActiveNotifyListDeleteSuccess(t)
            }

            override fun onError(e: Throwable) {
                deleteActiveNotify?.ActiveNotifyListDeleteError(e)
            }
        })
    }


    fun deleteSystemNotifyRequest(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.deleteSystemNotify(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            deleteSystemNotify?.SystemNotifyDeleteListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                deleteSystemNotify?.SystemNotifyDeleteListSuccess(t)
            }

            override fun onError(e: Throwable) {
                deleteSystemNotify?.SystemNotifyDeleteListError(e)
            }
        })
    }


    var getatmelistResult: HttpInteface.queryAtmeList? = null
    var getcommandmelistResult: HttpInteface.queryCommandMeList? = null

    fun queryAtMeListRequest(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.queryAtMeList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            getatmelistResult?.AtmeListError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                getatmelistResult?.AtmeListSuccess(t)
            }

            override fun onError(e: Throwable) {
                getatmelistResult?.AtmeListError(e)
            }
        })
    }

    fun queryCommandMeListRequest(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.queryCommandMeList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            getcommandmelistResult?.CommandError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                getcommandmelistResult?.CommandSuccess(t)
            }

            override fun onError(e: Throwable) {
                getcommandmelistResult?.CommandError(e)
            }
        })
    }


    fun getMsgNotifyCount(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PrivateService::class.java)?.getMsgCount(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            getMsgCount?.getNotifyCountError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                getMsgCount?.getNotifyCountSuccess(t)
            }

            override fun onError(e: Throwable) {
                getMsgCount?.getNotifyCountError(e)
            }
        })
    }

    var partyHome: HttpInteface.PartyHome? = null
    fun getPartyHome(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PartyService::class.java)?.partyHome(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            partyHome?.getPartyHomeError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                partyHome?.getPartyHomeSuccess(t)
            }

            override fun onError(e: Throwable) {
                partyHome?.getPartyHomeError(e)
            }
        })
    }

    var partyDetail: HttpInteface.PartyDetail? = null
    fun getPartyDetail(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PartyService::class.java)?.partyDetail(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            partyDetail?.getPartyDetailError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                partyDetail?.getPartyDetailSuccess(t)
            }

            override fun onError(e: Throwable) {
                partyDetail?.getPartyDetailError(e)
            }
        })
    }

    var partyOrganization: HttpInteface.PartyOrganization? = null
    fun getPartyOrganization(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PartyService::class.java)?.partyOrganization(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            partyOrganization?.getPartyOrganizationError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                partyOrganization?.getPartyOrganizationSuccess(t)
            }

            override fun onError(e: Throwable) {
                partyOrganization?.getPartyOrganizationError(e)
            }
        })
    }


    var partySign: HttpInteface.PartySign? = null
    fun getPartySign(map: HashMap<String, String>) {//请求报列表
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PartyService::class.java)?.partySignList(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            partySign?.getPartySignError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                partySign?.getPartySignSuccess(t)
            }

            override fun onError(e: Throwable) {
                partySign?.getPartySignError(e)
            }
        })
    }


    var partySearch: HttpInteface.PartySearch? = null
    fun getPartySearch(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PartyService::class.java)?.partySearch(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            partySearch?.getPartySearchError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                partySearch?.getPartySearchSuccess(t)
            }

            override fun onError(e: Throwable) {
                partySearch?.getPartySearchError(e)
            }
        })
    }

    var partySubject: HttpInteface.PartySuject_inf? = null
    var partyMobo: HttpInteface.PartyMoto_inf? = null
    var partyClock: HttpInteface.PartyClock_inf? = null
    fun getPartySubject(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PartyService::class.java)?.partySubject(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            partySubject?.PartySubjectError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                partySubject?.PartySubjectSucccess(t)
            }

            override fun onError(e: Throwable) {
                partySubject?.PartySubjectError(e)
            }
        })
    }

    fun getPartyMobo(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PartyService::class.java)?.partyMoto(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            partyMobo?.PartyMotoError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                partyMobo?.PartyMotoSucccess(t)
            }

            override fun onError(e: Throwable) {
                partyMobo?.PartyMotoError(e)
            }
        })
    }

    fun getPartyClock(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PartyService::class.java)?.partyClock(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            partyClock?.PartyClockError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                partyClock?.PartyClockSucccess(t)
            }

            override fun onError(e: Throwable) {
                partyClock?.PartyClockError(e)
            }
        })
    }

    var partyAlarm: HttpInteface.PartyAlbum_inf? = null

    fun getPartyAlumb(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PartyService::class.java)?.partyAlbum(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            partyAlarm?.PartyAlbumError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                partyAlarm?.PartyAlbumSucccess(t)
            }

            override fun onError(e: Throwable) {
                partyAlarm?.PartyAlbumError(e)
            }
        })
    }


    var partyRanking: HttpInteface.PartyRanking_inf? = null
    fun getPartyRanking(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PartyService::class.java)?.partyRanking(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            partyRanking?.PartyRankingError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                partyRanking?.PartyRankingSucccess(t)
            }

            override fun onError(e: Throwable) {
                partyRanking?.PartyRankingError(e)
            }
        })
    }


    var partyRestore: HttpInteface.PartyRestore_inf? = null
    fun getPartyRestore(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PartyService::class.java)?.partyRestore(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            partyRestore?.PartyRestoreError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                partyRestore?.PartyRestoreSucccess(t)
            }

            override fun onError(e: Throwable) {
                partyRestore?.PartyRestoreError(e)
            }
        })
    }


    var partyUnRead: HttpInteface.PartyUnReadNotify_inf? = null
    fun getPartyActiveUnRead(map: HashMap<String, String>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        NetWorkManager.instance.getOkHttpRetrofit()?.create(PartyService::class.java)?.partyUnReadActive(token, NetWorkManager.instance.getBaseRequestBody(map)!!)?.map(ServerResponseError())?.doOnError {
            partyUnRead?.PartyUnReadNotifyError(ExceptionEngine.handleException(it))
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                partyUnRead?.PartyUnReadNotifySucccess(t)
            }

            override fun onError(e: Throwable) {
                partyUnRead?.PartyUnReadNotifyError(e)
            }
        })
    }
}