package com.gk.erp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gk.erp.app.App;
import com.gk.erp.constants.Constants;
import com.gk.erp.utils.CustomRequest;
import com.gk.erp.view.FloatLabeledEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录只是去获取基本的信息 不再做别的事
 */
public class LoginActivity extends ActionBarActivity implements Response.Listener<JSONObject>,Response.ErrorListener{

    private ProgressDialog pDialog;

    private FloatLabeledEditText loginText,passText;
    private TextView login;

    private String username,password;

    private Map<String,String> params;

    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        flag = getIntent().getBooleanExtra("flag",false);
        if(flag){
            show();
            loginText.setText(App.getInstance().getAccount());
            passText.setText(App.getInstance().getPassword());
        }else {
            autoLogin();
        }
    }

    void init(){


        login = (TextView) findViewById(R.id.login);
        loginText = (FloatLabeledEditText) findViewById(R.id.txt_username);
        passText = (FloatLabeledEditText) findViewById(R.id.txt_password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 处理 登录事件
                password = passText.getText().toString();
                if(!App.getInstance().isConnected()){
                    Toast.makeText(getApplicationContext(), R.string.network_error,Toast.LENGTH_SHORT).show();
                }else if(checkAccount() && checkPassword()){
                    login();
                }
            }
        });

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("登录中···");
        pDialog.setCancelable(false);
    }

    private boolean checkAccount(){
        username = loginText.getText().toString();
        loginText.setError(null);
        if(username.length() == 0){
            loginText.setError("用户名不可为空");
            return false;
        }
        return true;
    }

    private boolean checkPassword(){
        password = passText.getText().toString();
        passText.setError(null);
        if(password.length() == 0){
            passText.setError("密码不可以为空");
            return false;
        }
        return true;
    }

    private void login(){
        showDialog();
        params = new HashMap<>();
        params.put("account",username);
        params.put("password",password);
        CustomRequest jsonReq = new CustomRequest(Constants.METHOD_ACCOUNT_AUTHORIZE,params,this,this);
        App.getInstance().addToRequestQueue(jsonReq);
    }

    private void showDialog(){
        if(flag &&  !pDialog.isShowing())
            pDialog.show();
    }
    private void hidpDialog(){
        if(flag && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(flag)
            Toast.makeText(getApplicationContext(),"登录失败 ",Toast.LENGTH_LONG).show();
        else{
            setContentView(R.layout.activity_login);
            init();
            Toast.makeText(getApplicationContext(),"自动登录失败",Toast.LENGTH_SHORT).show();
            flag = true;
        }
        hidpDialog();
    }

    @Override
    public void onResponse(JSONObject response) {

        if(App.getInstance().authorize(response,params)){
            App.getInstance().saveData();
            App.getInstance().readData();
            Intent intent = null;
            switch (App.getInstance().getType()){
                case 0:case 1:
                    intent = new Intent(getApplicationContext(),MainActivity.class);
                    break;
                case 2:
                    intent = new Intent(getApplicationContext(),MainGuestActivity.class);
                    break;
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else{
            if(flag)
                Toast.makeText(getApplicationContext(),"登录失败 ",Toast.LENGTH_LONG).show();
            else {
                show();
                Toast.makeText(getApplicationContext(), "自动登录失败", Toast.LENGTH_SHORT).show();
                flag = true;
            }
        }
        hidpDialog();
    }

    private void autoLogin(){
        App.getInstance().readData();
        username =App.getInstance().getAccount();
        password = App.getInstance().getPassword();

        login();

    }

    private void show(){
        setContentView(R.layout.activity_login);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pDialog != null)
            pDialog.dismiss();
    }
}
