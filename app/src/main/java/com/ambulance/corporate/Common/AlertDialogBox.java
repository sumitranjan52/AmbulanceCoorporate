/*
 * Copyright (c) 2018. Sumit Ranjan
 */

package com.ambulance.corporate.Common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.ambulance.corporate.Interfaces.AmbulanceDialogInterface;

/**
 * Created by sumit on 31-Mar-18.
 */

public class AlertDialogBox {

    private Context mContext;

    public AlertDialogBox(Context mContext) {
        this.mContext = mContext;
    }

    public AlertDialog dialogBuilderWithoutAction(String title, String msg, boolean cancellable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(cancellable);
        builder.setTitle(title);
        builder.setMessage(msg);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public void dialogBuilderWithSingleAction(String title, String msg, boolean cancellable, String posText, final AmbulanceDialogInterface posClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(cancellable);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(posText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                posClick.onClick(dialog, which);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogBuilderWithTwoAction(String title, String msg, boolean cancellable
            , String posText, final AmbulanceDialogInterface posClick, String negText, final AmbulanceDialogInterface negClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(cancellable);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(posText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                posClick.onClick(dialog, which);
            }
        });
        builder.setNegativeButton(negText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                negClick.onClick(dialog, which);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
