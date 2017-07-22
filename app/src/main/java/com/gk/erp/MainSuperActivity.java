package com.gk.erp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gk.erp.adapter.DrawAdapter;
import com.gk.erp.app.App;
import com.gk.erp.fragment.DepartFragment;
import com.gk.erp.fragment.TaskFragment;
import com.gk.erp.model.DrawerItem;

import java.util.ArrayList;
import java.util.List;

public class MainSuperActivity extends ActionBarActivity {

    private DrawerLayout mDrawLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private List<DrawerItem> mDrawerItems;

    private Handler mHandler;


    private TextView txt_name,txt_depart;

    TaskFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();
        prepareNaigationDrawerItems();
        fragment = TaskFragment.newInstance(1);
        commitFragment(fragment);
        init();

        //TODO 看代码 看看作用
        if(savedInstanceState == null){
            int position = 0;
            selectItem(position,mDrawerItems.get(position).getTag());
            //TODO change
//            mDrawLayout.openDrawer(mDrawerList);
        }
//        setTitle(mDrawerItems.get(0).getTitle());
        mDrawLayout.setDrawerListener(mDrawerToggle);



        //TODO 加菜单

    }

    private void init(){
        //TODO toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawLayout = (DrawerLayout) findViewById(R.id.layout_drawer);
        //TODO 可以重写设置更新
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawLayout,toolbar, R.string.open, R.string.close);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        mDrawLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);



        mDrawerList = (ListView) findViewById(R.id.list_view);
        mDrawerList.setAdapter(new DrawAdapter(this,mDrawerItems));

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(i > 0){
                    selectItem(i-1,mDrawerItems.get(i-1).getTag());
                }
            }
        });

        View headerView = getLayoutInflater().inflate(R.layout.nav_header_main,mDrawerList,false);
        txt_name = (TextView) headerView.findViewById(R.id.txt_name);
        txt_depart = (TextView) headerView.findViewById(R.id.txt_depart);

        txt_name.setText(App.getInstance().getName());

        txt_depart.setText(App.getInstance().getDepartName());

//        if(tempId != -1 && App.getInstance().getDeparts().containsKey(tempId)){
//            txt_depart.setText(App.getInstance().getDeparts().get(tempId).getDepartmentName());
//        }else{
//            txt_depart.setText("部门获取错误，请联系管理员");
//        }

        mDrawerList.addHeaderView(headerView);


    }

    //TODO　修改 2016年12月8日11:18:00
    private void selectItem(int position,int drawTag){

        switch (drawTag){
            case DrawerItem.DRAWER_ITEM_TAG_LIST_VIEWS:
                fragment.setShowType(1); //1 部门的全部
                fragment.getItems();
                break;
            case DrawerItem.DRAWER_ITEM_TAG_SHAPE_IMAGE_VIEWS:
                fragment.setShowType(2); //2 是只显示自己
                fragment.getItems();
                break;
            case DrawerItem.DRAWER_ITEM_TAG_LEFT_MENUS:
                //TODO
                App.getInstance().removeData();
                App.getInstance().readData();
                App.getInstance().saveData();
                Intent intent= new Intent(MainSuperActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("flag",true);
                startActivity(intent);
                break;
        }
        mDrawerList.setItemChecked(position+1, true);
        setTitle(mDrawerItems.get(position).getTitle());
        mDrawLayout.closeDrawer(mDrawerList);



    }

    //TODO 这次不需要这个 但是不删除
    private Fragment getFragmentByDrawerTag(int drawerTag) {
        Fragment fragment = null;
        switch (drawerTag){
            case DrawerItem.DRAWER_ITEM_TAG_LIST_VIEWS:
                fragment = TaskFragment.newInstance();
                break;
            case DrawerItem.DRAWER_ITEM_TAG_SHAPE_IMAGE_VIEWS:
                fragment = new DepartFragment();
                break;
            case DrawerItem.DRAWER_ITEM_TAG_LEFT_MENUS:
                //TODO
                App.getInstance().removeData();
                App.getInstance().readData();
                App.getInstance().saveData();
                Intent intent= new Intent(MainSuperActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("flag",true);
                startActivity(intent);
                break;
        }
        return  fragment;
    }

    public void commitFragment(Fragment fragment) {
        // Using Handler class to avoid lagging while
        // committing fragment in same time as closing
        // navigation drawer
        mHandler.post(new MainSuperActivity.CommitFragmentRunnable(fragment));
    }

    //TODO 这里实例化填充的东西
    private void prepareNaigationDrawerItems(){
        mDrawerItems = new ArrayList<>();
        //任务
        mDrawerItems.add(new DrawerItem(R.string.drawer_icon_list_views, R.string.task, DrawerItem.DRAWER_ITEM_TAG_LIST_VIEWS));
     //   mDrawerItems.add(new DrawerItem(R.string.drawer_icon_shape_image_views, R.string.my_task, DrawerItem.DRAWER_ITEM_TAG_SHAPE_IMAGE_VIEWS));
        mDrawerItems.add(new DrawerItem(R.string.drawer_icon_left_menus,R.string.string_logout,DrawerItem.DRAWER_ITEM_TAG_LEFT_MENUS));
    }

    private class CommitFragmentRunnable implements Runnable {

        private Fragment fragment;

        public CommitFragmentRunnable(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void run() {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //TODO 菜单选项 空白处理就好
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }


}
