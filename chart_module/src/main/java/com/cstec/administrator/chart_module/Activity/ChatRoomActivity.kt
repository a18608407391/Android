package com.cstec.administrator.chart_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.ViewModel.ChatRoomViewModel
import com.cstec.administrator.chart_module.databinding.ActivityChartRoomBinding
import com.zk.library.Base.BaseActivity


class ChatRoomActivity : BaseActivity<ActivityChartRoomBinding, ChatRoomViewModel>(), View.OnClickListener {
    override fun onClick(v: View?) {


    }

    override fun initVariableId(): Int {

        return BR.char_room_viewmodel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_chart_room
    }


    override fun initViewModel(): ChatRoomViewModel? {
        return ViewModelProviders.of(this)[ChatRoomViewModel::class.java]
    }


    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }
}