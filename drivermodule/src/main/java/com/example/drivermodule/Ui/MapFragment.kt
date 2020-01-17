package com.example.drivermodule.Ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.example.drivermodule.databinding.FragmentMapBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils


@Route(path = RouterUtils.MapModuleConfig.MAP_FR)
class MapFragment : BaseFragment<FragmentMapBinding, MapFrViewModel>() {
    override fun initContentView(): Int {
        return R.layout.fragment_map
    }

    override fun initVariableId(): Int {
        return BR.map_fr_Model
    }

    override fun initData() {
        super.initData()
        viewModel?.inject(this)
    }
}