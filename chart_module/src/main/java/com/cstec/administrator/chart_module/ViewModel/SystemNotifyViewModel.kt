package com.cstec.administrator.chart_module.ViewModel

import android.databinding.ObservableArrayList
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.chart_module.Activity.SystemNotifyListActivity
import com.cstec.administrator.chart_module.BR
import com.elder.zcommonmodule.Entity.SystemNotifyData
import com.cstec.administrator.chart_module.R
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.elder.zcommonmodule.Inteface.SimpleClickListener
import com.elder.zcommonmodule.MSG_RETURN_REQUEST
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_sys_notify.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class SystemNotifyViewModel : BaseViewModel(), SwipeRefreshLayout.OnRefreshListener, HttpInteface.querySystemNotifyList, TitleComponent.titleComponentCallBack, SimpleClickListener, HttpInteface.SocialDynamicsList, HttpInteface.deleteSystemNotifyList {
    override fun SystemNotifyDeleteListSuccess(response: String) {
        items.clear()
    }

    override fun SystemNotifyDeleteListError(ex: Throwable) {
        Toast.makeText(context, "系统通知清除失败", Toast.LENGTH_SHORT).show()
    }

    override fun ResultSDListSuccess(it: String) {
        systemNotifyListActivity.dismissProgressDialog()
        var dyna = Gson().fromJson<DynamicsCategoryEntity>(it, DynamicsCategoryEntity::class.java)
        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, systemNotifyListActivity.location).withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, dyna.data!![0]).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 7).navigation()
    }

    override fun ResultSDListError(ex: Throwable) {
        systemNotifyListActivity.dismissProgressDialog()
    }

    override fun onSimpleClick(entity: Any) {
        var data = entity as SystemNotifyData
        when (data.msgTargetIdType) {
            0 -> {
                systemNotifyListActivity.setResult(MSG_RETURN_REQUEST)
                finish()
            }
            2 -> {
//                ARouter.getInstance().build(RouterUtils.LogRecodeConfig.SAME_CITY_RANKING).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, systemNotifyListActivity.location).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1).withSerializable(RouterUtils.LogRecodeConfig.LOCATION_SIDE, "local").navigation()
            }
            1 -> {
                systemNotifyListActivity.showProgressDialog("加载中......")
                HttpRequest.instance.DynamicListResult = this
                var map = HashMap<String, String>()
                map["id"] = data.msgTargetId.toString()
                map["pageSize"] = "1"
                map["length"] = "1"
                map["type"] = "5"
                map["yAxis"] = systemNotifyListActivity.location!!.longitude.toString()
                map["xAxis"] = systemNotifyListActivity.location!!.latitude.toString()
                HttpRequest.instance.getDynamicsList(map)
            }
        }
    }

    override fun onComponentClick(view: View) {
        returnBack()
    }

    fun returnBack() {
        if (!destroyList!!.contains("MsgActivity")) {
            ARouter.getInstance().build(RouterUtils.Chat_Module.MSG_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, systemNotifyListActivity.location).navigation()
        } else {
            finish()
        }
    }


    fun showDialog() {
        var dialog = DialogUtils.createNomalDialog(systemNotifyListActivity, "是否清除所有系统消息", "取消", "清除")
        dialog.setOnBtnClickL(OnBtnClickL {
            dialog.dismiss()
        }, OnBtnClickL {
            HttpRequest.instance.deleteSystemNotify = this
            var map = HashMap<String, String>()
            map["type"] = "1"
            HttpRequest.instance.deleteSystemNotifyRequest(map)
            dialog.dismiss()
        })
        dialog.show()
    }

    override fun onComponentFinish(view: View) {
        showDialog()
    }

    override fun SystemNotifyListSuccess(response: String) {
        if (response.isNullOrEmpty()) {
            return
        }
        var s = Gson().fromJson<ArrayList<SystemNotifyData>>(response, object : TypeToken<ArrayList<SystemNotifyData>>() {}.type)
        s.forEach {
            items.add(it)
        }
    }

    override fun SystemNotifyListError(ex: Throwable) {

    }

    override fun onRefresh() {
        pageSize = 1
        lenth = 20
        initDatas()
        CoroutineScope(uiContext).launch {
            delay(10000)
            systemNotifyListActivity.sys_swipe.isRefreshing = false
        }
    }

    lateinit var systemNotifyListActivity: SystemNotifyListActivity
    fun inject(systemNotifyListActivity: SystemNotifyListActivity) {
        this.systemNotifyListActivity = systemNotifyListActivity
        titleComponent.title.set("系统通知")
        titleComponent.rightText.set("清除")
        titleComponent.callback = this
        initDatas()
    }

    var listener: SimpleClickListener = this

    var titleComponent = TitleComponent()

    var adapter = BindingRecyclerViewAdapter<SystemNotifyData>()

    var items = ObservableArrayList<SystemNotifyData>()

    var itemBinding = ItemBinding.of<SystemNotifyData>(BR.sys_notify_item_model, R.layout.sys_notify_item_layout).bindExtra(BR.listener, listener)

    var pageSize = 1

    var lenth = 20

    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            Log.e("result", "加载更多" + t)
            if (t > 1) {
                pageSize++
                initDatas()
            }
        }
    })

    fun initDatas() {
        HttpRequest.instance.getSystemNotify = this
        HttpRequest.instance.querySystemNotify(HashMap())
    }

}