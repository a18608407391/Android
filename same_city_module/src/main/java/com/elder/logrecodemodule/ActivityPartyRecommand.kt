package com.elder.logrecodemodule

import android.databinding.ObservableArrayList
import com.elder.logrecodemodule.Entity.ActivityPartyEntity
import java.io.Serializable

class ActivityPartyRecommand : Serializable {
    var list = ObservableArrayList<ActivityPartyEntity.HotRecommend>()
}