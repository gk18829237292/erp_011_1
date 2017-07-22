package com.gk.erp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gk.erp.app.App;
import com.gk.erp.constants.Constants;
import com.gk.erp.entry.DepartEntry;
import com.gk.erp.entry.TaskEntry;
import com.gk.erp.fragment.TaskFragment;
import com.gk.erp.utils.CustomRequest;
import com.gk.erp.utils.ProgressDialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskListActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener,Response.Listener<JSONObject>,Response.ErrorListener{


    private ListView listView;


    private List<String> list = new ArrayList<>();

    private List<TaskEntry> entries = new ArrayList<>();

    private ProgressDialog pDialog;

    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("任务选择");

        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
//                data.putExtra("code",departs.get(position).getDepartmentId());
                data.putExtra("taskName",entries.get(position).getTaskName());
                data.putExtra("taskId",entries.get(position).getTaskId());
                setResult(-1,data);
                finish();
            }
        });

        pDialog = ProgressDialogUtils.newInstance(TaskListActivity.this,"加载中···");
        ProgressDialogUtils.showProgressDialog(pDialog);
        getItems();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();

    }

    private void getItems(){
        Map<String,String> params = new HashMap<>();
        params.put("type","1");
        params.put("departId",App.getInstance().getDepartId()+"");
        CustomRequest jsonReq = new CustomRequest(Constants.GET_TASK,params,this,this);
        App.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public void onRefresh() {
        //TODO 不做操作
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //出错
        Toast.makeText(getApplicationContext(),"获取任务列表错误,请返回重试",Toast.LENGTH_SHORT).show();
        ProgressDialogUtils.hideProgressDialog(pDialog);
    }

    @Override
    public void onResponse(JSONObject response) {
        if(response.has("error")) return;

        try {
            JSONArray tasks = response.getJSONArray("tasks");
            entries.clear();
            entries.addAll(TaskEntry.getAllTaskFromJson(tasks));
            list.clear();
            for(TaskEntry entry:entries){
                list.add(entry.getTaskName());
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            onErrorResponse(null);
        }
        ProgressDialogUtils.hideProgressDialog(pDialog);
    }
}
