package com.example.private_module.ViewModel.NewVession

import android.databinding.ObservableArrayList
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.FansEntity
import com.example.private_module.Activity.NewVession.CommandActivity
import com.example.private_module.BR
import com.example.private_module.R
import com.zk.library.Base.BaseViewModel
import kotlinx.android.synthetic.main.command_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class CommandViewModel : BaseViewModel(), SwipeRefreshLayout.OnRefreshListener {


    override fun onRefresh() {
        pageSize = 1
        lenth = 20
        initDatas()
        CoroutineScope(uiContext).launch {
            delay(10000)
            commandActivity.command_swipe.isRefreshing = false
        }
    }


    var titleComponent = TitleComponent()

    var adapter = BindingRecyclerViewAdapter<FansEntity.FansBean>()

    var items = ObservableArrayList<FansEntity.FansBean>()

    var itemBinding = ItemBinding.of<FansEntity.FansBean>(BR.fans_item_entity, R.layout.fans_items_layout)
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

    private fun initDatas() {
//        HttpRequest.instance.DynamicFocusResult = this
//        HttpRequest.instance.privateGetFansList = this
//        var mao = HashMap<String, String>()
//        mao.put("pageSize", pageSize.toString())
//        mao.put("length", lenth.toString())
//        HttpRequest.instance.getPrivateFansList(mao)
    }

    lateinit var commandActivity: CommandActivity
    fun inject(commandActivity: CommandActivity) {
        this.commandActivity = commandActivity
    }
}