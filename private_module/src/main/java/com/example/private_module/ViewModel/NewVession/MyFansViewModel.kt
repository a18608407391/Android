package com.example.private_module.ViewModel.NewVession

import android.databinding.ObservableArrayList
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.FansEntity
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Inteface.DoubleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.Dialog.NormalDialog
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.private_module.Activity.NewVession.MyFansActivity
import com.example.private_module.Activity.inteface.FansItemClick
import com.example.private_module.BR
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_myfans.*
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


class MyFansViewModel : BaseViewModel(), HttpInteface.PrivateFansList, TitleComponent.titleComponentCallBack, FansItemClick, HttpInteface.SocialDynamicsFocus, SwipeRefreshLayout.OnRefreshListener, DoubleClickListener {
    override fun onItemClick(en: Any) {
        var entity = en as FansEntity.FansBean
        if (entity.followed == 0) {
            curItem = entity
            myFansActivity.showProgressDialog(getString(R.string.http_focus_request))
            var map = java.util.HashMap<String, String>()
            map["fansMemberId"] = entity.memberId.toString()
            HttpRequest.instance.getDynamicsFocus(map)
        } else {
            var dialog = DialogUtils.createNomalDialog(myFansActivity, getString(R.string.cancle_focus_warm), getString(R.string.cancle), getString(R.string.confirm))
            dialog.setOnBtnClickL(OnBtnClickL {
                dialog.dismiss()
            }, OnBtnClickL {
                curItem = entity
                myFansActivity.showProgressDialog(getString(R.string.http_focus_request))
                var map = HashMap<String, String>()
                map["fansMemberId"] = entity.memberId.toString()
                HttpRequest.instance.getDynamicsFocus(map)
                dialog.dismiss()
            })
            dialog.show()
        }
    }

    override fun onImgClick(en: Any) {
        var entity = en as FansEntity.FansBean
        ARouter.getInstance()
                .build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(myFansActivity.location?.latitude!!, myFansActivity.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, myFansActivity.location?.aoiName!!, myFansActivity.location!!.poiName))
                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity.memberId)
                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 5)
                .navigation()
    }

    override fun onRefresh() {
        pageSize = 1
        lenth = 20
        initDatas()
        CoroutineScope(uiContext).launch {
            delay(10000)
            myFansActivity.fans_swipe.isRefreshing = false
        }
    }

    override fun ResultFocusSuccess(it: String) {
        myFansActivity.dismissProgressDialog()
        if (curItem?.followed == 0) {
            curItem?.followed = 1
        } else {
            curItem?.followed = 0
        }
        var index = items.indexOf(curItem)
        items.set(index, curItem)
        Toast.makeText(context, "关注成功！", Toast.LENGTH_SHORT).show()
    }

    override fun ResultFocusError(ex: Throwable) {
        myFansActivity.dismissProgressDialog()
    }


    var curItem: FansEntity.FansBean? = null
    override fun FansClick(entity: FansEntity.FansBean) {
        if (entity.followed == 0) {
            curItem = entity
            myFansActivity.showProgressDialog(getString(R.string.http_focus_request))
            var map = java.util.HashMap<String, String>()
            map["fansMemberId"] = entity.memberId.toString()
            HttpRequest.instance.getDynamicsFocus(map)
        } else {
            var dialog = DialogUtils.createNomalDialog(myFansActivity, getString(R.string.cancle_focus_warm), getString(R.string.cancle), getString(R.string.confirm))
            dialog.setOnBtnClickL(OnBtnClickL {
                dialog.dismiss()
            }, OnBtnClickL {
                curItem = entity
                myFansActivity.showProgressDialog(getString(R.string.http_focus_request))
                var map = HashMap<String, String>()
                map["fansMemberId"] = entity.memberId.toString()
                HttpRequest.instance.getDynamicsFocus(map)
                dialog.dismiss()
            })
            dialog.show()
        }
    }

    override fun onComponentClick(view: View) {
        if (myFansActivity.type == 1) {
            if (AppManager.activityStack!!.size == 1) {
                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                        .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, myFansActivity.id)
                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, myFansActivity.location)
                        .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 0)
                        .navigation(myFansActivity, object : NavCallback() {
                            override fun onArrival(postcard: Postcard?) {
                                finish()
                            }
                        })
            } else {
                finish()
            }
        } else {
            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(myFansActivity, object : NavCallback() {
                override fun onArrival(postcard: Postcard?) {
                    finish()
                }
            })
        }
    }

    override fun onComponentFinish(view: View) {
    }

    override fun ResultPrivateFansSuccess(it: String) {
        var fans = Gson().fromJson<FansEntity>(it, FansEntity::class.java)
        if (pageSize == 1) {
            items.clear()
        }
        fans.data!!.forEach {
            items.add(it)
        }
        myFansActivity.fans_swipe.isRefreshing = false

        Log.e("result", "粉丝数据" + it)
    }

    override fun ResultPrivateFansError(ex: Throwable) {
    }

    var titleComponent = TitleComponent()


    lateinit var myFansActivity: MyFansActivity
    fun inject(myFansActivity: MyFansActivity) {
        this.myFansActivity = myFansActivity
        titleComponent.title.set(getString(R.string.my_fans))
        titleComponent.rightText.set("")
        titleComponent.callback = this

        initDatas()
    }

    var listener: DoubleClickListener = this

    var adapter = BindingRecyclerViewAdapter<FansEntity.FansBean>()

    var items = ObservableArrayList<FansEntity.FansBean>()

    var itemBinding = ItemBinding.of<FansEntity.FansBean>(BR.fans_item_entity, R.layout.fans_items_layout).bindExtra(BR.listener, this.listener)


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
        HttpRequest.instance.DynamicFocusResult = this
        HttpRequest.instance.privateGetFansList = this
        var mao = HashMap<String, String>()
        mao.put("pageSize", pageSize.toString())
        mao.put("length", lenth.toString())
        HttpRequest.instance.getPrivateFansList(mao)
    }


}