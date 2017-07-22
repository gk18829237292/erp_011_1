package com.gk.erp.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gk.erp.R;
import com.gk.erp.entry.PictureEntry;
import com.gk.erp.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import static com.gk.erp.constants.Constants.WEB_SITE;

/**
 * Created by pc_home on 2016/12/3.
 */

public class ImageAdapter  extends BaseAdapter{

    private LayoutInflater inflater;
    private List<PictureEntry> entries;

    public ImageAdapter(Context context, List<PictureEntry> entries){
        inflater = LayoutInflater.from(context);
        this.entries = entries;
        System.out.println( "ImageAdapter : "  +entries.size());
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
        System.out.println("Image position : " + position);
        final ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item_pics,parent,false);
            holder = new ViewHolder();
            holder.pic1 = (ImageView) convertView.findViewById(R.id.pic_1);
            holder.pic2 = (ImageView) convertView.findViewById(R.id.pic_2);
            holder.pic3 = (ImageView) convertView.findViewById(R.id.pic_3);
            holder.pic4 = (ImageView) convertView.findViewById(R.id.pic_4);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        ImageUtil.displayImage(holder.pic1,WEB_SITE +"img/gk1.jpg",null);
        ImageUtil.displayImage(holder.pic2,WEB_SITE +"img/gk2.jpg",null);
        ImageUtil.displayImage(holder.pic3,WEB_SITE +"img/a1.jpg",null);
        ImageUtil.displayImage(holder.pic4,WEB_SITE +"img/a2.jpg",null);
        return convertView;
    }

    private class ViewHolder{
        ImageView pic1,pic2,pic3,pic4;
    }

}
