package com.example.private_module.ViewModel.NewVession

import android.databinding.ObservableArrayList
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.CommandData
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.elder.zcommonmodule.Inteface.DoubleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.private_module.Activity.NewVession.CommandActivity
import com.example.private_module.BR
import com.example.private_module.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.command_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class CommandViewModel : BaseViewModel(), SwipeRefreshLayout.OnRefreshListener, HttpInteface.queryCommandMeList, HttpInteface.SocialDynamicsList, TitleComponent.titleComponentCallBack, DoubleClickListener {

    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {

    }

    fun returnBack() {
        if (!destroyList!!.contains("MsgActivity")) {
            ARouter.getInstance().build(RouterUtils.Chat_Module.MSG_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, commandActivity.location).navigation()
        } else {
            finish()
        }
    }

    override fun ResultSDListSuccess(it: String) {
        commandActivity.dismissProgressDialog()
        var dyna = Gson().fromJson<DynamicsCategoryEntity>(it, DynamicsCategoryEntity::class.java)
        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, commandActivity.location).withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, dyna.data!![0]).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 5).navigation()
    }

    override fun ResultSDListError(ex: Throwable) {
        commandActivity.dismissProgressDialog()
    }

    override fun onItemClick(entity: Any) {
        if (System.currentTimeMillis() - cur < 1000) {
            return
        } else {
            cur = System.currentTimeMillis()
        }

        commandActivity.showProgressDialog("加载中......")
        var data = entity as CommandData
        HttpRequest.instance.DynamicListResult = this
        var map = HashMap<String, String>()
        map["memberId"] = data.DYNAMIC_MEMBER_ID.toString()
        map["id"] = data.DYNAMIC_ID.toString()
        map["pageSize"] = "1"
        map["length"] = "1"
        map["type"] = "5"
        map["yAxis"] = commandActivity.location!!.longitude.toString()
        map["xAxis"] = commandActivity.location!!.latitude.toString()
        HttpRequest.instance.getDynamicsList(map)
    }

    override fun onImgClick(entity: Any) {
        if (System.currentTimeMillis() - cur < 1000) {
            return
        } else {
            cur = System.currentTimeMillis()
        }
        var entity = entity as CommandData
        Log.e("result", "当前数据" + Gson().toJson(entity))
        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity.MEMBER_ID.toString())
                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, commandActivity.location)
                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 9).navigation()
    }

    var cur = 0L


    override fun CommandSuccess(it: String) {
        Log.e("result", "CommandSuccess" + it)
        if (it.isNullOrEmpty()) {
            return
        }
        var itemss = Gson().fromJson<ArrayList<CommandData>>(it, object : TypeToken<ArrayList<CommandData>>() {}.type)
        itemss.forEach {
            items.add(it)
        }
    }

    override fun CommandError(ex: Throwable) {
        Log.e("result", "CommandError" + ex.message)
    }


    override fun onRefresh() {
        pageSize = 1
        lenth = 20
        initDatas()
        CoroutineScope(uiContext).launch {
            delay(10000)
            commandActivity.command_swipe.isRefreshing = false
        }
    }

    var listener: DoubleClickListener = this
    var titleComponent = TitleComponent()
    var adapter = BindingRecyclerViewAdapter<CommandData>()
    var items = ObservableArrayList<CommandData>()
    var itemBinding = ItemBinding.of<CommandData>(BR.command_data_item_entity, R.layout.command_items_layout).bindExtra(BR.listener, listener)
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
        HttpRequest.instance.getcommandmelistResult = this
        var mao = HashMap<String, String>()
        HttpRequest.instance.queryCommandMeListRequest(mao)
    }

    lateinit var commandActivity: CommandActivity
    fun inject(commandActivity: CommandActivity) {
        this.commandActivity = commandActivity

        titleComponent.title.set("评论")
        titleComponent.rightText.set("")
        titleComponent.rightIcon.set(context.getDrawable(R.drawable.read_all))
        titleComponent.callback = this
        initDatas()
    }
}