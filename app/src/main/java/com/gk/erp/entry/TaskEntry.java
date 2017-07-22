package com.gk.erp.entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc_home on 2016/11/30.
 */

public class TaskEntry implements Serializable{

    private Long taskId;
    private long department_id;
    private long startTime;
    private long endTime;
    private String createAccount;
    private String place;
    private long financing;
    private String goal;
    private int type;
    private int num;
    private int completNum;
    private String taskName;
    private String departName;

    public static TaskEntry getTaskFromJson(JSONObject json) throws JSONException {
        TaskEntry entry =new TaskEntry();
        entry.setTaskId(json.getLong("taskId"));
        entry.setDepartment_id(json.getLong("departmentId"));
        entry.setStartTime(json.getLong("startTime"));
        entry.setEndTime(json.getLong("endTime"));
        entry.setCreateAccount(json.getString("createAccount"));
        entry.setPlace(json.getString("place"));
        entry.setFinancing(json.getLong("financing"));
        entry.setGoal(json.getString("goal"));
        entry.setType(json.getInt("type"));
        entry.setNum(json.getInt("num"));
        entry.setCompletNum(json.getInt("completNum"));
        entry.setTaskName(json.getString("taskName"));
        entry.setDepartName(json.getString("departName"));
//        System.out.println(entry);
        return entry;
    }

//    public static Map<Long,TaskEntry> getAllTaskFromJson(JSONArray jsonArray) throws JSONException {
//        Map<Long,TaskEntry> entryMap = new HashMap<Long, TaskEntry>();
//        for(int i = 0;i<jsonArray.length();i++){
//            TaskEntry entry = getTaskFromJson((JSONObject) jsonArray.get(i));
//            entryMap.put(entry.getTaskId(),entry);
//        }
//        return  entryMap;
//    }

    public static List<TaskEntry> getAllTaskFromJson(JSONArray jsonArray) throws JSONException {
        List<TaskEntry> entries = new ArrayList<>();
        for(int i = 0;i<jsonArray.length();i++){
            TaskEntry entry = getTaskFromJson((JSONObject) jsonArray.get(i));
            entries.add(entry);
        }
        return entries;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setCompletNum(int completNum) {
        this.completNum = completNum;
    }

    public void setCreateAccount(String createAccount) {
        this.createAccount = createAccount;
    }

    public void setDepartment_id(long department_id) {
        this.department_id = department_id;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setFinancing(long financing) {
        this.financing = financing;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getCompletNum() {
        return completNum;
    }

    public String getCreateAccount() {
        return createAccount;
    }

    public long getDepartment_id() {
        return department_id;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getFinancing() {
        return financing;
    }

    public String getGoal() {
        return goal;
    }

    public int getNum() {
        return num;
    }

    public String getPlace() {
        return place;
    }

    public long getStartTime() {
        return startTime;
    }

    public Long getTaskId() {
        return taskId;
    }

    public int getType() {
        return type;
    }

    public String getStatue(){
        if(num == completNum) return "已完成";
        else return "进行中";
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    @Override
    public String toString() {
        return "TaskEntry{" +
                "completNum=" + completNum +
                ", taskId=" + taskId +
                ", department_id=" + department_id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createAccount='" + createAccount + '\'' +
                ", place='" + place + '\'' +
                ", financing=" + financing +
                ", goal='" + goal + '\'' +
                ", type=" + type +
                ", num=" + num +
                ", taskName='" + taskName + '\'' +
                ", departName='" + departName + '\'' +
                '}';
    }
}
