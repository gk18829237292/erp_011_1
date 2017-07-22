package com.gk.erp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gk.erp.app.App;
import com.gk.erp.constants.Constants;
import com.gk.erp.entry.TaskEntry;
import com.gk.erp.utils.CustomRequest;
import com.gk.erp.utils.ProgressDialogUtils;
import com.gk.erp.utils.TimeUtils;

import org.feezu.liuli.timeselector.TimeSelector;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends ActionBarActivity implements Response.Listener<JSONObject>,Response.ErrorListener{

    private final static String ERROR = "请检查";

    private ProgressDialog pDialog;

    private View view_depart,view_startTime,view_endTime;

    private EditText txt_place,txt_finacing,txt_name,txt_goal,txt_num;
    private TextView txt_startTime,txt_endTime,txt_depart;
    private Switch sw_important;
    private Button btn_submit;

    private String name,place,financing,num,goal;
    private long startTime,endTime,departId;
    private int type;


    // 1更新 2 添加
    private int actionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);



        actionType = getIntent().getIntExtra("action",0);
        System.out.println("actionType  " + actionType);
        init();
        if(actionType == 1){
            fill();
        }
        pDialog = ProgressDialogUtils.newInstance(AddTaskActivity.this,"提交中···");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("任务添加");
    }

    private void init(){
        /*    private View
    private EditText
    private TextView
    private Switch sw_important; */
        //EditText  txt_place,txt_finacing,txt_name,txt_goal,txt_num;
        txt_name = (EditText) findViewById(R.id.txt_taskName);
        txt_place = (EditText) findViewById(R.id.txt_place);
        txt_finacing = (EditText) findViewById(R.id.txt_finacing);
        txt_num = (EditText) findViewById(R.id.txt_num);
        txt_goal = (EditText) findViewById(R.id.txt_goal);

        //TextView txt_startTime,txt_endTime,txt_depart
        txt_startTime = (TextView) findViewById(R.id.txt_startTime);
        txt_endTime = (TextView) findViewById(R.id.txt_endTime);
        txt_depart = (TextView) findViewById(R.id.txt_depart);

        //View view_depart,view_startTime,view_startTime;
        view_startTime = findViewById(R.id.view_startTime);
        view_endTime = findViewById(R.id.view_endTime);
        view_depart = findViewById(R.id.view_depart);

        //switch
        sw_important = (Switch) findViewById(R.id.sw_important);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        view_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeSelector timeSelector=  new TimeSelector(AddTaskActivity.this,new TimeSelector.ResultHandler(){
                    @Override
                    public void handle(String time) {
                        txt_startTime.setText(time);
                    }
                },"2015-10-27 09:33", "2016-11-29 21:54");
//                timeSelector.setScrollUnit(TimeSelector.SCROLLTYPE.HOUR, TimeSelector.SCROLLTYPE.MINUTE);
                timeSelector.setScrollUnit(TimeSelector.SCROLLTYPE.HOUR, TimeSelector.SCROLLTYPE.MINUTE,TimeSelector.SCROLLTYPE.YEAR,TimeSelector.SCROLLTYPE.MONTH,TimeSelector.SCROLLTYPE.DAY);

                timeSelector.show();
            }
        });

        view_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeSelector timeSelector=  new TimeSelector(AddTaskActivity.this,new TimeSelector.ResultHandler(){
                    @Override
                    public void handle(String time) {
                        txt_endTime.setText(time);
                    }
                },"2015-10-27 09:33", "2016-11-29 21:54");
                timeSelector.setScrollUnit(TimeSelector.SCROLLTYPE.HOUR, TimeSelector.SCROLLTYPE.MINUTE,TimeSelector.SCROLLTYPE.YEAR,TimeSelector.SCROLLTYPE.MONTH,TimeSelector.SCROLLTYPE.DAY);
                timeSelector.show();
            }
        });

        view_depart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTaskActivity.this,DepartmentListActivity.class);
                startActivityForResult(intent,1);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!App.getInstance().isConnected()){
                    Toast.makeText(getApplicationContext(),getString(R.string.network_error),Toast.LENGTH_SHORT).show();
                }else if(cheackName() && checkDepartId() && checkEndTime() && checkFinancing() && checkGoal() && checkNum() && checkPlace() && checkStartTime()){

                    ProgressDialogUtils.showProgressDialog(pDialog);
                    submit();
                }
            }
        });


    }

    /**
     * 需要在init 后调用
     */
    private void fill(){
        TaskEntry entry = App.getInstance().getTaskEntry();

        txt_name.setText(entry.getTaskName());
        txt_place.setText(entry.getPlace());
        txt_finacing.setText(entry.getFinancing()+"");
        txt_num.setText(entry.getNum()+"");
        txt_goal.setText(entry.getGoal());

        txt_startTime.setText(TimeUtils.convert2String(entry.getStartTime()));
        txt_endTime.setText(TimeUtils.convert2String(entry.getEndTime()));
        txt_depart.setText(entry.getDepartName());

        if(entry.getType() == 1){
            sw_important.setChecked(true);
        }else {
            sw_important.setChecked(false);
        }

        departId = entry.getDepartment_id();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode ==RESULT_OK){
            departId = data.getLongExtra("departId",-1);
            String departName = data.getStringExtra("departName");
            System.out.println("select name :" + departName +"  " + departId);
            if(departId != -1){
                txt_depart.setText(departName);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //之所以没用函数，是因为想在这里完成数据的赋值
    private boolean cheackName(){
        name = txt_name.getText().toString();
        txt_name.setError(null);
        if(name.length() == 0){
            txt_name.setError(ERROR);
            return false;
        }
        return  true;
    }

    private boolean checkPlace(){
        place = txt_place.getText().toString();
        txt_place.setError(null);
        if(place.length() <=0){
            txt_place.setError(ERROR);
            return false;
        }
        return true;
    }

    private boolean checkFinancing(){
        financing = txt_finacing.getText().toString();
        txt_finacing.setError(null);
        if(financing.length() <=0){
            txt_finacing.setError(ERROR);
            return false;
        }
        return true;
    }

    private boolean checkNum(){
        num = txt_num.getText().toString();
        txt_num.setError(null);
        if(num.length()<=0){
            txt_num.setError(ERROR);
            return false;
        }
        return true;
    }

    private boolean checkGoal(){
        goal = txt_goal.getText().toString();
        txt_goal.setError(null);
        if(goal.length()<=0){
            txt_goal.setError(ERROR);
            return false;
        }
        return true;
    }


    private boolean checkStartTime(){
        String time = txt_startTime.getText().toString();
        txt_startTime.setError(null);
        if(time.length() <=0){
            txt_startTime.setError(ERROR);
            return false;
        }
        try {
            startTime = TimeUtils.convert2Long(time);
        }catch (Exception e){
            txt_startTime.setError(ERROR);
            return false;
        }
        return true;
    }

    private boolean checkEndTime(){
        String time = txt_endTime.getText().toString();
        txt_endTime.setError(null);
        if(time.length() <=0){
            txt_endTime.setError(ERROR);
            return false;
        }
        try {
            endTime = TimeUtils.convert2Long(time);
        }catch (Exception e){
            txt_endTime.setError(ERROR);
            return false;
        }
        return true;
    }

    private boolean checkDepartId(){
        if(departId == -1){
            txt_depart.setError(ERROR);
            return false;
        }
        return true;
    }

    private void submit(){
        int type = 0;
        Map<String,String> params = new HashMap<>();
        params.put("Actiontype",actionType+"");
        params.put("taskName",name);
        params.put("taskPlace",place);
        params.put("financing",financing);
        params.put("startTime",startTime+"");
        params.put("endTime",endTime+"");
        params.put("num",num);
        params.put("goal",goal);
        params.put("departmentId",departId+"");
        params.put("account",App.getInstance().getAccount());
        //TODO 打印
        System.out.println(params);
        if(sw_important.isChecked()) type = 1;
        else type = 0;
        params.put("tasktype",type+"");
        if(actionType == 1){ // update
            params.put("taskid",App.getInstance().getTaskEntry().getTaskId()+"");
        }
        CustomRequest jsonReq = new CustomRequest(Constants.METHOD_TASK_INSERT,params,this,this);

        App.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(AddTaskActivity.this,"提交失败,请重试",Toast.LENGTH_SHORT).show();
        ProgressDialogUtils.hideProgressDialog(pDialog);
    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(AddTaskActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
        ProgressDialogUtils.hideProgressDialog(pDialog);
        setResult(RESULT_OK);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pDialog != null)
            pDialog.dismiss();
    }


}
