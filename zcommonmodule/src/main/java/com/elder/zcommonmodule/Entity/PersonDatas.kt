package com.elder.zcommonmodule.Entity

import android.databinding.ObservableField


data class PersonDatas(var url: ObservableField<String>, var name: ObservableField<String>, var teamName: ObservableField<String>, var memberId: ObservableField<String>, var isMySelf: ObservableField<Boolean>,var TextColor :ObservableField<Int>) {

    var select = ObservableField<Boolean>(false)

    var isTeamer =  ObservableField<Boolean>(false)

}