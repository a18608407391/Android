package com.example.drivermodule.ViewModel

import android.util.Log
import android.view.View
import com.amap.api.maps.*
import com.amap.api.maps.model.*
import com.example.drivermodule.Activity.SmoothActivity
import com.example.drivermodule.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.OSUtil
import kotlinx.android.synthetic.main.activity_smooth.*
import org.cs.tec.library.Base.Utils.context
import com.amap.api.maps.utils.SpatialRelationUtil
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.amap.api.trace.TraceListener
import com.amap.api.trace.TraceLocation
import com.example.drivermodule.AMapUtil
import com.elder.zcommonmodule.SmoothOverLay
import org.cs.tec.library.Base.Utils.getColor


class SmoothViewModel : BaseViewModel(), TraceListener, AMap.OnMarkerDragListener, RouteSearch.OnRouteSearchListener {
    override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {
        var path = p0?.paths
        var point = ArrayList<LatLng>()
        var steplist = ArrayList<LatLonPoint>()
        var line = ArrayList<PolylineOptions>()
        var derection = ArrayList<Float>()
        path?.get(0)?.steps?.forEach {
            var step = it
            var op = ArrayList<LatLng>()
            step.polyline.forEach {
                point.add(AMapUtil.convertToLatLng(it))
                op.add(AMapUtil.convertToLatLng(it))
                derection.add(getDirection(step.orientation))
            }
            var opition = PolylineOptions()
            opition.color(getColor(R.color.line_color)).width(18F).points = op
            line.add(opition)
            steplist.add(it.polyline[0])
        }

        var marker = smoothActivity.smoothmap.map.addMarker(MarkerOptions().position(LatLng(point[0].latitude, point[0].longitude)).zIndex(2f)
                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked)))

        moving = SmoothOverLay(smoothActivity.smoothmap.map, marker)
        smoothActivity.smoothmap.map.moveCamera(CameraUpdateFactory.changeLatLng(point[0]))
        var start = point[0]
        val pair = SpatialRelationUtil.calShortestDistancePoint(point, start)
        point.set(pair.first, start)
        moving.setVisible(true)
        moving.setPoints(point)
        moving.setTotalDuration(120)
        moving.startSmoothMove()
        var count = 0
        var opition = PolylineOptions()
        opition.color(getColor(R.color.line_color)).width(14F)
        var poi = smoothActivity.smoothmap.map.addPolyline(opition)
        var list = ArrayList<LatLng>()
        list.add(point[0])
        var Aroute = 0F
        count = 0
        var number = 0
        var currentIndex = 0
        var routeAngel = 0F

        var time = System.currentTimeMillis()
        moving.setAllTimeListener {
            if (System.currentTimeMillis() - time > 400) {
                time = System.currentTimeMillis()
//                smoothActivity.smoothmap.map.animateCamera(CameraUpdateFactory.changeLatLng(moving.position))
                smoothActivity.smoothmap.map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(moving.position, 18F, 50F, derection[it])), 500, object : AMap.CancelableCallback {
                    override fun onFinish() {
                    }

                    override fun onCancel() {
                    }

                })
            }


//            if (it != currentIndex) {
//                currentIndex = it
//                if (routeAngel != moving.`object`.rotateAngle) {
//                    routeAngel -= moving.`object`.rotateAngle % 360
//                    moving.stopMove()
//                    smoothActivity.smoothmap.map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(moving.position, 18F, 50F, routeAngel)), 500, object : AMap.CancelableCallback {
//                        override fun onFinish() {
//                            moving.startSmoothMove()
//                        }
//
//                        override fun onCancel() {
//
//                        }
//                    })
//                }
//            } else {
//                smoothActivity.smoothmap.map.moveCamera(CameraUpdateFactory.changeLatLng(moving.position))
//            }


