package com.zk.library.Worker

import android.os.Build
import android.support.annotation.RequiresApi
import androidx.work.*
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*


val dateFormat = SimpleDateFormat("hh:mm:ss", Locale.getDefault())

var requestData = Data.Builder().putString("start", dateFormat.format(Date())).build()
val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
@RequiresApi(Build.VERSION_CODES.O)
var WorkRequest = PeriodicWorkRequestBuilder<WorkController>(Duration.ofMinutes(5)).setConstraints(constraints).setInputData(requestData).build()



