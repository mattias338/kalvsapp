package com.example.banken.kalvsapp.data;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Entry {
    private final Activity activity;
    private final Date date;

    public Entry(Activity activity, Date date) {
        this.activity = activity;
        this.date = date;
    }

    public Activity getActivity() {
        return activity;
    }

    public Date getDate() {
        return date;
    }

    public String toPersistentString() {
        return "" + activity + "," + date.getTime();
    }

    public static Entry fromPersistentString(String s) {
        String[] split = s.split(",");
        String activityS = split[0];
        String dateS = split[1];

        Activity activity = Activity.valueOf(activityS);
        Date date = new Date(Long.parseLong(dateS));

        return new Entry(activity, date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entry entry = (Entry) o;

        if (activity != entry.activity) return false;
        return date != null ? date.equals(entry.date) : entry.date == null;

    }

    @Override
    public int hashCode() {
        int result = activity != null ? activity.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "" + date + ": " + activity;
    }

    public String toUiString(Database database) {
        switch (activity) {
            case WAKE_UP :
                List<Entry> list = database.getList();
                int thisIndex = list.indexOf(this);
                for (int i = thisIndex; i >= 0; i--) {
                    Entry entry = list.get(i);
                    if (entry.activity == Activity.FALL_SLEEP) {
                        long diffMillis = date.getTime() - entry.date.getTime();
                        long diffMinutes = diffMillis / (60 * 1000);
                        String ret = ". Slept for ";
                        if (diffMinutes > 60) {
                            ret = ret + (diffMinutes / 60) + " hour(s) and ";
                        }
                        ret = ret + (diffMinutes % 60) + " minute(s).";
                        return toString() + ret;
                    }
                }

                // Intended fall through
            default :
                return toString();
        }
    }

    public static void main(String[] args) {
        Date date = new Date();
        Activity activity = Activity.EAT_BOTH_SIDES;

        Entry entry = new Entry(activity, date);
        String s = entry.toPersistentString();

        Entry restoredEntry = fromPersistentString(s);

        boolean equals = entry.equals(restoredEntry);

        System.out.println("equals = " + equals);
    }
}
