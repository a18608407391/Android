package com.elder.zcommonmodule.Entity

import android.databinding.ObservableField
import java.io.Serializable


data class UIdeviceInfo(var date: ObservableField<String>, var img: ObservableField<String>, var distance: ObservableField<String>, var time: ObservableField<String>, var avar: ObservableField<String>, var fileUrl: ObservableField<String>? = null, var ridingEndBackgroudImg: ObservableField<String> = ObservableField(""), var id: ObservableField<String>, var baseUrl: ObservableField<String>) : Serializable

