package com.elder.blogin.Utils

import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.blogin.Activity.RegisterActivity
import com.elder.blogin.Activity.RegisterEnterPasswordActivity
import com.elder.blogin.R
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Http.BaseObserver
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import java.util.concurrent.TimeUnit

fun getCode(activity: AppCompatActivity, mobile: String, type: Int) {
   var dialog =    DialogUtils.showProgress(activity, getString(R.string.http_loading))
    Observable.create(ObservableOnSubscribe<Response> {
        var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
        var map = HashMap<String, String>()
        map["mobile"] = mobile
        map["type"] = type.toString()
        var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
        var request = Request.Builder().url(Base_URL + "AmoskiActivity/memberUser/getMobileCode").post(body).build()
        var call = client.newCall(request)
        var response = call.execute()
        it.onNext(response)
    }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
        return@Function it.body()?.string()
    }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(activity) {
        override fun onNext(t: String) {
            super.onNext(t)
            var request = Gson().fromJson<BaseResponse>(t, BaseResponse::class.java)
            if (request.code == 0 || request.code == 20005) {
                if (request.code == 0) {
                    if (activity is RegisterActivity) {
                        if (type == 1) {
                            ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER_SETTINGPASS).withString(RouterUtils.LoginModuleKey.PHONE_NUMBER, mobile).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 1).navigation()
                        } else if (type == 2) {
                            ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER_SETTINGPASS).withString(RouterUtils.LoginModuleKey.PHONE_NUMBER, mobile).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 2).navigation()
                        } else if (type == 3) {
                            ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER_SETTINGPASS).withString(RouterUtils.LoginModuleKey.PHONE_NUMBER, mobile).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 3).navigation()
                        } else if (type == 4) {
                            ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER_SETTINGPASS).withString(RouterUtils.LoginModuleKey.PHONE_NUMBER, mobile).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 4).navigation()
                        } else if (type == 5) {
                            ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER_SETTINGPASS).withString(RouterUtils.LoginModuleKey.PHONE_NUMBER, mobile).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 6).navigation()
                        }
                    } else if (activity is RegisterEnterPasswordActivity) {
                        activity.mViewModel?.timer?.start()
                    }
                } else {
                    var t = request.data as Double
                    var number = t.toInt()
                    if (type == 1) {
                        ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER_SETTINGPASS).withString(RouterUtils.LoginModuleKey.PHONE_NUMBER, mobile).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 1).withInt(RouterUtils.LoginModuleKey.COUNT_DOWN_TIMER, number).navigation()
                    } else if (type == 2) {
                        ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER_SETTINGPASS).withString(RouterUtils.LoginModuleKey.PHONE_NUMBER, mobile).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 2).withInt(RouterUtils.LoginModuleKey.COUNT_DOWN_TIMER, number).navigation()
                    } else if (type == 3) {
                        ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER_SETTINGPASS).withString(RouterUtils.LoginModuleKey.PHONE_NUMBER, mobile).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 3).withInt(RouterUtils.LoginModuleKey.COUNT_DOWN_TIMER, number).navigation()
                    } else if (type == 4) {
                        ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER_SETTINGPASS).withString(RouterUtils.LoginModuleKey.PHONE_NUMBER, mobile).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 4).withInt(RouterUtils.LoginModuleKey.COUNT_DOWN_TIMER, number).navigation()
                    } else if (type == 5) {
//                        ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER_SETTINGPASS).withString(RouterUtils.LoginModuleKey.PHONE_NUMBER, mobile).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 6).withInt(RouterUtils.LoginModuleKey.COUNT_DOWN_TIMER, number).navigation()
                    }
                }
            } else {
                Toast.makeText(context, request.msg, Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        override fun onError(e: Throwable) {
            super.onError(e)
            dialog.dismiss()
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
        }

        override fun onComplete() {
            super.onComplete()
        }
    })
}