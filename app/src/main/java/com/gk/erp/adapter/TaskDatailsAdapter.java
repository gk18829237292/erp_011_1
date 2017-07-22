package com.gk.erp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gk.erp.FileUploadActivity;
import com.gk.erp.ImagePageActivity;
import com.gk.erp.MainGuestActivity;
import com.gk.erp.R;
import com.gk.erp.app.App;
import com.gk.erp.entry.PictureEntry;
import com.gk.erp.entry.ReportEntry;
import com.gk.erp.utils.ImageUtil;
import com.gk.erp.utils.TimeUtils;
import com.gk.erp.view.AnimatedExpandableListView;
import com.gk.erp.view.NoScrollGridView;

import java.util.ArrayList;
import java.util.List;

import static com.gk.erp.constants.Constants.WEB_SITE;

/**
 * Created by pc_home on 2016/12/3.
 */

public class TaskDatailsAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter{

    private LayoutInflater inflater;
    private Context context;
    private List<List<ReportEntry>> entries;

    public TaskDatailsAdapter(Context context, List<List<ReportEntry>> entries){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.entries = entries;
    }


     @Override
    public View getRealChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
         final ChildViewHolder holder;

         if(convertView == null){
             holder = new ChildViewHolder();
             convertView = inflater.inflate(R.layout.list_report_item,parent,false);
             holder.name = (TextView) convertView.findViewById(R.id.txt_name);
             holder.time = (TextView) convertView.findViewById(R.id.txt_time);
             holder.comment  = (TextView) convertView.findViewById(R.id.txt_comment);
             holder.imgs = (NoScrollGridView) convertView.findViewById(R.id.img_pics);
             holder.supervise = (NoScrollGridView) convertView.findViewById(R.id.super_vise);
//             holder.adapter = ;

             holder.btn_super = (TextView) convertView.findViewById(R.id.txt_super);
             convertView.setTag(holder);
         }else{
             holder = (ChildViewHolder) convertView.getTag();
         }



         final ReportEntry entry = entries.get(groupPosition).get(childPosition);
         holder.name.setText(entry.getName());
         holder.time.setText(TimeUtils.convert2String(entry.getTime()));
         holder.comment.setText(entry.getComment());
         holder.adapter = new SuperviseAdapter(context,entries.get(groupPosition).get(childPosition).getSupers());
         holder.supervise.setAdapter(holder.adapter);
         if(App.getInstance().getType() == 1){
             holder.btn_super.setVisibility(View.VISIBLE);
             holder.btn_super.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     System.out.println("click me");
                     //TODO 实现评论上传
                     Intent intent = new Intent(context, FileUploadActivity.class);
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     intent.putExtra("type",1);
                     intent.putExtra("reportId",entry.getId());
                     intent.putExtra("reportName",entry.getName());
                     context.startActivity(intent);
                 }
             });
         }else {
             holder.btn_super.setVisibility(View.GONE);
         }

         if(entry.getPaths() == null || entry.getPaths().size() == 0){
             holder.imgs.setVisibility(View.GONE);
         }else{
             holder.imgs.setAdapter(new NoScrollGridAdapter(context,entry.getPaths()));
         }
         if(entry.getSupers() == null || entry.getSupers().size() == 0){
             holder.supervise.setVisibility(View.GONE);
         }else {
             holder.adapter.notifyDataSetChanged();

         }
         holder.imgs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 System.out.println("img : "+ position);
//                 Toast.makeText(context,position,Toast.LENGTH_SHORT).show();
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

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return entries.get(groupPosition).size();
    }

    @Override
    public int getGroupCount() {
        return entries.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return entries.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return entries.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupViewHolder holder;
        System.out.println("Task groupPosition : " + groupPosition);
        if(convertView == null){
            holder = new GroupViewHolder();
            convertView = inflater.inflate(R.layout.list_item_expanable_report,parent,false);
            holder.report_title = (TextView) convertView.findViewById(R.id.txt_report_title);
            convertView.setTag(holder);
        }else{
            holder = (GroupViewHolder) convertView.getTag();
        }
        holder.report_title.setText("第 "+ (groupPosition +1)+" 次报告");
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private class ChildViewHolder{
        private TextView name,time,comment,btn_super;
        private NoScrollGridView imgs;
        private NoScrollGridView supervise;
        private SuperviseAdapter adapter;
    }

    private class GroupViewHolder{
        private TextView report_title;

    }
}
