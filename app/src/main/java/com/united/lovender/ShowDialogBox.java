package com.united.lovender;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;


public class ShowDialogBox {

    private Context context;

    ShowDialogBox(Context context){
        this.context = context;
    }

    //    error message
    public void errorMessage(String title, String message){
    }

    //    success message
    public void successMessage(String title, String message){
    }

    //    success message
    public void successMessageMaterialDialogs(String title, String message){
        try {
            new MaterialDialog.Builder(context)
                    .title(title)
                    .titleColor(Color.BLACK)
                    .content(message)
                    .contentColor(context.getResources().getColor(R.color.black))
                    .backgroundColor(context.getResources().getColor(R.color.white))
                    .positiveColor(context.getResources().getColor(R.color.red))
                    .positiveText("okay")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
        catch (Exception e){
            //
        }
    }

    //    warning message
    public void warningMessage(String title, String message){
    }


}