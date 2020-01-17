package com.example.drivermodule.Ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.maps.model.Marker
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.MapPointViewModel
import com.example.drivermodule.databinding.FragmentMapPointBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils


@Route(path = RouterUtils.MapModuleConfig.MAP_POINT_FR)
class MapPointFragment : BaseFragment<FragmentMapPointBinding, MapPointViewModel>() {

    override fun initContentView(): Int {
        return R.layout.fragment_map_point
    }

    override fun initVariableId(): Int {
        return BR.map_point_viewModel
    }

    override fun initData() {
        super.initData()
        viewModel?.inject(this)
    }

    fun onInfoWindowClick(it: Marker) {
        viewModel?.addPoint(it)
    }
}