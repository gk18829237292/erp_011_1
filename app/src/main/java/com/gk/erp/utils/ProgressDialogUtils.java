package com.gk.erp.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by pc_home on 2016/12/7.
 */

public class ProgressDialogUtils {

    public static ProgressDialog newInstance(Context context){
        return newInstance(context,"登录中···",false);
    }

    public static ProgressDialog newInstance(Context context,String msg){
        return newInstance(context,msg,false);
    }

    public static ProgressDialog newInstance(Context context,String msg,boolean canceable){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(canceable);
        return progressDialog;
    }

    public static void hideProgressDialog(ProgressDialog pDialog){
        if(pDialog != null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    public static void showProgressDialog(ProgressDialog pDialog){
        if(pDialog != null && !pDialog.isShowing()){
            pDialog.show();
        }
    }


}
