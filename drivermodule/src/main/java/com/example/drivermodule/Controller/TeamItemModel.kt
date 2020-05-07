package com.example.drivermodule.Controller

import android.app.AlertDialog
import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.LatLonPoint
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Entity.SoketBody.*
import com.elder.zcommonmodule.Inteface.Locationlistener
import com.elder.zcommonmodule.Widget.LoginUtils.BaseDialogFragment
import com.example.drivermodule.R
import com.example.drivermodule.Fragment.MapFragment
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.google.gson.Gson
import com.zk.library.Base.BaseApplication
import com.elder.zcommonmodule.Component.ItemViewModel
import com.elder.zcommonmodule.Entity.LatLonLocal
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Entity.RemoveBody
import com.elder.zcommonmodule.Utils.Dialog.NormalDialog
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.drivermodule.Fragment.SearchActivity
import com.example.drivermodule.Activity.Team.TeamSettingActivity
import com.example.drivermodule.Utils.AMapUtil
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.ServiceEven
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Bus.event.RxBusEven.Companion.MinaDataReceive
import com.zk.library.Bus.event.RxBusEven.Companion.TeamSocketConnectSuccess
import com.zk.library.Bus.event.RxBusEven.Companion.TeamSocketDisConnect
import com.zk.library.Utils.RouterUtils
import com.zk.library.binding.command.ViewAdapter.image.SimpleTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.ioContext
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
//组队控制器控制器

class TeamItemModel : ItemViewModel<MapFrViewModel>() {
    //组队逻辑处理
    var markerList = HashMap<String, Marker>()
    var TeamInfo: TeamPersonInfo? = null            //本地组队信息，目前用处不大
    var teamer: Int = 0                      //队长id
    var teamCode = ObservableField<String>()           //队伍编号
    var teamId: String? = null                //队伍ID
    var teamName: String? = null               //队伍名字
    var markerListNumber = ArrayList<String>()           //界面maker点编号集合
    var titleName = ObservableField<String>()            //标题名
    var date = ObservableField<String>()           //日期
    var districtTv = ObservableField<String>()          //描述文本
    var visibleScroller = ObservableField<Boolean>(true)       //
    var teamNavigation = ObservableField<Boolean>(false)       //是否有组队导航
    var create: CreateTeamInfoDto? = null                 //创建队伍基本信息
    var join: TeamPersonnelInfoDto? = null                //加入队伍基本信息
    var isLoginTimeOut = false              //是否登录超时
    var startTime: Long = 0              //开始时间
    var location: AMapLocation? = null            //当前定位点
    var type: String? = null   //队伍创建加入界面返回的参数       //
    var soketNavigation: SoketNavigation? = null         //组队导航信息
    fun restAllData() {
        //状态清空 队伍解散或者退出队伍
        markerListNumber.forEach {
            markerList[it]?.remove()
        }
        markerListNumber.clear()
        markerList.clear()
        TeamInfo = null
        teamer = 0
        teamCode.set("")
        teamId = null
        teamName = null
        markerListNumber.clear()
        titleName.set("")
        date.set("")
        districtTv.set("")
        visibleScroller.set(true)
        teamNavigation.set(false)
        create = null
        join = null
        startTime = 0
        soketNavigation = null
        personDatas.clear()
    }

