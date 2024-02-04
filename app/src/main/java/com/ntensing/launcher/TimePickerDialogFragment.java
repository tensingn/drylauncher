package com.ntensing.launcher;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class TimePickerDialogFragment extends DialogFragment {
    TimePickerDialog.OnTimeSetListener listener;

    private static final String TAG = "TimePickerFragment";

    public TimePickerDialogFragment(TimePickerDialog.OnTimeSetListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = getIntFromBundle("hour");
        int minute = getIntFromBundle("minute");

        final Calendar c = Calendar.getInstance();
        hour = hour != -1 ? hour : c.get(Calendar.HOUR_OF_DAY);
        minute = minute != -1 ? minute : c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), listener, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    private int getIntFromBundle(String key) {
        int value = -1;

        Bundle receivedBundle = getArguments();
        if (receivedBundle != null) {
            value = receivedBundle.getInt(key, -1);
        }

        return value;
    }
}
