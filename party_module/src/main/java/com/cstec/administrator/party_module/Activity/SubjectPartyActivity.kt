package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TabLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.party_module.R
import com.zk.library.Base.BaseActivity
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.ViewModel.SubjectPartyViewModel
import com.cstec.administrator.party_module.databinding.ActivitySubjectPartyBinding
import com.elder.zcommonmodule.Entity.Location
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_subject_party.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions


@Route(path = RouterUtils.PartyConfig.SUBJECT_PARTY)
class SubjectPartyActivity : BaseActivity<ActivitySubjectPartyBinding, SubjectPartyViewModel>() {
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

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_subject_party
    }

    override fun initViewModel(): SubjectPartyViewModel? {
        return ViewModelProviders.of(this)[SubjectPartyViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        subject_viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(subject_TabLayout))
        subject_TabLayout.setupWithViewPager(subject_viewPager)
        subject_TabLayout.addOnTabSelectedListener(mViewModel!!)
        CoroutineScope(uiContext).launch {
            delay(200)
            subject_TabLayout.getTabAt(type)!!.select()
        }
        var s = RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "ActiveWebGotoApp") {
                finish()
            }
        }
        RxSubscriptions.add(s)
        mViewModel?.inject(this)
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }
}