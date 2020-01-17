package com.elder.logrecodemodule.ViewModel

import com.elder.logrecodemodule.Activity.CityLocationActivity
import com.zk.library.Base.BaseViewModel
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class CityLocationViewModel : BaseViewModel() {


    lateinit var cityLocationActivity: CityLocationActivity
    fun inject(cityLocationActivity: CityLocationActivity) {
        this.cityLocationActivity = cityLocationActivity


    }


    var onEditTextChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {

        }
    })

}