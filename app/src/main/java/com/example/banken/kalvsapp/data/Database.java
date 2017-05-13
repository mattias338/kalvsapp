package com.example.banken.kalvsapp.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Database {
    private List<Entry> entries = new ArrayList<>();

    public Database() {
        entries.add(new Entry(Activity.EAT_BOTH_SIDES, new Date()));
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
        entries.remove(position);
    }
}
