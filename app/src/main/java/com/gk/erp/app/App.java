package com.gk.erp.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.gk.erp.entry.TaskEntry;
import com.gk.erp.entry.UserEntry;
import com.gk.erp.utils.SprefUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pc_home on 2016/11/30.
 */

public class App extends Application{

    private static final String TAG =App.class.getSimpleName();

    private static App mInstance;


    private RequestQueue mRequestQueue;

    private UserEntry userEntry;

    private String account,password, name,departName;
    private int type;
    private long departId = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }

    }



    public Boolean authorize(JSONObject authobj){
        if(authobj.has("error")){
            return  false;
        }
        return true;
    }



    public void readData(){
//        account = sharedPref.getString("account","");
//        password = sharedPref.getString("password","");
//        name = sharedPref.getString("name","");
//        type = sharedPref.getInt("type",-1);
//        if(type == 2){
//            departId = sharedPref.getLong("departId",-1);
//            departName = sharedPref.getString("departName","");
//        }
    }

    public void removeData(){
//        sharedPref.edit().putString("account","").apply();
//        sharedPref.edit().putString("password","").apply();
//        sharedPref.edit().putString("name","").apply();
//        sharedPref.edit().putInt("type",-1).apply();
//        sharedPref.edit().putLong("departId",-1).apply();
//        sharedPref.edit().putString("departName","").apply();
    }

    public void saveData(){
//        sharedPref.edit().putString("account",account).apply();
//        sharedPref.edit().putString("password",password).apply();
//        sharedPref.edit().putString("name",name).apply();
//        sharedPref.edit().putInt("type",type).apply();
//        if(type == 2){
//            sharedPref.edit().putLong("departId",departId).apply();
//            sharedPref.edit().putString("departName",departName).apply();
//        }
    }

    public static synchronized App getInstance(){return mInstance;}

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnectedOrConnecting()){
            return  true;
        }
        return  false;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    //用于 数据共享
    private TaskEntry taskEntry;

    public TaskEntry getTaskEntry() {
        return taskEntry;
    }

    public void setTaskEntry(TaskEntry taskEntry) {
        this.taskEntry = taskEntry;
    }

    public UserEntry getUserEntry() {
        return userEntry;
    }
    public UserEntry getUserEntry(SprefUtils sprefUtils) {
        if(userEntry == null)
            userEntry = UserEntry.getFromSpref(sprefUtils);
        return userEntry;
    }

    public void setUserEntry(UserEntry userEntry) {
        this.userEntry = userEntry;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public long getDepartId() {
        return departId;
    }

    public void setDepartId(long departId) {
        this.departId = departId;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public static App getmInstance() {
        return mInstance;
    }

    public static void setmInstance(App mInstance) {
        App.mInstance = mInstance;
    }

    public RequestQueue getmRequestQueue() {
        return mRequestQueue;
    }

    public void setmRequestQueue(RequestQueue mRequestQueue) {
        this.mRequestQueue = mRequestQueue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