//            if (it > count) {
//                if (steplist.contains(converLatPoint(point[it]))) {
//                    Log.e("result", "包含")
//                    var t = steplist.indexOf(converLatPoint(point[it]))
//                    var Aroute = getDirection(path!![0]!!.steps[t].orientation)
//                    smoothActivity.smoothmap.map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(point[it], 18F, 50F, Aroute)))
//                } else if (steplist.contains(converLatPoint(point[it + 1]))) {
//                    var t = steplist.indexOf(converLatPoint(point[it + 1]))
//                    var Aroute = getDirection(path!![0]!!.steps[t].orientation)
//                    smoothActivity.smoothmap.map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(point[it + 1], 18F, 50F, Aroute)), 600, object : AMap.CancelableCallback {
//                        override fun onFinish() {
//                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                        }
//
//                        override fun onCancel() {
//                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                        }
//                    })
//                }
//                count = it
//            }
            list.add(moving.position)
            poi.points = list
        }
    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
    }

    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
    }

    override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
    }

    lateinit var moving: SmoothOverLay
    override fun onMarkerDragEnd(p0: Marker?) {
        Log.e("result", "onMarkerDragEnd")
    }

    override fun onMarkerDragStart(p0: Marker?) {
        Log.e("result", "onMarkerDragStart")
    }

    override fun onMarkerDrag(p0: Marker?) {
        Log.e("result", "onMarkerDrag")
    }

    override fun onRequestFailed(p0: Int, p1: String?) {
        Log.e("result", "onRequestFailed" + p1 + "p0" + p0)
    }

    override fun onTraceProcessing(p0: Int, p1: Int, p2: MutableList<LatLng>?) {
        Log.e("result", "onTraceProcessing" + p1 + "p0" + p0)
    }

    override fun onFinished(p0: Int, list: MutableList<LatLng>, p2: Int, p3: Int) {
        var point = ArrayList<LatLng>()
        list.forEach {
            point.add(LatLng(it.latitude, it.longitude))
        }
        var marker = smoothActivity.smoothmap.map.addMarker(MarkerOptions().position(LatLng(point[0].latitude, point[0].longitude)).zIndex(2f)
                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked)))
        moving = SmoothOverLay(smoothActivity.smoothmap.map, marker)
        smoothActivity.smoothmap.map.moveCamera(CameraUpdateFactory.changeLatLng(point[0]))
        var start = point[0]
        val pair = SpatialRelationUtil.calShortestDistancePoint(point, start)
        point.set(pair.first, start)
        moving.setVisible(true)
        moving.setPoints(point)
        moving.setTotalDuration(60)
        moving.startSmoothMove()
        var routeAngel = 0F
        var listp = ArrayList<LatLng>()
        var opition = PolylineOptions()
        opition.color(getColor(R.color.line_color)).width(14F)
        var poi = smoothActivity.smoothmap.map.addPolyline(opition)
        var onFinish = true
        var startTime = System.currentTimeMillis()
        smoothActivity.smoothmap.map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(start, 18F, 50F, routeAngel)))
//        smoothActivity.smoothmap.map.moveCamera(CameraUpdateFactory.zoomBy(100F))
        moving.setAllTimeListener {
            var angel = moving.`object`.rotateAngle
            var realRoute = angel % 360
            Log.e("result", realRoute.toString() + "当前角度" +  smoothActivity.smoothmap.map.cameraPosition.bearing)
            if (routeAngel != realRoute) {
              routeAngel = getDirectionArea(realRoute)
            }
            if (System.currentTimeMillis() - startTime > 400) {
                startTime = System.currentTimeMillis()
                smoothActivity.smoothmap.map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(moving.position, 18F, 50F, routeAngel)), 500, object : AMap.CancelableCallback {
                    override fun onFinish() {
                    }

                    override fun onCancel() {
                    }
                })
            }
            listp.add(moving.position)
            poi.points = listp
        }


