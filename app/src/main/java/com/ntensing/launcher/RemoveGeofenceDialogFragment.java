package com.ntensing.launcher;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class RemoveGeofenceDialogFragment extends DialogFragment {
    RemoveGeofenceDialogListener listener;
    private String geofenceID;

    public RemoveGeofenceDialogFragment(String geofenceID) {
        super();
        this.geofenceID = geofenceID;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.removeGeofenceDialogTitle))
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> listener.onRemoveGeofenceDialogYesClick(geofenceID))
                .setNegativeButton(getString(R.string.no), (dialog, id) -> listener.onRemoveGeofenceDialogNoClick(geofenceID));
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (RemoveGeofenceDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement RemoveGeofenceDialogListener");
        }
    }

    public interface RemoveGeofenceDialogListener {
        void onRemoveGeofenceDialogYesClick(String geofenceId);
        void onRemoveGeofenceDialogNoClick(String geofenceId);
    }
}
