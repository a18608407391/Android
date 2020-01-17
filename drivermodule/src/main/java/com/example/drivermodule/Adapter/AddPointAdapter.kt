package com.example.drivermodule.Adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.help.Tip
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.elder.zcommonmodule.converLatPoint
import com.example.drivermodule.Entity.PointEntity
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.DriverViewModel
import com.example.drivermodule.ViewModel.MapPointViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.district_item.view.*

class AddPointAdapter : BaseItemDraggableAdapter<PointEntity, BaseViewHolder>, OnItemDragListener {
    var start = 0
    override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
        if (start != pos) {
            var s = driverViewModel?.mapPointController.pointList[start]
            var m = driverViewModel?.mapPointController.pointList[pos]


            Log.e("result","问题1"+ Gson().toJson(driverViewModel.mapPointController.pointList))

            driverViewModel.mapPointController.pointList.set(start, m)
            driverViewModel.mapPointController.pointList.set(pos, s)
//            setDatas(driverViewModel.mapPointController.pointList)
            Log.e("result","问题2" +  Gson().toJson(driverViewModel.mapPointController.pointList))
            var q = driverViewModel.mapActivity.getDrverFragment().viewModel?.status!!.passPointDatas[start]

            var w = driverViewModel.mapActivity.getDrverFragment().viewModel?.status!!.passPointDatas[pos]

            driverViewModel.mapActivity.getDrverFragment().viewModel?.status!!.passPointDatas.set(start, w)
            driverViewModel.mapActivity.getDrverFragment().viewModel?.status!!.passPointDatas.set(pos, q)
            driverViewModel.mapActivity?.mapUtils?.setDriverRoute(converLatPoint(driverViewModel?.mapPointController.startMaker?.position!!), LatLonPoint(driverViewModel.driverModel?.status.navigationEndPoint?.latitude!!, driverViewModel.driverModel?.status.navigationEndPoint?.longitude!!), driverViewModel.mapActivity.getDrverFragment().viewModel?.status!!.passPointDatas)

        }
    }

    override fun onItemDragMoving(source: RecyclerView.ViewHolder?, from: Int, target: RecyclerView.ViewHolder?, to: Int) {

    }

    override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
        start = pos
    }

    var data: ArrayList<PointEntity>

    constructor(id: Int, data: ArrayList<PointEntity>) : super(id, data) {
        this.data = data
    }

    fun setDatas(data: ArrayList<PointEntity>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun convert(helper: BaseViewHolder?, item: PointEntity?) {
        helper?.addOnClickListener(R.id.point_delete)
        var point = item?.point
        if (point?.latitude == 0.0 && point?.longitude == 0.0) {
            helper!!.setVisible(R.id.point_delete, false)
            helper!!.setVisible(R.id.point_arrow, false)
        } else {
            helper!!.setVisible(R.id.point_delete, true)
            helper!!.setVisible(R.id.point_arrow, true)
        }
        helper!!.setText(R.id.point_address, item?.address)
    }

    lateinit var driverViewModel: MapPointViewModel
    fun setModel(driverViewModel: MapPointViewModel) {
        this.driverViewModel = driverViewModel
    }
}