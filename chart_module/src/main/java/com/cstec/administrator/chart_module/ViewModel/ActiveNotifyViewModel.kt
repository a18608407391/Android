package com.cstec.administrator.chart_module.ViewModel

import android.databinding.ObservableArrayList
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.chart_module.Activity.ActiveNotifyListActivity
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.Data.ActiveNotifyData
import com.elder.zcommonmodule.Entity.SystemNotifyData
import com.cstec.administrator.chart_module.R
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Inteface.SimpleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_active_notify.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class ActiveNotifyViewModel : BaseViewModel(), SwipeRefreshLayout.OnRefreshListener, HttpInteface.queryActiveNotifyList, TitleComponent.titleComponentCallBack, SimpleClickListener, HttpInteface.deleteSystemNotifyList {
    override fun SystemNotifyDeleteListSuccess(response: String) {
        activeNotifyListActivity.swip.isRefreshing = false
        items.clear()
    }

    override fun SystemNotifyDeleteListError(ex: Throwable) {
        Toast.makeText(context, "系统通知清除失败", Toast.LENGTH_SHORT).show()
    }


    override fun onSimpleClick(entity: Any) {
        var data = entity as ActiveNotifyData
        var code = Integer.valueOf(data.msg_title!!.split(":")[1])
        var bigType = data.msg_title!!.split(":")[2]
        when (Integer.valueOf(bigType)) {
            1 -> {
//                ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_CLOCK_DETAIL).withInt(RouterUtils.PartyConfig.PARTY_ID, entity.msgTargetId)
//                        .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
//                                activeNotifyListActivity.location).withInt(RouterUtils.PartyConfig.PARTY_CODE, code).navigation()

                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                       activeNotifyListActivity.location)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, entity.msgTargetId)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_CODE,code)
                startFragment(activeNotifyListActivity, RouterUtils.PartyConfig.PARTY_CLOCK_DETAIL, bundle)
            }
            2 -> {

                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        activeNotifyListActivity.location)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, entity.msgTargetId)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_CODE,code)
                startFragment(activeNotifyListActivity, RouterUtils.PartyConfig.PARTY_SUBJECT_DETAIL, bundle)
//                ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_SUBJECT_DETAIL).withInt(RouterUtils.PartyConfig.PARTY_ID, entity.ID)
//                        .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
//                                Location(activity.location!!.latitude, activity.location!!.longitude)).withInt(RouterUtils.PartyConfig.NavigationType, 3).withInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE).withString(RouterUtils.PartyConfig.PARTY_CITY, activity.party_city).navigation(activity.activity, object : NavCallback() {
//                            override fun onArrival(postcard: Postcard?) {
//                                finish()
//                            }
//                        })
            }
            3 -> {

                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        activeNotifyListActivity.location)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, entity.msgTargetId)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_CODE,code)
                startFragment(activeNotifyListActivity, RouterUtils.PartyConfig.PARTY_DETAIL, bundle)
            }
        }
    }

    override fun onComponentClick(view: View) {
//        finish()
        activeNotifyListActivity._mActivity!!.onBackPressedSupport()
    }

    override fun onComponentFinish(view: View) {
        showDialog()
    }
    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            RxBusEven.ACTIVE_WEB_GO_TO_APP -> {
                activeNotifyListActivity._mActivity!!.onBackPressedSupport()
            }
        }
    }
    override fun ActiveNotifyListSuccess(response: String) {
        if (response.isNullOrEmpty()) {
            return
        }
        var s = Gson().fromJson<ArrayList<ActiveNotifyData>>(response, object : TypeToken<ArrayList<ActiveNotifyData>>() {}.type)

        s.forEach {
            items.add(it)
        }
    }

    override fun ActiveNotifyListError(ex: Throwable) {

    }

    override fun onRefresh() {
        pageSize = 1
        lenth = 20
        initDatas()
        CoroutineScope(uiContext).launch {
            delay(10000)
            activeNotifyListActivity.swip.isRefreshing = false
        }
    }

    var titleComponent = TitleComponent()

    var adapter = BindingRecyclerViewAdapter<ActiveNotifyData>()

    var items = ObservableArrayList<ActiveNotifyData>()
    var listener: SimpleClickListener = this
    var itemBinding = ItemBinding.of<ActiveNotifyData>(BR.active_notify_item_model, R.layout.active_notify_item_layout).bindExtra(BR.listener, listener)

    var pageSize = 1

    var lenth = 20

    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            Log.e("result", "加载更多" + t)

            if (t <lenth*pageSize) {
                return
            }else{
                pageSize++
                initDatas()
            }
//            if (t > 1) {
//                pageSize++
//                initDatas()
//            }
        }
    })

    fun initDatas() {
        HttpRequest.instance.getActiveNotify = this
        HttpRequest.instance.queryActiveNotify(HashMap())
    }

    fun showDialog() {
        var dialog = DialogUtils.createNomalDialog(activeNotifyListActivity.activity!!, "是否清除所有活动消息", "取消", "清除")
        dialog.setOnBtnClickL(OnBtnClickL {
            dialog.dismiss()
        }, OnBtnClickL {
            HttpRequest.instance.deleteSystemNotify = this
            var map = HashMap<String, String>()
            map["type"] = "2"
            HttpRequest.instance.deleteSystemNotifyRequest(map)
            dialog.dismiss()
        })
        dialog.show()
    }

    lateinit var activeNotifyListActivity: ActiveNotifyListActivity
    fun inject(activeNotifyListActivity: ActiveNotifyListActivity) {
        this.activeNotifyListActivity = activeNotifyListActivity
        titleComponent.title.set("活动通知")
        titleComponent.rightText.set("清除")
        titleComponent.callback = this
        initDatas()
    }

}