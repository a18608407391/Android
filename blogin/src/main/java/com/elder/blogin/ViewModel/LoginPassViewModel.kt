package com.elder.blogin.ViewModel

import android.databinding.ObservableField
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.blogin.Activity.LoginPassActivity
import com.elder.blogin.R
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.deleteDriverStatus
import com.elder.zcommonmodule.DataBases.queryDriverStatus
import com.elder.zcommonmodule.DataBases.queryUserInfo
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Http.BaseObserver
import com.elder.zcommonmodule.Http.NetWorkManager
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Service.Login.LoginService
import com.elder.zcommonmodule.Service.RetrofitClient
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.RouterUtils.PrivateModuleConfig.Companion.USER_INFO
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_loginpass.*
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ToastUtils
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.util.concurrent.TimeUnit


class LoginPassViewModel : BaseViewModel(){

    var loginname = ObservableField<String>()
    var loginpass = ObservableField<String>()
    var passVisible = ObservableField<Boolean>(false)
    var type = ObservableField<Int>()

    fun onClick(view: View) {
        when (view.id) {
            R.id.forget_pass -> {
                ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 3).navigation()
            }
            R.id.nopass_login -> {
                ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 2).navigation()
            }
            R.id.visible_icon_change -> {
                if (passVisible.get()!!) {
                    loginPassActivity.visible_pass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    passVisible.set(false)
                } else {
                    passVisible.set(true)
                    loginPassActivity.visible_pass.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                }
            }
            R.id.login_btn -> {
                if (TextUtils.isEmpty(loginname.get())) {
                    Toast.makeText(loginPassActivity, getString(R.string.loginnamenoEmpty), Toast.LENGTH_SHORT).show()
                    return
                }
                if (loginname.get()?.length!! < 11) {
                    Toast.makeText(loginPassActivity, getString(R.string.loginnameError), Toast.LENGTH_SHORT).show()
                    return
                }
                if (TextUtils.isEmpty(loginpass.get())) {
                    Toast.makeText(loginPassActivity, getString(R.string.loginpassnoEmpty), Toast.LENGTH_SHORT).show()
                    return
                }
                if (loginpass.get()?.length!! < 6) {
                    Toast.makeText(loginPassActivity, getString(R.string.loginpassSizeWarm), Toast.LENGTH_SHORT).show()
                    return
                }
                loginPassActivity.showProgressDialog(getString(R.string.http_loading_login))
                var map = HashMap<String, String>()
                map["mobile"] = loginname.get()!!
                map["pwd"] = loginpass.get()!!
                NetWorkManager.instance.getOkHttpRetrofit()?.create(LoginService::class.java)?.login(NetWorkManager.instance.getBaseRequestBody(map)!!)?.map {
                    return@map it
                }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.doOnError {
                    Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
                    loginPassActivity.dismissProgressDialog()
                }?.onErrorResumeNext(Observable.empty())?.subscribe {
                    loginPassActivity.dismissProgressDialog()
                    if (it.code == 0) {
                        PreferenceUtils.putString(context, USER_PHONE, loginname.get()!!)
                        PreferenceUtils.putString(context, USER_PASS, loginpass.get())
                        PreferenceUtils.putString(context, USER_TOKEN, it.data as String)
                        PreferenceUtils.putLong(context, TOKEN_LIMIT, System.currentTimeMillis())
                        Log.e("result", "当前的TokenByLogin" + it.data as String)
//                        getUserInfo(it.data.toString())
                        PreferenceUtils.putBoolean(context, RE_LOGIN, false)
                        PreferenceUtils.putString(context, USERID, it.msg)
                        if (checkDriverStatus()) {
                            var statusList = queryDriverStatus(it.msg!!)
                            if (statusList.size != 0 && statusList[0].startDriver.get() != 2) {
                                var dialog = DialogUtils.createNomalDialog(loginPassActivity!!, getString(R.string.checked_exception_out), getString(R.string.finish_driver), getString(R.string.continue_driving))
                                dialog.setOnBtnClickL(OnBtnClickL {
                                    ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "cancle").navigation(loginPassActivity, object : NavCallback() {
                                        override fun onArrival(postcard: Postcard?) {
                                            deleteDriverStatus(it.msg!!)
                                            loginPassActivity.finish()
                                        }
                                    })
                                }, OnBtnClickL {
                                    dialog.dismiss()
                                    ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "continue").navigation(loginPassActivity, object : NavCallback() {
                                        override fun onArrival(postcard: Postcard?) {
                                            loginPassActivity.finish()
                                        }
                                    })
                                })
                                dialog.show()
                            } else {
                                ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(loginPassActivity, object : NavCallback() {
                                    override fun onArrival(postcard: Postcard?) {
                                        loginPassActivity.finish()
                                    }
                                })
                            }
                        } else {
                            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(loginPassActivity, object : NavCallback() {
                                override fun onArrival(postcard: Postcard?) {
                                    loginPassActivity.finish()
                                }
                            })
                        }
                    } else {
                        Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    lateinit var loginPassActivity: LoginPassActivity
    fun inject(loginPassActivity: LoginPassActivity) {
        this.loginPassActivity = loginPassActivity
        type.set(loginPassActivity.type)
    }


    var phoneTextChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            loginname.set(t)
        }
    })


    var passwordTextChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            loginpass.set(t)
        }
    })
}