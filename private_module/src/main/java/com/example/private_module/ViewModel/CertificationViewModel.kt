package com.example.private_module.ViewModel

import android.databinding.ObservableField
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.REAL_CODE
import com.elder.zcommonmodule.REAL_NAME
import com.elder.zcommonmodule.USER_TOKEN
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.private_module.Activity.CertificationActivity
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_auth.view.*
import kotlinx.android.synthetic.main.activity_certfication.*
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Utils.RegexUtils
import java.util.concurrent.TimeUnit


class CertificationViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {


    var visibleEdit = ObservableField<Boolean>(false)

    var number = ObservableField<String>("")
    var name = ObservableField<String>("name")


    override fun onComponentClick(view: View) {
//        finish()
        certificationActivity!!._mActivity!!.onBackPressedSupport()
    }

    override fun onComponentFinish(view: View) {

    }

    lateinit var certificationActivity: CertificationActivity
    var component = TitleComponent()
    fun inject(certificationActivity: CertificationActivity) {
        this.certificationActivity = certificationActivity
        component.title.set(getString(R.string.name_auth))
        component.arrowVisible.set(false)
        component.rightText.set("")
        component.callback = this
        visibleEdit.set(certificationActivity.auth)
        var na = PreferenceUtils.getString(context, REAL_NAME)
        var num = PreferenceUtils.getString(context, REAL_CODE)
        number.set(num)
        name.set(na)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.start_auth -> {
                var name = certificationActivity.person_name.text.toString().trim()
                var code = certificationActivity.person_number.text.toString().trim()
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(code)) {
                    Toast.makeText(context, getString(R.string.certification_warm), Toast.LENGTH_SHORT).show()
                    return
                }
                if (code.length != 18) {
                    Toast.makeText(context, getString(R.string.certification_error), Toast.LENGTH_SHORT).show()
                    return
                }

                var token = PreferenceUtils.getString(context, USER_TOKEN)
                certificationActivity.showProgressDialog(getString(R.string.certification_checking))
                Observable.create(ObservableOnSubscribe<Response> {
                    var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
                    var map = HashMap<String, String>()
                    map["identityNumber"] = code
                    map["realName"] = name
                    var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
                    var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).url(Base_URL + "AmoskiActivity/userCenterManage/userAutonym").post(body).build()
                    var call = client.newCall(request)
                    var response = call.execute()
                    it.onNext(response)
                }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                    return@Function it.body()?.string()
                }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    var request = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
                    if (request.code == 0) {
                        certificationActivity!!._mActivity!!.onBackPressedSupport()
                    } else {

                    }
                    Toast.makeText(context, request.msg, Toast.LENGTH_SHORT).show()
                    certificationActivity.dismissProgressDialog()
                }
            }
        }
    }


}