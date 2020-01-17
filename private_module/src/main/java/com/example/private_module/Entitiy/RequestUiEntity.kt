package com.example.private_module.Entitiy

import android.databinding.ObservableField
import java.io.Serializable


data class RequestUiEntity(var isChecked: ObservableField<Boolean>, var id: ObservableField<Int>, var name: ObservableField<String>, var pId: ObservableField<Int>) : Serializable