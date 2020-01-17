package com.elder.blogin.ViewModel

import android.databinding.ObservableField
import android.os.CountDownTimer
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.blogin.Activity.LoginActivity
import com.elder.blogin.Activity.LoginPassActivity
import com.elder.blogin.Activity.RegisterActivity
import com.elder.blogin.Activity.RegisterEnterPasswordActivity
import com.elder.blogin.R
import com.elder.blogin.Utils.getCode
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.deleteDriverStatus
import com.elder.zcommonmodule.DataBases.queryDriverStatus
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Http.BaseObserver
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_loginpass.*
import kotlinx.android.synthetic.main.activity_register_enterpass.*
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getColor
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.setPhone
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.util.concurrent.TimeUnit


class RegisterEnterPassViewModel : BaseViewModel() {
    var phone = ObservableField<String>("137 8710 1911")
    var text = ObservableField<String>(getString(R.string.send_again))
    var textColor = ObservableField<Int>(getColor(R.color.againSendColor))
    var isDowm = false
    var verifyCode = ObservableField<String>()
    var topText = ObservableField<String>(getString(R.string.send_note_warm))
    var newloginpass = ObservableField<String>()
    var loginpass = ObservableField<String>()
    var visibleIcon = ObservableField<Boolean>(false)
    var totalTime: Long = 0
    var clickable = ObservableField<Boolean>(true)

    lateinit var registerEnterPasswordActivity: RegisterEnterPasswordActivity
    fun inject(registerEnterPasswordActivity: RegisterEnterPasswordActivity) {
        this.registerEnterPasswordActivity = registerEnterPasswordActivity
        if (registerEnterPasswordActivity.second == 0) {
            registerEnterPasswordActivity.second = 120000
        } else {
            registerEnterPasswordActivity.second = registerEnterPasswordActivity.second!! * 1000
        }
        totalTime = registerEnterPasswordActivity.second?.toLong()!!
        initTimer(registerEnterPasswordActivity.second?.toLong()!!)
        phone.set(setPhone(registerEnterPasswordActivity.phone!!))
        timer?.start()
        if (registerEnterPasswordActivity.type == 3) {
            topText.set(getString(R.string.set_new_password))
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.start_timer -> {
                if (isDowm) {
                    return
                } else {
                    getCode(registerEnterPasswordActivity, registerEnterPasswordActivity.phone!!, registerEnterPasswordActivity.type)
                }
            }
            R.id.register_btn -> {
                Log.e("result", loginpass.get() + verifyCode.get())
                if (TextUtils.isEmpty(verifyCode.get())) {
                    Toast.makeText(context, getString(R.string.verifyCodeCanNoEmpty), Toast.LENGTH_SHORT).show()
                    return
                }
                if (verifyCode.get()?.length!! != 4) {
                    Toast.makeText(context, getString(R.string.verifyCodeError), Toast.LENGTH_SHORT).show()
                    return
                }
                when (registerEnterPasswordActivity.type) {
                    1 -> {
                        doRegister()
                    }
                    2 -> {
                        doNoPassLogin()
                    }
                    3 -> {
                        doRegister()
                    }
                    4 -> {
                        doSettingAccout()
                    }
                    5 -> {
                        dovalidOldMobile()
                    }
                    6 -> {
                        doUpdataPhone()
                    }
                }
            }
            R.id.main_back_pass -> {
                finish()
            }
            R.id.register_visible_icon -> {
                if (visibleIcon.get()!!) {
                    registerEnterPasswordActivity.register_pass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    visibleIcon.set(false)
                } else {
                    visibleIcon.set(true)
                    registerEnterPasswordActivity.register_pass.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                }
            }
        }
    }