    lateinit var mapFr: MapFragment
    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            TeamSocketConnectSuccess -> {
                //组队Socket链接成功
                var so = Soket()
                so.type = 1000
                so.body = Soket.SocketRequest()
                so.body?.token = mapFr.token
                so.sendTime = System.currentTimeMillis()
                if (create != null) {
                    viewModel.TeamStatus?.teamCreate = create
                    so.teamCode = create?.teamCode
                    so.teamId = create?.id.toString()
                    startTime = create?.createTime!!
                } else if (join != null) {
                    viewModel.TeamStatus?.teamJoin = join
                    so.teamCode = join?.teamCode
                    so.teamId = join?.id.toString()
                    startTime = join?.createTime!!
                } else {
                    viewModel?.changerFragment(1)
                    if (viewModel.TeamStatus!!.teamJoin != null) {
                        join = viewModel.TeamStatus?.teamJoin
                        so.teamCode = viewModel.TeamStatus?.teamJoin?.teamCode
                        so.teamId = viewModel.TeamStatus?.teamJoin?.id.toString()
                        startTime = viewModel.TeamStatus?.teamJoin?.createTime!!
                    } else if (viewModel.TeamStatus?.teamCreate != null) {
                        create = viewModel.TeamStatus?.teamCreate
                        so.teamCode = viewModel.TeamStatus?.teamCreate?.teamCode
                        so.teamId = viewModel.TeamStatus?.teamCreate?.id.toString()
                        startTime = viewModel.TeamStatus?.teamCreate?.createTime!!
                    }
                }

                titleName.set("")
                so.userId = mapFr.user.data!!.id
                teamId = so.teamId
                teamCode.set(so.teamCode)

                sendOrder(so, false)
                viewModel.TeamStatus?.teamStatus = TeamStarting
                viewModel?.TeamStatus?.save()

            }
            TeamSocketDisConnect -> {
                //组队Socket已断开  已在BaseApplication中处理
            }
            MinaDataReceive -> {
                //组队数据接收
                doSocket((it.value) as BasePacketReceive)

            }
        }
    }

    override fun ItemViewModel(viewModel: MapFrViewModel): ItemViewModel<MapFrViewModel> {

        mapFr = viewModel.mapActivity

        return super.ItemViewModel(viewModel)

    }

    fun sendOrder(n: Soket, flag: Boolean) {
            //发送指令
        if (mapFr.onStart && flag) {

            CoroutineScope(uiContext).launch {

                mapFr.showProgressDialog(getString(R.string.get_message))

            }

        }

        var pos = ServiceEven()

        pos.type = "sendData"

        pos.gson = Gson().toJson(n) + "\\r\\n"

        RxBus.default?.post(pos)

    }


    var HeartTimeLimit = 0L

    private fun doSocket(it: BasePacketReceive) {
        if (mapFr.isAdded) {

            if (BaseApplication.isClose) {

                return

            }

            if (it.code == 0) {

                when (it.type) {

                    SocketDealType.HEART_BEAT.code -> {
                        //发送心跳
                        if (System.currentTimeMillis() - HeartTimeLimit < 10000) {
                            return
                        } else {
                            //10秒一次心跳
                            var so = Soket()
                            so.type = SocketDealType.HEART_BEAT.code
                            sendOrder(so, false)
                            HeartTimeLimit = System.currentTimeMillis()
                        }
                    }
                    SocketDealType.JOINTEAM.code -> {
                        //队员加入通知
                        if (markerListNumber.contains(it.userId.toString())) {
                            var marker = markerList[it.userId.toString()]
                            marker!!.remove()
                            markerListNumber.remove(it.userId.toString())
                            markerList.remove(it.userId.toString())
                        }
                        //查询信息
                        var m = Soket()
                        m.type = 1006
                        m.teamCode = teamCode.get()
                        sendOrder(m, false)
                        viewModel?.component!!.arrowVisible.set(false)
                        viewModel?.component!!.rightVisibleType.set(false)
                        viewModel?.component!!.rightText.set("")
                        viewModel?.component!!.rightIcon.set(context.getDrawable(R.drawable.ic_sousuo))
                    }
                    SocketDealType.SENDPOINT.code -> {
                        //发送定位点
                        var local = Gson().fromJson<LatLonLocal>(it.body, LatLonLocal::class.java)
                        if (it.userId.toString() != mapFr.user.data?.memberId) {
                            if (location != null) {
                                if (markerList.containsKey(it.userId.toString())) {
                                    var marker = markerList[it.userId.toString()]
                                    marker?.position = LatLng(local.latitude!!, local.longitude!!)
                                    var title = marker?.title
                                    var m = fomatDistance(LatLng(location?.latitude!!, location!!.longitude), LatLng(local.latitude!!, local.longitude!!))
                                    marker?.title = title!!.split(",")[0] + "," + m
                                    markerList.set(it.userId.toString(), marker!!)
                                }
                            }
                        }
                    }
                    SocketDealType.OFFLINE.code -> {
                        //离线
                        if (markerListNumber.contains(it.userId.toString())) {
                            var maker = markerList[it.userId.toString()]
                            maker?.remove()
                            markerList.remove(it.userId.toString())
                            markerListNumber.remove(it.userId.toString())
                        }
                        var m = Soket()
                        m.type = 1006
                        m.teamCode = teamCode.get()
                        sendOrder(m, true)
                    }
                    SocketDealType.LEAVETEAM.code -> {
                        //离开队伍
                        if (it.userId.toString() == mapFr.user.data?.memberId) {
                            CoroutineScope(uiContext).launch {
                                Toast.makeText(context, "已离开队伍！", Toast.LENGTH_SHORT).show()
                            }
                            endTeam(false)
                        } else {
                            var c = it
                            TeamInfo?.redisData?.dtoList?.forEach {
                                if (it.memberId == c.userId) {
                                    CoroutineScope(uiContext).launch {
                                        Toast.makeText(context, it.memberName + "已离开队伍！", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            var maker = markerList[it.userId.toString()]
                            maker?.remove()
                            markerList.remove(it.userId.toString())
                            markerListNumber.remove(it.userId.toString())
                            var m = Soket()
                            m.type = 1006
                            m.teamCode = teamCode.get()
                            sendOrder(m, true)
                        }
                    }
                    SocketDealType.REJECTTEAM.code -> {
                        //队员被移除通知
                        var remove = Gson().fromJson<RemoveBody>(it.body, RemoveBody::class.java)
                        var body = remove.userIds?.split(",")
                        if (body?.contains(mapFr.user.data?.memberId)!!) {
                            //判断自己是否在内
                            CoroutineScope(uiContext).launch {
                                Toast.makeText(mapFr.activity, getString(R.string.remove_by_team), Toast.LENGTH_SHORT).show()
                            }
                            var even = RxBusEven()
                            even.type = RxBusEven.Team_reject_even
                            RxBus.default!!.post(even)
                            AppManager.get()?.finishActivity(TeamSettingActivity::class.java)
                            endTeam(false)
                        } else {
                            CoroutineScope(uiContext).launch {
                                Toast.makeText(context, "已有队员被移除队伍！", Toast.LENGTH_SHORT).show()
                            }
                            body.forEachIndexed { index, s ->
                                if (!s.isEmpty()) {
                                    markerList[s]?.remove()
                                    markerList.remove(s)
                                    markerListNumber.remove(s)
                                }
                            }
                            var m = Soket()
                            m.type = 1006
                            m.teamCode = teamCode.get()
                            sendOrder(m, true)
                        }
                    }
                    SocketDealType.GETTEAMINFO.code -> {
                        //获取队内信息
                        if (mapFr.isAdded) {
                            TeamInfo = Gson().fromJson<TeamPersonInfo>(it.body, TeamPersonInfo::class.java)
                            if (!it.body!!.isEmpty()) {
                                initInfo()
                            }
                            if (TeamInfo?.redisData?.navigationPoint != null) {
                                if (TeamInfo?.redisData?.navigationPoint?.contains("\\")!!) {
                                    TeamInfo?.redisData?.navigationPoint = TeamInfo?.redisData?.navigationPoint?.replace("\\", "", true)
                                }
                                soketNavigation = Gson().fromJson<SoketNavigation>(TeamInfo?.redisData?.navigationPoint, SoketNavigation::class.java)
                                if (teamer.toString() == mapFr.user.data?.id) {
                                    teamNavigation.set(false)
                                    districtTv.set("")
                                } else {
                                    teamNavigation.set(true)
                                    districtTv.set(soketNavigation!!.navigation_end?.aoiName)
                                }
                            } else {
                                if (viewModel?.status?.navigationType == 1 && teamer.toString() == mapFr.user.data?.id) {
                                    sendNavigationNotify()
                                }
                                districtTv.set("")
                                teamNavigation.set(false)
                            }
                        }
                    }
                    SocketDealType.TEAMER_PASS.code -> {
                        var m = Soket()
                        m.type = 1006
                        m.teamCode = teamCode.get()
                        sendOrder(m, true)
                    }
                    SocketDealType.UPDATETEAMINFO.code -> {
                        //队内信息更新
                        var m = Soket()
                        m.type = 1006
                        m.teamCode = teamCode.get()
                        sendOrder(m, true)
                        viewModel?.component!!.arrowVisible.set(false)
                        viewModel?.component!!.rightVisibleType.set(false)
                        viewModel?.component!!.rightText.set("")
                        viewModel?.component!!.rightIcon.set(context.getDrawable(R.drawable.ic_sousuo))
                    }
                    SocketDealType.DISMISSTEAM.code -> {
                        //解散队伍
                        CoroutineScope(uiContext).launch {
                            Toast.makeText(context, "您的队伍已被解散！", Toast.LENGTH_SHORT).show()
                        }
                        var even = RxBusEven()
                        even.type = RxBusEven.Team_reject_even
                        RxBus.default!!.post(even)
                        endTeam(false)
                    }
                    SocketDealType.UPDATE_MANAGER.code -> {
                        var m = Soket()
                        m.type = 1006
                        m.teamCode = teamCode.get()
                        sendOrder(m, true)
                    }
                    SocketDealType.NAVIGATION_START.code -> {
                        // 导航通知
                        var obj = JSONObject(it.body)
                        var nav = obj.getString("navigationPoint")
                        soketNavigation = Gson().fromJson<SoketNavigation>(nav, SoketNavigation::class.java)
                        if (mapFr.onStart && teamer.toString() != mapFr.user.data?.id && viewModel?.status?.navigationType == 1) {
                            CoroutineScope(uiContext).launch {
                                createDistrictDialog()
//                        {"navigationPoint":"{\"wayPoint\":[],\"navigation_end\":{\"aoiName\":\"长沙县人民政府\",\"longitude\":113.07891494372052,\"latitude\":28.247158541709886}}"}
                            }
                        } else {
                            RxBus.default?.post(RxBusEven.getInstance(RxBusEven.DriverNavigationRouteChange, soketNavigation!!))
                        }
                        var m = Soket()
                        m.type = 1006
                        m.teamCode = teamCode.get()
                        sendOrder(m, true)
                    }
                }
            } else {
                if (it.type == 10009) {
                    //登录超时 重新登录后，加入组队
                    //关闭Mina
                    //登录
                    //检查用户信息
                    //重新进入组队
                    if (!isLoginTimeOut) {
                        closeMina()
                        //返回到骑行界面
                        CoroutineScope(uiContext).launch {
                            viewModel?.selectTab(0)
                            showLoginDialogFragment(mapFr)
                        }
                    }
                }
            }
        }
    }

    var notifyRouteChange: NormalDialog? = null
    private fun createDistrictDialog() {
        //队伍导航目的地更改提醒
        if (mapFr.isVisible && viewModel?.currentPosition == 1) {
            if (notifyRouteChange == null) {
                notifyRouteChange = DialogUtils.createNomalDialog(mapFr.activity!!, getString(R.string.route_change), getString(R.string.ignore), getString(R.string.change_route))
                notifyRouteChange!!.setOnBtnClickL(OnBtnClickL {
                    notifyRouteChange?.dismiss()
                }, OnBtnClickL {
                    notifyRouteChange?.dismiss()
                    backToDriver()
                    viewModel?.backStatus = true
                    if (viewModel?.status?.navigationType == 1) {
                        var list = ArrayList<LatLng>()
                        if (soketNavigation?.wayPoint != null && !soketNavigation?.wayPoint!!.isEmpty()) {
                            soketNavigation?.wayPoint!!.forEach {
                                list.add(AMapUtil.convertToLatLng(it))
                            }
                        }
                        viewModel?.status!!.navigationEndPoint = soketNavigation?.navigation_end
                        viewModel?.startNavi(list, 2)
                        RxBus.default?.post(RxBusEven.getInstance(RxBusEven.DriverNavigationRouteChange, soketNavigation!!))

                    } else {
                        toNavi(false)
                    }
                })
            }
            notifyRouteChange?.show()
        }
    }

    private fun toNavi(b: Boolean) {
        //组队导航跳转
        if (teamer.toString() == mapFr.user.data?.id) {
            var list = ArrayList<LatLng>()
            if (!viewModel?.status.passPointDatas.isEmpty()) {
                viewModel?.status.passPointDatas.forEach {
                    list.add(LatLng(it.latitude, it.longitude))
                }
            }
            viewModel?.status!!.navigationEndPoint = soketNavigation?.navigation_end
            if (viewModel?.status.startDriver.get() == DriverCancle) {
                viewModel?.startDriver(3)
            } else {
                viewModel?.startNavi(list, 3)
            }
//            viewModel?.startNavi(viewModel?.status!!.navigationStartPoint!!, viewModel?.status!!.navigationEndPoint!!, list, flag)
        } else {
            if (soketNavigation != null) {
                viewModel?.status.navigationEndPoint = soketNavigation?.navigation_end
                if (!soketNavigation?.wayPoint.isNullOrEmpty()) {
                    soketNavigation?.wayPoint!!.forEach {
                        viewModel?.status.passPointDatas.add(Location(it.latitude, it.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F))
                    }
                }
                if (viewModel?.status.startDriver.get() == DriverCancle) {
                    viewModel?.startDriver(3)
                } else {
                    var list = ArrayList<LatLng>()
                    if (!viewModel?.status.passPointDatas.isEmpty()) {
                        viewModel?.status.passPointDatas.forEach {
                            list.add(LatLng(it.latitude, it.longitude))
                        }
                    }
                    viewModel?.startNavi(list, 3)
                }
            }
        }
    }

     fun sendNavigationNotify() {
         //发送组队导航通知
        var so = Soket()
        so.type = SocketDealType.NAVIGATION_START.code
        so.teamCode = teamCode.get()
        so.teamId = teamId
        so.userId = mapFr.user.data?.memberId
        var sos = SoketNavigation()
        if (viewModel.status.passPointDatas != null) {
            viewModel.status.passPointDatas.forEach {
                sos.wayPoint!!.add(LatLonPoint(it.latitude, it.longitude))
            }
        }
        sos.navigation_end = viewModel.status?.navigationEndPoint
        so.body = Soket.SocketRequest()
        so.body?.navigationPoint = Gson().toJson(sos)
        var pos = ServiceEven()
        pos.type = "sendData"
        pos.gson = Gson().toJson(so) + "\\r\\n"
        RxBus.default?.post(pos)
    }


    fun doCreate(data: Bundle?) {
        if (data != null) {
            //创建队伍返回回调处理
            type = data.getString("type")
            when (type) {
                "create" -> {
                    create = data.getSerializable("data") as CreateTeamInfoDto?
                    startMinaService()
                }
                "join" -> {
                    join = data.getSerializable("data") as TeamPersonnelInfoDto?
                    startMinaService()
                }
                "cancle" -> {
                    viewModel.selectTab(0)
                }
            }
        } else {
            viewModel.selectTab(0)
        }
    }

    fun initInfo() {
        //队伍信息初始化
        if (TeamInfo != null && TeamInfo?.redisData != null && TeamInfo?.redisData?.createTime != null) {
            if (TeamInfo?.redisData?.teamName != null) {
                titleName.set(TeamInfo?.redisData?.teamName + "(" + TeamInfo?.redisData?.dtoList?.size + ")")
            }
            teamer = TeamInfo?.redisData?.teamer!!
            CoroutineScope(uiContext).launch {
                var simple = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                var d = Date(TeamInfo?.redisData?.validEndTime!!)
                date.set("有效期至：" + simple.format(d))
                addChildView(TeamInfo?.redisData!!.dtoList)
                viewModel?.component.isTeam.set(true)
            }
        } else {
        }
        mapFr.dismissProgressDialog()
    }

    //底部头像列表数据
    var personDatas = ObservableArrayList<TeamPersonnelInfoDto>().apply {
        this.add(TeamPersonnelInfoDto())
    }

    //分享弹出窗
    var shareDialog: AlertDialog? = null

    var BottomChildClick = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            //底部头像点击事件回调
            var entity = personDatas[t]
            if (t == 0) {
                if (TeamInfo?.redisData?.dtoList?.size == TeamInfo?.redisData?.teamMaxCount) {
                    Toast.makeText(context, getString(R.string.max_member) + TeamInfo?.redisData?.teamMaxCount + "人", Toast.LENGTH_SHORT).show()
                } else {
                    if (shareDialog == null) {
                        shareDialog = DialogUtils.createShareDialog(mapFr.activity!!, "", "")
                    } else if (!shareDialog!!.isShowing) {
                        shareDialog!!.show()
                    }
                    shareDialog?.findViewById<TextView>(R.id.share_frend)?.setOnClickListener {
                        if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                            CoroutineScope(uiContext).launch {
                                Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            shareWx(SendMessageToWX.Req.WXSceneSession)
                        }
                        shareDialog!!.dismiss()
                    }
                    shareDialog?.findViewById<TextView>(R.id.share_frendQ)?.setOnClickListener {
                        if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                            CoroutineScope(uiContext).launch {
                                Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            shareWx(SendMessageToWX.Req.WXSceneTimeline)
                        }
                        shareDialog!!.dismiss()
                    }
                }
            } else {
                if (markerListNumber.contains(entity.memberId.toString())) {
                    var maker = markerList.get(entity.memberId.toString())
                    mapFr.mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(maker!!.position, 15F), 1000, object : AMap.CancelableCallback {
                        override fun onFinish() {
                        }

                        override fun onCancel() {
                        }
                    })
                } else if (mapFr.user?.data?.memberId == entity.memberId.toString()) {
                    if (viewModel?.curPosition != null) {
                        mapFr.mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(viewModel?.curPosition!!.latitude, viewModel?.curPosition!!.longitude), 15F), 1000, object : AMap.CancelableCallback {
                            override fun onFinish() {
                            }

                            override fun onCancel() {

                            }
                        })
                    }
                }
            }
        }
    })

    fun shareWx(type: Int) {
        //微信分享
        if (TeamInfo == null && mapFr.activity!!.isFinishing) {
            return
        }
        CoroutineScope(ioContext).async {
            var file = Glide.with(mapFr.activity!!)
                    .load(getImageUrl(TeamInfo?.redisData?.dtoList!![0].memberHeaderUrl))
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            var files = file.get()

            if (files != null) {
                var bitmap = BitmapFactory.decodeFile(files.path)
                var newBitmap = ConvertUtils.compressByQuality(bitmap, 32000, true)
                var wx = WXWebpageObject()
                wx.webpageUrl = "http://amoski.net/yomoy/index.html?platform=android&teamCode=" + teamCode.get()
                var msg = WXMediaMessage()
                msg.mediaObject = wx
                msg.title = getString(R.string.team_code) + teamCode.get()
                msg.description = getString(R.string.share_description_by_team)
                msg.thumbData = newBitmap
                var req = SendMessageToWX.Req()
                req.transaction = System.currentTimeMillis().toString() + "img"
                req.message = msg
                req.scene = type
                BaseApplication.getInstance().mWxApi.sendReq(req)
            } else {
                Toast.makeText(context, "图片获取失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addChildView(dtoList: MutableList<TeamPersonnelInfoDto>) {
        //添加队员界面实现
        if (dtoList != null && dtoList.size != 0) {
            personDatas.clear()
            personDatas.add(TeamPersonnelInfoDto())
            if (markerListNumber.size > dtoList.size - 1) {
                //数量有误
                markerListNumber.forEach {
                    markerList[it]?.remove()
                }
                markerListNumber.clear()
                markerList.clear()
            }
            dtoList.forEach {
                if (markerListNumber.contains(it.memberId.toString())) {
                    var name = ""
                    if (it.memberName.trim().isEmpty()) {
                        name = getString(R.string.secret_name)
                    } else {
                        name = it.memberName
                    }
                    var m: List<String>
                    if (it.lastPoint != null) {
                        m = it.lastPoint.split(",")
                    } else {
                        m = it.joinAddr.split(",")
                    }
                    var maker = markerList[it.memberId.toString()]!!
                    maker.position = LatLng(m[0].toDouble(), m[1].toDouble())
                    if (location == null) {
                        if (it.status == "0") {
                            maker.title = "[离线]" + name + "," + "0M"
                        } else {
                            maker.title = name + "," + "0M"
                        }
                    } else {
                        var distanceTv = fomatDistance(LatLng(location!!.latitude, location!!.longitude), maker.position)
                        if (it.status == "0") {
                            maker.title = "[离线]" + name + "," + distanceTv
                        } else {
                            maker.title = name + "," + distanceTv
                        }
                    }
                    maker.snippet = it.teamRoleName
                    markerList.set(it.memberId.toString(), maker)
//                    maker.showInfoWindow()
                } else {
                    if (it.memberId.toString() != mapFr.user.data?.id) {
                        createImageMarker(it)
                    }
                }
                personDatas.add(it)
            }
        }
    }

    private fun createImageMarker(it: TeamPersonnelInfoDto) {
        //创建队员marker点
        var m = it.joinAddr.split(",")
        var position = LatLng(m[0].toDouble(), m[1].toDouble())
        var url = getImageUrl(it.memberHeaderUrl)
        var inflater = mapFr.activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflater.inflate(R.layout.teamm_maker_layout, null)
        view.findViewById<RelativeLayout>(R.id.maker_root).layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        var img = view.findViewById<ImageView>(R.id.team_marker_img)
        var maker = mapFr.mAmap.addMarker(MarkerOptions().position(position))
        var name = ""
        if (it.memberName.trim().isEmpty()) {
            name = getString(R.string.secret_name) + ",0M"
        } else {
            name = it.memberName + ",0M"
        }
        if (it.status == "0") {
            view!!.findViewById<ImageView>(R.id.team_marker_upBg).visibility = View.VISIBLE
            maker!!.title = "[离线]" + name
        } else {
            view!!.findViewById<ImageView>(R.id.team_marker_upBg).visibility = View.GONE
            maker!!.title = name
        }
        maker?.snippet = it.teamRoleName
        markerListNumber.add(it.memberId.toString())
        markerList.put(it.memberId.toString(), maker!!)
        var corners = CircleCrop()
        var options = RequestOptions().transform(corners).error(R.drawable.team_first).override(ConvertUtils.dp2px(48F), ConvertUtils.dp2px(48F))
        var listener = CustomListener(img, position, view, it, maker)
        Glide.with(context).load(url).apply(options).into(listener)
    }

    private fun fomatDistance(position: LatLng, markerPosition: LatLng): String? {
        //距离转String工具
        var s = AMapUtils.calculateLineDistance(position, markerPosition)
        var distanceTv = ""
        if (s > 1000) {
            distanceTv = DecimalFormat("0.0").format(s / 1000) + "KM"
        } else {
            distanceTv = DecimalFormat("0.0").format(s) + "M"
        }
        return distanceTv
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.team_setting -> {
                if (TeamInfo != null) {
                    // 跳转到设置界面
                    var fr = viewModel?.mapActivity.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
                    fr.start((ARouter.getInstance().build(RouterUtils.TeamModule.SETTING).navigation() as TeamSettingActivity).SettingValue(TeamInfo!!))
//                    ARouter.getInstance().build(RouterUtils.TeamModule.SETTING).withSerializable(RouterUtils.TeamModule.TEAM_INFO, TeamInfo).navigation()
                }
            }
        }
    }

    var xF = 0F
    var scrollerCallBack = BindingCommand(object : BindingConsumer<MotionEvent> {
        override fun call(event: MotionEvent) {
            //
            var action = event.action
            var x = event.x
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    xF = x
                }
                MotionEvent.ACTION_UP -> {
                    if (x.toInt() - xF.toInt() > 0) {
                        xF = 0F
                        visibleScroller!!.set(true)
                    } else if (x.toInt() - xF.toInt() < 0) {
                        visibleScroller!!.set(false)
                        xF = 0F
                    } else {
                        toNavi(true)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                }
            }
        }
    })

    fun onComponentFinish() {

        var fr = viewModel?.mapActivity.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
        fr.startForResult((ARouter.getInstance().build(RouterUtils.MapModuleConfig.SEARCH_ACTIVITY).navigation() as SearchActivity).setModel(0), RESULT_POINT)
//        viewModel?.mapActivity.startForResult( (ARouter.getInstance().build(RouterUtils.MapModuleConfig.SEARCH_ACTIVITY) as SearchActivity).setModel(0),RESULT_POINT)

//        ARouter.getInstance().build(RouterUtils.MapModuleConfig.SEARCH_ACTIVITY).withInt(RouterUtils.MapModuleConfig.SEARCH_MODEL, 0).navigation(mapFr.activity, RESULT_POINT)
    }

    fun custionView(maker: Marker?, view: View?) {
        isClick = true
        var title = maker?.title
        view?.findViewById<TextView>(R.id.team_window_title)!!.text = title!!.split(",")[0]
        var tex = view?.findViewById<TextView>(R.id.team_window_sna)
        if (maker?.snippet == "家属") {
            tex.visibility = View.GONE
        } else {
            tex.visibility = View.VISIBLE
            if (maker?.snippet == "骑士") {
                tex.background = context?.getDrawable(R.drawable.little_tv_color_corner)
            } else {
                tex.background = context?.getDrawable(R.drawable.little_tv_color_corner_yellow)
            }
        }
        view?.findViewById<TextView>(R.id.team_window_sna)!!.text = maker?.snippet
        view?.findViewById<TextView>(R.id.team_distance_show)!!.text = "距离" + title!!.split(",")[1]
    }

    fun onInfoWindowClick(it: Marker?) {

    }

    fun endTeam(b: Boolean) {
        Log.e("result", "解散队伍endTeam")
        CoroutineScope(uiContext).launch {
            var so = Soket()
            so.teamCode = teamCode.get()
            so.teamId = teamId
            so.userId = mapFr.user.data?.id
            if (b) {
                if (mapFr.user.data?.id == teamer.toString()) {
                    so.type = SocketDealType.DISMISSTEAM.code
                } else {
                    so.type = SocketDealType.LEAVETEAM.code
                }
            }
            mapFr.teamCode = null
            sendOrder(so, false)
            closeMina()
            if(viewModel.TeamStatus!=null){
                viewModel.TeamStatus!!.resetTeam()
            }
            viewModel.TeamStatus = null
            mapFr.viewModel?.selectTab(0)
        }
    }

    fun backToDriver() {
        Log.e("result", "backToDriver")
        if (BaseApplication.isClose) {
            //不发送消息 清空所有数据
            restAllData()
            Log.e("result", "isClose")
        } else {
            markerListNumber.forEach {
                markerList[it]?.remove()
            }
            markerListNumber.clear()
            markerList.clear()
            //发送消息
            Log.e("result", "isOpen")
        }
        if (viewModel?.status.startDriver.get() == TeamModel) {
            viewModel?.status.startDriver.set(DriverCancle)
        }
        Log.e("result", "backToDriver`1")
        viewModel?.component.isTeam.set(false)
        viewModel.changerFragment(0)
    }


    fun backToRoad() {
        if (BaseApplication.isClose) {
            //不发送消息 清空所有数据
            restAllData()
        } else {
            markerListNumber.forEach {
                markerList[it]?.remove()
            }
            markerListNumber.clear()
            markerList.clear()
            //发送消息
        }
        if (viewModel?.status.startDriver.get() == TeamModel) {
            viewModel?.status.startDriver.set(DriverCancle)
        }
        viewModel?.component.isTeam.set(false)
        viewModel.changerFragment(2)
    }

    fun onLocation(location: AMapLocation) {
        if (BaseApplication.MinaConnected) {
            //发送定位
            this.location = location
            var n = Soket()
            n.teamCode = teamCode.get()
            n.teamId = teamId
            n.userId = mapFr.user.data?.memberId
            n.type = SocketDealType.SENDPOINT.code
            n.body = Soket.SocketRequest()
            n.body!!.longitude = location.longitude
            n.body!!.latitude = location.latitude
            n.body!!.token = mapFr.token
            sendOrder(n, false)
        }
    }

    override fun onDismiss(fr: BaseDialogFragment, value: Any) {
        (viewModel?.items[0] as DriverItemModel).GoTeam()
        dialogFragment.functionDismiss = null
    }

    var isClick = false
    fun MapClick(p0: LatLng?) {
        var flag = false
        markerListNumber?.forEach {
            if (markerList!![it]?.isInfoWindowShown!!) {
                if (!isClick) {
                    markerList!![it]?.hideInfoWindow()
                    flag = true
                } else {
                    flag = false
                }
            }
        }
        isClick = flag
    }

    fun onFiveBtnClick(view: View) {
        when (view.id) {
            R.id.team_btn -> {
                viewModel?.selectTab(0)
            }
            R.id.change_map_point -> {
                if (viewModel?.status.navigationType == 0) {
                    viewModel.backStatus = true
                    backToDriver()
                    (viewModel?.items[0] as DriverItemModel).changeMap_Point_btn()
                } else {
                    var list = ArrayList<LatLng>()
                    viewModel?.status.passPointDatas.forEach {
                        list.add(LatLng(it.latitude, it.longitude))
                    }
                    viewModel?.startNavi(list, 2)
                }
            }
            R.id.setting_btn -> {
                if (viewModel?.curPosition != null) {
                    mapFr.mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(viewModel?.curPosition!!.latitude, viewModel?.curPosition!!.longitude), 15F), 1000, object : AMap.CancelableCallback {
                        override fun onFinish() {
                        }

                        override fun onCancel() {
                        }
                    })
                }
            }
        }
    }

    inner class CustomListener : SimpleTarget<Drawable> {
        var img: ImageView? = null
        var position: LatLng? = null
        var view: View? = null
        var it: TeamPersonnelInfoDto
        var maker: Marker? = null

        constructor(img: ImageView, position: LatLng, view: View, it: TeamPersonnelInfoDto, maker: Marker) {
            this.img = img
            this.position = position
            this.view = view
            this.it = it
            this.maker = maker
        }

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            if (maker != null) {
                img!!.setImageDrawable(resource)
                maker?.setIcon(BitmapDescriptorFactory.fromView(view))
            }
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            super.onLoadFailed(errorDrawable)
            if (maker != null) {
                img!!.setImageResource(R.drawable.default_avatar)
                maker?.setIcon(BitmapDescriptorFactory.fromView(view))
            }
        }
    }
}