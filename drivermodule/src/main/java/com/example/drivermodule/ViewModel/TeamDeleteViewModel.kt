package com.example.drivermodule.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.PersonDatas
import com.elder.zcommonmodule.Entity.SoketBody.BasePacketReceive
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonInfo
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonnelInfoDto
import com.elder.zcommonmodule.getImageUrl
import com.example.drivermodule.Fragment.Team.DeleteActivity
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


class TeamDeleteViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        deleteActivity._mActivity!!.onBackPressedSupport()
    }

    override fun onComponentFinish(view: View) {
        if (deleteList.size == 0) {
            Toast.makeText(context, getString(R.string.delete_empty_warm), Toast.LENGTH_SHORT).show()
        } else {
            var id = PreferenceUtils.getString(context, USERID)
            var flag = false
            deleteList.forEach {
                if (it.memberId.toString() == id) {
                    flag = true
                }
            }
            if (!flag) {
                var intent = Bundle()
                intent.putSerializable("info", deleteList)
                deleteActivity.setFragmentResult(TeamSettingViewModel.REQUEST_TEAM_DELETE, intent)
                deleteActivity._mActivity!!.onBackPressedSupport()
            } else {
                Toast.makeText(context, getString(R.string.can_not_delete_myself), Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it?.type) {
            RxBusEven.Team_reject_even -> {
                deleteActivity._mActivity!!.onBackPressedSupport()
            }
        }
    }

    var deleteList = ArrayList<TeamPersonnelInfoDto>()
    var dispose: Disposable? = null
    var component = TitleComponent()
    lateinit var deleteActivity: DeleteActivity
    fun inject(deleteActivity: DeleteActivity) {
        this.deleteActivity = deleteActivity
        dispose = RxBus.default?.toObservable(BasePacketReceive::class.java)?.subscribe {
            if (it.type == 1006) {
                deleteActivity.info = Gson().fromJson<TeamPersonInfo>(it.body, TeamPersonInfo::class.java)
                CoroutineScope(uiContext).launch {
                    invalidate()
                }
            }
        }
        invalidate()
    }


    fun invalidate() {
        var id = PreferenceUtils.getString(context, USERID)
        component.title.set(getString(R.string.delete_member))
        component.rightText.set(getString(R.string.delete) + "(" + deleteList.size + ")")
        component.arrowVisible.set(false)
        component.setCallBack(this)
        if (deleteActivity.info != null) {
            items.clear()
            deleteList.clear()
            deleteActivity.info?.redisData?.dtoList?.forEach {
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
                    da.isTeamer.set(false)
                } else {
                    da.isTeamer.set(true)
                }
                items.add(da)
            }
        }
    }


    var items = ObservableArrayList<PersonDatas>()
    var itembinding = ItemBinding.of<PersonDatas> { itemBinding, position, item ->
        itemBinding.set(BR.delete_item, R.layout.team_delete_ry_item).bindExtra(BR.position, position).bindExtra(BR.model_delete_controller, this@TeamDeleteViewModel)
    }


    var adapter = BindingRecyclerViewAdapter<PersonDatas>()

    fun onImgItemClick(position: Int, data: PersonDatas) {
        if (data.isTeamer.get()!!) {
            return
        }
        if (!data.select.get()!!) {
            data.select.set(true)
            deleteActivity.info?.redisData?.dtoList?.forEach {
                if (it.memberId.toString() == data.memberId.get()!!) {
                    deleteList.add(it)
                }
            }
            Log.e("result", "选择了1" + Gson().toJson(deleteList))
        } else {
            data.select.set(false)
            deleteActivity.info?.redisData?.dtoList?.forEach {
                if (it.memberId.toString() == data.memberId.get()!!) {
                    deleteList.remove(it)
                }
            }
        }
        items.set(position, data)
        component.rightText.set(getString(R.string.delete) + "(" + deleteList.size + ")")
    }

}