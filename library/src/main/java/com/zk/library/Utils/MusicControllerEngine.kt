package org.cs.tec.library.Utils

import com.zk.library.Base.BaseApplication
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.event.MusicServiceEvent


class MusicControllerEngine {
    var application: BaseApplication

    constructor(application: BaseApplication) {
        this.application = application
        RxBus.default!!.toObservable(MusicServiceEvent::class.java).subscribe {

        }
    }

//    fun startService(opition: String, url: String) {
//        var intent = Intent(application, MusicService::class.java)
//        intent.putExtra("opition", opition)
//        intent.putExtra("url", url)
//        application.startService(intent)
//    }
//
//    fun startService(opition: String) {
//        var intent = Intent(application, MusicService::class.java)
//        intent.putExtra("opition", opition)
//        application.startService(intent)
//    }
//
//    fun startService(opition: String, progress: Int) {
//        var intent = Intent(application, MusicService::class.java)
//        intent.putExtra("opition", opition)
//        intent.putExtra("progress", progress)
//        application.startService(intent)
//    }


}