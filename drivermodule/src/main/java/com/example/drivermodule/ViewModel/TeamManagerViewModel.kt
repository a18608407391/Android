package com.example.drivermodule.ViewModel

import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.widget.Toast
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.PersonDatas
import com.elder.zcommonmodule.Entity.Role
import com.elder.zcommonmodule.Entity.SoketBody.BasePacketReceive
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonInfo
import com.elder.zcommonmodule.Service.Error.ApiException
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.getImageUrl
import com.example.drivermodule.Activity.Team.TeamManagerActivity
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.TeamSettingViewModel.Companion.REQUEST_TEAM_MANAGER
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.BaseViewModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class TeamManagerViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack, HttpInteface.QueryRollInfo {


    var roleList: ArrayList<Role>? = null
    override fun QueryRollInfoSuccess(it: String) {
        teamManagerActivity.dismissProgressDialog()
        roleList = Gson().fromJson<ArrayList<Role>>(it, object : TypeToken<ArrayList<Role>>() {}.type)
//        roleList = JSON.parseObject(it, object : TypeReference<ArrayList<Role>>() {})
        roleList!!.forEach {
            list.add(it.roleName!!)
        }
    }

    override fun QueryRollInfoError(ex: Throwable) {
        teamManagerActivity.dismissProgressDialog()
        Toast.makeText(context, getString(R.string.net_error), Toast.LENGTH_SHORT).show()
    }

    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {
        var intent = Intent()
        intent.putExtra("info", teamManagerActivity.info)
        teamManagerActivity.setResult(REQUEST_TEAM_MANAGER, intent)
        finish()
    }

    lateinit var teamManagerActivity: TeamManagerActivity
    var dispose: Disposable? = null
    fun inject(teamManagerActivity: TeamManagerActivity) {
        this.teamManagerActivity = teamManagerActivity
        dispose = RxBus.default?.toObservable(BasePacketReceive::class.java)?.subscribe {
            if (it.type == 1006) {
                Log.e("result",it.body)
                teamManagerActivity.info = Gson().fromJson<TeamPersonInfo>(it.body, TeamPersonInfo::class.java)
                CoroutineScope(uiContext).launch {
                    invalidate()
                }
            }
        }
        teamManagerActivity.showProgressDialog(getString(R.string.http_loading))
        var map = HashMap<String, String>()
        HttpRequest.instance.getManagerName(map)
        invalidate()
    }


    fun invalidate() {
        component.title.set(getString(R.string.team_manager))
        component.arrowVisible.set(false)
        component.setCallBack(this)
        currentItemPosition = 0
        items.clear()
        HttpRequest.instance.setQueryRollInfoResult(this)
        teamManagerActivity.info?.redisData?.dtoList?.forEachIndexed { index, it ->
            if (it.teamRoleColor == null || it.teamRoleColor.isEmpty()) {
                it.teamRoleColor = "2D3138"
            }
            var name = ""
            if (it.memberName.trim().isEmpty()) {
                name = getString(R.string.secret_name)
            } else {
                name = it.memberName
            }
            items.add(PersonDatas(ObservableField(getImageUrl(it.memberHeaderUrl)), ObservableField(name), ObservableField(it.teamRoleName), ObservableField(it.memberId.toString()), ObservableField(false), ObservableField(Color.parseColor("#" + it.teamRoleColor)),index))
        }
    }

    var component = TitleComponent()

    var items = ObservableArrayList<PersonDatas>()

    var itemBinding = ItemBinding.of<PersonDatas> { itemBinding, position, item ->
        itemBinding.set(BR.manager_items, R.layout.team_manager_ry_item).bindExtra(BR.position, position).bindExtra(BR.manager_model_controller, this@TeamManagerViewModel)
    }

    var adapter = BindingRecyclerViewAdapter<PersonDatas>()


    var currentItemPosition = 0
    fun onItemClick(position: Int, datas: PersonDatas) {
        currentItemPosition = position
        DialogUtils.showGenderDialog(teamManagerActivity, genderCommand, list, getString(R.string.choice_role))
    }

    var list = ArrayList<String>()

    fun showDialog() {
        HttpRequest.instance.setQueryRollInfoResult(this)
        var map = HashMap<String, String>()
        HttpRequest.instance.getManagerName(map)
    }

    var genderCommand = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            //获取
            var roleIndex = list.indexOf(t)
            var role = roleList!![roleIndex]

            var Uiitems = items[currentItemPosition]

            Uiitems.teamName.set(role.roleName)
            Uiitems.TextColor.set(Color.parseColor("#" + role.roleColor))
            items[currentItemPosition] = Uiitems

            var data = teamManagerActivity.info?.redisData?.dtoList!![currentItemPosition]
            data.teamRoleId = role.id!!
            data.teamRoleName = role.roleName
            data.teamRoleColor = role.roleColor
            teamManagerActivity.info?.redisData?.dtoList!![currentItemPosition] = data
        }
    })

}