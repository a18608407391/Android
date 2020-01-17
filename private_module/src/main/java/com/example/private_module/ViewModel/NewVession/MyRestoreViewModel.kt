package com.example.private_module.ViewModel.NewVession

import android.databinding.ObservableArrayList
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.CollectionEntity
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.elder.zcommonmodule.Entity.RestoreEntity
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.private_module.Activity.NewVession.MyRestoreActivity
import com.example.private_module.BR
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_my_restore.*
import kotlinx.android.synthetic.main.activity_mylike.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class MyRestoreViewModel : BaseViewModel(), HttpInteface.PrivateRestoreList, TitleComponent.titleComponentCallBack, SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        pageSize = 1
        lenth = 20
        initDatas()

        CoroutineScope(uiContext).launch {
            delay(10000)
            restoreActivity.restore_swipe.isRefreshing = false
        }
    }

    override fun onComponentClick(view: View) {
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(restoreActivity, object : NavCallback() {
            override fun onArrival(postcard: Postcard?) {
                finish()
            }
        })
    }

    override fun onComponentFinish(view: View) {
    }

    override fun ResultPrivateDynamicSuccess(it: String) {
        if (it.length < 10) {
            return
        }
        if (pageSize == 1) {
            items.clear()
        }
        var entity = Gson().fromJson<CollectionEntity>(it, CollectionEntity::class.java)
        entity.data?.forEach {
            items.add(it)
        }
        restoreActivity.restore_swipe.isRefreshing = false
    }

    override fun ResultPrivateDynamicError(ex: Throwable) {

    }

    lateinit var restoreActivity: MyRestoreActivity
    fun inject(myRestoreActivity: MyRestoreActivity) {
        this.restoreActivity = myRestoreActivity
        titleComponent.title.set(getString(R.string.my_restore))
        titleComponent.callback = this
        titleComponent.rightText.set("")
        HttpRequest.instance.privateRestoreList = this
        initDatas()
    }

    var titleComponent = TitleComponent()
    var adapter = BindingRecyclerViewAdapter<CollectionEntity.Collection>()
    var items = ObservableArrayList<CollectionEntity.Collection>()
    var itemBinding = ItemBinding.of<CollectionEntity.Collection>(BR.restore_item_entity, R.layout.restore_items_layout)


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
        var mao = HashMap<String, String>()
        mao.put("pageSize", pageSize.toString())
        mao.put("length", lenth.toString())
        HttpRequest.instance.getPrivateCollection(mao)
    }

}