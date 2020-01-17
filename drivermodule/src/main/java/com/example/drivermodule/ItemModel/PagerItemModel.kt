package com.example.drivermodule.ItemModel

import android.databinding.ObservableField
import com.example.drivermodule.Entity.RoadBook.BottomHoriDatas
import com.example.drivermodule.ViewModel.RoadBookViewModel
import com.zk.library.Base.ItemViewModel


class PagerItemModel : ItemViewModel<RoadBookViewModel>() {

    var title = ObservableField<String>("")
    var isCheck = ObservableField<Boolean>(false)
    fun BottomhoriClick(view: BottomHoriDatas) {

    }

}