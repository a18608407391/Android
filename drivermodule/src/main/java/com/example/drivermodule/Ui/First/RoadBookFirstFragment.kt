package com.example.drivermodule.Ui.First

import android.databinding.ViewDataBinding
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel


class RoadBookFirstFragment : BaseFragment<ViewDataBinding, BaseViewModel>() {


    override fun initContentView(): Int {
        return R.layout.road_book_first_fr
    }

    override fun initVariableId(): Int {
        return BR.first_fr_model
    }

}