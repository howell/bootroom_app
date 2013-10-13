package com.tactical_foul.bootroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: scaldwell
 * Date: 10/12/13
 * Time: 8:52 PM
 */
public class EditGametimeDialogFragment extends DialogFragment {

    public interface UpdateTimeListener {
        public void setTime(int minutes, int seconds);
    }

    private UpdateTimeListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            mListener = (UpdateTimeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement UpdateTimeListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_edit_gametime, null));
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TextView minutesView = (TextView) getDialog().findViewById(R.id.edit_gametime_dialog_minutes);
                TextView secondsView = (TextView) getDialog().findViewById(R.id.edit_gametime_dialog_seconds);
                int minutes = Integer.parseInt(minutesView.getText().toString());
                int seconds = Integer.parseInt(secondsView.getText().toString());
                mListener.setTime(minutes, seconds);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        return builder.create();
    }
}
