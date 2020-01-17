package com.elder.zcommonmodule.Entity

import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import java.io.Serializable


data class WeatherEntity(var icon: ObservableField<Drawable>, var time: ObservableField<String>, var temperature: ObservableField<String>) : Serializable