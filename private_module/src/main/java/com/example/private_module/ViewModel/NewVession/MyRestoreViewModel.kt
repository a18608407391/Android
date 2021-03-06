package com.example.private_module.ViewModel.NewVession

import android.databinding.ObservableArrayList
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.DETAIL_RESULT
import com.elder.zcommonmodule.Entity.*
import com.elder.zcommonmodule.Inteface.SimpleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.private_module.Activity.NewVession.MyRestoreActivity
import com.example.private_module.BR
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.BaseFragment
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


class MyRestoreViewModel : BaseViewModel(), HttpInteface.PrivateRestoreList, TitleComponent.titleComponentCallBack, SwipeRefreshLayout.OnRefreshListener, SimpleClickListener, HttpInteface.SocialDynamicsList {
    override fun ResultSDListSuccess(it: String) {
        restoreActivity.dismissProgressDialog()
        restoreActivity.swp!!.isRefreshing = false
        var dyna = Gson().fromJson<DynamicsCategoryEntity>(it, DynamicsCategoryEntity::class.java)
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, restoreActivity.location)
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, dyna.data!![0])
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
        model.arguments = bundle
        restoreActivity.startForResult(model, DETAIL_RESULT)
    }

    override fun ResultSDListError(ex: Throwable) {

    }

    override fun onSimpleClick(entity: Any) {
        var c = entity as CollectionEntity.Collection
        if (c.BIG_TYPE == null || c.BIG_TYPE == 0) {
            restoreActivity.showProgressDialog("加载中......")
            HttpRequest.instance.DynamicListResult = this
            var map = HashMap<String, String>()
            map["memberId"] = c.MEMBER_ID.toString()
            map["id"] = c.DYNAMIC_ID.toString()
            map["pageSize"] = "1"
            map["length"] = "1"
            map["type"] = "5"
            map["yAxis"] = restoreActivity.location!!.longitude.toString()
            map["xAxis"] = restoreActivity.location!!.latitude.toString()
            HttpRequest.instance.getDynamicsList(map)
//            if (c.releaseDynamicParent == null) {
//                Log.e("result","动态"+Gson().toJson(c))
//                Toast.makeText(restoreActivity.activity, "该动态已被删除！", Toast.LENGTH_SHORT).show()
//            } else {
//                var bundle = Bundle()
//                bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, restoreActivity.location)
//                bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, c.releaseDynamicParent)
//                var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
//                model.arguments = bundle
//                restoreActivity.startForResult(model, DETAIL_RESULT)
////                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, restoreActivity.location).withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, c.releaseDynamicParent).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 8).navigation()
//            }
        } else if (c.BIG_TYPE == 1) {
            ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_CLOCK_DETAIL).withInt(RouterUtils.PartyConfig.PARTY_ID, Integer.valueOf(entity.ID))
                    .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                            restoreActivity.location).withInt(RouterUtils.PartyConfig.NavigationType, 2).withInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE).navigation()
        } else if (c.BIG_TYPE == 2) {
            ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_SUBJECT_DETAIL).withInt(RouterUtils.PartyConfig.NavigationType, 2).withInt(RouterUtils.PartyConfig.PARTY_ID, Integer.valueOf(entity.ID))
                    .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                            restoreActivity.location).withInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE).navigation()
        } else if (c.BIG_TYPE == 3) {
            ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_DETAIL).withInt(RouterUtils.PartyConfig.NavigationType, 2).withInt(RouterUtils.PartyConfig.PARTY_ID, Integer.valueOf(entity.ID))
                    .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                            restoreActivity.location).withInt(RouterUtils.PartyConfig.NavigationType, 2).withInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE).navigation()
        }
    }

    override fun onRefresh() {
        pageSize = 1
        lenth = 20
        initDatas()
        CoroutineScope(uiContext).launch {
            delay(10000)
            restoreActivity.swp!!.isRefreshing = false
        }
    }

    override fun onComponentClick(view: View) {
        restoreActivity._mActivity!!.onBackPressedSupport()
//        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(restoreActivity, object : NavCallback() {
//            override fun onArrival(postcard: Postcard?) {
//                finish()
//            }
//        })
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
            var item = it
            if (it.BIG_TYPE == 1) {
                item.DISTANCE = "全长" + item.DISTANCE + "km" + " \n距离" + it.SQRTVALUE + "km"
                var start = item.ACTIVITY_START!!.split(" ")[0]
                var stop = item.ACTIVITY_STOP!!.split(" ")[0]
                item.ACTIVITY_START = start + "至" + stop
            } else if (it.BIG_TYPE == 2) {
                var start = item.ACTIVITY_START!!.split(" ")[0]
                var stop = item.ACTIVITY_STOP!!.split(" ")[0]
                if (item.DISTANCE == null) {
                    item.DISTANCE = "0"
                }
                item.ACTIVITY_START = start + "至" + stop + " \n距离" + item.SQRTVALUE + "km"
                if (item.TICKET_PRICE.isNullOrEmpty() || item.TICKET_PRICE!!.toDouble() <= 0) {
                    item.TICKET_PRICE = "免费"
                } else {
                    item.TICKET_PRICE = getString(R.string.rmb) + item.TICKET_PRICE
                }
                if (!item.TYPE.isNullOrEmpty()) {
                    item.TYPE!!.split(",").forEachIndexed { index, s ->
                        when (index) {
                            0 -> {
                                item.type1 = s
                            }
                            1 -> {
                                item.type2 = s
                            }
                            2 -> {
                                item.type3 = s
                            }
                        }
                    }
                }
            } else if (it.BIG_TYPE == 3) {
                if (item.DISTANCE == null) {
                    item.DISTANCE = "0"
                }
                item.DISTANCE = "时长" + item.DAY + "天" + " " + "里程" + item.DISTANCE + "km"
                if (item.TICKET_PRICE.isNullOrEmpty() || item.TICKET_PRICE!!.toDouble() <= 0) {
                    item.TICKET_PRICE = "免费"
                } else {
                    item.TICKET_PRICE = getString(R.string.rmb) + item.TICKET_PRICE
                }
            }
            items.add(item)
        }
        restoreActivity.swp!!.isRefreshing = false
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

    var listener: SimpleClickListener = this
    var titleComponent = TitleComponent()
    var adapter = BindingRecyclerViewAdapter<CollectionEntity.Collection>()
    var items = ObservableArrayList<CollectionEntity.Collection>()
    var itemBinding = ItemBinding.of<CollectionEntity.Collection> { itemBinding, position, item ->
        when (item.BIG_TYPE) {
            1 -> {
                //打卡
                itemBinding.set(BR.restore_item_entity, R.layout.restore_items_clock_layout).bindExtra(BR.listener, listener)
            }
            2 -> {
                //主题
                itemBinding.set(BR.restore_item_entity, R.layout.restore_items_subject_layout).bindExtra(BR.listener, listener)
            }
            3 -> {
                //摩旅活动
                itemBinding.set(BR.restore_item_entity, R.layout.restore_items_active_layout).bindExtra(BR.listener, listener)
            }
            0 -> {
                itemBinding.set(BR.restore_item_entity, R.layout.restore_items_layout).bindExtra(BR.listener, listener)
            }
        }
    }


    var pageSize = 1

    var lenth = 20

    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            if (t < lenth * pageSize) {
                return
            } else {
                pageSize++
                initDatas()
            }
        }
    })


    private fun initDatas() {
        var mao = HashMap<String, String>()
        mao.put("start", pageSize.toString())
        mao.put("pageSize", lenth.toString())
        mao.put("x", restoreActivity.location!!.longitude.toString())
        mao.put("y", restoreActivity.location!!.latitude.toString())
        HttpRequest.instance.getPrivateCollection(mao)
    }

}