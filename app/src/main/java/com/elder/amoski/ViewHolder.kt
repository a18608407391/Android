package com.elder.amoski

import android.support.design.widget.Snackbar
import android.databinding.adapters.SeekBarBindingAdapter.setProgress
import com.liulishuo.filedownloader.util.FileDownloadUtils.getTargetFilePath
import com.liulishuo.filedownloader.BaseDownloadTask
import android.widget.TextView
import android.widget.ProgressBar
import java.lang.ref.WeakReference


private class ViewHolder(private val weakReferenceContext: WeakReference<SplashActivity>?,
                         private val pb: ProgressBar, private val detailTv: TextView?, private val speedTv: TextView,
                         private val position: Int) {
    private var filenameTv: TextView? = null

    fun setFilenameTv(filenameTv: TextView) {
        this.filenameTv = filenameTv
    }

    private fun updateSpeed(speed: Int) {
        speedTv.text = String.format("%dKB/s", speed)
    }

    fun updateProgress(sofar: Int, total: Int, speed: Int) {
        if (total == -1) {
            // chunked transfer encoding data
            pb.isIndeterminate = true
        } else {
            pb.max = total
            pb.progress = sofar
        }

        updateSpeed(speed)

        if (detailTv != null) {
            detailTv.text = String.format("sofar: %d total: %d", sofar, total)
        }
    }

    fun updatePending(task: BaseDownloadTask) {
        if (filenameTv != null) {
            filenameTv!!.text = task.filename
        }
    }

    fun updatePaused(speed: Int) {
        toast(String.format("paused %d", position))
        updateSpeed(speed)
        pb.isIndeterminate = false
    }

    fun updateConnected(etag: String, filename: String) {
        if (filenameTv != null) {
            filenameTv!!.text = filename
        }
    }

    fun updateWarn() {
        toast(String.format("warn %d", position))
        pb.isIndeterminate = false
    }

    fun updateError(ex: Throwable, speed: Int) {
        toast(String.format("error %d %s", position, ex))
        updateSpeed(speed)
        pb.isIndeterminate = false
        ex.printStackTrace()
    }

    fun updateCompleted(task: BaseDownloadTask) {

        toast(String.format("completed %d %s", position, task.targetFilePath))

        if (detailTv != null) {
            detailTv.text = String.format("sofar: %d total: %d",
                    task.smallFileSoFarBytes, task.smallFileTotalBytes)
        }

        updateSpeed(task.speed)
        pb.isIndeterminate = false
        pb.max = task.smallFileTotalBytes
        pb.progress = task.smallFileSoFarBytes
    }

    private fun toast(msg: String) {
        if (this.weakReferenceContext != null && this.weakReferenceContext!!.get() != null) {
//            Snackbar.make(weakReferenceContext!!.get().startBtn1, msg, Snackbar.LENGTH_LONG).show()
        }
    }

}