package com.ntensing.launcher;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;

import java.time.LocalTime;

public class TimePreference extends Preference
        implements TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "TimePreference";

    public TimePreference(@NonNull Context context, FragmentManager fragmentManager) {
        super(context);

        setOnPreferenceClickListener(listener -> {
            new TimePickerDialogFragment(this).show(fragmentManager, "START_TIME");
            return false;
        });
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        LocalTime time = LocalTime.of(hour, minute);

        persistString(time.toString());
        setSummary(time.toString());
        Log.d(TAG, "Set time to " + time);
    }
}
