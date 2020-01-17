package com.elder.zcommonmodule.Utils;

import android.support.annotation.NonNull;

import com.elder.zcommonmodule.Entity.Location;

public class LatLngPoint implements Comparable<LatLngPoint> {
    /**
     * 用于记录每一个点的序号
     */
    public int id;
    /**
     * 每一个点的经纬度
     */
    public Location latLng;

    public LatLngPoint(int id,Location latLng){
        this.id = id;
        this.latLng = latLng;
    }

    @Override
    public int compareTo(@NonNull LatLngPoint o) {
        if (this.id < o.id) {
            return -1;
        } else if (this.id > o.id)
            return 1;
        return 0;
    }
}