package com.example.banken.kalvsapp.data;

public enum Activity {
    BIG_POOP,
    SMALL_POOP,
    EAT_ONE_SIDE,
    EAT_BOTH_SIDES,
    FALL_SLEEP,
    WAKE_UP
    ;

    public static String[] toStringArray() {
        Activity[] values = Activity.values();
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = values[i].name();
        }
        return strings;
    }

    public static Activity fromOrdinal(int p) {
        Activity[] values = Activity.values();
        for (Activity value : values) {
            if (value.ordinal() == p) {
                return value;
            }
        }
        return null;
    }
}
