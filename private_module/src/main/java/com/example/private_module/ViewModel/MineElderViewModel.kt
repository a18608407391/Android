package com.example.private_module.ViewModel

import com.elder.zcommonmodule.Component.TitleComponent
import com.example.private_module.Activity.MineElderActivity
import com.example.private_module.R
import com.zk.library.Base.BaseViewModel

class MineElderViewModel : BaseViewModel() {

    lateinit var mineElderActivity: MineElderActivity
    var component = TitleComponent()

    fun inject(activity: MineElderActivity) {
        this.mineElderActivity = activity
        component.title.set("我的徽章")
        component.leftIcon.set(mineElderActivity.getDrawable(R.drawable.arrow_white))
        component.rightIcon.set(mineElderActivity.getDrawable(R.drawable.trans_bg))
        component.arrowVisible.set(false)
        component.rightVisibleType.set(false)

    }
}