package com.zk.library.Worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters


class WorkController : Worker {


    constructor(context: Context, request: WorkerParameters) : super(context, request)

    override fun doWork(): Result {

        return Result.success()
    }


}