package com.example.drivermodule.Entity

import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import java.io.Serializable


data class RoadDetailEntity(var img: ObservableField<Drawable>, var name: ObservableField<String>, var address: ObservableField<String>) {
    private var index = 0
    fun setIndex(index: Int) {
        this.index = index
    }

    fun getIndex(): Int {
        return index
    }

}



