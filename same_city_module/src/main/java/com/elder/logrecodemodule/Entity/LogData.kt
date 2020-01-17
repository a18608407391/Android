package com.elder.logrecodemodule.Entity

import android.databinding.ObservableField
import android.view.View


class LogData {
    var month = ObservableField<String>()
    var BigMoth = ObservableField<String>()
    var isChecked = ObservableField<Boolean>(false)

    fun onClick(view: View) {
        if (isChecked.get() == true) {
            isChecked.set(false)
        } else {
            isChecked.set(true)
        }
    }
}