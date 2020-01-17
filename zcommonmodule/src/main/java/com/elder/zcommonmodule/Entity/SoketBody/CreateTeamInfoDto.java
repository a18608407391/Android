package com.elder.zcommonmodule.Entity.SoketBody;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//骑行组队信息表
public class CreateTeamInfoDto implements Serializable {
    private static final long serialVersionUID = 12121323232L;

    //主键(自动增长)
    private int id;
    //队伍名称
    private String teamName;
    //队伍加入口令
    private String teamCode;
    //队长
    private Integer teamer;
    //创建时间
    private Long createTime;
    //创建人
    private int teamMaxCount;

    public int getTeamMaxCount() {
        return teamMaxCount;
    }

    public void setTeamMaxCount(int teamMaxCount) {
        this.teamMaxCount = teamMaxCount;
    }

    private String createUser;
    //状态(0:失效 1:有效)
    private String status;
    //有效时间(天)
    private Integer validDay;
    //有效截止时间
    private Long validEndTime;

    //队员人员信息

    //加入队伍地址
    private String joinAddr;
    //离开队伍时间
    private Long leaveTime;
    //离开队伍地址
    private String leaveAddr;

    private String navigationPoint;

    public String getNavigationPoint() {
        return navigationPoint;
    }

    public void setNavigationPoint(String navigationPoint) {
        this.navigationPoint = navigationPoint;
    }

    //redis队伍数据结构
    private Map<String, TeamPersonnelInfoDto> personMap = new HashMap<String, TeamPersonnelInfoDto>();

    //队员人员信息
    private List<TeamPersonnelInfoDto> dtoList = new ArrayList<TeamPersonnelInfoDto>();


    private List<TeamPersonnelInfoDto> list = new ArrayList<TeamPersonnelInfoDto>();



    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    public Integer getTeamer() {
        return teamer;
    }

    public void setTeamer(Integer teamer) {
        this.teamer = teamer;
    }


    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getValidDay() {
        return validDay;
    }

    public void setValidDay(Integer validDay) {
        this.validDay = validDay;
    }


    public String getJoinAddr() {
        return joinAddr;
    }

    public void setJoinAddr(String joinAddr) {
        this.joinAddr = joinAddr;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getValidEndTime() {
        return validEndTime;
    }

    public void setValidEndTime(Long validEndTime) {
        this.validEndTime = validEndTime;
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

    public Map<String, TeamPersonnelInfoDto> getPersonMap() {
        return personMap;
    }

    public void setPersonMap(Map<String, TeamPersonnelInfoDto> personMap) {
        this.personMap = personMap;
    }

    public List<TeamPersonnelInfoDto> getDtoList() {
        return dtoList;
    }

    public List<TeamPersonnelInfoDto> getList() {
        return list;
    }

    public void setList(List<TeamPersonnelInfoDto> list) {
        this.list = list;
    }

    public void setDtoList(List<TeamPersonnelInfoDto> dtoList) {
        this.dtoList = dtoList;
    }
}