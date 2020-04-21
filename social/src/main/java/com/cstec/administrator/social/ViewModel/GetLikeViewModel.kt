package com.cstec.administrator.social.ViewModel

import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.social.Activity.GetLikeActivity
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.R
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.CommandData
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.elder.zcommonmodule.Entity.LikesEntity
import com.elder.zcommonmodule.Inteface.DoubleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class GetLikeViewModel : BaseViewModel(), HttpInteface.GetLikeResult, TitleComponent.titleComponentCallBack, DoubleClickListener, SwipeRefreshLayout.OnRefreshListener, HttpInteface.SocialDynamicsList {
    override fun ResultSDListSuccess(it: String) {
        activity.dismissProgressDialog()
        var dyna = Gson().fromJson<DynamicsCategoryEntity>(it, DynamicsCategoryEntity::class.java)
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, dyna.data!![0])
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
        startFragment(activity, RouterUtils.SocialConfig.SOCIAL_DETAIL, bundle)
//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location).withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, dyna.data!![0]).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1).navigation()
    }

    override fun ResultSDListError(ex: Throwable) {
        activity.dismissProgressDialog()
    }

    override fun onRefresh() {
        pageSize = 1
        length = 10
        initData()
        CoroutineScope(uiContext).launch {
            delay(10000)
            activity.swip.isRefreshing = false
        }
    }

    var cur = 0L

    override fun onItemClick(entity: Any) {
        if (System.currentTimeMillis() - cur < 1000) {
            return
        } else {
            cur = System.currentTimeMillis()
        }
        var entity = entity as LikesEntity.LikeBean
        if (entity.releaseDynamicParent != null) {
            var bundle = Bundle()
            bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, entity.releaseDynamicParent)
            bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
            startFragment(activity, RouterUtils.SocialConfig.SOCIAL_DETAIL, bundle)
//            ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL)
//                    .withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, entity.releaseDynamicParent)
//                    .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
//                    .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1).navigation()
        } else {
            Log.e("result", "动态id" + entity.dynamicId)
            activity.showProgressDialog("加载中......")
            HttpRequest.instance.DynamicListResult = this
            var map = HashMap<String, String>()
            map["id"] = entity.dynamicId.toString()
            map["pageSize"] = "1"
            map["length"] = "1"
            map["type"] = "5"
            map["yAxis"] = activity.location!!.longitude.toString()
            map["xAxis"] = activity.location!!.latitude.toString()
            HttpRequest.instance.getDynamicsList(map)
        }
    }

    override fun onImgClick(entity: Any) {
        var entity = entity as LikesEntity.LikeBean

        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity.memberId)
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
        model.arguments = bundle
        activity.start(model)
//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity.memberId)
//                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
//                .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
//                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 4).navigation()
    }

    override fun onComponentClick(view: View) {
        activity._mActivity!!.onBackPressedSupport()
//        activity.doback()
    }

    override fun onComponentFinish(view: View) {
    }

    override fun GetLikeSuccess(it: String) {
        activity.dismissProgressDialog()
        var entity = Gson().fromJson<LikesEntity>(it, LikesEntity::class.java)
        activity.swip.isRefreshing = false
        if (pageSize == 1) {
            items.clear()
        }
        entity?.data!!.forEach {
            items.add(it)
        }
    }

    override fun getLikeError(it: Throwable) {
        activity.dismissProgressDialog()
    }

    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            if (t < length * pageSize) {
                return
            } else {
                pageSize++
                initData()
            }
        }
    })


    var titleComponent = TitleComponent()

    lateinit var activity: GetLikeActivity
    fun inject(getLikeActivity: GetLikeActivity) {

        this.activity = getLikeActivity
        titleComponent.title.set("收到的赞")
        titleComponent.rightText.set("")
        titleComponent.callback = this
        initData()
    }

    var pageSize = 1
    var length = 10

    fun initData() {
        HttpRequest.instance.getLikeResult = this
        var map = HashMap<String, String>()
        map["id"] = activity.id!!
        map["pageSize"] = pageSize.toString()
        map["length"] = length.toString()
        HttpRequest.instance.GetLike(map)
    }

    var listener: DoubleClickListener = this

    var adapter = BindingRecyclerViewAdapter<LikesEntity.LikeBean>()

    var items = ObservableArrayList<LikesEntity.LikeBean>()

    var itembinding = ItemBinding.of<LikesEntity.LikeBean>(BR.getlike_model, R.layout.getlike_child_layout).bindExtra(BR.listener, listener)

}