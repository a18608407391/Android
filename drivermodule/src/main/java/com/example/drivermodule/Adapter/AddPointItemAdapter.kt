package com.example.drivermodule.Adapter

import android.support.v7.widget.RecyclerView
import com.amap.api.services.core.LatLonPoint
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.elder.zcommonmodule.converLatPoint
import com.example.drivermodule.Controller.MapPointItemModel
import com.example.drivermodule.Entity.PointEntity
import com.example.drivermodule.R

class AddPointItemAdapter : BaseItemDraggableAdapter<PointEntity, BaseViewHolder>, OnItemDragListener {
    var start = 0
    override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
        if (start != pos) {
            var s = mapPointmodel?.pointList[start]
            var m = mapPointmodel?.pointList[pos]
            mapPointmodel.pointList.set(start, m)
            mapPointmodel.pointList.set(pos, s)
            var q = mapPointmodel.viewModel?.status!!.passPointDatas[start]
            var w = mapPointmodel.viewModel?.status!!.passPointDatas[pos]
            mapPointmodel.viewModel?.status!!.passPointDatas.set(start, w)
            mapPointmodel.viewModel?.status!!.passPointDatas.set(pos, q)
            mapPointmodel.mapFr?.mapUtils?.setDriverRoute(converLatPoint(mapPointmodel.startMaker?.position!!), LatLonPoint(mapPointmodel.viewModel?.status.navigationEndPoint?.latitude!!, mapPointmodel.viewModel?.status.navigationEndPoint?.longitude!!),mapPointmodel.viewModel?.status!!.passPointDatas)
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

    lateinit var mapPointmodel :MapPointItemModel
    fun setModel(mapPointmodel :MapPointItemModel){
        this.mapPointmodel  = mapPointmodel
    }
}
