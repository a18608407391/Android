package com.example.private_module.ViewModel

import android.view.View
import android.widget.Toast
import com.elder.zcommonmodule.Component.TitleComponent
import com.example.private_module.Activity.NotifyCationActivity
import com.example.private_module.R
import com.zk.library.Base.BaseViewModel
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString


class NotifyCationViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {
        Toast.makeText(context, getString(R.string.notdata), Toast.LENGTH_SHORT).show()
    }


    lateinit var notifyCationActivity: NotifyCationActivity
    fun inject(notifyCationActivity: NotifyCationActivity) {
        this.notifyCationActivity = notifyCationActivity
        component.callback = this
        component.arrowVisible.set(false)
        component.rightText.set(getString(R.string.remove))
        component.title.set(getString(R.string.system_notify))
    }


    var component = TitleComponent()


}