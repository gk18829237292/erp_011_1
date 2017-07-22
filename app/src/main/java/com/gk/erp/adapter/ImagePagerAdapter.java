package com.gk.erp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gk.erp.fragment.ImageDetailFragment;

import java.util.List;

/**
 * Created by pc_home on 2016/12/4.
 */

public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    private List<String> fileList;

    public ImagePagerAdapter(FragmentManager fm,List<String> fileList) {
        super(fm);
        this.fileList = fileList;
    }


    @Override
    public Fragment getItem(int position) {
        String url = fileList.get(position);
        return new ImageDetailFragment().newInstance(url);
    }

    @Override
    public int getCount() {
        return fileList== null? 0 :fileList.size();
    }
}
