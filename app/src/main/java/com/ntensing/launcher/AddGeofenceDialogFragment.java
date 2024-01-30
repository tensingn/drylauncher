package com.ntensing.launcher;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;

public class AddGeofenceDialogFragment extends DialogFragment {
    AddGeofenceDialogListener listener;
    private float radius = 250f;
    private LatLng latLng;

    public AddGeofenceDialogFragment(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.addGeofenceDialogTitle))
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> listener.onAddGeofenceDialogYesClick(latLng, radius))
                .setNegativeButton(getString(R.string.no), (dialog, id) -> listener.onAddGeofenceDialogNoClick());
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (AddGeofenceDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement AddGeofenceDialogListener");
        }
    }

    public interface AddGeofenceDialogListener {
        void onAddGeofenceDialogYesClick(LatLng latLng, float radius);
        void onAddGeofenceDialogNoClick();
    }
}
