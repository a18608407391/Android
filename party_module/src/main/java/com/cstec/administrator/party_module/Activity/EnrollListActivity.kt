package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.EnrollViewModel
import com.cstec.administrator.party_module.databinding.ActivityEnrollBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils


class EnrollListActivity : BaseActivity<ActivityEnrollBinding, EnrollViewModel>() {

    @Autowired(name = RouterUtils.PartyConfig.PARTY_ID)
    @JvmField
    var id: String? = null
    override fun initVariableId(): Int {
        return BR.enroll_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_enroll
    }

    override fun initViewModel(): EnrollViewModel? {
        return ViewModelProviders.of(this)[EnrollViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }
}