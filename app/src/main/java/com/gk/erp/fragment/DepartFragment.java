package com.gk.erp.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gk.erp.AddDepartActivity;
import com.gk.erp.AddTaskActivity;
import com.gk.erp.R;
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
 */
public class DepartFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,Response.Listener<JSONObject>,Response.ErrorListener{

    private ListView listView;

    private ArrayAdapter adapter;

    private List<String> list = new ArrayList<>();

    private List<DepartEntry> entries = new ArrayList<>();

    private ProgressDialog pDialog;

    private FloatingActionButton fab;
    private SwipeRefreshLayout mItemContainer;
    public DepartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_depart, container, false);
        mItemContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemContainer.setOnRefreshListener(this);
        listView = (ListView) rootView.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
        initAdmin();
        fab = (FloatingActionButton) rootView.findViewById(R.id.fabButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddDepartActivity.class);
                //TODO 设置 操作类型
                intent.putExtra("action",2); // 2 insert
                getActivity().startActivity(intent);
            }
        });
        getItems();
        return rootView;
    }

    /**
     * 需要在 listview 实例化后调用
     */
    private void initAdmin(){
        List<String> popupMenuItemList = new ArrayList<>();
        popupMenuItemList.add("删除");

        PopupList popupList = new PopupList();
        popupList.init(getActivity(), listView, popupMenuItemList, new PopupList.OnPopupListClickListener() {
            @Override
            public void onPopupListClick(View contextView, final int contextPosition, int position) {
                //TODO 在这里启动 编辑和删除选项


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
        Map<String,String> params = new HashMap<>();
        params.put("type","1");
        CustomRequest jsonReq = new CustomRequest(Constants.GET_DEPART,params,this,this);
        App.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getActivity(),"更新失败",Toast.LENGTH_LONG).show();
        loadingComplete();
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
        }
        loadingComplete();
    }
    private void loadingComplete(){
        mItemContainer.setRefreshing(false);
    }


    public void delete(int position){
        Map<String,String> params = new HashMap<>();
        params.put("departId",entries.get(position).getDepartmentId()+"");
        params.put("account",App.getInstance().getAccount());
        CustomRequest jsonReq = new CustomRequest(Constants.DELETE_DEPART, params, new Response.Listener<JSONObject>() {
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
