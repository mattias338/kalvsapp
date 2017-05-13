package com.example.banken.kalvsapp;

import android.app.Fragment;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.banken.kalvsapp.data.Activity;
import com.example.banken.kalvsapp.data.Database;
import com.example.banken.kalvsapp.data.Entry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//public class MainActivity extends AppCompatActivity {
public class MainActivity extends ListActivity implements ActivityPickerListener {
    private static final Database database = new Database();
    private BaseAdapter adapter;
    private List<Map<String, String>> entriesForSimpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(com.android.internal.R.layout.list_content_simple);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EntryPicker entryPicker = new EntryPicker();
                entryPicker.setListener(MainActivity.this);
                entryPicker.show(MainActivity.this.getFragmentManager(), "hello");
//                Activity activityA = entryPicker.getActivityA();
//                Intent pickActivityIntent = new Intent(MainActivity.this, EntryPicker.class);
//                startActivityForResult(pickActivityIntent, 0);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                addData(activityA);
            }
        });

        updateData();
    }

    protected void addData(Activity activity) {
        Date date = new Date();
        database.addEntry(new Entry(activity, date));
        updateData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateData() {
        String mapkey = "entry-string";
        if (adapter == null) {
            List<Entry> entries = database.getList();
            entriesForSimpleAdapter = new ArrayList<>();
            for (Entry entry : entries) {
                Map<String, String> map = new HashMap<>();
                map.put(mapkey, entry.toUiString(database));
                entriesForSimpleAdapter.add(map);
            }

            adapter = new SimpleAdapter(this, entriesForSimpleAdapter, android.R.layout.simple_list_item_1,
                    new String[]{mapkey}, new int[]{android.R.id.text1});

//            adapter = new ArrayAdapter<>(this,
//                    android.R.layout.simple_list_item_1, database.toArray() /*new String[]{"a", "b"}*/);
            getListView().setAdapter(adapter);
        } else {
//            Entry entry = database.getList().get(database.getList().size() - 1);
//            Map<String, String> map = new HashMap<>();
//            map.put(mapkey, entry.toUiString(database));
            List<Entry> entries = database.getList();
            entriesForSimpleAdapter.clear();
            for (Entry entry : entries) {
                Map<String, String> map = new HashMap<>();
                map.put(mapkey, entry.toUiString(database));
                entriesForSimpleAdapter.add(map);
            }
//            entriesForSimpleAdapter.add(map);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPickedActivity(Activity activity) {
        addData(activity);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        database.removeEntry(position);
        updateData();
    }
}
