package com.gk.erp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gk.erp.R;
import com.gk.erp.app.App;
import com.gk.erp.constants.Constants;
import com.gk.erp.utils.CustomRequest;
import com.gk.erp.utils.ProgressDialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddDepartActivity extends ActionBarActivity implements Response.Listener<JSONObject>,Response.ErrorListener{

    private ProgressDialog pDialog;

    private EditText txt_departName;
    private Button btn_submit;

    private String departName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_depart);


        init();

        pDialog = ProgressDialogUtils.newInstance(AddDepartActivity.this,"提交中···");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("添加部门");
    }

    private void init(){
        txt_departName = (EditText) findViewById(R.id.txt_departName);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cicke me ");
                if(!App.getInstance().isConnected()){
                    Toast.makeText(getApplicationContext(),getString(R.string.network_error),Toast.LENGTH_SHORT).show();
                }else if(checkDepartName()){
                    ProgressDialogUtils.showProgressDialog(pDialog);
                    submit();
                }
            }
        });



    }

    private boolean checkDepartName(){
        departName = txt_departName.getText().toString();
        txt_departName.setError(null);
        if(departName.length() <=0){
            txt_departName.setError("请检查");
            return false;
        }
        return true;
    }

    private void submit(){
        Map<String,String> params = new HashMap<>();
        params.put("departName",departName);
        System.out.println(params);
        CustomRequest jsonReq = new CustomRequest(Constants.METHOD_DEPARTMENT_INSERT,params,this,this);
        App.getInstance().addToRequestQueue(jsonReq);
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
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(AddDepartActivity.this,"未知错误,请重试",Toast.LENGTH_SHORT).show();
        ProgressDialogUtils.hideProgressDialog(pDialog);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            int type = response.getInt("type");
            if(type == 1){
                Toast.makeText(AddDepartActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                finish();
            }else if(type == 2){
                Toast.makeText(AddDepartActivity.this,response.getString("msg"),Toast.LENGTH_SHORT).show();
                onErrorResponse(null);
            }else{
                onErrorResponse(null);
            }
        } catch (JSONException e) {
            onErrorResponse(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pDialog != null)
            pDialog.dismiss();
    }
}
