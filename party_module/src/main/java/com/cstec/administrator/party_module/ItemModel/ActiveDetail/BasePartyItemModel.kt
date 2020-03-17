package com.cstec.administrator.party_module.ItemModel.ActiveDetail

import com.zk.library.Base.BaseViewModel
import com.zk.library.Base.ItemViewModel


open class BasePartyItemModel : ItemViewModel<BaseViewModel>() {


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

  open fun load(flag:Boolean){

    }

}