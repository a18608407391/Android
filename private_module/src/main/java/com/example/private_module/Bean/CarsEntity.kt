package com.example.private_module.Bean

import android.databinding.ObservableField
import java.io.Serializable


class CarsEntity : Serializable {

    var imgUrl: ObservableField<String> = ObservableField("")

    var carName: ObservableField<String> = ObservableField("")
    var carBrandId: ObservableField<String> = ObservableField("")
    var brandName: ObservableField<String> = ObservableField("")
    var brandTypeName: ObservableField<String> = ObservableField("")
    var isDefault  :ObservableField<Int> = ObservableField(0)
    var id  :ObservableField<String> = ObservableField("")
    var imgShot :ObservableField<String> = ObservableField("")
}