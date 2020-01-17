package org.cs.tec.library.binding.command.ViewAdapter.Banner

class Banner {

    /**
     * pic : [{"type":6,"mo_type":4,"code":"http://music.taihe.com/h5pc/spec_detail?id=1290&columnid=86","randpic":"http://business.cdn.qianqian.com/qianqian/pic/bos_client_1533870073fb593cea2f51be6b5c3ea2d179efe5a3.jpg","randpic_ios5":"","randpic_desc":"","randpic_ipad":"","randpic_qq":"","randpic_2":"","randpic_iphone6":"","special_type":0,"ipad_desc":"","is_publish":"111111"},{"type":6,"mo_type":4,"code":"http://music.taihe.com/h5pc/spec_detail?id=1286&columnid=92","randpic":"http://business.cdn.qianqian.com/qianqian/pic/bos_client_1533800429fb270972d62cecb177c1834ff17756c0.jpg","randpic_ios5":"","randpic_desc":"","randpic_ipad":"","randpic_qq":"","randpic_2":"","randpic_iphone6":"","special_type":0,"ipad_desc":"","is_publish":"111111"},{"type":6,"mo_type":4,"code":"http://music.taihe.com/h5pc/spec_detail?id=1284&columnid=87","randpic":"http://business.cdn.qianqian.com/qianqian/pic/bos_client_15336130839fdf9251a9a5649ded85e702563db012.jpg","randpic_ios5":"","randpic_desc":"","randpic_ipad":"","randpic_qq":"","randpic_2":"","randpic_iphone6":"","special_type":0,"ipad_desc":"","is_publish":"111111"},{"type":2,"mo_type":2,"code":"601921467","randpic":"http://business.cdn.qianqian.com/qianqian/pic/bos_client_15338105095af98693be88a71b0df65a95ee1cc55d.jpg","randpic_ios5":"","randpic_desc":"","randpic_ipad":"http://business.cdn.qianqian.com/qianqian/pic/bos_client_153381051358234b157fac652c906dfc9f69e322de.jpg","randpic_qq":"","randpic_2":"","randpic_iphone6":"","special_type":0,"ipad_desc":"","is_publish":"111111"}]
     * error_code : 22000
     */
    var error_code: Int = 0
    var pic: List<PicBean>? = null

    class PicBean {
        /**
         * type : 6
         * mo_type : 4
         * code : http://music.taihe.com/h5pc/spec_detail?id=1290&columnid=86
         * randpic : http://business.cdn.qianqian.com/qianqian/pic/bos_client_1533870073fb593cea2f51be6b5c3ea2d179efe5a3.jpg
         * randpic_ios5 :
         * randpic_desc :
         * randpic_ipad :
         * randpic_qq :
         * randpic_2 :
         * randpic_iphone6 :
         * special_type : 0
         * ipad_desc :
         * is_publish : 111111
         */

        var type: Int = 0
        var mo_type: Int = 0
        var code: String? = null
        var randpic: String? = null
        var randpic_ios5: String? = null
        var randpic_desc: String? = null
        var randpic_ipad: String? = null
        var randpic_qq: String? = null
        var randpic_2: String? = null
        var randpic_iphone6: String? = null
        var special_type: Int = 0
        var ipad_desc: String? = null
        var is_publish: String? = null
    }
}