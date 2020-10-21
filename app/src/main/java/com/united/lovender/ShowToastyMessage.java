package com.united.lovender;

import android.content.Context;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class ShowToastyMessage {

    private Context context;

    ShowToastyMessage(Context context){
        this.context = context;
    }

//    error message
    public void errorMessage(String message){
        try {
            Toasty.Config.getInstance().setErrorColor(context.getResources().getColor(R.color.googleRed)).apply();
            Toasty.error(context, message, Toast.LENGTH_SHORT, true).show();
        }
        catch (Exception e){
            //
        }
    }

    //    error message
    public void errorMessageLong(String message){
        try {
            Toasty.Config.getInstance().setErrorColor(context.getResources().getColor(R.color.googleRed)).apply();
            Toasty.error(context, message, Toast.LENGTH_LONG, true).show();
        }
        catch (Exception e){
            //
        }
    }

    //    success message
    public void successMessage(String message){
        try {
            Toasty.Config.getInstance().setSuccessColor(context.getResources().getColor(R.color.lightGreen)).apply();
            Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();
        }
        catch (Exception e){
            //
        }
    }

    //    warning message
    public void warningMessage(String message){
        try {
            Toasty.Config.getInstance().setWarningColor(context.getResources().getColor(R.color.burlyWood)).apply();
            Toasty.warning(context, message, Toast.LENGTH_SHORT, true).show();
        }
        catch (Exception e){
            //
        }
    }


}