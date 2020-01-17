package com.example.private_module.Entitiy

import com.example.private_module.Bean.QueryAllcars
import java.io.Serializable


class CertificationEntity : Serializable {

    var code = 0
    var msg: String? = null
    var data: Certification? = null


    class Certification {
        var isattestation: String? = null
        var bindVehicle: QueryAllcars.AllcarsResult? = null
    }

}