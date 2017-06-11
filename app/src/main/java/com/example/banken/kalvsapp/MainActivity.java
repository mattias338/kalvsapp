package com.example.banken.kalvsapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.banken.kalvsapp.data.Activity;
import com.example.banken.kalvsapp.data.AllTasksReadListener;
import com.example.banken.kalvsapp.data.Database;
import com.example.banken.kalvsapp.data.Entry;

import java.util.Date;

//public class MainActivity extends AppCompatActivity {
public class MainActivity extends ListActivity implements ActivityPickerListener {
    private Database database = new Database();
    private SimpleAdapterManager simpleAdapterManager;

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


        simpleAdapterManager = new SimpleAdapterManager(this, database);
        getListView().setAdapter(simpleAdapterManager.getAdapter());
        database.readFromFile(this, new AllTasksReadListener() {
            @Override
            public void onReady() {
                updateData(false);
            }
        });
    }

    protected void addData(Activity activity) {
        Date date = new Date();
        database.addEntry(new Entry(activity, date));
        updateData(true);
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

    private void updateData(boolean store) {
        simpleAdapterManager.update();
        if (store) {
            database.storeToFile(this);
        }
    }

    @Override
    public void onPickedActivity(Activity activity) {
        addData(activity);
    }

    @Override
    protected void onListItemClick(ListView l, View v, final int position, long id) {
        super.onListItemClick(l, v, position, id);
        Entry entry = database.getEntry(position);

        new AlertDialog.Builder(this)
                .setTitle("Delete entry?")
                .setMessage("Do you really want to delete " + entry.toString() + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        database.removeEntry(position);
                        updateData(true);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}
