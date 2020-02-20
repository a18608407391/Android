package com.cstec.administrator.chart_module.ViewModel

import android.databinding.ObservableArrayList
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import com.cstec.administrator.chart_module.Activity.ActiveNotifyListActivity
import com.cstec.administrator.chart_module.BR
import com.elder.zcommonmodule.Entity.SystemNotifyData
import com.cstec.administrator.chart_module.R
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.zk.library.Base.BaseViewModel
import kotlinx.android.synthetic.main.activity_active_notify.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class ActiveNotifyViewModel : BaseViewModel(), SwipeRefreshLayout.OnRefreshListener, HttpInteface.queryActiveNotifyList, TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {

    }

    override fun ActiveNotifyListSuccess(response: String) {
        Log.e("result", "ActiveNotifyListSuccess" + response)

    }

    override fun ActiveNotifyListError(ex: Throwable) {
        Log.e("result", "ActiveNotifyListError" + ex.message)
    }

    override fun onRefresh() {
        pageSize = 1
        lenth = 20
        initDatas()
        CoroutineScope(uiContext).launch {
            delay(10000)
            activeNotifyListActivity.active_swipe.isRefreshing = false
        }
    }

    var titleComponent = TitleComponent()

    var adapter = BindingRecyclerViewAdapter<SystemNotifyData>()

    var items = ObservableArrayList<SystemNotifyData>()

    var itemBinding = ItemBinding.of<SystemNotifyData>(BR.active_notify_item_model, R.layout.active_notify_item_layout)

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
        HttpRequest.instance.getActiveNotify = this
        HttpRequest.instance.queryActiveNotify(HashMap())
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