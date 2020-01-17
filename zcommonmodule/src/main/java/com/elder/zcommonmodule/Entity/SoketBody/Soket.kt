package com.elder.zcommonmodule.Entity.SoketBody


class Soket {

    var type = 0

    var userId: String? = null

    var teamId: String? = null

    var body: SocketRequest? = null

    var sendTime: Long? = null

    var teamCode: String? = null

    class SocketRequest {
        var token: String? = null
        var teamName: String? = null
        var userIds: String? = null
        var dtoList: List<TeamPersonnelInfoDto>? = null
        var latitude: Double? = null
        var longitude: Double? = null
        var navigationPoint: String? = null
    }

}