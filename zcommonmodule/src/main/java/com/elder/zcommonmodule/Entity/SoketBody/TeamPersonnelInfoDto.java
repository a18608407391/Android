package com.elder.zcommonmodule.Entity.SoketBody;

import java.io.Serializable;
import java.sql.Timestamp;

//组队人员信息表
public class  TeamPersonnelInfoDto implements  Serializable{

    //主键(自动增长)
    private Integer id;
    //队伍id
    private Integer teamId;
    //会员id
    private Integer memberId;
    //会员名称
    private String memberName  = "";
    //会员头像路径
    private String memberHeaderUrl;
    //队伍角色id
    private int teamRoleId;
    //队伍角色名称
    private String teamRoleName;
    //加入队伍时间
    private Long createTime;

    //加入队伍地址
    private String joinAddr;
    //离开队伍时间
    private Long leaveTime;
    //离开队伍地址
    private String leaveAddr;

    private String teamRoleColor =  "2D3138";

    //队伍口令
    private String teamCode;

    //socket channel id

    //状态
    private String status;

    //最后一次上传点数据
    private String lastPoint;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberHeaderUrl() {
        return memberHeaderUrl;
    }

    public void setMemberHeaderUrl(String memberHeaderUrl) {
        this.memberHeaderUrl = memberHeaderUrl;
    }

    public int getTeamRoleId() {
        return teamRoleId;
    }

    public void setTeamRoleId(Integer teamRoleId) {
        this.teamRoleId = teamRoleId;
    }

    public String getTeamRoleName() {
        return teamRoleName;
    }

    public void setTeamRoleName(String teamRoleName) {
        this.teamRoleName = teamRoleName;
    }

    public String getJoinAddr() {
        return joinAddr;
    }

    public void setJoinAddr(String joinAddr) {
        this.joinAddr = joinAddr;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(Long leaveTime) {
        this.leaveTime = leaveTime;
    }

    public String getLeaveAddr() {
        return leaveAddr;
    }

    public void setLeaveAddr(String leaveAddr) {
        this.leaveAddr = leaveAddr;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastPoint() {
        return lastPoint;
    }

    public void setLastPoint(String lastPoint) {
        this.lastPoint = lastPoint;
    }

    public String getTeamRoleColor() {
        return teamRoleColor;
    }

    public void setTeamRoleColor(String teamRoleColor) {
        this.teamRoleColor = teamRoleColor;
    }
}