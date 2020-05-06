package com.example.drivermodule.ViewModel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.amap.api.location.AMapLocation
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Entity.SoketBody.CreateTeamInfoDto
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonnelInfoDto
import com.elder.zcommonmodule.Http.BaseObserver
import com.elder.zcommonmodule.REQUEST_CREATE_JOIN
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.Service.Error.ApiException
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Service.SERVICE_CREATE_MINA
import com.elder.zcommonmodule.USER_TOKEN
import com.example.drivermodule.Activity.Team.CreateTeamActivity
import com.example.drivermodule.R
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.PreferenceUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.getWindowWidth
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.util.concurrent.TimeUnit


class CreateTeamViewModel : BaseViewModel(), HttpInteface.CreateTeamResult, HttpInteface.JoinTeamResult, TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        var bundle = Bundle()
        bundle.putString("type", "cancle")
//        bundle.putSerializable("data", info)
//        bundle.putString("teamCode", info.teamCode)
        createTeamActivity.setFragmentResult(REQUEST_CREATE_JOIN, bundle)
        createTeamActivity._mActivity!!.onBackPressedSupport()

//        var intent = Intent()
//        intent.putExtra("type", "cancle")
//        createTeamActivity.setResult(REQUEST_CREATE_JOIN, intent)
//        finish()
    }

    override fun onComponentFinish(view: View) {

    }

    override fun JoinTeamSuccess(it: String) {
        createTeamActivity.dismissProgressDialog()
        if (it.isEmpty()) {
            return
        }

        if (!it?.startsWith("{")) {
            Toast.makeText(createTeamActivity.activity, it, Toast.LENGTH_SHORT).show()
        } else {
            var info = Gson().fromJson<TeamPersonnelInfoDto>(it, TeamPersonnelInfoDto::class.java)
            info.teamCode = password

            var bundle = Bundle()
            bundle.putString("type", "join")
            bundle.putSerializable("data", info)
            createTeamActivity.setFragmentResult(REQUEST_CREATE_JOIN, bundle)
            createTeamActivity._mActivity!!.onBackPressedSupport()
//            var intent = Intent()
//            intent.putExtra("type", "join")
//            intent.putExtra("data", info)
//            createTeamActivity.setResult(REQUEST_CREATE_JOIN, intent)
//            finish()
        }
    }


    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            RxBusEven.BrowserSendTeamCode -> {
                createTeamActivity._mActivity!!.onBackPressedSupport()
            }
        }
    }

    override fun JoinTeamError(ex: Throwable) {
        createTeamActivity.dismissProgressDialog()
        Toast.makeText(context, getString(R.string.net_error), Toast.LENGTH_SHORT).show()
    }

    override fun CreateTeamSuccess(it: String) {
        createTeamActivity.dismissProgressDialog()
        if (it.isEmpty()) {
            return
        }
        if (!it?.startsWith("{")) {
            Toast.makeText(createTeamActivity.activity, it, Toast.LENGTH_SHORT).show()
        } else {
            var info = Gson().fromJson<CreateTeamInfoDto>(it, CreateTeamInfoDto::class.java)

            var bundle = Bundle()
            bundle.putString("type", "create")
            bundle.putSerializable("data", info)
            bundle.putString("teamCode", info.teamCode)
            createTeamActivity.setFragmentResult(REQUEST_CREATE_JOIN, bundle)
            createTeamActivity._mActivity!!.onBackPressedSupport()
//            var intent = Intent()
//            intent.putExtra("type", "create")
//            intent.putExtra("data", info)
//            intent.putExtra("teamCode", info.teamCode)
//            createTeamActivity.setResult(REQUEST_CREATE_JOIN, intent)
//            finish()
        }
    }

    override fun CreateTeamError(ex: Throwable) {
        createTeamActivity.dismissProgressDialog()
        Toast.makeText(context, getString(R.string.net_error), Toast.LENGTH_SHORT).show()
    }

    var location: Location? = null
    var password: String = ""
    lateinit var createTeamActivity: CreateTeamActivity
    fun inject(createTeamActivity: CreateTeamActivity) {
        this.createTeamActivity = createTeamActivity
        component.title.set(getString(R.string.team_create))
        component.rightText.set("")
        component.arrowVisible.set(false)
        component.setCallBack(this)
        HttpRequest.instance.setCreateTeamResult(this)
        HttpRequest.instance.setJoinTeamResult(this)
    }

    var component = TitleComponent()

    var textChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            password = t
        }
    })


    fun onClick(view: View) {
        when (view.id) {
            R.id.create_btn -> {
                if (location == null) {
                    Toast.makeText(context, getString(R.string.onLocation), Toast.LENGTH_SHORT).show()
                    return
                }
//                var token = PreferenceUtils.getString(createTeamActivity, USER_TOKEN)
//                Observable.create(ObservableOnSubscribe<Response> {
//                    var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
//                    var map = HashMap<String, String>()
//                    map["joinAddr"] = location?.latitude.toString() + "," + location?.longitude.toString()
//                    var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
//                    var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiRidingTeam/ridingTeam/createTeam").build()
//                    var call = client.newCall(request)
//                    var response = call.execute()
//                    it.onNext(response)
//                }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
//                    return@Function it.body()?.string()
//                }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(createTeamActivity) {
//
//
//                    override fun onNext(t: String) {
//                        super.onNext(t)
//                        Log.e("result", t)
//                    }
//                })

                createTeamActivity.showProgressDialog(getString(R.string.on_create_team))
                var map = HashMap<String, String>()
                map["joinAddr"] = location?.latitude.toString() + "," + location?.longitude.toString()
                HttpRequest.instance.createTeam(map)
            }

            R.id.join_btn -> {
                if (location == null) {
                    return
                }
                if (password?.length != 6) {
                    Toast.makeText(context, getString(R.string.team_code_error), Toast.LENGTH_SHORT).show()
                    return
                }
                createTeamActivity.showProgressDialog(getString(R.string.on_join_team))
                var map = HashMap<String, String>()
                map["teamCode"] = password
                map["joinAddr"] = location?.latitude.toString() + "," + location?.longitude.toString()
                HttpRequest.instance.JoinTeam(map)
            }
        }
    }
}