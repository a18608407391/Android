package com.example.drivermodule.Ui

import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.elder.zcommonmodule.DriverCancle
import com.elder.zcommonmodule.Entity.SoketBody.CreateTeamInfoDto
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonnelInfoDto
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.TeamModel
import com.elder.zcommonmodule.TeamStarting
import com.example.drivermodule.Activity.MapActivity
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.TeamViewModel
import com.example.drivermodule.databinding.FragmentTeamBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.fragment_team.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus


@Route(path = RouterUtils.MapModuleConfig.TEAM_FR)
class TeamFragment : BaseFragment<FragmentTeamBinding, TeamViewModel>() {
    var create: CreateTeamInfoDto? = null
    var join: TeamPersonnelInfoDto? = null
    var type: String = ""
    override fun initContentView(): Int {
        return R.layout.fragment_team
    }
    override fun initVariableId(): Int {
        return BR.team_model
    }
    var xF = 0F
    override fun initData() {
        super.initData()
        Log.e("team","initData")
        var mapActivity = activity as MapActivity
        viewModel?.inject(this, mapActivity)
        scroller.setOnDragListener { v, event ->
            return@setOnDragListener false
        }
        scroller.setOnTouchListener { v, event ->
            var action = event.action
            var x = event.x
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    xF = x
                }
                MotionEvent.ACTION_UP -> {
                    if (x.toInt() - xF.toInt() > 0) {
                        xF = 0F
                        viewModel?.visibleScroller!!.set(true)
                    } else if (x.toInt() - xF.toInt() < 0) {
                        viewModel?.visibleScroller!!.set(false)
                        xF = 0F
                    } else {
                        viewModel?.toNavi(true)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                }
            }
            return@setOnTouchListener true
        }
    }
    fun doCreate(data: Intent?) {
        if (data != null) {
            type = data.extras.getString("type")
            if (type == "create") {
                doDialog()
                create = data.getSerializableExtra("data") as CreateTeamInfoDto?
                var pos = ServiceEven()
                pos.type = "splashContinue"
                RxBus.default?.post(pos)
            } else if (type == "join") {
                doDialog()
                join = data.getSerializableExtra("data") as TeamPersonnelInfoDto?
                var pos = ServiceEven()
                pos.type = "splashContinue"
                RxBus.default?.post(pos)
            } else if (type == "cancle") {
                if ((activity as MapActivity).mViewModel?.currentPosition != 0) {
                    (activity as MapActivity).mViewModel?.changerFragment(0)
                    (activity as MapActivity).mViewModel?.selectTab(0)
                } else {
                    (activity as MapActivity).mViewModel?.selectTab(0)
                }
            }
        } else {
            (activity as MapActivity).mViewModel?.selectTab(0)
        }
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
    var isClick = false
    fun doDialog() {
        CoroutineScope(uiContext).launch {
            var dia = viewModel?.mapActivity?.showProgressDialog(getString(R.string.getTeamInfoLoading))
            delay(10000)
            if (dia?.isShowing!!) {
                dia.dismiss()
                viewModel?.endTeam(false)
                Toast.makeText(context, getString(R.string.get_info_error), Toast.LENGTH_SHORT).show()
            }
        }
        if (viewModel?.driverModel?.status?.startDriver?.get() == DriverCancle) {
            viewModel?.driverModel?.status?.startDriver?.set(TeamModel)
        }
        viewModel?.addDispose()
        viewModel?.driverModel?.TeamStatus?.teamStatus = TeamStarting
        viewModel?.mapActivity?.mViewModel?.component?.isTeam?.set(true)
        viewModel?.mapActivity?.mViewModel?.component?.Drivering?.set(true)
    }
    fun MapClick(p0: LatLng?) {
        var flag = false
        viewModel?.markerListNumber?.forEach {
            if (viewModel?.markerList!![it]?.isInfoWindowShown!!) {
                if (!isClick) {
                    viewModel?.markerList!![it]?.hideInfoWindow()
                    flag = true
                } else {
                    flag = false
                }
            }
        }
        isClick = flag
    }
}