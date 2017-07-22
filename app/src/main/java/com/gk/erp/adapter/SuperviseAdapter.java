package com.gk.erp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gk.erp.ImagePageActivity;
import com.gk.erp.R;
import com.gk.erp.entry.ReportEntry;
import com.gk.erp.utils.TimeUtils;
import com.gk.erp.view.NoScrollGridView;

import java.util.ArrayList;
import java.util.List;

import static com.gk.erp.constants.Constants.WEB_SITE;


/**
 * Created by pc_home on 2016/12/4.
 */

public class SuperviseAdapter extends BaseAdapter {

    private Context context;
    private List<ReportEntry> entries;
    private LayoutInflater inflater;

    public SuperviseAdapter(Context context,List<ReportEntry> entries){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item_expandable_report_child,parent,false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.txt_name);
            holder.time = (TextView) convertView.findViewById(R.id.txt_time);
            holder.comment  = (TextView) convertView.findViewById(R.id.txt_comment);
            holder.imgs = (NoScrollGridView) convertView.findViewById(R.id.img_pics);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final ReportEntry entry = entries.get(position);
        holder.name.setText(entry.getName());
        holder.time.setText(TimeUtils.convert2String(entry.getTime()));
        holder.comment.setText(entry.getComment());
        if(entry.getPaths() == null || entry.getPaths().size() == 0){
            holder.imgs.setVisibility(View.GONE);
        }else{
            holder.imgs.setAdapter(new NoScrollGridAdapter(context,entry.getPaths()));
        }
        holder.imgs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                System.out.println("img : "+ position);
                imageBrower(position, (ArrayList<String>) entry.getPaths());
            }
        });
        return convertView;
    }

    protected void imageBrower(int position,ArrayList<String> urls){
        Intent intent = new Intent(context, ImagePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ImagePageActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImagePageActivity.EXTRA_IMAGE_INDEX, position);
        context.startActivity(intent);
    }
    private class ViewHolder{
        private TextView name,time,comment;
        private NoScrollGridView imgs;
    }
}
