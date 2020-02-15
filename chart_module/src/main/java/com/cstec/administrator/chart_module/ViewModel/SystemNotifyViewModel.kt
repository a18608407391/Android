package com.cstec.administrator.chart_module.ViewModel

import android.databinding.ObservableArrayList
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import com.cstec.administrator.chart_module.Activity.SystemNotifyListActivity
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.Data.SystemNotifyData
import com.cstec.administrator.chart_module.R
import com.elder.zcommonmodule.Component.TitleComponent
import com.zk.library.Base.BaseViewModel
import kotlinx.android.synthetic.main.activity_sys_notify.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class SystemNotifyViewModel : BaseViewModel(), SwipeRefreshLayout.OnRefreshListener {
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
    }

    var titleComponent = TitleComponent()

    var adapter = BindingRecyclerViewAdapter<SystemNotifyData>()

    var items = ObservableArrayList<SystemNotifyData>()

    var itemBinding = ItemBinding.of<SystemNotifyData>(BR.sys_notify_item_model, R.layout.sys_notify_item_layout)

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}