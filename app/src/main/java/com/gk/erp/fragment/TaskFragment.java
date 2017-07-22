package com.gk.erp.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gk.erp.R;
import com.gk.erp.AddTaskActivity;
import com.gk.erp.TaskDetailsActivity;
import com.gk.erp.adapter.TaskAdapter;
import com.gk.erp.app.App;
import com.gk.erp.constants.Constants;
import com.gk.erp.entry.DepartEntry;
import com.gk.erp.entry.TaskEntry;
import com.gk.erp.utils.CustomRequest;
import com.gk.erp.utils.PopupList;
import com.gk.erp.utils.ProgressDialogUtils;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * 2016年12月8日13:47:44 update 大改
 */
public class TaskFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,Response.Listener<JSONObject>,Response.ErrorListener{

    private static final int REQUESTFORUPDATE = 123;

    private ListView listView;
    private List<TaskEntry> entries = new ArrayList<>();

    private SwipeRefreshLayout mItemContainer;

    private TaskAdapter taskAdapter;

    private FloatingActionButton fab;

    private ProgressDialog pDialog;

    public static TaskFragment newInstance(){

        return newInstance(0);
    }

    public static TaskFragment newInstance(int showType){
        TaskFragment fragment = new TaskFragment();
        fragment.setShowType(showType);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskAdapter = new TaskAdapter(getActivity(),entries);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_task,container,false);

        mItemContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemContainer.setOnRefreshListener(this);
        listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(getActivity(), TaskDetailsActivity.class);
                    intent.putExtra("taskId",entries.get(position).getTaskId());
                    App.getInstance().setTaskEntry(entries.get(position));
                    getActivity().startActivity(intent);
                    listView.setItemChecked(position,false);

            }
        });
        listView.setAdapter(taskAdapter);

        if(App.getInstance().getType() == 0){
            initAdmin();
        }

//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//                showWindow(view);
//                return true;
//            }
//        });

        fab = (FloatingActionButton) rootView.findViewById(R.id.fabButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTaskActivity.class);
                //TODO 设置 操作类型
                intent.putExtra("action",2); // 2 insert
                getActivity().startActivity(intent);
                getActivity().startActivityForResult(intent,REQUESTFORUPDATE);
            }
        });
        if(showType != 0){
            fab.setVisibility(View.GONE);
        }
        getItems();
        return rootView;
    }

    /**
     * 需要在 listview 实例化后调用
     */
    private void initAdmin(){
        List<String> popupMenuItemList = new ArrayList<>();
        popupMenuItemList.add("编辑");
        popupMenuItemList.add("删除");

        PopupList popupList = new PopupList();
        popupList.init(getActivity(), listView, popupMenuItemList, new PopupList.OnPopupListClickListener() {
            @Override
            public void onPopupListClick(View contextView, final int contextPosition, int position) {
                //TODO 在这里启动 编辑和删除选项
                if(position == 0){ //编辑
                    Intent intent = new Intent(getActivity(), AddTaskActivity.class);
                    intent.putExtra("action",1); // 1 update
                    intent.putExtra("taskId",entries.get(position).getTaskId());
                    App.getInstance().setTaskEntry(entries.get(contextPosition));
                    getActivity().startActivityForResult(intent,REQUESTFORUPDATE);
                }else{ //删除
                    try {
                        Dialog dialog = new  AlertDialog.Builder(getActivity()).setTitle("删除任务").setMessage("确认删除吗？")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pDialog = ProgressDialogUtils.newInstance(getActivity(),"删除中···");
                                        ProgressDialogUtils.showProgressDialog(pDialog);
                                        delete(contextPosition);
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).create();
                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        popupList.setTextSize(popupList.sp2px(16));
        popupList.setTextPadding(popupList.dp2px(10), popupList.dp2px(5), popupList.dp2px(10), popupList.dp2px(5));
        popupList.setIndicatorView(popupList.getDefaultIndicatorView(popupList.dp2px(16), popupList.dp2px(8), 0xFF444444));
    }

    @Override
    public void onRefresh() {
        if(!App.getInstance().isConnected()){
            Toast.makeText(getActivity(),"请检查网络连接",Toast.LENGTH_LONG).show();
        }else{
            mItemContainer.setRefreshing(true);
            getItems();
        }
    }

    public void getItems(){
        System.out.println("刷新中···");
        Map<String,String> params = new HashMap<>();
        params.put("type",showType+""); 		// 0 获取全部 1 按部门获取 2 按账号获取
        if(showType == 2){
            params.put("account",App.getInstance().getAccount());
        }else if(showType == 1){
            params.put("departId",App.getInstance().getDepartId()+"");
        }
        CustomRequest jsonReq = new CustomRequest(Constants.GET_TASK,params,this,this);
        App.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getActivity(),"未知错误失败",Toast.LENGTH_LONG).show();
        loadingComplete();
    }

    @Override
    public void onResponse(JSONObject response) {
        if(response.has("error")) return;
//        Toast.makeText(getActivity(),"更新成功，加载中",Toast.LENGTH_SHORT).show();

        try {
            JSONArray tasks = response.getJSONArray("tasks");
            entries.clear();
            entries.addAll(TaskEntry.getAllTaskFromJson(tasks));
            taskAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadingComplete();
    }

    private void loadingComplete(){
        mItemContainer.setRefreshing(false);
        ProgressDialogUtils.hideProgressDialog(pDialog);
    }

    public int  showType = 0;

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }


    public void delete(int position){
        Map<String,String> params = new HashMap<>();
        params.put("taskId",entries.get(position).getTaskId()+"");
        params.put("account",App.getInstance().getAccount());
        CustomRequest jsonReq = new CustomRequest(Constants.DELETE_TASK, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response.has("error")){
                    String msg = "未知错误，失败";
                    if(response.has("msg")){
                        try {
                            msg = response.getString("msg");
                        } catch (JSONException e) {
                            msg = "未知错误，失败";
                        }
                    }
                    Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_SHORT).show();
                    getItems();
                }
                ProgressDialogUtils.hideProgressDialog(pDialog);
            }
        }, this);

        App.getInstance().addToRequestQueue(jsonReq);
    }

}
