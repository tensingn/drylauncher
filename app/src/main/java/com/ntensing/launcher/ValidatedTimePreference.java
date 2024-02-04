package com.ntensing.launcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import java.time.LocalTime;

public class ValidatedTimePreference extends TimePreference {
    private static final String TAG = "ValidatedTimePreference";
    private ValidatedTimePreferenceComparator comparator;
    private LocalTime otherTime;
    private String otherTimePreferenceKey;
    private boolean otherTimeIsPreference;

    public ValidatedTimePreference(@NonNull Context context,
                                   FragmentManager fragmentManager,
                                   ValidatedTimePreferenceComparator comparator,
                                   LocalTime otherTime) {
        super(context, fragmentManager);

        this.comparator = comparator;
        this.otherTime = otherTime;
    }

    public ValidatedTimePreference(@NonNull Context context,
                                   FragmentManager fragmentManager,
                                   ValidatedTimePreferenceComparator comparator,
                                   String otherTimePreferenceKey) {
        super(context, fragmentManager);

        this.comparator = comparator;
        this.otherTimePreferenceKey = otherTimePreferenceKey;
        this.otherTimeIsPreference = true;
    }

    private boolean validate(LocalTime time) {
        if (otherTimeIsPreference) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String compareTimePrefString = prefs.getString(otherTimePreferenceKey, null);

            // if compareTimePrefString is null, the time pref to compare against has not been set,
            // so no need to compare
            if (compareTimePrefString == null) return true;

            try {
                otherTime = LocalTime.parse(compareTimePrefString);
            } catch(Exception e) {
                Log.e(TAG, "invalid time preference: " + compareTimePrefString);
                throw e;
            }
        }

        switch (comparator) {
            case BEFORE:
                return time.isBefore(otherTime);
            case AFTER:
                return time.isAfter(otherTime);
            default:
                return false;
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        LocalTime time = LocalTime.of(hour, minute);
        if (!validate(time)) {
            handleInvalidTime(otherTime);
            return;
        }

        super.onTimeSet(timePicker, hour, minute);
    }

    private void handleInvalidTime(LocalTime otherTime) {
        String logMessage = "invalid time selected";
        String toastMessage = "invalid time selected";

        switch (comparator) {
            case BEFORE:
                logMessage = "invalid time. time must be before " + otherTime.toString();
                toastMessage = "Time must be before " + otherTime;
                break;
            case AFTER:
                logMessage = "invalid time. time must be after " + otherTime.toString();
                toastMessage = "Time must be after " + otherTime;
                break;
        }

        Log.e(TAG, logMessage);
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    public enum ValidatedTimePreferenceComparator {
        BEFORE,
        AFTER
    }
}
