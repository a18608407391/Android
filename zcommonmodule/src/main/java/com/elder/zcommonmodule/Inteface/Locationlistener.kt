package com.elder.zcommonmodule.Inteface

import com.amap.api.location.AMapLocation


interface Locationlistener {
    fun onLocation(location: AMapLocation)
}