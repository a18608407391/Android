package com.example.drivermodule

import com.amap.api.navi.model.AMapCalcRouteResult

interface CalculateRouteListener {

    fun CalculateCallBack(p0: AMapCalcRouteResult)
}