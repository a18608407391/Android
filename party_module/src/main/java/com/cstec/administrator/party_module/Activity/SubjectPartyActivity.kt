package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.util.Log
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.party_module.R
import com.zk.library.Base.BaseActivity
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.ViewModel.SubjectPartyViewModel
import com.cstec.administrator.party_module.databinding.ActivitySubjectPartyBinding
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Utils.Utils
import com.zk.library.Base.BaseFragment
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_subject_party.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.getStatusBarHeight
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions


@Route(path = RouterUtils.PartyConfig.SUBJECT_PARTY)
class SubjectPartyActivity : BaseFragment<ActivitySubjectPartyBinding, SubjectPartyViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_subject_party
    }

    @Autowired(name = RouterUtils.PartyConfig.PARTY_LOCATION)
    @JvmField
    var location: Location? = null

    @Autowired(name = RouterUtils.PartyConfig.PARTY_CITY)
    @JvmField
    var city: String? = null


    @Autowired(name = RouterUtils.PartyConfig.Party_SELECT_TYPE)
    @JvmField
    var type: Int = 0

    override fun initVariableId(): Int {
        return BR.subject_party_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_subject_party
//    }
//
//    override fun initViewModel(): SubjectPartyViewModel? {
//        return ViewModelProviders.of(this)[SubjectPartyViewModel::class.java]
//    }

    override fun initData() {
        super.initData()
        RxBus.default!!.post(RxBusEven.getInstance(RxBusEven.StatusBar, getStatusBarHeight()))
        Utils.setStatusTextColor(true, activity)
        location = arguments!!.getSerializable(RouterUtils.PartyConfig.PARTY_LOCATION) as Location?
        city = arguments!!.getString(RouterUtils.PartyConfig.PARTY_CITY)
        type = arguments!!.getInt(RouterUtils.PartyConfig.Party_SELECT_TYPE)
        subject_viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(subject_TabLayout))
        subject_TabLayout.setupWithViewPager(subject_viewPager)
        subject_TabLayout.addOnTabSelectedListener(viewModel!!)
        CoroutineScope(uiContext).launch {
            delay(200)
            subject_TabLayout.getTabAt(type)!!.select()
        }
        viewModel?.inject(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        RxBus.default!!.post(RxBusEven.getInstance(RxBusEven.StatusBar, 0))
    }

//    fun returnBack() {
//        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this, object : NavCallback() {
//            override fun onArrival(postcard: Postcard?) {
//                finish()
//            }
//        })
//    }

//    override fun doPressBack() {
//        super.doPressBack()
//        returnBack()
//    }
}