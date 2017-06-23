package com.example.banken.kalvsapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;

import com.example.banken.kalvsapp.data.Activity;
import com.example.banken.kalvsapp.data.AllTasksReadListener;
import com.example.banken.kalvsapp.data.Database;
import com.example.banken.kalvsapp.data.Entry;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Database database = new Database();
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(com.android.internal.R.layout.list_content_simple);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EntryPicker entryPicker = new EntryPicker();
                entryPicker.setListener(new ActivityPickerListener() {
                    @Override
                    public void onPickedActivity(Activity activity) {
                        addData(activity);
                    }
                });
                entryPicker.show(MainActivity.this.getFragmentManager(), "hello");
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                addData(activityA);
            }
        });

        ListView listView = (ListView) findViewById(android.R.id.list);
        arrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, database.getTextRepresentation());
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Entry entry = database.getEntry(position);

                new AlertDialog.Builder(MainActivity.this)
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
        });

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
        if (id == R.id.delete_old_ata) {
            final NumberPicker numberPicker = new NumberPicker(this);
            numberPicker.setMaxValue(100);
            numberPicker.setMinValue(0);
            new AlertDialog.Builder(this).
                    setTitle("Delete data older than X days.").
                    setView(numberPicker).
                    setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int value = numberPicker.getValue();
                            deleteData(value);
                        }
                    }).
                    setNegativeButton(android.R.string.cancel, null).
                    show();
        }

        if (id == R.id.filter_data) {
            Set<Activity> activities = new HashSet<>();
            for (Entry entry : database.getList()) {
                activities.add(entry.getActivity());
            }
            final String[] activitiesArray = new String[activities.size()];
            int i = 0;
            for (Activity activity : activities) {
                activitiesArray[i] = activity.name();
                i++;
            }
            boolean[] initSelectedActivites = new boolean[activitiesArray.length];
            for (i = 0; i < initSelectedActivites.length; i++) {
                initSelectedActivites[i] = true;
            }
            final Set<Activity> selectedActivities = activities;
            new AlertDialog.Builder(this).
                    setTitle("Select activities to show").
                    setMultiChoiceItems(activitiesArray, initSelectedActivites, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            Activity selectedActivity = Activity.valueOf(activitiesArray[which]);
                            if (isChecked) {
                                selectedActivities.add(selectedActivity);
                            } else {
                                selectedActivities.remove(selectedActivity);
                            }
                        }
                    }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateVisibilities(selectedActivities);
                }
            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();

        }

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateVisibilities(Set<Activity> selectedActivities) {
        database.setVisibleActivities(selectedActivities);
        updateData(false);

    }

    private void deleteData(int which) {
        Log.d("d", "delete data " + which);
        database.deleteOlderThan(which);
        updateData(true);
    }

    private void updateData(boolean store) {
        arrayAdapter.notifyDataSetChanged();
        if (store) {
            database.storeToFile(this);
        }
    }
}
