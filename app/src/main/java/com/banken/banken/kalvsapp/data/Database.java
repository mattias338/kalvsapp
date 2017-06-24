package com.banken.banken.kalvsapp.data;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Database {
    private static final String FILENAME = "kalvsapp-data";
    private static final byte[] LINE_BREAK = System.getProperty("line.separator").getBytes();
    private final List<Entry> entries = new ArrayList<>();
    private Set<Activity> visibleActivities;

    public Database() {
    }

    public void addEntry(Entry e) {
        entries.add(e);
        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                return -o1.getDate().compareTo(o2.getDate());
            }
        });
    }

    public Entry[] toArray() {
        return entries.toArray(new Entry[0]);
    }

    public String[] toStringArray() {
        Entry[] entries = toArray();
        String[] strings = new String[entries.length];
        for (int i = 0; i < entries.length; i++) {
            strings[i] = entries[i].toString();
        }
        return strings;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public List<Entry> getVisibleEntries() {
        List<Entry> visibleEntries = new ArrayList<>();
        for (Entry entry : entries) {
            if (visibleActivities == null || visibleActivities.contains(entry.getActivity())) {
                visibleEntries.add(entry);
            }
        }
        return visibleEntries;
    }

    public void removeEntry(int position) {
        entries.remove(position);
    }

    public void removeEntry(Entry entry) {
        removeEntry(entries.indexOf(entry));
    }

    public void storeToFile(final Context context) {
        AsyncTask<Database, java.lang.Void, java.lang.Void> storeTask = new AsyncTask<Database, java.lang.Void, java.lang.Void>() {
            @Override
            protected java.lang.Void doInBackground(Database... params) {
                Database database = params[0];
                try (FileOutputStream fileOutputStream =
                             context.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
                    for (Entry entry : database.getEntries()) {
                        fileOutputStream.write(entry.toPersistentString().getBytes());
                        fileOutputStream.write(LINE_BREAK);
                    }
                    fileOutputStream.flush();
                } catch (FileNotFoundException e) {
//                    Log.e(TAG, "doInBackground: File not found", e);
                } catch (IOException e) {
//                    Log.e(TAG, "doInBackground: IO exception", e);
                }
                return null;
            }
        };
        storeTask.execute(this);
    }

    public void readFromFile(final Context context, final AllTasksReadListener onDone) {
        AsyncTask<Database, Void, Void> asyncTask = new AsyncTask<Database, Void, Void>() {
            @Override
            protected Void doInBackground(Database... params) {
                Database database = params[0];
                try {
                    BufferedReader bufferedReader =
                            new BufferedReader(new InputStreamReader(context.openFileInput(FILENAME)));
                    String entryLine;

                    while ((entryLine = bufferedReader.readLine()) != null) {
                        database.addEntry(Entry.fromPersistentString(entryLine));
                    }
                } catch (FileNotFoundException e) {
//                    Log.e(TAG, "doInBackground: File not found", e);
                } catch (IOException e) {
//                    Log.e(TAG, "doInBackground: IO exception", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                onDone.onReady();
            }
        };
        asyncTask.execute(this);
    }

    public Entry getEntry(int position) {
        return entries.get(position);
    }

    public Entry getVisibleEntry(int position) {
        if (visibleActivities == null) {
            return getEntry(position);
        } else {
            for (Entry entry : entries) {
                if (visibleActivities.contains(entry.getActivity())) {
                    if (position == 0) {
                        return entry;
                    }
                    position--;
                }
            }
        }
        throw new RuntimeException("Terrible state...");
    }

    public void deleteOlderThan(int numberOfDays) {
        long now = System.currentTimeMillis();
        long maxDiff = numberOfDays * 24 * 60 * 60 * 1000;

        Iterator<Entry> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if (now - entry.getDate().getTime() > maxDiff) {
                iterator.remove();
            }
        }
    }

    public void setVisibleActivities(Set<Activity> visibleActivities) {
        this.visibleActivities = visibleActivities;
    }
}
