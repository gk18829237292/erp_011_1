package com.gk.erp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import com.gk.erp.R;
import com.gk.erp.adapter.ImageAdapter;
import com.gk.erp.entry.PictureEntry;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends ActionBarActivity {

    private ListView listView;
    private List<PictureEntry> entries;
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        listView = (ListView) findViewById(R.id.list_view);

        entries = new ArrayList<>();
        entries.add(new PictureEntry());
        entries.add(new PictureEntry());
        entries.add(new PictureEntry());
        entries.add(new PictureEntry());
        entries.add(new PictureEntry());
        adapter = new ImageAdapter(getApplicationContext(),entries);
        listView.setAdapter(adapter);
    }
}
