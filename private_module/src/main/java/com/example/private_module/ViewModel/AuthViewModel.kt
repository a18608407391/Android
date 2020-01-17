package com.example.private_module.ViewModel

import android.databinding.ObservableField
import android.text.BoringLayout
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.REQUEST_BOND_CAR
import com.elder.zcommonmodule.USER_TOKEN
import com.example.private_module.Activity.AuthActivity
import com.example.private_module.Entitiy.CertificationEntity
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
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


class AuthViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {

    var isAuth = ObservableField<Boolean>(false)
    var isBinder = ObservableField<String>(getString(R.string.no_bond))

    override fun onComponentClick(view: View) {
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
        finish()

    }

    override fun onComponentFinish(view: View) {

    }

    lateinit var authActivity: AuthActivity

    var component = TitleComponent()
    fun inject(authActivity: AuthActivity) {
        this.authActivity = authActivity
        component.setCallBack(this)
        component.title.set(getString(R.string.auth_center))
        component.rightText.set("")
        component.arrowVisible.set(false)
        initDatas()
    }

    fun initDatas() {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/queryUserAutonymAndBindCar").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe {
            var result = Gson().fromJson<CertificationEntity>(it, CertificationEntity::class.java)

            Log.e("result",it)
            if (result.code == 0) {
                if (result.data?.bindVehicle == null) {
                    //未绑定
                    isBinder.set(getString(R.string.no_bond))
                } else {
                    isBinder.set(result.data?.bindVehicle?.carName)
                    //已绑定
                }
                if (result.data?.isattestation == "1") {
                    //实名
                    isAuth.set(true)
                } else {
                    //
                    isAuth.set(false)
                }
            }
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.user_auth -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.CERTIFICATION).withBoolean(RouterUtils.PrivateModuleConfig.USER_AUTH, isAuth.get()!!).navigation(authActivity, REQUEST_BOND_CAR)
            }
            R.id.car_binder -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.ADD_CARS).navigation(authActivity, REQUEST_BOND_CAR)
            }
            R.id.add_car -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.BOND_CARS).navigation(authActivity, REQUEST_BOND_CAR)
            }
        }
    }
}