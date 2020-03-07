package com.example.drivermodule.ViewModel

import android.app.AlertDialog
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.PersonDatas
import com.elder.zcommonmodule.Entity.SoketBody.BasePacketReceive
import com.elder.zcommonmodule.Entity.SoketBody.SocketDealType
import com.elder.zcommonmodule.Entity.SoketBody.Soket
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonInfo
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.getImageUrl
import com.example.drivermodule.Activity.Team.TeamSettingActivity
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.google.gson.Gson
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.ioContext
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.http.NetworkUtil
import java.text.SimpleDateFormat
import java.util.*


class TeamSettingViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).navigation()
        finish()
    }

    override fun onComponentFinish(view: View) {

    }

    lateinit var teamSettingActivity: TeamSettingActivity
    var teamName = ObservableField<String>()
    var teamerName = ObservableField<String>()
    var roleName = ObservableField<String>()
    var bottomText = ObservableField<String>(getString(R.string.exit_team))
    var date = ObservableField<String>()
    var id: String = ""
    var teamerVisible = ObservableField<Boolean>(false)

    companion object {
        var REQUEST_TEAM_NAME = 121
        var REQUEST_TEAM_MANAGER = 122
        var REQUEST_TEAM_DELETE = 123
        var REQUEST_TEAM_PASS = 124
    }

    var dispose: Disposable? = null

    fun inject(teamSettingActivity: TeamSettingActivity) {
        this.teamSettingActivity = teamSettingActivity
        dispose = RxBus.default?.toObservable(BasePacketReceive::class.java)?.subscribe {
            if (it.type == 1006) {
                teamSettingActivity.info = Gson().fromJson<TeamPersonInfo>(it.body, TeamPersonInfo::class.java)
                CoroutineScope(uiContext).launch {
                    validate()
                }
            }
        }
        RxSubscriptions.add(dispose)
        component.title.set(getString(R.string.team_setting))
        component.rightText.set("")
        PreferenceUtils.getString(context, USERID)
        component.arrowVisible.set(false)
        component.setCallBack(this)
        validate()
    }

    fun validate() {
        id = PreferenceUtils.getString(context, USERID)
        if (teamSettingActivity.info?.redisData?.teamer.toString() == id) {
            teamerVisible.set(true)
        } else {
            teamerVisible.set(false)
        }
        if (teamSettingActivity.info?.redisData?.teamer.toString() == id) {
            bottomText.set(getString(R.string.delete_team))
        } else {
            bottomText.set(getString(R.string.exit_team))
        }
        if (teamSettingActivity?.info != null) {
            teamName.set(teamSettingActivity?.info?.redisData?.teamName)
            items.clear()
            if (teamSettingActivity.info?.redisData != null) {
                var simple = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                var d = Date(teamSettingActivity.info?.redisData?.validEndTime!!)
                date.set(simple.format(d))
            }
            items.apply {
                this?.add(PersonDatas(ObservableField("0"), ObservableField(org.cs.tec.library.Base.Utils.getString(R.string.invite)), ObservableField(""), ObservableField(""), ObservableField(false), ObservableField(Color.TRANSPARENT), 0))
                if (teamSettingActivity.info?.redisData?.teamer.toString() == id) {
                    this?.add(PersonDatas(ObservableField("1"), ObservableField(org.cs.tec.library.Base.Utils.getString(R.string.remove)), ObservableField(""), ObservableField(""), ObservableField(false), ObservableField(Color.TRANSPARENT), 1))
                }
            }
            teamName.set(teamSettingActivity.info?.redisData?.teamName)
            teamSettingActivity?.info?.redisData?.dtoList?.forEachIndexed { index, it ->
                var name = ""
                if (it.teamRoleColor == null || it.teamRoleColor.isEmpty()) {
                    it.teamRoleColor = "2D3138"
                }
                if (it.memberName.trim().isEmpty()) {
                    name = getString(R.string.secret_name)
                } else {
                    name = it.memberName
                }
                if (it.memberId.toString() == id) {
                    roleName.set(it.teamRoleName)
                    teamerName.set(it.memberName)
                    items.add(PersonDatas(ObservableField(getImageUrl(it.memberHeaderUrl)), ObservableField(name), ObservableField(it.teamRoleName), ObservableField(it.memberId.toString()), ObservableField(true), ObservableField(Color.parseColor("#" + it.teamRoleColor)), index+2))
                } else {
                    items.add(PersonDatas(ObservableField(getImageUrl(it.memberHeaderUrl)), ObservableField(name), ObservableField(it.teamRoleName), ObservableField(it.memberId.toString()), ObservableField(false), ObservableField(Color.parseColor("#" + it.teamRoleColor)), index+2))
                }
            }
            items.sortBy {
                it.position
            }
        }
    }


    var component = TitleComponent()
    var items = ObservableArrayList<PersonDatas>()
    var itemBinding = ItemBinding.of<PersonDatas> { itemBinding, position, item ->
        itemBinding.set(BR.team_items, R.layout.team_setting_ry_item).bindExtra(BR.position, position).bindExtra(BR.team_items_model, this@TeamSettingViewModel)
    }

    var shareDialog: AlertDialog? = null
    var adapter = BindingRecyclerViewAdapter<PersonDatas>()
    var CurrentClickTime: Long = 0


    fun onItemClick(datas: PersonDatas, position: Int) {
        if (System.currentTimeMillis() - CurrentClickTime < 500) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        if (position == 1) {
            var id = PreferenceUtils.getString(context, USERID)
            if (teamSettingActivity.info?.redisData?.teamer.toString() == id) {
                ARouter.getInstance().build(RouterUtils.TeamModule.DELETE).withSerializable(RouterUtils.TeamModule.TEAM_INFO, teamSettingActivity.info).navigation(teamSettingActivity, REQUEST_TEAM_DELETE)
            }
        } else if (position == 0) {
            if (teamSettingActivity?.info?.redisData?.dtoList?.size == teamSettingActivity?.info?.redisData?.teamMaxCount) {
                Toast.makeText(context, getString(R.string.max_member) + teamSettingActivity?.info?.redisData?.teamMaxCount + "人", Toast.LENGTH_SHORT).show()
            } else {
                if (shareDialog == null) {
                    shareDialog = DialogUtils.createShareDialog(teamSettingActivity, "", "")
                } else if (shareDialog != null && !shareDialog!!.isShowing) {
                    shareDialog!!.show()
                }
                shareDialog?.findViewById<TextView>(R.id.share_frend)?.setOnClickListener {
                    if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                        Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                    } else {
                        shareWx(SendMessageToWX.Req.WXSceneSession)
                        shareDialog!!.dismiss()
                    }
                }
                shareDialog?.findViewById<TextView>(R.id.share_frendQ)?.setOnClickListener {
                    if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                        Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                    } else {
                        shareWx(SendMessageToWX.Req.WXSceneTimeline)
                        shareDialog!!.dismiss()
                    }
                }
            }
        }
    }


    fun shareWx(type: Int) {

        if (teamSettingActivity.info == null) {
            return
        }

        CoroutineScope(ioContext).async {
            var file = Glide.with(teamSettingActivity)
                    .load(getImageUrl(teamSettingActivity.info?.redisData?.dtoList!![0].memberHeaderUrl))
                    .downloadOnly(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
            var files = file.get()
            if (files != null) {
                var bitmap = BitmapFactory.decodeFile(files.path)
                var newBitmap = ConvertUtils.compressByQuality(bitmap, 32000, true)
                var wx = WXWebpageObject()
                wx.webpageUrl = "http://amoski.net/yomoy/index.html?platform=android&teamCode=" + teamSettingActivity.info?.redisData?.teamCode
                var msg = WXMediaMessage(wx)
                msg.title = getString(R.string.team_code) + teamSettingActivity.info?.redisData?.teamCode
                msg.description = getString(R.string.share_description_by_team)
                msg.thumbData = newBitmap
                var req = SendMessageToWX.Req()
                req.transaction = "img" + System.currentTimeMillis()
                req.message = msg
                req.scene = type
                BaseApplication.getInstance().mWxApi.sendReq(req)
            }
        }
    }

    fun onClick(view: View) {
        if (System.currentTimeMillis() - CurrentClickTime < 500) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        when (view.id) {
            R.id.teamer_setting_click -> {
                ARouter.getInstance().build(RouterUtils.TeamModule.TEAMER_PASS).withSerializable(RouterUtils.TeamModule.TEAM_INFO, teamSettingActivity.info).navigation(teamSettingActivity, REQUEST_TEAM_PASS)
            }
            R.id.manager_click -> {
                if (teamSettingActivity.info?.redisData?.teamer.toString() == id) {
                    ARouter.getInstance().build(RouterUtils.TeamModule.MANAGER).withSerializable(RouterUtils.TeamModule.TEAM_INFO, teamSettingActivity.info).navigation(teamSettingActivity, REQUEST_TEAM_MANAGER)
                }
            }
            R.id.team_name_click -> {
                if (teamSettingActivity.info?.redisData?.teamer.toString() == id) {
                    ARouter.getInstance().build(RouterUtils.TeamModule.CHANGE_NAME).withSerializable(RouterUtils.TeamModule.TEAM_INFO, teamSettingActivity.info).navigation(teamSettingActivity, REQUEST_TEAM_NAME)
                }
            }
            R.id.exit_team -> {
                if (NetworkUtil.isNetworkAvailable(teamSettingActivity)) {
                    if (teamSettingActivity.info?.redisData?.teamer.toString() == id) {
                        var dialog = DialogUtils.createNomalDialog(teamSettingActivity, getString(R.string.exit_team_warm), getString(R.string.cancle), getString(R.string.confirm))
                        dialog.setOnBtnClickL(OnBtnClickL {
                            dialog.dismiss()
                        }, OnBtnClickL {
                            var so = Soket()
                            so.type = SocketDealType.DISMISSTEAM.code
                            so.teamCode = teamSettingActivity.info?.redisData?.teamCode
                            so.teamId = teamSettingActivity.info?.redisData?.id.toString()
                            so.userId = id
                            dialog.dismiss()
                            CoroutineScope(uiContext).launch {
                                delay(1000)
                                RxBus.default?.post("showProgress")
//                                com.elder.amoski.Service.Mina.SessionManager.getInstance().writeToServer(Gson().toJson(so) + "\\r\\n")
                                var pos = ServiceEven()
                                pos.type = "sendData"
                                pos.gson = Gson().toJson(so) + "\\r\\n"
                                RxBus.default?.post(pos)
                                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).navigation()
                                teamSettingActivity.finish()
                            }
                        })
                        dialog.show()
                    } else {
                        var dialog = DialogUtils.createNomalDialog(teamSettingActivity, getString(R.string.single_exit_team_warm), getString(R.string.cancle), getString(R.string.confirm))
                        dialog.setOnBtnClickL(OnBtnClickL {
                            dialog.dismiss()
                        }, OnBtnClickL {
                            dialog.dismiss()
                            var so = Soket()
                            so.type = SocketDealType.LEAVETEAM.code
                            so.teamCode = teamSettingActivity.info?.redisData?.teamCode
                            so.teamId = teamSettingActivity.info?.redisData?.id.toString()
                            so.userId = id
                            dialog.dismiss()
                            CoroutineScope(uiContext).launch {
                                delay(1000)
                                RxBus.default?.post("showProgress")
                                var pos = ServiceEven()
                                pos.type = "sendData"
                                pos.gson = Gson().toJson(so) + "\\r\\n"
                                RxBus.default?.post(pos)
//                                com.elder.amoski.Service.Mina.SessionManager.getInstance().writeToServer(Gson().toJson(so) + "\\r\\n")
                                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).navigation()
                                teamSettingActivity.finish()
                            }
                        })
                        dialog.show()
                    }
                }
            }
        }
    }
}