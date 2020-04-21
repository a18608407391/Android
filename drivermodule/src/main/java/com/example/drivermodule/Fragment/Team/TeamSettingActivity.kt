package com.example.drivermodule.Activity.Team

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.Entity.SoketBody.SocketDealType
import com.elder.zcommonmodule.Entity.SoketBody.Soket
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonInfo
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonnelInfoDto
import com.zk.library.Bus.ServiceEven
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.TeamSettingViewModel
import com.example.drivermodule.ViewModel.TeamSettingViewModel.Companion.REQUEST_TEAM_DELETE
import com.example.drivermodule.ViewModel.TeamSettingViewModel.Companion.REQUEST_TEAM_MANAGER
import com.example.drivermodule.ViewModel.TeamSettingViewModel.Companion.REQUEST_TEAM_NAME
import com.example.drivermodule.ViewModel.TeamSettingViewModel.Companion.REQUEST_TEAM_PASS
import com.example.drivermodule.databinding.ActivityTeamSettingBinding
import com.google.gson.Gson
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID


@Route(path = RouterUtils.TeamModule.SETTING)
class TeamSettingActivity : BaseFragment<ActivityTeamSettingBinding, TeamSettingViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_team_setting
    }

    @Autowired(name = RouterUtils.TeamModule.TEAM_INFO)
    @JvmField
    var info: TeamPersonInfo? = null
    var userId: String? = ""
    override fun initVariableId(): Int {
        return BR.team_setting
    }
//
//    override fun doPressBack() {
//        super.doPressBack()
//        mViewModel?.back()
//    }


    fun SettingValue(info: TeamPersonInfo): TeamSettingActivity {
        this.info = info
        return this@TeamSettingActivity
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_team_setting
//    }

//    override fun initViewModel(): TeamSettingViewModel? {
//        return ViewModelProviders.of(this)[TeamSettingViewModel::class.java]
//    }

    override fun initData() {
        super.initData()
        userId = PreferenceUtils.getString(context, USERID)
        viewModel?.inject(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            when (requestCode) {
                REQUEST_TEAM_NAME -> {
                    info?.redisData?.teamName = data.extras["info"] as String
                    viewModel?.teamName!!.set(info?.redisData?.teamName)
                    var so = Soket()
                    so.type = SocketDealType.UPDATETEAMINFO.code
                    so.teamCode = info?.redisData?.teamCode
                    so.teamId = info?.redisData?.id.toString()
                    so.userId = userId
                    so.body = Soket.SocketRequest()
                    so.body?.teamName = info?.redisData?.teamName
                    Log.e("result", "修改名字" + Gson().toJson(so))

//                    TeamViewModel.sendOrder(so)
                    RxBus.default?.post("showProgress")
                    var pos = ServiceEven()
                    pos.type = "sendData"
                    pos.gson = Gson().toJson(so) + "\\r\\n"
                    RxBus.default?.post(pos)
                }
                REQUEST_TEAM_MANAGER -> {
                    info = data.extras["info"] as TeamPersonInfo
                    var so = Soket()
                    so.type = SocketDealType.UPDATE_MANAGER.code
                    so.teamCode = info?.redisData?.teamCode
                    so.teamId = info?.redisData?.id.toString()
                    so.userId = userId
                    so.body = Soket.SocketRequest()
                    so.body?.dtoList = info?.redisData?.dtoList
                    Log.e("result", "角色管理" + Gson().toJson(so))
//                    TeamViewModel.sendOrder(so)

                    RxBus.default?.post("showProgress")
                    var pos = ServiceEven()
                    pos.type = "sendData"
                    pos.gson = Gson().toJson(so) + "\\r\\n"
                    RxBus.default?.post(pos)
                    viewModel?.validate()
                }
                REQUEST_TEAM_DELETE -> {
                    var ids = ""
                    var list = data.extras["info"] as ArrayList<TeamPersonnelInfoDto>
                    list.forEachIndexed { index, teamPersonnelInfoDto ->
                        ids += teamPersonnelInfoDto.memberId.toString() + ","
                    }
                    var so = Soket()
                    so.type = SocketDealType.REJECTTEAM.code
                    so.teamCode = info?.redisData?.teamCode
                    so.teamId = info?.redisData?.id.toString()
                    so.userId = userId
                    so.body = Soket.SocketRequest()
                    so.body?.userIds = ids
                    viewModel?.validate()
                    RxBus.default?.post("showProgress")
                    var pos = ServiceEven()
                    pos.type = "sendData"
                    pos.gson = Gson().toJson(so) + "\\r\\n"
                    RxBus.default?.post(pos)
                }
                REQUEST_TEAM_PASS -> {
                    info = data.extras["info"] as TeamPersonInfo
                    viewModel?.validate()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                REQUEST_TEAM_NAME -> {
                    info?.redisData?.teamName = data.getString("info")
                    viewModel?.teamName!!.set(info?.redisData?.teamName)
                    var so = Soket()
                    so.type = SocketDealType.UPDATETEAMINFO.code
                    so.teamCode = info?.redisData?.teamCode
                    so.teamId = info?.redisData?.id.toString()
                    so.userId = userId
                    so.body = Soket.SocketRequest()
                    so.body?.teamName = info?.redisData?.teamName
                    Log.e("result", "修改名字" + Gson().toJson(so))

//                    TeamViewModel.sendOrder(so)
                    RxBus.default?.post("showProgress")
                    var pos = ServiceEven()
                    pos.type = "sendData"
                    pos.gson = Gson().toJson(so) + "\\r\\n"
                    RxBus.default?.post(pos)
                }
                REQUEST_TEAM_MANAGER -> {
                    info = data.getSerializable("info") as TeamPersonInfo
                    var so = Soket()
                    so.type = SocketDealType.UPDATE_MANAGER.code
                    so.teamCode = info?.redisData?.teamCode
                    so.teamId = info?.redisData?.id.toString()
                    so.userId = userId
                    so.body = Soket.SocketRequest()
                    so.body?.dtoList = info?.redisData?.dtoList
                    Log.e("result", "角色管理" + Gson().toJson(so))
//                    TeamViewModel.sendOrder(so)

                    RxBus.default?.post("showProgress")
                    var pos = ServiceEven()
                    pos.type = "sendData"
                    pos.gson = Gson().toJson(so) + "\\r\\n"
                    RxBus.default?.post(pos)
                    viewModel?.validate()
                }
                REQUEST_TEAM_DELETE -> {
                    var ids = ""
                    var list = data.getSerializable("info") as ArrayList<TeamPersonnelInfoDto>
                    list.forEachIndexed { index, teamPersonnelInfoDto ->
                        ids += teamPersonnelInfoDto.memberId.toString() + ","
                    }
                    var so = Soket()
                    so.type = SocketDealType.REJECTTEAM.code
                    so.teamCode = info?.redisData?.teamCode
                    so.teamId = info?.redisData?.id.toString()
                    so.userId = userId
                    so.body = Soket.SocketRequest()
                    so.body?.userIds = ids
                    viewModel?.validate()
                    RxBus.default?.post("showProgress")
                    var pos = ServiceEven()
                    pos.type = "sendData"
                    pos.gson = Gson().toJson(so) + "\\r\\n"
                    RxBus.default?.post(pos)
                }
                REQUEST_TEAM_PASS -> {
                    info = data.getSerializable("info") as TeamPersonInfo
                    viewModel?.validate()
                }
            }
        }
    }
}