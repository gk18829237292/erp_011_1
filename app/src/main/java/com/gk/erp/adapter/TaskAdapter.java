package com.gk.erp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gk.erp.R;
import com.gk.erp.TaskDetailsActivity;
import com.gk.erp.entry.TaskEntry;
import com.gk.erp.utils.TimeUtils;

import java.util.List;

/**
 * Created by pc_home on 2016/12/3.
 */

public class TaskAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<TaskEntry> entries;
    private Context context;

    public TaskAdapter(Context context,List<TaskEntry> entries){
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int i) {
        return entries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){

            convertView = mInflater.inflate(R.layout.list_task_items,parent,false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.txt_name);
            holder.goal = (TextView) convertView.findViewById(R.id.txt_goal);
            holder.time = (TextView) convertView.findViewById(R.id.txt_time);
            holder.icon = (TextView)convertView.findViewById(R.id.icon_important);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final TaskEntry entry = entries.get(position);
        holder.name.setText(entry.getTaskName());
        holder.goal.setText(entry.getGoal());
        holder.time.setText(TimeUtils.convert2String(entry.getStartTime()));
        if(entry.getType() == 1){
            holder.icon.setVisibility(View.VISIBLE);
        }else{
            holder.icon.setVisibility(View.INVISIBLE);
        }



        return convertView;
    }

    private class ViewHolder{
        TextView name,goal,time,icon;
    }
}
