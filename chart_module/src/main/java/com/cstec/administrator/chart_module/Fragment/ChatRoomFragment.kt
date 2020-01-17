package com.cstec.administrator.chart_module.Fragment

import android.databinding.ViewDataBinding
import com.cstec.administrator.chart_module.Base.CharBaseFragment
import com.cstec.administrator.chart_module.R
import com.zk.library.Base.BaseViewModel
import com.cstec.administrator.chart_module.BR


class ChatRoomFragment  :CharBaseFragment<ViewDataBinding,BaseViewModel>(){
    override fun initVariableId(): Int {
        return BR.char_room_viewmodel
    }

    override fun initContentView(): Int {
        return R.layout.fragment_charroom
    }






}