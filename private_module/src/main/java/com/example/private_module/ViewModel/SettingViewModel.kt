package com.example.private_module.ViewModel

import android.content.ComponentName
import android.content.Intent
import android.databinding.ObservableField
import android.provider.Settings
import android.view.View
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Component.TitleComponent
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.RE_LOGIN
import com.elder.zcommonmodule.USER_PHONE
import com.elder.zcommonmodule.Utils.SettingUtils
import com.example.private_module.Activity.SettingActivity
import com.example.private_module.R
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.OSUtil
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus


class SettingViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {


    var visibleType = ObservableField<Int>(0)


    override fun onComponentClick(view: View) {
        if (setting.entity == 0) {
            finish()
        } else {
            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
            finish()
        }
    }

    override fun onComponentFinish(view: View) {
    }

    lateinit var setting: SettingActivity

    fun inject(settingActivity: SettingActivity) {
        this.setting = settingActivity
        visibleType.set(settingActivity.entity)
        component.callback = this
        component.rightText.set("")
        component.title.set(getString(R.string.setting))
        component.arrowVisible.set(false)
    }

    var component = TitleComponent()

    fun onClick(view: View) {
        when (view.id) {
            R.id.user_request -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.USERREQUEST).navigation()
            }
            R.id.user_manager -> {
                var phone = PreferenceUtils.getString(context, USER_PHONE)
                if (phone == null) {
                    ARouter.getInstance().build(RouterUtils.ActivityPath.REGISTER).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 4).navigation()
                } else {
                    ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.USERMANAGER).navigation()
                }
            }
            R.id.exit_login -> {
                RxBus.default?.post("ExiLogin")
                PreferenceUtils.putBoolean(context, RE_LOGIN, true)

//                context.startService(Intent(context, LowLocationService::class.java).setAction("stop"))
                var pos = ServiceEven()
                pos.type = "HomeStop"
                RxBus.default?.post(pos)

                ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation(setting, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        setting.finish()
                    }
                })
            }
            R.id.god_model_request -> {
                val brand = android.os.Build.BRAND
                when (brand.toLowerCase()) {
                    "xiaomi" -> {
                        var intent = Intent()
                        intent.component = ComponentName("com.miui.powerkeeper",
                                "com.miui.powerkeeper.ui.HiddenAppsConfigActivity");
                        intent.putExtra("package_name", context.packageName);
                        intent.putExtra("package_label", getString(R.string.app_name))
                        setting.startActivity(intent)
                    }
                    "smartisan" -> {
                        var intent = Intent()
                        intent.component = ComponentName("com.android.settings",
                                "com.android.settings.applications.ManageApplicationsActivity");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        setting.startActivity(intent)
                    }
                    "vivo" -> {
                        var intent = Intent()
                        intent.component = ComponentName("com.iqoo.secure",
                                "com.iqoo.secure.MainGuideActivity")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        setting.startActivity(intent)
                    }
                    "oppo" -> {
                        setting.startActivity(Intent(Settings.ACTION_SETTINGS))
                    }
                    "huawei" -> {
                        setting.startActivity(Intent(Settings.ACTION_SETTINGS))
                    }
//                    com.android.settings----com.android.settings.applications.ManageApplicationsActivity
                }
            }
            R.id.start_self_request -> {
                SettingUtils.enterWhiteListSetting(context)
            }
            R.id.battery_request -> {
                OSUtil.ignoreBatteryOptimization(setting)
            }
        }
    }
}