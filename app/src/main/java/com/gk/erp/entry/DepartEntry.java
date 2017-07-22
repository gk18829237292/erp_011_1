package com.gk.erp.entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc_home on 2016/12/1.
 */

public class DepartEntry {
    private long departmentId;
    private String departmentName;

    public static DepartEntry getdepartFromJson(JSONObject json) throws JSONException {
        DepartEntry entry = new DepartEntry();
        entry.setDepartmentId(json.getLong("departId"));
        entry.setDepartmentName(json.getString("departName"));
        return  entry;
    }

    public static Map<Long,DepartEntry> getAllDepartFromJson_1(JSONArray jsonArray) throws JSONException {
        Map<Long,DepartEntry> entryMap = new HashMap<Long, DepartEntry>();
        for(int i =0;i<jsonArray.length();i++){
            DepartEntry entry = getdepartFromJson((JSONObject) jsonArray.get(i));
            entryMap.put(entry.getDepartmentId(),entry);
        }
        return entryMap;
    }

    public static List<DepartEntry> getAllDepartFromJson(JSONArray jsonArray) throws JSONException {
        List<DepartEntry> entries = new ArrayList<>();
        for(int i =0;i<jsonArray.length();i++){
            DepartEntry entry = getdepartFromJson((JSONObject) jsonArray.get(i));
            entries.add(entry);
        }
        return entries;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }


}
