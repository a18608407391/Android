package com.example.drivermodule.Adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.converLatPoint
import com.example.drivermodule.Entity.PointEntity
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.DriverViewModel
import com.google.gson.Gson


class PointChangeAdapter : BaseItemDraggableAdapter<PointEntity, BaseViewHolder>, OnItemDragListener {

    lateinit var driverViewModel: DriverViewModel
    override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
        driverViewModel.pointAdapter.setDatas(data)
        driverViewModel.status.passPointDatas.clear()
        data.forEach {
            driverViewModel.status.passPointDatas.add(Location(it.point!!.latitude,it.point!!.longitude,System.currentTimeMillis().toString(),0F,0.0,0F,it.address!!) )
        }
    }

    override fun onItemDragMoving(source: RecyclerView.ViewHolder?, from: Int, target: RecyclerView.ViewHolder?, to: Int) {
        if (from != to) {

//            var first = driverViewModel.pointList[from]
//            var end = driverViewModel.pointList[to]
//            driverViewModel.pointList.set(to,first)
//            driverViewModel.pointList.set(from,end)
//            driverViewModel.pointAdapter.setDatas(driverViewModel.pointList)
        }
    }

    override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
        Log.e("result", data[pos].address + "当前" + Gson().toJson(data))
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
        helper!!.setText(R.id.point_address, item?.address)
        helper!!.setVisible(R.id.point_delete, true)
        helper.addOnClickListener(R.id.point_delete)
    }

    fun setModel(driverViewModel: DriverViewModel) {
        this.driverViewModel = driverViewModel
    }
}