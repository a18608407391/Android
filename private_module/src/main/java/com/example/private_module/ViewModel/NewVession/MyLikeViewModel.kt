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
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.elder.zcommonmodule.Entity.LikesEntity
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Inteface.SimpleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.private_module.Activity.NewVession.MyLikeActivity
import com.example.private_module.BR
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_myfocus.*
import kotlinx.android.synthetic.main.activity_mylike.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class MyLikeViewModel : BaseViewModel(), HttpInteface.PrivateLikeList, TitleComponent.titleComponentCallBack, HttpInteface.SocialDynamicsList, SwipeRefreshLayout.OnRefreshListener, SimpleClickListener {
    override fun onSimpleClick(entity: Any) {
        var entity  = entity as DynamicsCategoryEntity.Dynamics
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION,activity.location)
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, entity)
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
        model.arguments = bundle
        activity.startForResult(model, DETAIL_RESULT)
//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location).withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, entity).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 4).navigation()
    }

    override fun onRefresh() {
        pageSize =1
        lenth = 20
        initDatas()
        CoroutineScope(uiContext).launch {
            delay(10000)
            activity.swp!!.isRefreshing= false
        }
    }

    override fun ResultSDListSuccess(it: String) {

    }

    override fun ResultSDListError(ex: Throwable) {

    }

    override fun onComponentClick(view: View) {
        activity._mActivity!!.onBackPressedSupport()
//        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(activity.activity, object : NavCallback() {
//            override fun onArrival(postcard: Postcard?) {
//                finish()
//            }
//        })
    }

    override fun onComponentFinish(view: View) {
    }

    override fun ResultPrivateLikeSuccess(it: String) {
        if (it.length < 10) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            return
        }
        if (pageSize == 1) {
            items.clear()
        }
        var entity = Gson().fromJson<DynamicsCategoryEntity>(it, DynamicsCategoryEntity::class.java)

//        var entity = Gson().fromJson<LikesEntity>(it, LikesEntity::class.java)
        entity.data!!.forEach {
            items.add(it)
        }
        activity.swp!!.isRefreshing= false
    }

    override fun ResultPrivateLikeError(ex: Throwable) {
    }


    lateinit var activity: MyLikeActivity
    fun inject(myLikeActivity: MyLikeActivity) {
        this.activity = myLikeActivity
        titleComponent.title.set(getString(R.string.my_like))
        titleComponent.callback = this
        titleComponent.rightText.set("")

        initDatas()
    }

    var titleComponent = TitleComponent()

    var listener  :SimpleClickListener =this

    var adapter = BindingRecyclerViewAdapter<DynamicsCategoryEntity.Dynamics>()

    var items = ObservableArrayList<DynamicsCategoryEntity.Dynamics>()

    var itemBinding = ItemBinding.of<DynamicsCategoryEntity.Dynamics>(BR.like_item_entity, R.layout.like_items_layout).bindExtra(BR.listener,listener)

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
        HttpRequest.instance.privateLikeList = this
        HttpRequest.instance.DynamicListResult = this
        var mao = HashMap<String, String>()
        mao.put("pageSize", pageSize.toString())
        mao.put("length", lenth.toString())
        HttpRequest.instance.getPrivateLikeList(mao)
    }
}