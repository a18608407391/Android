package com.example.drivermodule

import android.util.Log
import com.amap.api.track.query.model.*
import com.google.gson.Gson


abstract class SimpleOnTrack : OnTrackListener {
    override fun onLatestPointCallback(p0: LatestPointResponse?) {
    }

    override fun onCreateTerminalCallback(p0: AddTerminalResponse?) {


    }

    override fun onQueryTrackCallback(p0: QueryTrackResponse?) {
    }

    override fun onDistanceCallback(p0: DistanceResponse?) {
    }

    override fun onQueryTerminalCallback(p0: QueryTerminalResponse?) {
        Log.e("result","onQueryTerminalCallback"+ Gson().toJson(p0) )


    }

    override fun onHistoryTrackCallback(p0: HistoryTrackResponse?) {
    }

    override fun onParamErrorCallback(p0: ParamErrorResponse?) {
    }

    override fun onAddTrackCallback(p0: AddTrackResponse?) {
    }


}