package com.cstec.administrator.party_module.ItemModel.ActiveDetail

import android.databinding.ObservableField
import com.elder.zcommonmodule.Service.HttpRequest
import com.zk.library.Base.BaseViewModel
import com.zk.library.Base.ItemViewModel
import org.cs.tec.library.binding.command.BindingAction
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


open class BasePartyItemModel : ItemViewModel<BaseViewModel>() {


    var refreshStatus = ObservableField(0)

    var refreshCommand = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            refresh()
        }
    })

   open fun refresh() {

    }

    override fun ItemViewModel(viewModel: BaseViewModel): BasePartyItemModel {
        this.viewModel = viewModel
        return this
    }

    fun initIntroduceData() {

    }


    fun initAlbumData() {

    }

    fun initRankingData() {

    }

    open fun load(flag: Boolean) {

    }


}