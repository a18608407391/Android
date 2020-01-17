package com.elder.zcommonmodule.DataBases

import android.content.Context


val Context.database :AmoskiDataBase
get()= AmoskiDataBase.getIncetance()!!