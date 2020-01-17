package com.cstec.administrator.social.ViewModel

import android.databinding.ObservableArrayList
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.cstec.administrator.social.Activity.MyDynamicsActivity
import com.cstec.administrator.social.Adapter.GridMyDynamicRecycleViewAdapter
import com.cstec.administrator.social.Entity.GridClickEntity
import com.cstec.administrator.social.Inteface.DynamicClickListener
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.elder.zcommonmodule.Entity.DynamicsSimple
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_my_dynamics.*
import kotlinx.android.synthetic.main.fragment_social.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.DecimalFormat
import java.util.HashMap


class MyDynamicsViewModel : BaseViewModel(), DynamicClickListener, HttpInteface.SocialDynamicsList, TitleComponent.titleComponentCallBack, SwipeRefreshLayout.OnRefreshListener {
    override fun deleteClick(view: DynamicsCategoryEntity.Dynamics) {
    }

    override fun spanclick(): BindingCommand<DynamicsSimple> {
        var spanclick = BindingCommand(object : BindingConsumer<DynamicsSimple> {
            override fun call(t: DynamicsSimple) {
                ARouter.getInstance()
                        .build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(myDynamicsActivity.location?.latitude!!, myDynamicsActivity.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, myDynamicsActivity.location?.aoiName!!, myDynamicsActivity.location!!.poiName))
                        .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, t.memberId)
                        .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 0)
                        .navigation()
            }
        })
        return spanclick
    }


    override fun onRefresh() {
        page = 20
        pageSize = 1
        initDatas()
        CoroutineScope(uiContext).launch {
            delay(10000)
            myDynamicsActivity.mysocial_swipe.isRefreshing = false
        }
    }

    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {

    }

    override fun ResultSDListSuccess(it: String) {
        Log.e("result", "我的动态数据" + it)
        if (it.length < 10) {
            return
        }
        if (pageSize != 1) {

        } else {
            items.clear()
        }
        var entity = Gson().fromJson<DynamicsCategoryEntity>(it, DynamicsCategoryEntity::class.java)
        entity.data?.forEach {
            var dis = AMapUtils.calculateLineDistance(LatLng(myDynamicsActivity.location!!.latitude, myDynamicsActivity.location!!.longitude), LatLng(it.xAxis!!.toDouble(), it.yAxis!!.toDouble()))
            it.distance = DecimalFormat("0.00").format(dis / 1000) + "KM"
            items.add(it)
        }
        adapter.initDatas(items)
        myDynamicsActivity.mysocial_swipe.isRefreshing = false
    }

    override fun ResultSDListError(ex: Throwable) {
    }


    var CurrentClickTime = 0L
    override fun LikeClick(entiy: DynamicsCategoryEntity.Dynamics) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }

        var view = entiy
        var position = items.indexOf(view)
        var m = Integer.valueOf(view.fabulousCount)
        if (view.isLike == 0) {
            view.isLike = 1
            m++
        } else {
            view.isLike = 0
            m--
        }
        view.fabulousCount = m.toString()
        var map = HashMap<String, String>()
        map["dynamicId"] = view.id!!
        HttpRequest.instance.getDynamicsLike(map)
//        config.submitList(Arrays.asList(view))
//        diff.update(, diff.calculateDiff(Arrays.asList(view)))
        items[position] = view
        adapter.notifyItemChanged(position, "LikeClick")
    }

    override fun storeClick(view: DynamicsCategoryEntity.Dynamics) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        Log.e("result", "storeClick")
        var position = items.indexOf(view)
        var m = Integer.valueOf(view.collectionCount)
        if (view.hasCollection == 0) {
            view.hasCollection = 1
            m++
        } else {
            view.hasCollection = 0
            m--
        }
        view.collectionCount = m.toString()
        var map = HashMap<String, String>()
        map["dynamicId"] = view.id!!
        HttpRequest.instance.getDynamicsCollection(map)
        items[position] = view
        adapter.notifyItemRangeChanged(0, adapter.itemCount, "storeClick")
    }

    override fun yelpClick(view: DynamicsCategoryEntity.Dynamics) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        RxBus.default?.postSticky(view)
        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, myDynamicsActivity.location).withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, view).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 2).navigation()
    }

    override fun retransClick(view: DynamicsCategoryEntity.Dynamics) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        if (myDynamicsActivity.location == null) {
            Toast.makeText(context, "获取定位信息异常，请重试!", Toast.LENGTH_SHORT).show()
            return
        }
        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_RELEASE).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, myDynamicsActivity.location).withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, view).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 3).navigation()
    }

    override fun avatarClick(view: DynamicsCategoryEntity.Dynamics) {
        ARouter.getInstance()
                .build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(myDynamicsActivity.location?.latitude!!, myDynamicsActivity.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, myDynamicsActivity.location?.aoiName!!, myDynamicsActivity.location!!.poiName))
                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, view?.memberId)
                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 2)
                .navigation()
    }

    override fun FocusClick(view: DynamicsCategoryEntity.Dynamics) {
//        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
//            return
//        } else {
//            CurrentClickTime = System.currentTimeMillis()
//        }
//        focusClickPosition = items.indexOf(view)
//        var map = HashMap<String, String>()
//        map["fansMemberId"] = view.memberId.toString()
//        HttpRequest.instance.getDynamicsFocus(map)
    }

    override fun bindingCommand(): BindingCommand<GridClickEntity> {
        return clickBinding
    }

    var clickBinding = BindingCommand(object : BindingConsumer<GridClickEntity> {
        override fun call(t: GridClickEntity) {
            var list = ObservableArrayList<String>()
            t.urlList!!.forEach {
                list.add(it)
            }
            DialogUtils.createBigPicShow(myDynamicsActivity!!, list, t.childPosition)
        }
    })

    var titleComponent = TitleComponent()
    var pageSize = 1
    var page = 20
    var items = ObservableArrayList<DynamicsCategoryEntity.Dynamics>()
    lateinit var adapter: GridMyDynamicRecycleViewAdapter
    lateinit var myDynamicsActivity: MyDynamicsActivity
    fun inject(myDynamicsActivity: MyDynamicsActivity) {
        this.myDynamicsActivity = myDynamicsActivity
        titleComponent.title.set("我的动态")
        titleComponent.rightText.set("")
        titleComponent.callback = this
        HttpRequest.instance.DynamicListResult = this
        initDatas()
        RxSubscriptions.add(RxBus.default?.toObservable(DynamicsCategoryEntity.Dynamics::class.java)?.subscribe {
            items.forEachIndexed { index, dynamics ->
                if (dynamics.id == it.id) {
                    items[index] = it
                } else if (dynamics.memberId == it.memberId && dynamics.followed != it.followed) {
                    dynamics.followed = it.followed
                    items.set(index, dynamics)
                }
            }
            adapter.initDatas(items)
        })
        adapter = GridMyDynamicRecycleViewAdapter(myDynamicsActivity, items, this)
        this.myDynamicsActivity = myDynamicsActivity
    }


    fun initDatas() {
        var map = HashMap<String, String>()
        map["pageSize"] = pageSize.toString()
        map["length"] = page.toString()
        map["yAxis"] = myDynamicsActivity.location!!.longitude.toString()
        map["xAxis"] = myDynamicsActivity.location!!.latitude.toString()
        map["type"] = "4"
        HttpRequest.instance.getDynamicsList(map)
    }

    var listener: DynamicClickListener = this

    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            Log.e("result", "加载更多" + t)
            if (t > 1) {
                pageSize++
                initDatas()
            }
        }
    })
}