    private fun doUpdataPhone() {
        //
        registerEnterPasswordActivity.showProgressDialog(getString(R.string.onBinding))
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            map["mobile"] = registerEnterPasswordActivity.phone!!
            map["validCode"] = verifyCode.get()!!
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/updBindMobile").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(registerEnterPasswordActivity) {
            override fun onNext(t: String) {
                super.onNext(t)
                var body = Gson().fromJson<BaseResponse>(t, BaseResponse::class.java)
                if (body.code == 0) {
                    Toast.makeText(context, body.msg, Toast.LENGTH_SHORT).show()
                    ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(registerEnterPasswordActivity, object : NavCallback() {
                        override fun onArrival(postcard: Postcard?) {
                            var m = AppManager.activityStack
                            AppManager.get()?.finishActivity(RegisterActivity::class.java)
                            AppManager.get()?.finishActivity(RegisterEnterPasswordActivity::class.java)
                        }
                    })
                } else {
                    Toast.makeText(context, body.msg, Toast.LENGTH_SHORT).show()
                    RxBus.default?.post("setttingError")
                }
                registerEnterPasswordActivity.dismissProgressDialog()
            }
        })
    }

    private fun dovalidOldMobile() {
        registerEnterPasswordActivity.showProgressDialog(getString(R.string.onBinding))
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            map["mobile"] = registerEnterPasswordActivity.phone!!
            map["validCode"] = verifyCode.get()!!
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/validOldMobile").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(registerEnterPasswordActivity) {
            override fun onNext(t: String) {
                super.onNext(t)
                var body = Gson().fromJson<BaseResponse>(t, BaseResponse::class.java)
                if (body.code == 0) {
                    ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 5).navigation()
                    finish()
                } else {
                    Toast.makeText(context, body.msg, Toast.LENGTH_SHORT).show()
                }
                registerEnterPasswordActivity.dismissProgressDialog()
            }
        })
    }

    private fun doSettingAccout() {
        registerEnterPasswordActivity.showProgressDialog(getString(R.string.onBinding))
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            map["mobile"] = registerEnterPasswordActivity.phone!!
            map["pwd"] = loginpass.get()!!
            map["validCode"] = verifyCode.get()!!
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/userBindMobile").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(registerEnterPasswordActivity) {
            override fun onNext(t: String) {
                super.onNext(t)
                //18390873151
                var body = Gson().fromJson<BaseResponse>(t, BaseResponse::class.java)
                if (body.code == 0) {
                    Toast.makeText(context, body.msg, Toast.LENGTH_SHORT).show()
                    ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(registerEnterPasswordActivity, object : NavCallback() {
                        override fun onArrival(postcard: Postcard?) {
                            var m = AppManager.activityStack
                            m!![m.size - 1].finish()
                            m!![m.size - 2].finish()
                        }
                    })
                } else {
                    Toast.makeText(context, body.msg, Toast.LENGTH_SHORT).show()
                    RxBus.default?.post("setttingError")
                }
                registerEnterPasswordActivity.dismissProgressDialog()
            }
        })

    }

    var timer: CountDownTimer? = null
    fun initTimer(totalTime: Long) {
        timer = object : CountDownTimer(totalTime, 1000) {
            override fun onFinish() {
                isDowm = false
                text.set(getString(R.string.send_again))
                textColor.set(getColor(R.color.againSendColor))
            }

            override fun onTick(millisUntilFinished: Long) {
                isDowm = true
                textColor.set(getColor(R.color.nomalTextColor))
                text.set((millisUntilFinished / 1000).toString() + "S")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
    }

    private fun doNoPassLogin() {

        Log.e("result","免密登录")
        registerEnterPasswordActivity.showProgressDialog(getString(R.string.http_loading_login))
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            map["mobile"] = registerEnterPasswordActivity.phone!!
            map["validCode"] = verifyCode.get()!!
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().url(Base_URL + "AmoskiActivity/memberUser/codeLoginAndForgetPwd").post(body).build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(registerEnterPasswordActivity) {
            override fun onNext(t: String) {
                super.onNext(t)
                Log.e("result","免密登录"+ t)
                var request = Gson().fromJson<BaseResponse>(t, BaseResponse::class.java)
                if (request.code == 0) {
                    PreferenceUtils.putString(context, USER_PHONE, registerEnterPasswordActivity.phone)
                    PreferenceUtils.putString(context, USER_PASS, loginpass.get())
                    PreferenceUtils.putString(context, USER_TOKEN, request.data as String)
                    PreferenceUtils.putString(context, USERID, request.msg)
                    if (checkDriverStatus()) {
                        var statusList = queryDriverStatus(request.msg!!)
                        if (statusList.size != 0 && statusList[0].startDriver.get() != 2) {
                            var dialog = DialogUtils.createNomalDialog(registerEnterPasswordActivity!!, getString(R.string.checked_exception_out), getString(R.string.finish_driver), getString(R.string.continue_driving))
                            dialog.setOnBtnClickL(OnBtnClickL {
                                PreferenceUtils.putString(context, USER_PHONE, registerEnterPasswordActivity.phone)
                                PreferenceUtils.putString(context, USER_PASS, loginpass.get())
                                PreferenceUtils.putString(context, USER_TOKEN, request.data as String)
                                ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(registerEnterPasswordActivity,object :NavCallback(){
                                    override fun onArrival(postcard: Postcard?) {
                                        deleteDriverStatus(request.msg!!)
                                        AppManager.get()?.finishActivity(RegisterActivity::class.java)
                                        AppManager.get()?.finishActivity(LoginActivity::class.java)
                                        AppManager.get()?.finishActivity(LoginPassActivity::class.java)
                                        registerEnterPasswordActivity.finish()
                                    }
                                })
//                                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.USER_SETTING).withInt(RouterUtils.PrivateModuleConfig.SETTING_CATEGORY, 0).navigation()
                            }, OnBtnClickL {
                                dialog.dismiss()
                                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "continue").navigation(registerEnterPasswordActivity, object : NavCallback() {
                                    override fun onArrival(postcard: Postcard?) {
                                        AppManager.get()?.finishActivity(RegisterActivity::class.java)
                                        AppManager.get()?.finishActivity(LoginActivity::class.java)
                                        AppManager.get()?.finishActivity(LoginPassActivity::class.java)
                                        registerEnterPasswordActivity.finish()
                                    }
                                })
                            })
                            dialog.show()
                        } else {
                            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(registerEnterPasswordActivity, object : NavCallback() {
                                override fun onArrival(postcard: Postcard?) {
                                    AppManager.get()?.finishActivity(RegisterActivity::class.java)
                                    AppManager.get()?.finishActivity(LoginActivity::class.java)
                                    AppManager.get()?.finishActivity(LoginPassActivity::class.java)
                                    registerEnterPasswordActivity.finish()
                                }
                            })
                        }
                    } else {
                        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(registerEnterPasswordActivity, object : NavCallback() {
                            override fun onArrival(postcard: Postcard?) {
                                AppManager.get()?.finishActivity(RegisterActivity::class.java)
                                AppManager.get()?.finishActivity(LoginActivity::class.java)
                                AppManager.get()?.finishActivity(LoginPassActivity::class.java)
                                registerEnterPasswordActivity.finish()
                            }
                        })
                    }
                } else {
//                    ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(registerEnterPasswordActivity, object : NavCallback() {
//                        override fun onArrival(postcard: Postcard?) {
//                            AppManager.get()?.finishActivity(RegisterActivity::class.java)
//                            AppManager.get()?.finishActivity(LoginActivity::class.java)
//                            AppManager.get()?.finishActivity(LoginPassActivity::class.java)
//                            registerEnterPasswordActivity.finish()
//                        }
//                    })

                    Toast.makeText(context,request.msg,Toast.LENGTH_SHORT).show()
                }
                registerEnterPasswordActivity.dismissProgressDialog()
            }

            override fun onComplete() {
                super.onComplete()
                registerEnterPasswordActivity.dismissProgressDialog()
            }

        })
    }

    fun doRegister() {
        registerEnterPasswordActivity.showProgressDialog(getString(R.string.http_loading_login))
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            map["mobile"] = registerEnterPasswordActivity.phone!!
            map["pwd"] = loginpass.get()!!
            map["type"] = registerEnterPasswordActivity.type.toString()
            map["validCode"] = verifyCode.get()!!
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().url(Base_URL + "AmoskiActivity/memberUser/register").post(body).build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(registerEnterPasswordActivity) {
            override fun onNext(t: String) {
                super.onNext(t)
                Log.e("result", t)
                var request = Gson().fromJson<BaseResponse>(t, BaseResponse::class.java)
                if (request.code == 0) {
                    if(registerEnterPasswordActivity.type==3){
                        PreferenceUtils.putString(context, USERID, request.msg)
                        if (checkDriverStatus()) {
                            var statusList = queryDriverStatus(request.msg!!)
                            if (statusList.size != 0 && statusList[0].startDriver.get() != 2) {
                                var dialog = DialogUtils.createNomalDialog(registerEnterPasswordActivity!!, getString(R.string.checked_exception_out), getString(R.string.finish_driver), getString(R.string.continue_driving))
                                dialog.setOnBtnClickL(OnBtnClickL {
                                    PreferenceUtils.putString(context, USER_PHONE, registerEnterPasswordActivity.phone)
                                    PreferenceUtils.putString(context, USER_PASS, loginpass.get())
                                    PreferenceUtils.putString(context, USER_TOKEN, request.data as String)
                                    ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(registerEnterPasswordActivity,object :NavCallback(){
                                        override fun onArrival(postcard: Postcard?) {
                                            deleteDriverStatus(request.msg!!)
                                            AppManager.get()?.finishActivity(RegisterActivity::class.java)
                                            AppManager.get()?.finishActivity(LoginActivity::class.java)
                                            AppManager.get()?.finishActivity(LoginPassActivity::class.java)
                                            registerEnterPasswordActivity.finish()
                                        }
                                    })
//                                    ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.USER_SETTING).withInt(RouterUtils.PrivateModuleConfig.SETTING_CATEGORY, 0).navigation()
                                }, OnBtnClickL {
                                    dialog.dismiss()
                                    ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "continue").navigation(registerEnterPasswordActivity, object : NavCallback() {
                                        override fun onArrival(postcard: Postcard?) {
                                            AppManager.get()?.finishActivity(RegisterActivity::class.java)
                                            AppManager.get()?.finishActivity(LoginActivity::class.java)
                                            AppManager.get()?.finishActivity(LoginPassActivity::class.java)
                                            registerEnterPasswordActivity.finish()
                                        }
                                    })
                                })
                                dialog.show()
                            } else {
                                ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(registerEnterPasswordActivity, object : NavCallback() {
                                    override fun onArrival(postcard: Postcard?) {
                                        AppManager.get()?.finishActivity(RegisterActivity::class.java)
                                        AppManager.get()?.finishActivity(LoginActivity::class.java)
                                        AppManager.get()?.finishActivity(LoginPassActivity::class.java)
                                        registerEnterPasswordActivity.finish()
                                    }
                                })
                            }
                        } else {
                            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(registerEnterPasswordActivity, object : NavCallback() {
                                override fun onArrival(postcard: Postcard?) {
                                    AppManager.get()?.finishActivity(RegisterActivity::class.java)
                                    AppManager.get()?.finishActivity(LoginActivity::class.java)
                                    AppManager.get()?.finishActivity(LoginPassActivity::class.java)
                                    registerEnterPasswordActivity.finish()
                                }
                            })
                        }
                    }else{
                        PreferenceUtils.putString(context, USER_PHONE, registerEnterPasswordActivity.phone)
                        PreferenceUtils.putString(context, USER_PASS, loginpass.get())
                        PreferenceUtils.putString(context, USER_TOKEN, request.data as String)
                        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(registerEnterPasswordActivity,object :NavCallback(){
                            override fun onArrival(postcard: Postcard?) {
                                AppManager.get()?.finishActivity(RegisterActivity::class.java)
                                AppManager.get()?.finishActivity(LoginActivity::class.java)
                                AppManager.get()?.finishActivity(LoginPassActivity::class.java)
                                registerEnterPasswordActivity.finish()
                            }
                        })
                    }

                } else {
                    Toast.makeText(context, request.msg, Toast.LENGTH_SHORT).show()
                }
                registerEnterPasswordActivity.dismissProgressDialog()
            }

            override fun onComplete() {
                super.onComplete()
                registerEnterPasswordActivity.dismissProgressDialog()
            }

        })
    }

    var passwordTextChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            loginpass.set(t)
        }
    })

    var newpasswordTextChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            newloginpass.set(t)
        }
    })

    var verifyTextChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            verifyCode.set(t)
        }
    })
}