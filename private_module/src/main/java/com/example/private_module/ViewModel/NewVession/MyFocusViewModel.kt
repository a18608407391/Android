package com.example.private_module.ViewModel.NewVession

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
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.FansEntity
import com.elder.zcommonmodule.Entity.FocusEntity
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Inteface.DoubleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.private_module.Activity.NewVession.MyFocusActivity
import com.example.private_module.Activity.inteface.FansItemClick
import com.example.private_module.BR
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_myfans.*
import kotlinx.android.synthetic.main.activity_myfocus.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class MyFocusViewModel : BaseViewModel(), HttpInteface.PrivateFocusList, TitleComponent.titleComponentCallBack, FansItemClick, HttpInteface.SocialDynamicsFocus, SwipeRefreshLayout.OnRefreshListener, DoubleClickListener {
    override fun onItemClick(en: Any) {
        var entity = en as FansEntity.FansBean
        var dialog = DialogUtils.createNomalDialog(myFocusActivity.activity!!, getString(R.string.cancle_focus_warm), getString(R.string.cancle), getString(R.string.confirm))
        dialog.setOnBtnClickL(OnBtnClickL {
            dialog.dismiss()
        }, OnBtnClickL {
            cur = entity
            myFocusActivity.showProgressDialog(getString(R.string.http_focus_request))
            var map = java.util.HashMap<String, String>()
            map["fansMemberId"] = entity.fansMemberId.toString()
            HttpRequest.instance.getDynamicsFocus(map)
            dialog.dismiss()
        })
        dialog.show()
    }

    override fun onImgClick(en: Any) {
        var entity = en as FansEntity.FansBean
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, myFocusActivity.location)
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID,  entity.fansMemberId)
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
        model.arguments = bundle
        myFocusActivity.start(model)

//
//        ARouter.getInstance()
//                .build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(myFocusActivity.location?.latitude!!, myFocusActivity.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, myFocusActivity.location?.aoiName!!, myFocusActivity.location!!.poiName))
//                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity.fansMemberId)
//                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 6)
//                .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
//                .navigation()


    }

    override fun onRefresh() {
        pageSize = 1
        lenth = 20
        initDatas()

        CoroutineScope(uiContext).launch {
            delay(10000)
            myFocusActivity.swp!!.isRefreshing = false
        }
    }

    override fun ResultFocusSuccess(it: String) {
        myFocusActivity.dismissProgressDialog()
        items.remove(cur)
    }

    override fun ResultFocusError(ex: Throwable) {
    }


    var cur: FansEntity.FansBean? = null
    override fun FansClick(entity: FansEntity.FansBean) {
        var dialog = DialogUtils.createNomalDialog(myFocusActivity.activity!!, getString(R.string.cancle_focus_warm), getString(R.string.cancle), getString(R.string.confirm))
        dialog.setOnBtnClickL(OnBtnClickL {
            dialog.dismiss()
        }, OnBtnClickL {
            cur = entity
            myFocusActivity.showProgressDialog(getString(R.string.http_focus_request))
            var map = java.util.HashMap<String, String>()
            map["fansMemberId"] = entity.fansMemberId.toString()
            HttpRequest.instance.getDynamicsFocus(map)
            dialog.dismiss()
        })
        dialog.show()
    }

    override fun onComponentClick(view: View) {
        myFocusActivity._mActivity!!.onBackPressedSupport()
//        if (myFocusActivity.type == 1) {
//            if (destroyList!!.contains("DriverHomeActivity")) {
//                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                        .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, myFocusActivity.id)
//                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, myFocusActivity.location)
//                        .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 0)
//                        .navigation(myFocusActivity.activity!!, object : NavCallback() {
//                            override fun onArrival(postcard: Postcard?) {
//                                finish()
//                            }
//                        })
//            } else {
//                finish()
//            }
//        } else {
//            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(myFocusActivity.activity!!, object : NavCallback() {
//                override fun onArrival(postcard: Postcard?) {
//                    finish()
//                }
//            })
//        }
    }

    override fun onComponentFinish(view: View) {
    }

    override fun ResultPrivateFocusSuccess(it: String) {

        var fans = Gson().fromJson<FansEntity>(it, FansEntity::class.java)
        if (pageSize == 1) {
            items.clear()
        }

        fans.data!!.forEach {
            items.add(it)
        }
        myFocusActivity.swp!!.isRefreshing = false
    }

    override fun ResultPrivateFocusError(ex: Throwable) {

    }

    lateinit var myFocusActivity: MyFocusActivity
    fun inject(myFocusActivity: MyFocusActivity) {
        this.myFocusActivity = myFocusActivity
        titleComponent.title.set("我的关注")
        titleComponent.callback = this
        titleComponent.rightText.set("")
        initDatas()
    }

    var listener: DoubleClickListener = this
    var titleComponent = TitleComponent()

    var adapter = BindingRecyclerViewAdapter<FansEntity.FansBean>()

    var items = ObservableArrayList<FansEntity.FansBean>()

    var itemBinding = ItemBinding.of<FansEntity.FansBean>(BR.focus_item_entity, R.layout.focus_items_layout).bindExtra(BR.listener, listener)


    var pageSize = 1

    var lenth = 20

    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            if (t <lenth*pageSize) {
                return
            }else{
                pageSize++
                initDatas()
            }
        }
    })


    private fun initDatas() {
        HttpRequest.instance.DynamicFocusResult = this
        HttpRequest.instance.privateGetFocusList = this
        var mao = HashMap<String, String>()
        mao.put("pageSize", pageSize.toString())
        mao.put("length", lenth.toString())
        HttpRequest.instance.getPrivateFocusList(mao)
    }
}