//        var mRoutePath = RouteSearch(smoothActivity)
//        mRoutePath.setRouteSearchListener(this)
//        var startPoint = list[0]
//        var endPoint = list[list.size - 1]
//        var wayPassPoint = ArrayList<LatLonPoint>()
//        var space = list.size / 16
//        list.forEachIndexed { index, latLng ->
//            if (index != 0 && index % space == 0) {
//                wayPassPoint.add(converLatPoint(latLng))
//            }
//        }
//
//        var fromAndTo = RouteSearch.FromAndTo(converLatPoint(startPoint), converLatPoint(endPoint))
//        var query = RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION,
//                wayPassPoint, null, "")// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
//        mRoutePath.calculateDriveRouteAsyn(query)// 异步路径规划驾车模式查询
    }

    lateinit var smoothActivity: SmoothActivity
    fun inject(smoothActivity: SmoothActivity) {
        this.smoothActivity = smoothActivity
        var trace = Gson().fromJson<ArrayList<TraceLocation>>(OSUtil.getJson(context, "location2.json"), object : TypeToken<ArrayList<TraceLocation>>() {}.type)
        var list = ArrayList<TraceLocation>()
        trace.forEach {
            var t = TraceLocation()
            t.speed = it.speed
            t.bearing = it.bearing
            Log.e("result",t.bearing.toString() + "角度问题")
            t.time = it.time
            t.latitude = it.longitude
            t.longitude = it.latitude
            list.add(t)
        }
//        var mTraceClient = LBSTraceClient(context)
//        mTraceClient.queryProcessedTrace(1149849, list, LBSTraceClient.TYPE_AMAP, this)

        var point = ArrayList<LatLng>()
        list.forEach {
            point.add(LatLng(it.latitude, it.longitude))
        }
        var marker = smoothActivity.smoothmap.map.addMarker(MarkerOptions().position(LatLng(point[0].latitude, point[0].longitude)).zIndex(2f)
                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked)))
        moving = SmoothOverLay(smoothActivity.smoothmap.map, marker)
        smoothActivity.smoothmap.map.moveCamera(CameraUpdateFactory.changeLatLng(point[0]))
        var start = point[0]
        val pair = SpatialRelationUtil.calShortestDistancePoint(point, start)
        point.set(pair.first, start)
        moving.setVisible(true)
        moving.setPoints(point)
        moving.setTotalDuration(10)
        moving.startSmoothMove()
        var routeAngel = 0F
        var listp = ArrayList<LatLng>()
        var opition = PolylineOptions()
        opition.color(getColor(R.color.line_color)).width(14F)
        var poi = smoothActivity.smoothmap.map.addPolyline(opition)
        var onFinish = true
        var startTime = System.currentTimeMillis()
        smoothActivity.smoothmap.map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(start, 18F, 50F, routeAngel)))
//        smoothActivity.smoothmap.map.moveCamera(CameraUpdateFactory.zoomBy(100F))
        moving.setAllTimeListener {
            var angel = moving.`object`.rotateAngle
            var realRoute = angel % 360
            Log.e("result", realRoute.toString() + "当前角度" +  smoothActivity.smoothmap.map.cameraPosition.bearing)
                routeAngel = getDirectionArea(list[it].bearing)
            if (System.currentTimeMillis() - startTime > 400) {
                startTime = System.currentTimeMillis()
                smoothActivity.smoothmap.map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(moving.position, 18F, 50F, routeAngel)), 500, object : AMap.CancelableCallback {
                    override fun onFinish() {
                    }

                    override fun onCancel() {
                    }
                })
            }
            listp.add(moving.position)
            poi.points = listp
        }
    }

    fun onClick(view: View) {

    }

    fun getDirection(or: String): Float {
        if (or == "北") {
            return 0F
        } else if (or == "东") {
            return 90F
        } else if (or == "南") {
            return 180F
        } else if (or == "西") {
            return 270F
        } else if (or == "东北") {
            return 45F
        } else if (or == "东南") {
            return 135F
        } else if (or == "西南") {
            return 225F
        } else if (or == "西北") {
            return 315F
        } else {
            return 0F
        }
    }


    fun getDirectionArea(route: Float): Float {
        if (route > 337.5 || route < 22.5) {
            return 0F
        } else if (22.5 <= route && route < 67.5) {
            //东北
            return 45F
        } else if (67.5 <= route && route < 112.5) {
            //东
            return 90F
        } else if (112.5 <= route && route < 157.5) {
            //东南
            return 135F
        } else if (157.5 < route && route < 202.5) {
            return 180F
        } else if (202.5 < route && route < 247.5) {
            return 225F
        } else if (247.5 < route && route < 292.5) {
            return 270F
        } else if (292.5 < route && route < 337.5) {
            return 315F
        } else {
            return 0F
        }
    }
}