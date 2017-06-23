package com.example.banken.kalvsapp.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class Database {
    private static final String FILENAME = "kalvsapp-data";
    private static final byte[] LINE_BREAK = System.getProperty("line.separator").getBytes();
    private final List<Entry> entries = new ArrayList<>();
    private final List<String> textRepresentation = new ArrayList<>();
    private Set<Activity> visibleActivities;

    public Database() {
    }

    public void addEntry(Entry e) {
        entries.add(e);
        updateTextRepresentation();
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

    public List<Entry> getList() {
        return entries;
    }

    public void removeEntry(int position) {
        entries.remove(entries.size() - 1 - position);
        updateTextRepresentation();
    }

    public void storeToFile(final Context context) {
        AsyncTask<Database, java.lang.Void, java.lang.Void> storeTask = new AsyncTask<Database, java.lang.Void, java.lang.Void>() {
            @Override
            protected java.lang.Void doInBackground(Database... params) {
                Database database = params[0];
                try (FileOutputStream fileOutputStream =
                             context.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
                    for (Entry entry : database.getList()) {
                        fileOutputStream.write(entry.toPersistentString().getBytes());
                        fileOutputStream.write(LINE_BREAK);
                    }
                    fileOutputStream.flush();
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "doInBackground: File not found", e);
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: IO exception", e);
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
                    Log.e(TAG, "doInBackground: File not found", e);
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: IO exception", e);
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
        return entries.get(entries.size() - 1 - position);
    }

    public List<String> getTextRepresentation() {
        return textRepresentation;
    }

    private void updateTextRepresentation() {
        textRepresentation.clear();
        for (Entry entry : entries) {
            if (visibleActivities == null || visibleActivities.contains(entry.getActivity())) {
                textRepresentation.add(entry.toUiString(this));
            }
        }
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
        updateTextRepresentation();
    }

    public void setVisibleActivities(Set<Activity> visibleActivities) {
        this.visibleActivities = visibleActivities;
        updateTextRepresentation();
    }
}
