package com.gk.erp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gk.erp.R;
import com.gk.erp.constants.Constants;
import com.gk.erp.utils.ImageUtil;

import java.util.List;

/**
 * Created by pc_home on 2016/12/4.
 */

public class NoScrollGridAdapter extends BaseAdapter {

    private Context context;
    private List<String> paths;

    public NoScrollGridAdapter(Context context, List<String> paths) {
        this.context = context;
        this.paths = paths;
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public Object getItem(int position) {
        return paths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_gridview,null);
            holder = new ViewHolder();
            holder.pic = (ImageView) convertView.findViewById(R.id.iv_pic);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        ImageUtil.displayImage(holder.pic, Constants.IMG_DOMAIN+ paths.get(position),null);

        return convertView;
    }

    private class ViewHolder{
        ImageView pic;
    }
}
