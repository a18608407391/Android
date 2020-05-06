package com.elder.amoski.ViewModel

import android.content.Intent
import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.chart_module.Fragment.MessageFragment
import com.cstec.administrator.social.Fragment.SocialFragment
import com.elder.amoski.Activity.HomeActivity
import com.elder.amoski.R
import com.elder.logrecodemodule.UI.ActivityFragment
import com.elder.logrecodemodule.UI.LogRecodeFragment
import com.elder.zcommonmodule.CALL_BACK_STATUS
import com.elder.zcommonmodule.DriverCancle
import com.elder.zcommonmodule.Entity.DriverDataStatus
import com.elder.zcommonmodule.Even.ActivityResultEven
import com.elder.zcommonmodule.Even.BooleanEven
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.Utils.Utils
import com.elder.zcommonmodule.Utils.getScaleUpAnimation
import com.example.private_module.UI.UserInfoFragment
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_home.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.http.NetworkUtil
import java.util.*


class HomeViewModel : BaseViewModel() {


    var statusHeight = ObservableField(0)


    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            RxBusEven.StatusBar -> {
                statusHeight.set(it.value as Int)
            }
        }
    }

    fun inject(homeActivity: HomeActivity) {

    }

}