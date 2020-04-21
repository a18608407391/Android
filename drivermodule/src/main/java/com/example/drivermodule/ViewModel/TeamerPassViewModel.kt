package com.example.drivermodule.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.PersonDatas
import com.elder.zcommonmodule.Entity.SoketBody.BasePacketReceive
import com.elder.zcommonmodule.Entity.SoketBody.SocketDealType
import com.elder.zcommonmodule.Entity.SoketBody.Soket
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonInfo
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.getImageUrl
import com.example.drivermodule.Fragment.Team.TeamerPassActivity
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.PreferenceUtils
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID


class TeamerPassViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {

    override fun onComponentClick(view: View) {
        teamerPassActivity._mActivity!!.onBackPressedSupport()
    }

    override fun onComponentFinish(view: View) {
        var flag = false
        var datas: PersonDatas? = null
        items.forEach {
            if (it.select.get()!!) {
                flag = it.select.get()!!
                datas = it
            } else {
                flag = false
            }
        }
        if (datas==null) {
            Toast.makeText(context, getString(R.string.select_null), Toast.LENGTH_SHORT).show()
            return
        } else {
            //userids
            //1010
            var userId = PreferenceUtils.getString(context, USERID)
            if (userId == datas?.memberId?.get()) {
                finish()
            } else {
                var so = Soket()
                so.type = SocketDealType.TEAMER_PASS.code
                so.teamCode = teamerPassActivity.info?.redisData?.teamCode
                so.teamId = teamerPassActivity.info?.redisData?.id.toString()
                so.userId = userId
                so.body = Soket.SocketRequest()
                so.body?.userIds = datas?.memberId!!.get()
                var pos = ServiceEven()
                pos.type = "sendData"
                pos.gson  = Gson().toJson(so) + "\\r\\n"
                RxBus.default?.post(pos)
                var intent = Bundle()
                teamerPassActivity.info?.redisData?.teamer = Integer.valueOf(datas!!.memberId.get()!!)
                intent.putSerializable("info", teamerPassActivity.info)
                teamerPassActivity.setFragmentResult(TeamSettingViewModel.REQUEST_TEAM_PASS, intent)
                teamerPassActivity._mActivity!!.onBackPressedSupport()
            }
        }
    }

    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it?.type) {
            RxBusEven.Team_reject_even -> {
                teamerPassActivity._mActivity!!.onBackPressedSupport()
            }
        }
    }
    lateinit var teamerPassActivity: TeamerPassActivity
    var component = TitleComponent()
    var dispose: Disposable? = null
    fun inject(teamerPassActivity: TeamerPassActivity) {
        this.teamerPassActivity = teamerPassActivity
        dispose = RxBus.default?.toObservable(BasePacketReceive::class.java)?.subscribe {
            if (it.type == 1006) {
                teamerPassActivity.info = Gson().fromJson<TeamPersonInfo>(it.body, TeamPersonInfo::class.java)
                CoroutineScope(uiContext).launch {
                    invalidate()
                }
            }
        }
        invalidate()
    }
    fun invalidate() {
        component.title.set(getString(R.string.pass_timer))
        component.rightText.set(getString(R.string.finish))
        component.arrowVisible.set(false)
        component.setCallBack(this)
        var id = PreferenceUtils.getString(context, USERID)
        if (teamerPassActivity.info != null) {
            items.clear()
            teamerPassActivity.info?.redisData?.dtoList?.forEach {
                if (it.teamRoleColor == null || it.teamRoleColor.isEmpty()) {
                    it.teamRoleColor = "2D3138"
                }
                var name = ""
                if (it.memberName.trim().isEmpty()) {
                    name = getString(R.string.secret_name)
                } else {
                    name = it.memberName
                }
                var da = PersonDatas(ObservableField(getImageUrl(it.memberHeaderUrl)), ObservableField(name), ObservableField(it.teamRoleName), ObservableField(it.memberId.toString()), ObservableField(false), ObservableField(Color.parseColor("#" + it.teamRoleColor)))
                if (it.memberId.toString() != id) {
                    da.select.set(false)
                    da.isTeamer.set(false)
                } else {
                    da.select.set(true)
                    da.isTeamer.set(true)
                }

                items.add(da)
            }
        }
    }

    var items = ObservableArrayList<PersonDatas>()
    var itembinding = ItemBinding.of<PersonDatas> { itemBinding, position, item ->
        itemBinding.set(BR.delete_item, R.layout.team_pass_timer_ry_item).bindExtra(BR.position, position).bindExtra(BR.model_pass_controller, this@TeamerPassViewModel)
    }


    var adapter = BindingRecyclerViewAdapter<PersonDatas>()

    fun onImgItemClick(position: Int, data: PersonDatas) {
        if (data.select.get()!!) {
            data.select.set(false)
        } else {
            items.forEach {
                if (it.select.get()!!) {
                    it.select.set(false)
                }
            }
            data.select.set(true)
        }
        items.set(position, data)
    }
}