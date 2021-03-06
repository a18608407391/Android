package com.cstec.administrator.party_module.ViewModel

import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.party_module.Activity.EnrollListActivity
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Inteface.SimpleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.io.Serializable


class EnrollViewModel : BaseViewModel(), HttpInteface.PartySign, TitleComponent.titleComponentCallBack, SimpleClickListener {
    override fun onSimpleClick(entity: Any) {

        var entity = entity as EnrollEntity.Enroll

        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID,  entity.ID.toString())
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
        model.arguments = bundle
        activity.start(model)

//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity.ID.toString())
//                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
//                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                .withInt(RouterUtils.Chat_Module.Chat_TARGET_ID, activity.ids)
//                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 11).navigation()
    }

    override fun onComponentClick(view: View) {
        activity._mActivity!!.onBackPressedSupport()
    }

    override fun onComponentFinish(view: View) {
    }

    override fun getPartySignError(it: Throwable) {

    }

    override fun getPartySignSuccess(it: String) {
        if (it.isNullOrEmpty()) {
            return
        }
        var entity = Gson().fromJson<EnrollEntity>(it, EnrollEntity::class.java)
        entity.data!!.forEach {
            items.add(it)
        }
    }

    lateinit var activity: EnrollListActivity
    fun inject(enrollListActivity: EnrollListActivity) {
        this.activity = enrollListActivity
        titleComponent.title.set("报名列表")
        titleComponent.arrowVisible.set(false)
        titleComponent.rightText.set("")
        titleComponent.callback = this
        initData()
    }

    var start = 1
    var pageSize = 10
    private fun initData() {
        HttpRequest.instance.partySign = this
        var map = HashMap<String, String>()
        map["id"] = activity.ids.toString()
        map["start"] = start.toString()
        map["pageSize"] = pageSize.toString()
        HttpRequest.instance.getPartySign(map)
    }

    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            if (t < pageSize * start) {
                return
            } else {
                start++
                initData()
            }
        }
    })


    var titleComponent = TitleComponent()

    var adapter = BindingRecyclerViewAdapter<EnrollEntity.Enroll>()

    var items = ObservableArrayList<EnrollEntity.Enroll>()

    var simpleClick: SimpleClickListener = this

    var itemBinding = ItemBinding.of<EnrollEntity.Enroll>(BR.enroll_model, R.layout.enroll_item_layout)
            .bindExtra(BR.listener, simpleClick)


    class EnrollEntity : Serializable {
        var data: ArrayList<Enroll>? = null

        class Enroll : Serializable {
            var NAME: String? = null
            var CREATE_DATA: String? = null
            var ID = 0
            var HEAD_IMG_FILE: String? = null
            var RN = 0
            var SUM_COUNT: String? = null
        }
    }


}