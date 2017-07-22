package com.gk.erp.entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc_home on 2016/12/3.
 */

public class ReportEntry {

    private String account;
    private String name;
    private Long id;
    private String comment;
    private List<String> paths;
    private long time;
    private int index;
    private List<ReportEntry> supers;

    public  ReportEntry(){
        paths = new ArrayList<>();
        supers = new ArrayList<>();
    }

    public static ReportEntry getReportFromJson(JSONObject jsonObject) throws JSONException {
        ReportEntry entry = new ReportEntry();
        entry.setAccount(jsonObject.getString("account"));
        entry.setName(jsonObject.getString("name"));
        entry.setId(jsonObject.getLong("id"));
        entry.setComment(jsonObject.getString("comment"));
        entry.setTime(jsonObject.getLong("time"));
        if(jsonObject.has("index")){
            entry.setIndex(jsonObject.getInt("index"));
        }
        if(jsonObject.has("pics")){
            JSONArray jsonArray = jsonObject.getJSONArray("pics");
            for(int i =0;i<jsonArray.length();i++){
                entry.paths.add(jsonArray.getString(i));
            }
        }
        if(jsonObject.has("supervise")){
            JSONArray jsonArray = jsonObject.getJSONArray("supervise");
            for(int i =0;i<jsonArray.length();i++) {
                entry.supers.add(ReportEntry.getReportFromJson((JSONObject) jsonArray.get(i)));
            }
        }
        return  entry;
    }

    public static Map<Integer,List<ReportEntry>> getAllReportFromJson(JSONArray jsonArray) throws JSONException {
        Map<Integer,List<ReportEntry>> entries = new HashMap<>();
        for(int i = 0;i<jsonArray.length();i++){
            ReportEntry entry = ReportEntry.getReportFromJson((JSONObject) jsonArray.get(i));
            if(!entries.containsKey(entry.getIndex())){
                entries.put(entry.getIndex(),new ArrayList<ReportEntry>());
            }
            entries.get(entry.getIndex()).add(entry);
        }
        return entries;
    }

    public static List<List<ReportEntry>> spiltReports(Map<Integer,List<ReportEntry>> entries){
        List<List<ReportEntry>> allReports = new ArrayList<>();
        int index = 1;
        while(entries.containsKey(index)){
            allReports.add(entries.get(index));
            index++;
        }

        return allReports;
    }
    public static List<List<ReportEntry>> getAllReportFromJsonWithAccount(JSONArray jsonArray) throws JSONException {
        Map<Integer,List<ReportEntry>> entries = new HashMap<>();
        int maxIndex = 0;
        for(int i = 0;i<jsonArray.length();i++){
            ReportEntry entry = ReportEntry.getReportFromJson((JSONObject) jsonArray.get(i));
            if(!entries.containsKey(entry.getIndex())){
                entries.put(entry.getIndex(),new ArrayList<ReportEntry>());
                if(maxIndex < entry.getIndex()) maxIndex = entry.getIndex();
            }
            entries.get(entry.getIndex()).add(entry);
        }

        List<List<ReportEntry>> allReports = new ArrayList<>();
        int index = 1;
        List<ReportEntry> empty = new ArrayList<>();
        for(;index<=maxIndex;index++){
            if(entries.containsKey(index)){
                allReports.add(entries.get(index));
            }else{
                allReports.add(empty);
            }
        }


        return allReports;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public List<ReportEntry> getSupers() {
        return supers;
    }

    public void setSupers(List<ReportEntry> supers) {
        this.supers = supers;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
