package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.cstec.administrator.party_module.R
import com.zk.library.Base.BaseActivity
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.ViewModel.SubjectPartyViewModel
import com.cstec.administrator.party_module.databinding.ActivitySubjectPartyBinding

class SubjectPartyActivity : BaseActivity<ActivitySubjectPartyBinding, SubjectPartyViewModel>() {
    override fun initVariableId(): Int {
        return BR.subject_party_model
    }
    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_subject_party
    }
    override fun initViewModel(): SubjectPartyViewModel? {
        return ViewModelProviders.of(this)[SubjectPartyViewModel::class.java]
    }
    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }
}