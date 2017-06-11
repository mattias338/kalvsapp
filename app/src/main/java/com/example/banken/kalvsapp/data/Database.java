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
import java.util.List;

import static android.content.ContentValues.TAG;

public class Database {
    private static final String FILENAME = "kalvsapp-data";
    private static final byte[] LINE_BREAK = System.getProperty("line.separator").getBytes();
    private List<Entry> entries = new ArrayList<>();

    public Database() {
    }

    public void addEntry(Entry e) {
        entries.add(e);
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
}
