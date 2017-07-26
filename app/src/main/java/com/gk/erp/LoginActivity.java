package com.gk.erp;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gk.erp.app.App;
import com.gk.erp.constants.Constants;
import com.gk.erp.entry.UserEntry;
import com.gk.erp.utils.CustomRequest;
import com.gk.erp.utils.LoginUtils;
import com.gk.erp.utils.ToastUtils;
import com.gk.erp.view.FloatLabeledEditText;
import com.gk.erp.view.MyProgressDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录只是去获取基本的信息 不再做别的事
 */
public class LoginActivity extends BaseActivity implements Response.Listener<JSONObject>,Response.ErrorListener{

    private static final String TAG = "LoginActivity";
    private FloatLabeledEditText loginText,passText;
    private TextView login;
    private String username,password;
    private Map<String,String> params;
    private boolean mIsLastSuccess = false; //在上次登录成功的时候设置为true
    private MyProgressDialog pDialog;

    private static final String LOGIN_FLAG = "LOGIN_FLAG";
    @Override
    public void initView() {
        setContentView(R.layout.activity_login);
        pDialog = new MyProgressDialog(mContext,"登录中···",null);
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

    }

    @Override
    public void initData() {
        mIsLastSuccess = mSpref.getBoolean(LOGIN_FLAG,false);
        if(mIsLastSuccess && App.getInstance().getUserEntry(mSpref) != null){
            autoLogin();
        }
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
        pDialog.show();
        params = new HashMap<>();
        params.put("account",username);
        params.put("password",password);
        CustomRequest jsonReq = new CustomRequest(Constants.METHOD_ACCOUNT_AUTHORIZE,params,this,this);
        App.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(mIsLastSuccess) {
            setContentView(R.layout.activity_login);
            Toast.makeText(getApplicationContext(), "自动登录失败", Toast.LENGTH_SHORT).show();
            mIsLastSuccess = false;
        }else{
            Toast.makeText(getApplicationContext(),"登录失败 : " + error.getMessage(),Toast.LENGTH_LONG).show();
        }
        UserEntry.clearSpref(mSpref);
        mSpref.put(LOGIN_FLAG,false);
        pDialog.dismiss();
    }

    @Override
    public void onResponse(JSONObject response) {
        ToastUtils.showShortToast(mContext,"success");
//        mSpref.put(LOGIN_FLAG,true);
        Log.d(TAG,"response is "+ response);
        UserEntry userEntry = UserEntry.getFromJson(response);

        if(userEntry != null){//登录成功
            App.getInstance().setUserEntry(userEntry);
            mSpref.put(LOGIN_FLAG,true);
            userEntry.writeToSpref(mSpref);
            mSpref.commit();
            Log.d(TAG,"userEntry is " + userEntry.toString());
            ToastUtils.showShortToast(mContext,"登录成功");
        }else{//登录失败
            ToastUtils.showShortToast(mContext,"登录失败");
        }
        pDialog.dismiss();
    }

    private void autoLogin(){
        UserEntry entry = App.getInstance().getUserEntry();
        username = entry.getAccount();
        password = entry.getPassword();
        login();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pDialog != null)
            pDialog.dismiss();
    }




}
