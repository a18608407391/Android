package com.cstec.administrator.party_module

import java.io.Serializable


class PartyDetailEntity : Serializable {


    class PartyDetailPartOne : Serializable {

    }


    class PartyDetailRoadListItem : Serializable {


        //type = 0  其实部分  type =1 内容头接口部分  type =2 内容尾接口部分
        var type = 0

        var itemtype = 0

    }


}