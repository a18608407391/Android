package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.party_module.Activity.SubjectPartyActivity
import com.cstec.administrator.party_module.R
import com.zk.library.Base.BaseViewModel
import com.zk.library.Base.ItemViewModel
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.BasePartyItemModel
import com.cstec.administrator.party_module.ItemModel.ClockItemModel
import com.cstec.administrator.party_module.ItemModel.MoBrigadeItemModel
import com.cstec.administrator.party_module.ItemModel.SubjectItemModel
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.DialogUtils
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_subject_party.*
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class SubjectPartyViewModel : BaseViewModel(), TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {

    var msgCount = ObservableField(0)
    var onCreate = false

    var cur = 0L
    override fun onTabSelected(p0: TabLayout.Tab?) {
        if (!onCreate) {
            if (System.currentTimeMillis() - cur > 1000) {
                items[subject.type].load(true)
            }
            cur = System.currentTimeMillis()
        } else {
            items[p0!!.position].load(true)
        }
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {

    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {
    }

    lateinit var subject: SubjectPartyActivity
    fun inject(subjectPartyActivity: SubjectPartyActivity) {
        this.subject = subjectPartyActivity
    }

    var city = ObservableField<String>("湖南省")

    var items = ObservableArrayList<BasePartyItemModel>().apply {
        this.add(SubjectItemModel().ItemViewModel(this@SubjectPartyViewModel))
        this.add(MoBrigadeItemModel().ItemViewModel(this@SubjectPartyViewModel))
        this.add(ClockItemModel().ItemViewModel(this@SubjectPartyViewModel))
    }


    var adapter = BindingViewPagerAdapter<BasePartyItemModel>()

    var itemBinding = ItemBinding.of<BasePartyItemModel> { itemBinding, position, item ->
        when (position) {
            0 -> {
                itemBinding.set(BR.subject_item_model, R.layout.subject_item_model_layout)
            }
            1 -> {
                itemBinding.set(BR.mo_brigade_item_model, R.layout.mo_brigade_item_model_layout)
            }
            2 -> {
                itemBinding.set(BR.clock_item_model, R.layout.clock_item_model_layout)
            }
        }
    }
    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            RxBusEven.ACTIVE_WEB_GO_TO_APP -> {
                subject._mActivity!!.onBackPressedSupport()
            }
        }
    }
    var mTiltes = arrayOf("主题", "摩旅", "打卡")

    var pagerTitle = BindingViewPagerAdapter.PageTitles<BasePartyItemModel> { position, item ->
        mTiltes[position]
    }

    var province = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            city.set(t)
            items[subject.subject_viewPager.currentItem].load(true)
        }
    })

    fun onClick(view: View) {
        when (view.id) {
            R.id.arrow -> {
                subject!!._mActivity!!.onBackPressedSupport()
//                subject.returnBack()
            }
            R.id.title_ev -> {
                DialogUtils.showProviceDialog(subject!!.activity!!, province, "选择城市")
            }
            R.id.search_address -> {
                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        subject.location)

                bundle.putString(RouterUtils.PartyConfig.PARTY_CITY, city.get())
                startFragment(subject, RouterUtils.PartyConfig.SEARCH_PARTY, bundle)

            }
            R.id.notify_icon -> {
                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        subject.location)
                startFragment(subject, RouterUtils.Chat_Module.ActiveNotify_AC, bundle)
//                ARouter.getInstance().build(RouterUtils.Chat_Module.ActiveNotify_AC).withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION, subject.location).navigation()

            }
        }
    }
}