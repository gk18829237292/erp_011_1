package com.gk.erp.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.gk.erp.utils.Logger;


/**
 * 进程条，后面添加关于取消的选项
 *
 * Created by ke.gao on 2017/7/4.
 */

public class MyProgressDialog {

    private ProgressDialog pDialog;

    /**
     *
     * @param context 建议使用 Class.this,不建议使用 getContentContext
     */
    public MyProgressDialog(Context context, DialogInterface.OnCancelListener cancelListener){
        this(context,"","",cancelListener);
    }

    public MyProgressDialog(Context context, String message, DialogInterface.OnCancelListener cancelListener){
        this(context,message,"",cancelListener);
    }

    public MyProgressDialog(Context context, String message, String title, DialogInterface.OnCancelListener cancelListener){
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(message);
        pDialog.setTitle(title);
        pDialog.setCancelable(true);
        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setOnCancelListener(cancelListener);
    }

    public void dismiss(){
        if(pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    public void show(){
        if(!pDialog.isShowing()){
            Logger.d("MyProgressDialog","show it");
            pDialog.show();
        }
    }

}
