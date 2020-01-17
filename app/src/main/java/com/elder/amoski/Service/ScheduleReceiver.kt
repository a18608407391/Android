package com.elder.amoski.Service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.elder.zcommonmodule.Service.SERVICE_AUTO_BOOT_COMPLETED
import com.elder.zcommonmodule.Service.SERVICE_CREATE
import com.zk.library.Utils.PreferenceUtils


class ScheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var action = intent?.action
        when (action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                PreferenceUtils.putBoolean(context, SERVICE_AUTO_BOOT_COMPLETED, true)
                var service = Intent(context, LowLocationService::class.java)
                service.action = SERVICE_CREATE
                context?.startService(service)
            }

        }
    }
}