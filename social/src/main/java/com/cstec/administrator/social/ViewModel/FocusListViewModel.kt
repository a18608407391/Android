package com.cstec.administrator.social.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.social.Activity.FocusListActivity
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.Entity.FoucusEntity
import com.elder.zcommonmodule.Entity.SocialHoriEntity
import com.cstec.administrator.social.R
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Inteface.SimpleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.getString


class FocusListViewModel : BaseViewModel(), HttpInteface.SocialDynamicsFocuserList, TitleComponent.titleComponentCallBack, SwipeRefreshLayout.OnRefreshListener, HttpInteface.SocialDynamicsLikerList, SimpleClickListener {
    override fun onSimpleClick(entity: Any) {
        var so = entity as SocialHoriEntity

        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, focusListActivity.location)
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID,so.memberId.toString())
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
        model.arguments = bundle
        focusListActivity.start(model)
//
//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, so.memberId.toString())
//                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, focusListActivity.location)
//                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 7).navigation()

    }

    override fun ResultLikerSuccess(it: String) {
        var entity = Gson().fromJson<FoucusEntity>(it, FoucusEntity::class.java)
        entity.data?.forEach {
            items.add(it)
        }
    }

    override fun ResultLikerError(ex: Throwable) {
    }

    override fun onRefresh() {
    }

    override fun onComponentClick(view: View) {
      focusListActivity._mActivity!!.onBackPressedSupport()
    }

    override fun onComponentFinish(view: View) {
    }

    override fun ResultFocuserSuccess(it: String) {

    }

    override fun ResultFocuserError(ex: Throwable) {
        Log.e("result", ex.message + "网路哦错误")

    }


    lateinit var focusListActivity: FocusListActivity
    var pageSize = 1
    var lenth = 50


    var component = TitleComponent()
    fun inject(focusListActivity: FocusListActivity) {
        this.focusListActivity = focusListActivity
        component.callback = this
        component.title.set(getString(R.string.focus_list_str))
        component.rightVisibleType.set(true)
        component.arrowVisible.set(false)
        component.rightText.set("")
        var map = HashMap<String, String>()
        map["dynamicId"] = focusListActivity?.detail!!.id.toString()
        map["pageSize"] = pageSize.toString()
        map["length"] = lenth.toString()
        HttpRequest.instance.DynamicLikerListResult = this
        HttpRequest.instance.getDynamicsLikeList(map)
    }

    var adapter = BindingRecyclerViewAdapter<SocialHoriEntity>()

    var items = ObservableArrayList<SocialHoriEntity>()

    var simple: SimpleClickListener = this

    var itemBinding = ItemBinding.of<SocialHoriEntity> { itemBinding, position, item ->
        itemBinding.set(BR.focus_item_model, R.layout.focus_item_layout).bindExtra(BR.listener, simple)
    }
}