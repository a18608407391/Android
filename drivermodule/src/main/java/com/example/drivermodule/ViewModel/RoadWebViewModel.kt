package com.example.drivermodule.ViewModel.RoadBook

import android.databinding.ObservableField
import android.util.Log
import android.view.View
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Component.TitleComponent
import com.example.drivermodule.Activity.RoadWebActivity
import com.example.drivermodule.R
import com.zk.library.Base.BaseViewModel
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class RoadWebViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {
    }

    lateinit var roadWebViewModel: RoadWebActivity

    var webUrl = ObservableField<String>("")

    var titleComponent = TitleComponent()
    fun inject(roadWebActivity: RoadWebActivity) {
        this.roadWebViewModel = roadWebActivity
        titleComponent.arrowVisible.set(false)
        titleComponent.rightText.set("")
        var url = ""
        if (roadWebActivity.type == 0) {
            titleComponent.title.set(getString(R.string.pass_point_detail))
            url = Base_URL + "AmoskiWebActivity/personalcenter/appRoute/pathPointDetail.html" + "?id=" + roadWebActivity.id
        } else if (roadWebActivity.type == 1) {
            titleComponent.title.set(getString(R.string.road_detail))
            url = Base_URL + "AmoskiWebActivity/personalcenter/appRoute/roadDetail.html" + "?id=" + roadWebActivity.id
        } else if (roadWebActivity.type == 2) {
            titleComponent.title.set(getString(R.string.road_simple_detail))
            url = Base_URL + "AmoskiWebActivity/personalcenter/appRoute/roadBookDetail.html" + "?id=" + roadWebActivity.id
        }else if(roadWebActivity.type == 3){
            titleComponent.title.set(getString(R.string.road_line_simple_detail))
            url = Base_URL + "AmoskiWebActivity/personalcenter/appRoute/lineroadDetail.html" + "?id=" + roadWebActivity.id
        }
        Log.e("result",url)
        titleComponent.callback = this

        webUrl.set(url)
    }


    var roadCommand = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
        }

    })


}