package com.cstec.administrator.chart_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.databinding.ActivityCharBinding
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.Base.ChartBaseActivity
import com.cstec.administrator.chart_module.ViewModel.ChartViewModel
import kotlinx.android.synthetic.main.activity_char.*

class ChatActivity  :ChartBaseActivity<ActivityCharBinding, ChartViewModel>(){

    override fun initVariableId(): Int {
        return BR.Chart_ViewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_char
    }

    override fun initViewModel(): ChartViewModel? {
        return ViewModelProviders.of(this)[ChartViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        main_view.initModule()
        main_view.setOnPageChangeListener(mViewModel)
        main_view.setOnClickListener(mViewModel)
        mViewModel?.inject(this)
    }
}