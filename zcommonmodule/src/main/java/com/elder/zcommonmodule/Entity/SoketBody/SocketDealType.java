package com.elder.zcommonmodule.Entity.SoketBody;


/**
 * @Author: hf
 * @Desciption: 所有socket处理类型
 * @Date:14:00 2019/6/18
 * @param:No such property: code for class: Script1
 */
public enum SocketDealType {
    ///公用的socket处理类型
    JOINTEAM(1000, "创建或加入队伍"),
    SENDPOINT(1001, "发送队友点数据"),
    OFFLINE(1002, "队友离线"),
    HEART_BEAT(1003, "心跳"),
    LEAVETEAM(1004, "队友离开队伍"),
    REJECTTEAM(1005, "队友被踢出队伍"),
    GETTEAMINFO(1006, "查询队伍信息"),
    UPDATETEAMINFO(1007, "修改队伍信息"),
    UPDATE_MANAGER(1009,"更新角色职务信息"),
    TEAMER_PASS(1010,"队长转让"),
    NAVIGATION_START(1011,"队长转让"),
    DISMISSTEAM(1008, "解散队伍");


    private String message;
    private int code;

    SocketDealType(int code, String message) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
