package com.example.private_module.ViewModel.NewVession

import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.DETAIL_RESULT
import com.elder.zcommonmodule.Entity.*
import com.elder.zcommonmodule.Inteface.DoubleClickListener
import com.elder.zcommonmodule.Inteface.SimpleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.private_module.Activity.NewVession.AtmeActivity
import com.example.private_module.BR
import com.example.private_module.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_myfans.*
import kotlinx.android.synthetic.main.atme_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class AtmeViewModel : BaseViewModel(), SwipeRefreshLayout.OnRefreshListener, HttpInteface.queryAtmeList, TitleComponent.titleComponentCallBack, HttpInteface.SocialDynamicsList, DoubleClickListener {
    override fun onItemClick(entity: Any) {
        if (System.currentTimeMillis() - cur < 1000) {
            return
        } else {
            cur = System.currentTimeMillis()
        }

        atmeActivity.showProgressDialog("加载中......")
        var data = entity as AtmeData
        HttpRequest.instance.DynamicListResult = this
        var map = HashMap<String, String>()
        map["id"] = data.DYNAMIC_ID.toString()
        map["pageSize"] = "1"
        map["length"] = "1"
        map["type"] = "5"
        map["yAxis"] = atmeActivity.location!!.longitude.toString()
        map["xAxis"] = atmeActivity.location!!.latitude.toString()
        HttpRequest.instance.getDynamicsList(map)
    }

    override fun onImgClick(entity: Any) {
        if (System.currentTimeMillis() - cur < 1000) {
            return
        } else {
            cur = System.currentTimeMillis()
        }
        var entity = entity as AtmeData

        Log.e("result", "当前数据" + Gson().toJson(entity))
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, atmeActivity.location)
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID,  entity.MEMBER_ID)
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
        model.arguments = bundle
        atmeActivity.start(model)

//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity.MEMBER_ID)
//                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, atmeActivity.location)
//                .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
//                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 8).navigation()


    }

    override fun ResultSDListSuccess(it: String) {
        atmeActivity.dismissProgressDialog()
        atmeActivity.swp!!.isRefreshing = false
        var dyna = Gson().fromJson<DynamicsCategoryEntity>(it, DynamicsCategoryEntity::class.java)
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION,atmeActivity.location)
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, dyna.data!![0])
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
        model.arguments = bundle
        atmeActivity.startForResult(model, DETAIL_RESULT)

//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, atmeActivity.location).withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, dyna.data!![0]).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 6).navigation()
    }

    override fun ResultSDListError(ex: Throwable) {
        atmeActivity.dismissProgressDialog()
    }

    override fun onComponentClick(view: View) {
//        returnBack()
        atmeActivity._mActivity!!.onBackPressedSupport()

    }

    override fun onComponentFinish(view: View) {
    }

    fun returnBack() {
//        if (!destroyList!!.contains("MsgActivity")) {
//            ARouter.getInstance().build(RouterUtils.Chat_Module.MSG_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, atmeActivity.location).navigation()
//        } else {
            finish()
//        }
    }

    var cur = 0L

    override fun AtmeListSuccess(it: String) {
        Log.e("result", it)
        if (it.isNullOrEmpty()) {
            return
        } else {
            var itemse = Gson().fromJson<ArrayList<AtmeData>>(it, object : TypeToken<ArrayList<AtmeData>>() {}.type)
            Log.e("result","数据长度"+itemse.size.toString())

            itemse.forEach {
                items.add(it)
            }
        }
    }

    override fun AtmeListError(ex: Throwable) {
        Toast.makeText(context, "网络错误，请重试！", Toast.LENGTH_SHORT).show()
    }

    override fun onRefresh() {
        pageSize = 1
        lenth = 20
        initDatas()
        CoroutineScope(uiContext).launch {
            delay(10000)
            atmeActivity.swp!!.isRefreshing = false
        }
    }

    var listener: DoubleClickListener = this
    var titleComponent = TitleComponent()

    var adapter = BindingRecyclerViewAdapter<AtmeData>()

    var items = ObservableArrayList<AtmeData>()

    var itemBinding = ItemBinding.of<AtmeData>(BR.atme_item_entity, R.layout.atme_items_layout).bindExtra(BR.listener, listener)


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
        }
    })

    private fun initDatas() {
        HttpRequest.instance.getatmelistResult = this
        var mao = HashMap<String, String>()
        HttpRequest.instance.queryAtMeListRequest(mao)
    }


    lateinit var atmeActivity: AtmeActivity
    fun inject(atmeActivity: AtmeActivity) {
        this.atmeActivity = atmeActivity
        initDatas()
        titleComponent.title.set("@我的")
        titleComponent.rightIcon.set(context.getDrawable(R.drawable.read_all))
        titleComponent.rightText.set("")
        titleComponent.callback = this
        initDatas()
    }
}