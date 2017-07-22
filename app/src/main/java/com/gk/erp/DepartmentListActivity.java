package com.gk.erp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gk.erp.app.App;
import com.gk.erp.constants.Constants;
import com.gk.erp.entry.DepartEntry;
import com.gk.erp.utils.CustomRequest;
import com.gk.erp.utils.ProgressDialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class DepartmentListActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener,Response.Listener<JSONObject>,Response.ErrorListener{

    private ListView listView;


    private List<String> list = new ArrayList<>();

    private List<DepartEntry> entries = new ArrayList<>();

    private ProgressDialog pDialog;

    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("部门选择");

        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra("departId",entries.get(position).getDepartmentId());
                data.putExtra("departName",entries.get(position).getDepartmentName());
                setResult(RESULT_OK,data);
                finish();
            }
        });

        pDialog = ProgressDialogUtils.newInstance(DepartmentListActivity.this,"加载中···");
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
        CustomRequest jsonReq = new CustomRequest(Constants.GET_DEPART,params,this,this);
        App.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public void onRefresh() {
        //TODO 不做操作
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //出错
        Toast.makeText(getApplicationContext(),"获取部门列表错误,请返回重试",Toast.LENGTH_SHORT).show();
        ProgressDialogUtils.hideProgressDialog(pDialog);
    }

    @Override
    public void onResponse(JSONObject response) {
        if(response.has("error")) return;

        try {
            JSONArray departs = response.getJSONArray("departs");
            entries.clear();
            entries.addAll(DepartEntry.getAllDepartFromJson(departs));
            list.clear();
            for(DepartEntry entry:entries){
                list.add(entry.getDepartmentName());
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            onErrorResponse(null);
        }
        ProgressDialogUtils.hideProgressDialog(pDialog);
    }
}
