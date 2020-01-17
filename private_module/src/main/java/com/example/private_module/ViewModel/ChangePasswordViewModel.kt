package com.example.private_module.ViewModel

import android.databinding.ObservableField
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Http.BaseObserver
import com.elder.zcommonmodule.USER_PASS
import com.elder.zcommonmodule.USER_PHONE
import com.elder.zcommonmodule.USER_TOKEN
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.private_module.Activity.ChangePasswordActivity
import com.example.private_module.R
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
import kotlinx.android.synthetic.main.activity_cg_pass.*
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.setPhone
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.util.concurrent.TimeUnit


class ChangePasswordViewModel : BaseViewModel() {
    lateinit var changePasswordActivity: ChangePasswordActivity
    var visibleIcon = ObservableField<Boolean>(false)
    var visiblenewIcon = ObservableField<Boolean>(false)
    var phone = ObservableField<String>("137 8710 1911")
    var newloginpass = ObservableField<String>("")
    var loginpass = ObservableField<String>("")
    fun inject(changePasswordActivity: ChangePasswordActivity) {
        this.changePasswordActivity = changePasswordActivity
        changePasswordActivity.change_new_edit
        var p = PreferenceUtils.getString(context, USER_PHONE)
        phone.set(setPhone(p))
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

    fun onClick(view: View) {
        when (view.id) {
            R.id.change_back -> {
                finish()
            }
            R.id.change_pass_btn -> {
                if (loginpass.get()?.isEmpty()!!) {
                    Toast.makeText(changePasswordActivity, getString(R.string.EnterPassWordWarm), Toast.LENGTH_SHORT).show()
                    return
                }
                if (newloginpass.get()?.isEmpty()!!) {
                    Toast.makeText(changePasswordActivity, getString(R.string.new_password_enter_warm), Toast.LENGTH_SHORT).show()
                    return
                }
                if (newloginpass.get()?.length!! < 6) {
                    Toast.makeText(changePasswordActivity, getString(R.string.loginpassSizeWarm), Toast.LENGTH_SHORT).show()
                    return
                }
                var token = PreferenceUtils.getString(context, USER_TOKEN)
                changePasswordActivity.showProgressDialog(getString(R.string.loading))
                Observable.create(ObservableOnSubscribe<Response> {
                    var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
                    var map = HashMap<String, String>()
                    map["pwd"] = newloginpass.get()!!
                    map["oldPwd"] = loginpass.get()!!
                    var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
                    var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).url(Base_URL + "AmoskiActivity/userCenterManage/userUpdPwd").post(body).build()
                    var call = client.newCall(request)
                    var response = call.execute()
                    it.onNext(response)
                }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                    return@Function it.body()?.string()
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(changePasswordActivity) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        var request = Gson().fromJson<BaseResponse>(t, BaseResponse::class.java)
                        if (request.code == 0) {
                            finish()
                        } else {

                        }
                        Toast.makeText(context, request.msg, Toast.LENGTH_SHORT).show()
                        changePasswordActivity.dismissProgressDialog()
                    }

                    override fun onComplete() {
                        super.onComplete()
                        changePasswordActivity.dismissProgressDialog()
                    }
                })
            }
            R.id.visible_new_icon -> {
                if (visibleIcon.get()!!) {
                    changePasswordActivity.change_new_edit.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    visiblenewIcon.set(false)
                } else {
                    visiblenewIcon.set(true)
                    changePasswordActivity.change_new_edit.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                }
            }
            R.id.visible_old_icon -> {
                if (visibleIcon.get()!!) {
                    changePasswordActivity.change_old_edit.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    visibleIcon.set(false)
                } else {
                    visibleIcon.set(true)
                    changePasswordActivity.change_old_edit.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                }
            }
        }
    }
}