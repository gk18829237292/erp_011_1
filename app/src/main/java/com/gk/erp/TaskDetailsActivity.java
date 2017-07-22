package com.gk.erp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gk.erp.adapter.TaskDatailsAdapter;
import com.gk.erp.app.App;
import com.gk.erp.constants.Constants;
import com.gk.erp.entry.ReportEntry;
import com.gk.erp.entry.TaskEntry;
import com.gk.erp.utils.CustomRequest;
import com.gk.erp.utils.FileUploadManager;
import com.gk.erp.utils.TimeUtils;
import com.gk.erp.view.AnimatedExpandableListView;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 由 TaskFragment 启动
 */
public class TaskDetailsActivity extends ActionBarActivity implements Response.Listener<JSONObject>,Response.ErrorListener{

    private AnimatedExpandableListView listView;
    private TaskDatailsAdapter adapter;
    private List<List<ReportEntry>> allReports = new ArrayList<>();
    private static long taskId;

    private SwipeRefreshLayout mItemContainer;

    private TextView txt_user,txt_complete,txt_startTime,txt_endTime,txt_place,txt_finacing,txt_name,txt_goal,txt_icon,txt_num;


    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        taskId = getIntent().getLongExtra("taskId",0);

        listView = (AnimatedExpandableListView) findViewById(R.id.list_view_expandable);

        fab = (FloatingActionButton) findViewById(R.id.fabButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskDetailsActivity.this, FileUploadActivity.class);
                intent.putExtra("type",0);
                intent.putExtra("taskId",taskId);
                intent.putExtra("taskName",App.getInstance().getTaskEntry().getTaskName());
                startActivityForResult(intent,100);
            }
        });
        if(App.getInstance().getType() == 2){
            fab.setVisibility(View.VISIBLE);
        }

        mItemContainer = (SwipeRefreshLayout) findViewById(R.id.container_items);

        //region  mItemContainer 点击事件
        mItemContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!App.getInstance().isConnected()){
                    Toast.makeText(getApplicationContext(),"请检查网络连接",Toast.LENGTH_LONG).show();
                }else{
                    mItemContainer.setRefreshing(true);
                    getItems();
                }
            }
        });
        //endregion

        adapter = new TaskDatailsAdapter(getApplicationContext(), allReports);
        listView.setAdapter(adapter);

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                System.out.println("it's me");
                Toast.makeText(getApplicationContext(),""+childPosition,Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        //region  Set indicator (arrow) to the right
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                50, r.getDisplayMetrics());
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            listView.setIndicatorBounds(width - px, width);
        } else {
            listView.setIndicatorBoundsRelative(width - px, width);
        }
        //endregion


        getItems();
        initHead();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("任务详情");


    }

    private void initHead(){
        //txt_user,txt_complete,txt_startTime,txt_startTime,txt_place,txt_finacing,txt_name,txt_goal,txt_icon;
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_goal = (TextView) findViewById(R.id.txt_goal);
        txt_startTime = (TextView) findViewById(R.id.txt_startTime);
        txt_endTime = (TextView) findViewById(R.id.txt_endTime);
        txt_user = (TextView) findViewById(R.id.txt_user);
        txt_finacing = (TextView) findViewById(R.id.txt_financing);
        txt_place = (TextView) findViewById(R.id.txt_place);
        txt_complete = (TextView) findViewById(R.id.txt_complete);
        txt_icon = (TextView) findViewById(R.id.icon_important);
        txt_num = (TextView) findViewById(R.id.txt_num);

        TaskEntry entry = App.getInstance().getTaskEntry();
        txt_name.setText(entry.getTaskName());
        txt_goal.setText(entry.getGoal());
        txt_startTime.setText(TimeUtils.convert2String(entry.getStartTime()));
        txt_endTime.setText(TimeUtils.convert2String(entry.getEndTime()));
        txt_user.setText(entry.getCreateAccount());
        txt_finacing.setText(entry.getFinancing()+"");
        txt_place.setText(entry.getPlace());
        if(entry.getType() == 1){
            txt_icon.setVisibility(View.VISIBLE);
        }
        txt_complete.setText(entry.getCompletNum()+"");
        txt_num.setText(entry.getNum()+"");

    }

    private void fillText(){

    }

    private void getItems(){
        Map<String,String> params = new HashMap<String,String>();
        params.put("taskId",taskId+"");
        params.put("type",App.getInstance().getType()+"");
        if(App.getInstance().getType() == 2){
            params.put("account",App.getInstance().getAccount());
        }
        CustomRequest jsonReq1 = new CustomRequest(Constants.GET_REPORT_ALL,params,this,this);
        App.getInstance().addToRequestQueue(jsonReq1);
    }

    private void loadingComplete(){
        mItemContainer.setRefreshing(false);
    }


    @Override
    public void onResponse(JSONObject response) {
        if(!response.has("noerror"))return;
        try {
            JSONArray reports = response.getJSONArray("reports");
            System.out.println("reports : " + reports.length());
            List<List<ReportEntry>> enties =  ReportEntry.getAllReportFromJsonWithAccount(reports);
            System.out.println("enties size  " + enties.size());
            allReports.clear();
            allReports.addAll(enties);
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),"刷新成功 加载中",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadingComplete();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        for(StackTraceElement ele : error.getStackTrace()){
            System.out.println(ele.getClassName());
            System.out.println(ele.toString());
        }
        loadingComplete();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            getItems();
        }
    }
}
