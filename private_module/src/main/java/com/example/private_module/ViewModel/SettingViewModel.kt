package com.example.private_module.ViewModel

import android.content.ComponentName
import android.content.Intent
import android.databinding.ObservableField
import android.provider.Settings
import android.support.annotation.NonNull
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.RE_LOGIN
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.USER_PHONE
import com.elder.zcommonmodule.USER_TOKEN
import com.elder.zcommonmodule.Utils.SettingUtils
import com.example.private_module.Activity.SettingActivity
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.OSUtil
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus


class SettingViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack, HttpInteface.ExitLogin {
    override fun ExitLoginSuccess(it: String) {
        setting.dismissProgressDialog()
        RxBus.default?.post("ExiLogin")
        PreferenceUtils.putBoolean(context, RE_LOGIN, true)
        PreferenceUtils.putString(context, USER_TOKEN, null)
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

    override fun ExitLoginError(ex: Throwable) {

        Toast.makeText(context, "退出登录错误", Toast.LENGTH_SHORT).show()
        setting.dismissProgressDialog()
    }


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
                exit()
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
                        try {
                            showActivity("com.coloros.phonemanager")
                        } catch (e1: Exception) {
                            try {
                                showActivity("com.oppo.safe")
                            } catch (e2: Exception) {
                                try {
                                    showActivity("com.coloros.oppoguardelf")
                                } catch (e3: Exception) {
                                    showActivity("com.coloros.safecenter")
                                }
                            }
                        }
                    }
                    "huawei" -> {
                        try {
                            showActivity("com.huawei.systemmanager",
                                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")
                        } catch (e: Exception) {
                            showActivity("com.huawei.systemmanager",
                                    "com.huawei.systemmanager.optimize.bootstart.BootStartActivity")
                        }

                    }
                    "honor" -> {
                        try {
                            showActivity("com.huawei.systemmanager",
                                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")
                        } catch (e: Exception) {
                            showActivity("com.huawei.systemmanager",
                                    "com.huawei.systemmanager.optimize.bootstart.BootStartActivity")
                        }
                    }//                    com.android.settings----com.android.settings.applications.ManageApplicationsActivity
                    "meizu" -> {
                        showActivity("com.meizu.safe")
                    }
                    "samsung" -> {
                        try {
                            showActivity("com.samsung.android.sm_cn")
                        } catch (e: Exception) {
                            showActivity("com.samsung.android.sm")
                        }
                    }
                    "letv" -> {
                        showActivity("com.letv.android.letvsafe",
                                "com.letv.android.letvsafe.AutobootManageActivity");
                    }
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

    private fun showActivity(@NonNull packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)
    }

    /**
     * 跳转到指定应用的指定页面
     */
    private fun showActivity(@NonNull packageName: String, @NonNull activityDir: String) {
        val intent = Intent()
        intent.component = ComponentName(packageName, activityDir)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }


    fun exit() {
        setting.showProgressDialog("正在退出登录中......")
        HttpRequest.instance.exit = this
        HttpRequest.instance.exitLogin(HashMap())
    }
}