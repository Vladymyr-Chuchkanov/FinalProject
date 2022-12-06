package com.chuchkanov.finalproject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class CustomDialogFragment extends DialogFragment {

    private Removable removable;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        removable = (Removable) context;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String name = getArguments().getString("name");
        final int pos = getArguments().getInt("pos");
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Видалення")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Ви хочете видалити " + name + " зі списку останніх?")
                .setPositiveButton("Так", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removable.remove(name, pos);
                    }
                })
                .setNegativeButton("Ні", null)
                .create();
    }
}