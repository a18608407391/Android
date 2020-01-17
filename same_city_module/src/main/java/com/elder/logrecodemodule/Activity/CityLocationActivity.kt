package com.elder.logrecodemodule.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.logrecodemodule.BR
import com.elder.logrecodemodule.R
import com.elder.logrecodemodule.ViewModel.CityLocationViewModel
import com.elder.logrecodemodule.databinding.ActivityCityLocationBinding
import com.elder.zcommonmodule.Widget.CityPicker.CityPicker
import com.elder.zcommonmodule.Widget.CityPicker.CityPickerDialogFragment
import com.elder.zcommonmodule.Widget.CityPicker.adapter.OnPickListener
import com.elder.zcommonmodule.Widget.CityPicker.model.City
import com.elder.zcommonmodule.Widget.CityPicker.model.HotCity
import com.elder.zcommonmodule.Widget.CityPicker.model.LocateState
import com.elder.zcommonmodule.Widget.CityPicker.model.LocatedCity
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import java.util.ArrayList


@Route(path = RouterUtils.LogRecodeConfig.SAME_CITY_LOCATION_AC)
class CityLocationActivity : BaseActivity<ActivityCityLocationBinding, CityLocationViewModel>() {
    override fun initVariableId(): Int {
        return BR.city_model_entity
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_city_location
    }

    override fun initViewModel(): CityLocationViewModel? {
        return ViewModelProviders.of(this)[CityLocationViewModel::class.java]
    }

    override fun initData() {
        super.initData()

    }
}