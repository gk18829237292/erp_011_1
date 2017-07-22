package com.gk.erp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.gk.erp.app.App;
import com.gk.erp.constants.Constants;
import com.gk.erp.utils.FileUploadManager;
import com.gk.erp.utils.MultipartRequest;
import com.gk.erp.utils.ProgressDialogUtils;
import com.lidong.photopicker.PhotoPickerActivity;
import com.lidong.photopicker.PhotoPreviewActivity;
import com.lidong.photopicker.SelectModel;
import com.lidong.photopicker.intent.PhotoPickerIntent;
import com.lidong.photopicker.intent.PhotoPreviewIntent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现评论以及审核上传的功能 根据type 来区分
 */
public class FileUploadActivity extends ActionBarActivity implements Response.Listener<JSONObject>,Response.ErrorListener{

    private static final int REQUEST_CAMERA_CODE = 10;
    private static final int REQUEST_PREVIEW_CODE = 20;
    private static final int REQUEST_TASK_LIST = 30;
    private ArrayList<String> imagePaths = new ArrayList<>();


    private GridView gridView;
    private GridAdapter gridAdapter;


    private String TAG =MainActivity.class.getSimpleName();

    private Button btn_submit;
    private TextView txt_taskName;
    private EditText txt_comment,txt_num;

    private String comment,num;
    private long taskId = -1;
    private long reportId = -1;

    private ProgressDialog pDialog;

    private int actionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionType = getIntent().getIntExtra("type",0);

        if(actionType == 0){
            setContentView(R.layout.activity_file_upload);
        }else{
            setContentView(R.layout.activity_super_upload);
        }




        init();
        if(actionType == 0){
            taskId = getIntent().getLongExtra("taskId",-1);
            String taskName = getIntent().getStringExtra("taskName");
            txt_taskName.setText(taskName);
        }else if(actionType == 1){
            reportId = getIntent().getLongExtra("reportId",-1);
            String reportName = getIntent().getStringExtra("reportName");
            txt_taskName.setText(reportName);
        }


        pDialog = ProgressDialogUtils.newInstance(FileUploadActivity.this,"提交中···");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("任务提交");
    }

    private void init(){
        gridView = (GridView) findViewById(R.id.gridView);

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 3 ? 3 : cols;
        gridView.setNumColumns(cols);

        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imgs = (String) parent.getItemAtPosition(position);
                if ("000000".equals(imgs) ){
                    PhotoPickerIntent intent = new PhotoPickerIntent(FileUploadActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(9); // 最多选择照片数量，默认为6
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                }else{
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(FileUploadActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });
        imagePaths.add("000000");
        gridAdapter = new GridAdapter(imagePaths);
        gridView.setAdapter(gridAdapter);

        txt_comment = (EditText) findViewById(R.id.txt_comment);
        txt_taskName = (TextView) findViewById(R.id.txt_taskName);

        if(actionType == 0){
            txt_num = (EditText) findViewById(R.id.txt_num);
        }

        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!App.getInstance().isConnected()){
                    Toast.makeText(getApplicationContext(),getString(R.string.network_error),Toast.LENGTH_SHORT).show();
                }else if( actionType == 0 && checkNum() && checkComment()){
                    ProgressDialogUtils.showProgressDialog(pDialog);
                    submit();
                }else if(actionType ==1 && checkComment()){
                    ProgressDialogUtils.showProgressDialog(pDialog);
                    submit_1();
                }
            }
        });



    }

    private static final String ERROR ="";


    private boolean checkComment(){
        comment = txt_comment.getText().toString();
        txt_comment.setError(null);
        if(comment.length() <=0){
            txt_comment.setError(ERROR);
            return false;
        }
        return true;
    }

    private boolean checkNum(){
        num = txt_num.getText().toString();
        txt_num.setError(null);
        if(num.length() <=0){
            txt_num.setError(ERROR);
            return false;
        }
        return true;
    }

    private void submit(){

        Map<String,String> params = new HashMap<>();
        params.put("comment",comment);
        params.put("taskId",taskId+"");
        params.put("num",num);
        params.put("account",App.getInstance().getAccount());
        List<File> fileList = new ArrayList<>();
        for(String path:imagePaths){
            if(path.equals("000000")) continue;
            fileList.add(new File(path));
        }
        System.out.println(fileList);
        MultipartRequest jsonReq = new MultipartRequest(Constants.CREATE_REPORT,this,this,"fileList",fileList,params);


        App.getInstance().addToRequestQueue(jsonReq);
//        FileUploadManager.uploadMany();
    }

    private void submit_1(){
        Map<String,String> params = new HashMap<>();
        params.put("comment",comment);
        params.put("reportId",reportId+"");
        params.put("account",App.getInstance().getAccount());
        List<File> fileList = new ArrayList<>();
        for(String path:imagePaths){
            if(path.equals("000000")) continue;
            fileList.add(new File(path));
        }
        System.out.println(fileList);
        MultipartRequest jsonReq = new MultipartRequest(Constants.CREATE_SUPERVISE,this,this,"fileList",fileList,params);


        App.getInstance().addToRequestQueue(jsonReq);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    ArrayList<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                    Log.d(TAG, "list: " + "list = [" + list.size());
                    loadAdpater(list);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE:
                    ArrayList<String> ListExtra = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
                    Log.d(TAG, "ListExtra: " + "ListExtra = [" + ListExtra.size());
                    loadAdpater(ListExtra);
                    break;
                case REQUEST_TASK_LIST:
                    taskId = data.getLongExtra("taskId",-1);
                    System.out.println(taskId);
                    txt_taskName.setText(data.getStringExtra("taskName"));
                    break;
            }
        }
    }

    private void loadAdpater(ArrayList<String> paths){
        if (imagePaths!=null&& imagePaths.size()>0){
            imagePaths.clear();
        }
        if (paths.contains("000000")){
            paths.remove("000000");
        }
        paths.add("000000");
        imagePaths.addAll(paths);
        gridAdapter  = new GridAdapter(imagePaths);
        gridView.setAdapter(gridAdapter);
        try{
            JSONArray obj = new JSONArray(imagePaths);
            Log.e("--", obj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(),"提交出错，请重试",Toast.LENGTH_SHORT).show();
        ProgressDialogUtils.hideProgressDialog(pDialog);
    }

    @Override
    public void onResponse(JSONObject response) {
        ProgressDialogUtils.hideProgressDialog(pDialog);
        if(response.has("error")){
            if(response.has("msg")){
                try {
                    Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
            if(actionType == 0) setResult(RESULT_OK);
            Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;
        private LayoutInflater inflater;
        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
            if(listUrls.size() == 7){
                listUrls.remove(listUrls.size()-1);
            }
            inflater = LayoutInflater.from(FileUploadActivity.this);
        }

        public int getCount(){
            return  listUrls.size();
        }
        @Override
        public String getItem(int position) {
            return listUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_image, parent,false);
                holder.image = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            final String path=listUrls.get(position);
            if (path.equals("000000")){
                holder.image.setImageResource(R.mipmap.add_pic);
            }else {
                Glide.with(FileUploadActivity.this)
                        .load(path)
                        .placeholder(R.mipmap.default_error)
                        .error(R.mipmap.default_error)
                        .centerCrop()
                        .crossFade()
                        .into(holder.image);
            }
            return convertView;
        }
        class ViewHolder {
            ImageView image;
        }
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
}
