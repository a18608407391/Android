package com.cstec.administrator.social.ViewModel

import android.databinding.ObservableArrayList
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
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.elder.zcommonmodule.Entity.LikesEntity
import com.elder.zcommonmodule.Inteface.DoubleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding


class GetLikeViewModel : BaseViewModel(), HttpInteface.GetLikeResult, TitleComponent.titleComponentCallBack, DoubleClickListener, SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        initData()
    }

    override fun onItemClick(entity: Any) {
//        var entity = entity as LikesEntity.LikeBean
//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL)
//                .withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, entity)
//                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
//                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1).navigation()
    }

    override fun onImgClick(entity: Any) {
//        var entity = entity as LikesEntity.LikeBean
//        Log.e("result", "当前数据" + Gson().toJson(entity))
//        Log.e("result", "当前数据1" + entity.memberId)
//        Log.e("result", "当前数据2" + Gson().toJson(activity.location))
//
//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity.memberId)
//                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
//                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 4).navigation()
    }

    override fun onComponentClick(view: View) {
        if (destroyList!!.contains("DriverHomeActivity")) {
            ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                    .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
                    .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, activity.id)
                    .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 0).navigation(activity, object : NavCallback() {
                        override fun onArrival(postcard: Postcard?) {
                            finish()
                        }
                    })
        } else {
            finish()
        }
    }

    override fun onComponentFinish(view: View) {
    }

    override fun GetLikeSuccess(it: String) {

        var entity = Gson().fromJson<LikesEntity>(it, LikesEntity::class.java)

        entity?.data!!.forEach {
            items.add(it)
        }
    }

    override fun getLikeError(it: Throwable) {
    }


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
        Log.e("result", "当前memberID" + activity.id)
        var map = HashMap<String, String>()
        map["id"] = activity.id!!
        map["pageSize"] = pageSize.toString()
        map["length"] = length.toString()
        HttpRequest.instance.GetLike(map)
    }

    var listener: DoubleClickListener = this

    var adapter = BindingRecyclerViewAdapter<LikesEntity.LikeBean>()

    var items = ObservableArrayList<LikesEntity.LikeBean>()

    var itembinding = ItemBinding.of<LikesEntity.LikeBean>(BR.getlike_model, R.layout.getlike_child_layout).bindExtra(BR.listener,listener)

}