package com.example.drivermodule.Ui.First

import android.databinding.ViewDataBinding
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel


class RoadBookFirstWebFragment : BaseFragment<ViewDataBinding, BaseViewModel>() {
    override fun initContentView(): Int {

        return R.layout.roadbook_first_web
    }

    override fun initVariableId(): Int {
        return BR.first_web_model
    }

}