package com.example.drivermodule.ViewModel

import android.app.AlertDialog
import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.widget.CardView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.queryUserInfo
import com.elder.zcommonmodule.Entity.LatLonLocal
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Entity.RemoveBody
import com.elder.zcommonmodule.Entity.SoketBody.*
import com.elder.zcommonmodule.Entity.UserInfo
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.Utils.Dialog.NormalDialog
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.drivermodule.AMapUtil
import com.example.drivermodule.Activity.MapActivity
import com.example.drivermodule.Activity.Team.TeamSettingActivity
import com.example.drivermodule.R
import com.example.drivermodule.Ui.TeamFragment
import com.google.gson.Gson
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zk.library.binding.command.ViewAdapter.image.SimpleTarget
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_team.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.*
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class TeamViewModel : BaseViewModel() {
    var CurrentClickTime: Long = 0
    var isOnPasue = false
    var visibleScroller = ObservableField<Boolean>(true)
    fun sendOrder(n: Soket) {
        CoroutineScope(uiContext).launch {
            if (teamFragment.isAdded && !isOnPasue) {
                mapActivity.showProgressDialog(getString(R.string.get_message))
            }
            var pos = ServiceEven()
            pos.type = "sendData"
            pos.gson = Gson().toJson(n) + "\\r\\n"
            RxBus.default?.post(pos)
        }
    }

    override fun onResume() {
        super.onResume()
        isOnPasue = false
    }

    override fun onPause() {
        super.onPause()
        isOnPasue = true
    }


    fun onComponentClick(view: View) {
        if (System.currentTimeMillis() - CurrentClickTime < 1500) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        RxBus.default?.post(driverModel?.status)
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
    }

    fun backToDriver() {
        back()
        mapActivity?.mViewModel?.changerFragment(0)
    }

    fun backToRoad() {
        back()
        mapActivity?.mViewModel?.changerFragment(3)
    }

    fun back() {
        markerListNumber.forEach {
            markerList[it]?.remove()
        }
        markerListNumber.clear()
        markerList.clear()
        if (driverModel?.status.startDriver.get() == TeamModel) {
            driverModel?.status.startDriver.set(DriverCancle)
            mapActivity.mViewModel?.component!!.Drivering.set(false)
        } else if (driverModel?.status.startDriver.get() == Drivering) {
            mapActivity.mViewModel?.component!!.Drivering.set(true)
            driverModel.linearBg.set(Color.WHITE)
        }
        mapActivity.mViewModel?.component!!.isTeam.set(false)
    }

    fun onComponentFinish(view: View) {
        if (System.currentTimeMillis() - CurrentClickTime < 500) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        mapActivity.getDrverFragment().viewModel?.ComponentFinish()
    }

    var lastRemoveId = ""

    var locationDispose = RxBus.default?.toObservable(AMapLocation::class.java)?.subscribe {
        //DOUpload
        location = it
        if (BaseApplication.MinaConnected) {
            var n = Soket()
            n.teamCode = teamCode.get()
            n.teamId = teamId
            n.userId = mapActivity.user.data?.memberId
            n.type = SocketDealType.SENDPOINT.code
            n.body = Soket.SocketRequest()
            n.body!!.longitude = it.longitude
            n.body!!.latitude = it.latitude
            n.body!!.token = token

            var pos = ServiceEven()
            pos.type = "sendData"
            pos.gson = Gson().toJson(n) + "\\r\\n"
            RxBus.default?.post(pos)

//            com.elder.amoski.Service.Mina.SessionManager.getInstance().writeToServer(Gson().toJson(n) + "\\r\\n")
        }
    }

    var minaSoketDispose: Disposable? = null

    var minaConnectedDispose: Disposable? = null


    fun setMina() {
        Log.e("result", "setMina")
        minaConnectedDispose = RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "AppMinaConnected") {
                Log.e("result", "MinaConnected")
                BaseApplication.MinaConnected = true
                if (minaSoketDispose == null || minaSoketDispose?.isDisposed!!) {
                    minaSoketDispose = RxBus.default?.toObservable(BasePacketReceive::class.java)?.subscribe {
                        if (BaseApplication.MinaConnected) {
                            doSoket(it)
                        }
                    }
                }
                var so = Soket()
                so.type = 1000
                so.body = Soket.SocketRequest()
                so.body?.token = token
                so.sendTime = System.currentTimeMillis()
                if (teamFragment.create != null) {
                    driverModel.TeamStatus?.teamCreate = teamFragment.create
                    so.teamCode = teamFragment.create?.teamCode
                    so.teamId = teamFragment.create?.id.toString()
                    startTime = teamFragment?.create?.createTime!!
                } else if (teamFragment.join != null) {
                    driverModel.TeamStatus?.teamJoin = teamFragment.join
                    so.teamCode = teamFragment.join?.teamCode
                    so.teamId = teamFragment.join?.id.toString()
                    startTime = teamFragment?.join?.createTime!!
                } else {
                    mapActivity?.mViewModel?.changerFragment(1)
                    if (driverModel.TeamStatus?.teamJoin != null) {
                        teamFragment.join = driverModel.TeamStatus?.teamJoin
                        so.teamCode = driverModel.TeamStatus?.teamJoin?.teamCode
                        so.teamId = driverModel.TeamStatus?.teamJoin?.id.toString()
                        startTime = driverModel.TeamStatus?.teamJoin?.createTime!!
                    } else if (driverModel.TeamStatus?.teamCreate != null) {
                        teamFragment.create = driverModel.TeamStatus?.teamCreate
                        so.teamCode = driverModel.TeamStatus?.teamCreate?.teamCode
                        so.teamId = driverModel.TeamStatus?.teamCreate?.id.toString()
                        startTime = driverModel.TeamStatus?.teamCreate?.createTime!!
                    }
                }
                titleName.set("")
                mapActivity.mViewModel?.component!!.title.set("")
                so.userId = PreferenceUtils.getString(context, USERID)
                teamId = so.teamId
                teamCode.set(so.teamCode)
                driverModel.linearBg.set(Color.TRANSPARENT)
                sendOrder(so)
                RxSubscriptions.add(locationDispose)
                driverModel.TeamStatus?.teamStatus = TeamStarting
                driverModel?.TeamStatus?.save()
            } else if (it == "AppMINA_FORCE_CLOSE") {
                Log.e("result", "MINA_FORCE_CLOSE")
                CoroutineScope(uiContext).launch {
                    mapActivity.dismissProgressDialog()
                }
                driverModel.TeamStatus?.teamStatus = TeamOffline
                driverModel?.TeamStatus?.save()
            } else if (it == "showProgress") {
                CoroutineScope(uiContext).launch {
                    if (teamFragment.isAdded && !isOnPasue) {
                        mapActivity.showProgressDialog(getString(R.string.get_message))
                    }
                }
            } else if (it == "ExiLogin") {
                endTeam(false)
                mapActivity.finish()
            }
        }
        RxSubscriptions.add(minaConnectedDispose)
    }

    var date = ObservableField<String>()
    lateinit var teamFragment: TeamFragment
    lateinit var mapActivity: MapActivity
    lateinit var driverModel: DriverViewModel

    var startTime: Long = 0
    var token: String? = null

    override fun removeRxBus() {
        super.removeRxBus()
        removeDispose()
    }

    fun inject(teamFragment: TeamFragment, mapActivity: MapActivity) {
        this.teamFragment = teamFragment
        this.mapActivity = mapActivity
        setMina()
        this.token = PreferenceUtils.getString(context, USER_TOKEN)
        driverModel = mapActivity.getDrverFragment().viewModel!!
        Log.e("team", "${Gson().toJson(queryUserInfo(PreferenceUtils.getString(context, USERID)))}")
    }

    var teamCode = ObservableField<String>()
    var teamId: String? = null
    var teamer: Int = 0
    var teamName: String? = null
    var markerList = HashMap<String, Marker>()
    var markerListNumber = ArrayList<String>()
    var titleName = ObservableField<String>()


    fun createImageMarker(it: TeamPersonnelInfoDto) {
        var m = it.joinAddr.split(",")
        var position = LatLng(m[0].toDouble(), m[1].toDouble())
        var url = getImageUrl(it.memberHeaderUrl)
//                LatLng(m[0].toDouble(), m[1].toDouble()), getImageUrl(it.memberHeaderUrl), it.memberId,it.memberName
        var inflater = mapActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflater.inflate(R.layout.teamm_maker_layout, null)
        view.findViewById<RelativeLayout>(R.id.maker_root).layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        var img = view.findViewById<ImageView>(R.id.team_marker_img)
        var maker = mapActivity.mAmap.addMarker(MarkerOptions().position(position))
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


    inner class CustomListener : SimpleTarget<Drawable> {

        var img: ImageView? = null
        var position: LatLng? = null
        var view: View? = null
        lateinit var it: TeamPersonnelInfoDto

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
//                maker?.showInfoWindow()
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            super.onLoadFailed(errorDrawable)
            if (maker != null) {
                img!!.setImageResource(R.drawable.default_avatar)
                maker?.setIcon(BitmapDescriptorFactory.fromView(view))
//                maker?.showInfoWindow()
            }
        }
    }

    var TeamInfo: TeamPersonInfo? = null
    var location: AMapLocation? = null

    private fun doSoket(it: BasePacketReceive?) {
        if (mapActivity == null || !mapActivity.getTeamFragment().isAdded) {
            var pos = ServiceEven()
            pos.type = "MinaServiceCancle"
            RxBus.default?.post(pos)
//            context.startService(Intent(context, LowLocationService::class.java).setAction(SERVICE_CANCLE_MINA))
            removeDispose()
        }
        if (it == null) {
            mapActivity.dismissDialog()
            CoroutineScope(uiContext).launch {
                Toast.makeText(mapActivity, "SOCKET" + getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
            }
            return
        }
        if (it?.code != 0) {
            mapActivity.dismissDialog()
            if (it.type == 1000 && it?.code == 10009) {
                endTeam(false)
                PreferenceUtils.putBoolean(context, RE_LOGIN, true)
                ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation(mapActivity, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        mapActivity.finish()
                    }
                })
                CoroutineScope(uiContext).launch {
                    Toast.makeText(mapActivity, getString(R.string.login_invalidate), Toast.LENGTH_SHORT).show()
                }
            } else {
                if (it.code == 10009) {
                    if (System.currentTimeMillis() - currentTime > 10000) {
                        currentTime = System.currentTimeMillis()
                        endTeam(false)
                        CoroutineScope(uiContext).launch {
                            delay(1500)
                            if (mapActivity.mViewModel?.currentPosition == 1) {
                                mapActivity.mViewModel?.selectTab(1)
                            }
                            driverModel?.GoTeam()
                        }
                    }
                }
                CoroutineScope(uiContext).launch {
                    Toast.makeText(mapActivity, it?.msg, Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
        when (it?.type) {
            SocketDealType.HEART_BEAT.code -> {
                //心跳
                CoroutineScope(ioContext).async {
                    delay(10000)
                    var so = Soket()
                    so.type = SocketDealType.HEART_BEAT.code
                    var pos = ServiceEven()
                    pos.type = "sendData"
                    pos.gson = Gson().toJson(so) + "\\r\\n"
                    RxBus.default?.post(pos)
                }
            }
            SocketDealType.JOINTEAM.code -> {
                //发心跳
                if (markerListNumber.contains(it.userId.toString())) {
                    var marker = markerList[it.userId.toString()]
                    marker!!.remove()
                    markerListNumber.remove(it.userId.toString())
                    markerList.remove(it.userId.toString())
                }

                var so = Soket()
                so.type = 1003
                sendOrder(so)
                //查询信息
                var m = Soket()
                m.type = 1006
                m.teamCode = teamCode.get()
                sendOrder(m)
                mapActivity.mViewModel?.component!!.arrowVisible.set(false)
                mapActivity.mViewModel?.component!!.rightVisibleType.set(false)
                mapActivity.mViewModel?.component!!.rightText.set("")
                mapActivity.mViewModel?.component!!.rightIcon.set(context.getDrawable(R.drawable.ic_sousuo))
            }
            SocketDealType.SENDPOINT.code -> {
                //发送点
                var local = Gson().fromJson<LatLonLocal>(it.body, LatLonLocal::class.java)
                if (it.userId.toString() != mapActivity.user.data?.memberId) {
                    if (location != null) {
                        if (markerList.containsKey(it.userId.toString())) {
                            var marker = markerList[it.userId.toString()]
                            marker?.position = LatLng(local.latitude!!, local.longitude!!)
                            var title = marker?.title
                            var m = fomatDistance(LatLng(location?.latitude!!, location!!.longitude), LatLng(local.latitude!!, local.longitude!!))
                            marker?.title = title!!.split(",")[0] + "," + m
                            markerList.set(it.userId.toString(), marker!!)
//                            marker?.showInfoWindow()
                        }
                    }
//                    createImageMarker(LatLng(it.latitude,it.longitude),it.userId)
                }
            }
            SocketDealType.OFFLINE.code -> {
                if (markerListNumber.contains(it.userId.toString())) {
                    var maker = markerList[it.userId.toString()]
                    try {
                        maker?.remove()
                    } catch (ex: Throwable) {

                    }
                    markerList.remove(it.userId.toString())
                    markerListNumber.remove(it.userId.toString())
                }
                var m = Soket()
                m.type = 1006
                m.teamCode = teamCode.get()
                sendOrder(m)
            }
            SocketDealType.LEAVETEAM.code -> {
                //离开队伍
//                TeamInfo = Gson().fromJson<TeamPersonInfo>(it.body, TeamPersonInfo::class.java)
                if (it.userId.toString() == mapActivity.user.data?.memberId) {
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
                    sendOrder(m)
                }
            }
            SocketDealType.REJECTTEAM.code -> {
                var remove = Gson().fromJson<RemoveBody>(it.body, RemoveBody::class.java)
                var body = remove.userIds?.split(",")
                if (body?.contains(mapActivity.user.data?.memberId)!!) {
                    //判断自己是否在内
                    CoroutineScope(uiContext).launch {
                        Toast.makeText(mapActivity, getString(R.string.remove_by_team), Toast.LENGTH_SHORT).show()
                    }
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
                    sendOrder(m)
                }
            }
            SocketDealType.GETTEAMINFO.code -> {
                //获取信息
                if (teamFragment.isAdded) {
                    TeamInfo = Gson().fromJson<TeamPersonInfo>(it.body, TeamPersonInfo::class.java)
                    if (!it.body!!.isEmpty()) {
                        initInfo()
                    }
                    if (TeamInfo?.redisData?.navigationPoint != null) {
                        if (TeamInfo?.redisData?.navigationPoint?.contains("\\")!!) {
                            TeamInfo?.redisData?.navigationPoint = TeamInfo?.redisData?.navigationPoint?.replace("\\", "", true)
                        }
                        soketNavigation = Gson().fromJson<SoketNavigation>(TeamInfo?.redisData?.navigationPoint, SoketNavigation::class.java)
                        if (teamer.toString() == mapActivity.user.data?.id) {
                            teamNavigation.set(false)
                            districtTv.set("")
                        } else {
                            teamNavigation.set(true)
                            districtTv.set(soketNavigation!!.navigation_end?.aoiName)
                        }
                    } else {
                        if (driverModel?.status?.navigationType == 1 && teamer.toString() == mapActivity.user.data?.id) {
                            sendNavigationNotify()
                        }
                        districtTv.set("")
                        teamNavigation.set(false)
                    }
                }
//                createImageMarker(LatLng())
            }
            SocketDealType.TEAMER_PASS.code -> {
                var m = Soket()
                m.type = 1006
                m.teamCode = teamCode.get()
                sendOrder(m)
            }
            SocketDealType.UPDATETEAMINFO.code -> {
                //队伍信息修改了
                var m = Soket()
                m.type = 1006
                m.teamCode = teamCode.get()
                sendOrder(m)
                mapActivity.mViewModel?.component!!.arrowVisible.set(false)
                mapActivity.mViewModel?.component!!.rightVisibleType.set(false)
                mapActivity.mViewModel?.component!!.rightText.set("")
                mapActivity.mViewModel?.component!!.rightIcon.set(context.getDrawable(R.drawable.ic_sousuo))
            }
            SocketDealType.DISMISSTEAM.code -> {
                //解散队伍
                CoroutineScope(uiContext).launch {
                    Toast.makeText(context, "您的队伍已被解散！", Toast.LENGTH_SHORT).show()
                }
                AppManager.get()?.finishActivity(TeamSettingActivity::class.java)
                endTeam(false)
            }
            SocketDealType.UPDATE_MANAGER.code -> {
                var m = Soket()
                m.type = 1006
                m.teamCode = teamCode.get()
                sendOrder(m)
            }
            SocketDealType.NAVIGATION_START.code -> {
                var obj = JSONObject(it.body)
                var nav = obj.getString("navigationPoint")
                soketNavigation = Gson().fromJson<SoketNavigation>(nav, SoketNavigation::class.java)
                if (mapActivity.onStart && teamer.toString() != mapActivity.user.data?.id && driverModel?.status?.navigationType == 1) {
                    CoroutineScope(uiContext).launch {
                        createDistrictDialog()
//                        {"navigationPoint":"{\"wayPoint\":[],\"navigation_end\":{\"aoiName\":\"长沙县人民政府\",\"longitude\":113.07891494372052,\"latitude\":28.247158541709886}}"}
                    }
                } else {
                    RxBus.default?.post(soketNavigation!!)
                }
                var m = Soket()
                m.type = 1006
                m.teamCode = teamCode.get()
                sendOrder(m)
            }
        }
    }

    var currentTime = 0L

    var soketNavigation: SoketNavigation? = null

    var districtTv = ObservableField<String>()

    var teamNavigation = ObservableField<Boolean>(false)


    fun sendNavigationNotify() {
        var so = Soket()
        so.type = SocketDealType.NAVIGATION_START.code
        so.teamCode = teamCode.get()
        so.teamId = teamId
        so.userId = mapActivity.user.data?.memberId
        var sos = SoketNavigation()
        if (driverModel.status.passPointDatas != null) {
            driverModel.status.passPointDatas.forEach {
                sos.wayPoint!!.add(LatLonPoint(it.latitude, it.longitude))
            }
        }
        sos.navigation_end = driverModel.status?.navigationEndPoint
        so.body = Soket.SocketRequest()
        so.body?.navigationPoint = Gson().toJson(sos)
        var pos = ServiceEven()
        pos.type = "sendData"
        pos.gson = Gson().toJson(so) + "\\r\\n"
        RxBus.default?.post(pos)
    }


    fun initInfo() {
        if (TeamInfo != null && TeamInfo?.redisData != null && TeamInfo?.redisData?.createTime != null) {
            if (TeamInfo?.redisData?.teamName != null) {
                titleName.set(TeamInfo?.redisData?.teamName + "(" + TeamInfo?.redisData?.dtoList?.size + ")")
            }
            teamer = TeamInfo?.redisData?.teamer!!
            CoroutineScope(uiContext).launch {
                var simple = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                var d = Date(TeamInfo?.redisData?.validEndTime!!)
                date.set("有效期至：" + simple.format(d))
                addChildView(teamFragment.teamhorizontalLinear, TeamInfo?.redisData!!.dtoList)
            }
        } else {

        }
    }

    fun endTeam(flag: Boolean) {
//        removeDispose()


        var pos = ServiceEven()
        pos.type = "MinaServiceCancle"
        RxBus.default?.post(pos)


        if (flag) {
            if (mapActivity.user.data?.id == teamer.toString()) {
                var so = Soket()
                so.type = SocketDealType.DISMISSTEAM.code
                so.teamCode = teamCode.get()
                so.teamId = teamId
                so.userId = mapActivity.user.data?.id
                var pos = ServiceEven()
                pos.type = "sendData"
                pos.gson = Gson().toJson(so) + "\\r\\n"
                RxBus.default?.post(pos)
            } else {
                var so = Soket()
                so.type = SocketDealType.LEAVETEAM.code
                so.teamCode = teamCode.get()
                so.teamId = teamId
                so.userId = mapActivity.user.data?.memberId
                var pos = ServiceEven()
                pos.type = "sendData"
                pos.gson = Gson().toJson(so) + "\\r\\n"
                RxBus.default?.post(pos)
            }
        }

        driverModel.TeamStatus?.teamStatus = TeamNomal
        driverModel.TeamStatus?.resetTeam()
        teamId = null
        teamCode.set("")
        teamName = null
        teamer = 0
        teamFragment.create = null
        teamFragment.join = null
        markerListNumber.forEach {
            markerList[it]?.remove()
        }
        markerListNumber.clear()
        markerList.clear()
        if (driverModel?.status.startDriver.get() == TeamModel) {
            driverModel?.status.startDriver.set(DriverCancle)
            mapActivity.mViewModel?.component!!.Drivering.set(false)
        } else if (driverModel?.status.startDriver.get() == Drivering) {
            mapActivity.mViewModel?.component!!.Drivering.set(true)
        }
        mapActivity.mViewModel?.component!!.isTeam.set(false)
        driverModel.linearBg.set(Color.TRANSPARENT)
        driverModel.TeamStatus = null
        if (teamFragment.isAdded && teamFragment.teamhorizontalLinear != null) {
            CoroutineScope(uiContext).launch {
                mapActivity.dismissProgressDialog()
                teamFragment.teamhorizontalLinear.removeAllViews()
                teamFragment.teamhorizontalLinear.invalidate()
                delay(200)
                mapActivity?.mViewModel?.selectTab(0)
            }
        }
    }

    fun addDispose() {
//        RxSubscriptions.add(minaConnectedDispose)
//        RxSubscriptions.add(minaSoketDispose)
    }

    fun removeDispose() {
        minaSoketDispose?.dispose()
        minaSoketDispose = null
        minaConnectedDispose?.dispose()
        minaConnectedDispose = null
        RxSubscriptions.remove(minaConnectedDispose)
        RxSubscriptions.remove(minaSoketDispose)
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.team_setting -> {
                if (TeamInfo != null) {
                    ARouter.getInstance().build(RouterUtils.TeamModule.SETTING).withSerializable(RouterUtils.TeamModule.TEAM_INFO, TeamInfo).navigation()
                }
            }
        }
    }

    var notifyRouteChange: NormalDialog? = null
    fun createDistrictDialog() {
        if (notifyRouteChange == null) {
            notifyRouteChange = DialogUtils.createNomalDialog(mapActivity, getString(R.string.route_change), getString(R.string.ignore), getString(R.string.change_route))
            notifyRouteChange!!.setOnBtnClickL(OnBtnClickL {
                notifyRouteChange?.dismiss()
            }, OnBtnClickL {
                notifyRouteChange?.dismiss()
                mapActivity.getTeamFragment()?.viewModel?.backToDriver()
                driverModel?.backStatus = true
                if (driverModel?.status?.navigationType == 1) {
                    var list = ArrayList<LatLng>()
                    if (soketNavigation?.wayPoint != null && !soketNavigation?.wayPoint!!.isEmpty()) {
                        soketNavigation?.wayPoint!!.forEach {
                            list.add(AMapUtil.convertToLatLng(it))
                        }
                    }
                    driverModel?.status!!.navigationEndPoint = soketNavigation?.navigation_end
                    driverModel?.startNavi(driverModel?.status!!.navigationStartPoint!!, driverModel?.status!!.navigationEndPoint!!, list, false)
                    RxBus.default?.post(soketNavigation!!)
                } else {
                    toNavi(false)
                }
            })
        }
        notifyRouteChange?.show()
    }

    fun toNavi(flag: Boolean) {
//        mapActivity.getTeamFragment()?.viewModel?.backToDriver()
//        driverModel?.backStatus = true

        if (teamer.toString() == mapActivity.user.data?.id) {
            var list = ArrayList<LatLng>()
            if (!driverModel?.status.passPointDatas.isEmpty()) {
                driverModel?.status.passPointDatas.forEach {
                    list.add(LatLng(it.latitude, it.longitude))
                }
            }
            driverModel?.status!!.navigationEndPoint = soketNavigation?.navigation_end
            driverModel?.startNavi(driverModel?.status!!.navigationStartPoint!!, driverModel?.status!!.navigationEndPoint!!, list, flag)

        } else {
            if (soketNavigation != null) {
                driverModel?.status.navigationEndPoint = soketNavigation?.navigation_end
                if (!soketNavigation?.wayPoint.isNullOrEmpty()) {
                    soketNavigation?.wayPoint!!.forEach {
                        driverModel?.status.passPointDatas.add(Location(it.latitude, it.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F))
                    }
                }
                if (driverModel?.status.startDriver.get() == DriverCancle) {
                    driverModel?.startDrive(true)
                } else {
                    var list = ArrayList<LatLng>()
                    if (!driverModel?.status.passPointDatas.isEmpty()) {
                        driverModel?.status.passPointDatas.forEach {
                            list.add(LatLng(it.latitude, it.longitude))
                        }
                    }
                    driverModel?.startNavi(driverModel?.status!!.navigationStartPoint!!, driverModel?.status!!.navigationEndPoint!!, list, false)
                }
            }
        }
    }

    var personDatas = ObservableArrayList<TeamPersonnelInfoDto>().apply {
        this.add(TeamPersonnelInfoDto())
    }
    var shareDialog: AlertDialog? = null
    fun addChildView(layout: LinearLayout?, dtoList: MutableList<TeamPersonnelInfoDto>) {
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
                //                createImageMarker(LatLng(driverModel?.status.driverStartPoint?.latitude!!, driverModel?.status.driverStartPoint?.longitude!!), getImageUrl(it.memberHeaderUrl))

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
                    if (it.memberId.toString() != mapActivity.user.data?.id) {
                        createImageMarker(it)

                    } else {

                    }
                }
                personDatas.add(it)
            }
        }

        if (layout != null) {
            layout.removeAllViews()
        }
        personDatas.forEachIndexed { i, entity ->
            var inflater = mapActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflater.inflate(R.layout.horizontal_team_person_child, layout, false)
            view.setOnClickListener {
                if (i == 0) {
                    if (TeamInfo?.redisData?.dtoList?.size == TeamInfo?.redisData?.teamMaxCount) {
                        Toast.makeText(context, getString(R.string.max_member) + TeamInfo?.redisData?.teamMaxCount + "人", Toast.LENGTH_SHORT).show()
                    } else {
                        if (shareDialog == null) {
                            shareDialog = DialogUtils.createShareDialog(mapActivity, "", "")
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
                        mapActivity.mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(maker!!.position, 15F), 1000, object : AMap.CancelableCallback {
                            override fun onFinish() {
                            }

                            override fun onCancel() {
                            }
                        })
                    } else if (mapActivity.user?.data?.memberId == entity.memberId.toString()) {
                        if (mapActivity.getDrverFragment().curPoint != null) {
                            mapActivity.mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mapActivity.getDrverFragment().curPoint!!.latitude, mapActivity.getDrverFragment()!!.curPoint!!.longitude), 15F), 1000, object : AMap.CancelableCallback {
                                override fun onFinish() {
                                }

                                override fun onCancel() {
                                }
                            })
                        }
                    }
                }
            }
            var card = view.findViewById<CardView>(R.id.cardview)

            var img = view.findViewById<ImageView>(R.id.img_load)

            var teamName = view.findViewById<TextView>(R.id.team_name)

            var userName = view.findViewById<TextView>(R.id.user_name)
            var percen = view.findViewById<ImageView>(R.id.img_load_sevenPerson)
            if (entity.status == "0") {
                percen.visibility = View.VISIBLE
            } else {
                percen.visibility = View.GONE
            }
            if (entity.teamRoleId == 0 || entity.teamRoleId == 4) {
                teamName.setBackgroundColor(Color.TRANSPARENT)
            } else if (entity.teamRoleId == 1) {
                teamName.setBackgroundResource(R.drawable.little_tv_color_corner)
            } else if (entity.teamRoleId > 1) {
                teamName.setBackgroundResource(R.drawable.little_tv_color_corner_yellow)
            }
            if (i == 0) {
                userName.text = getString(R.string.invite)
                img.setImageResource(R.drawable.team_first)
            } else {
                var corners = CircleCrop()
                var options = RequestOptions().transform(corners).error(R.drawable.default_avatar).timeout(3000).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.dp2px(55F), ConvertUtils.dp2px(55F))
                Glide.with(img.context).asBitmap().load(getImageUrl(entity?.memberHeaderUrl)).apply(options).into(img)
                if (mapActivity.user.data!!.memberId == entity?.memberId.toString()) {
                    card.setCardBackgroundColor(getColor(R.color.line_color))
                } else {
                    card.setCardBackgroundColor(getColor(R.color.TenpercentBlackColor))
                }
                teamName.text = entity.teamRoleName
                if (entity.memberName == null || entity.memberName.trim().isEmpty()) {
                    userName.text = getString(R.string.secret_name)
                } else {
                    userName.text = entity.memberName
                }
            }
            layout?.addView(view)
            layout?.invalidate()
        }
        mapActivity.dismissProgressDialog()
    }


    fun shareWx(type: Int) {

        if (TeamInfo == null) {
            return
        }

        CoroutineScope(ioContext).async {
            var file = Glide.with(mapActivity)
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

    fun fomatDistance(position: LatLng, markerPosition: LatLng): String {

        var s = AMapUtils.calculateLineDistance(position, markerPosition)

        var distanceTv = ""
        if (s > 1000) {
            distanceTv = DecimalFormat("0.0").format(s / 1000) + "KM"
        } else {
            distanceTv = DecimalFormat("0.0").format(s) + "M"
        }
        return distanceTv
    }

    fun WriteToServer() {

    }

}