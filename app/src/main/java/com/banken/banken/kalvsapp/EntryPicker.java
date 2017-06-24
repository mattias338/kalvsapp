package com.banken.banken.kalvsapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.banken.banken.kalvsapp.data.Activity;

public class EntryPicker extends DialogFragment {
    private ActivityPickerListener activityPickerListener;
    private final ObjectHolder<Activity> activityHolder = new ObjectHolder<>();
    private MainActivity listener;

    public Activity getActivityA() {
        return activityHolder.object;
    }

    @Override
    public void onAttach(Context context) {
        System.out.println("EntryPicker.onAttach");
        super.onAttach(context);

//        activityPickerListener = (ActivityPickerListener) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        System.out.println("EntryPicker.onCreateDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select activity")
                .setItems(Activity.toStringArray(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityHolder.object = Activity.fromOrdinal(which);
                        activityPickerListener.onPickedActivity(activityHolder.object);
                    }
                });


//        Intent intent = new Intent();
//        intent.setData(Uri.parse(activityHolder.object.name()));
//        getActivity().setResult(0, intent);
        return builder.create();
    }

    public void setListener(ActivityPickerListener listener) {
        activityPickerListener = listener;
    }
}
