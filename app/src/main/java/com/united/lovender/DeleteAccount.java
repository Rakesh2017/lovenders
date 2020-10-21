package com.united.lovender;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import mehdi.sakout.fancybuttons.FancyButton;

public class DeleteAccount extends AppCompatActivity {

    private String reason;
    private SpinKitView spin_kit_delete_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_delete_account);

        MaterialSpinner materialSpinner = findViewById(R.id.dl_spinner);
        ImageButton back_button = findViewById(R.id.dl_back_ib);
        FancyButton submit = findViewById(R.id.dl_delete_btn);
        spin_kit_delete_account = findViewById(R.id.dl_delete_Account_spin_kit);

        materialSpinner.setItems("please choose a reason",
                "I have met someone via lovenders",
                "I am in relationship",
                "I don't like lovenders",
                "I am not able to find people of my interest",
                "I want a fresh start",
                "Not prefer to say");
        reason = "please choose a reason";

        materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                reason = item.toString();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowToastyMessage showToastyMessage = new ShowToastyMessage(DeleteAccount.this);
                if (TextUtils.equals(reason, "please choose a reason") || TextUtils.isEmpty(reason)){
                    showToastyMessage.warningMessage("please choose a reason");
                    return;
                }
                promptToDeleteAccount();
            }
        });

    }

    private void promptToDeleteAccount(){
        FancyAlertDialog.Builder alert1 = new FancyAlertDialog.Builder(DeleteAccount.this)
                .setTextTitle("Delete Account")
                .setTitleColor(R.color.appColor)
                .setBody("Are you sure to delete your account? Your all profile data will be erased. You won't be able to restore your data!")
                .setPositiveColor(R.color.red)
                .setBackgroundColor(R.color.white)
                .setNegativeColor(R.color.black90)
                .setNegativeButtonText("Cancel")
                .setPositiveButtonText("Yes, Delete Account!")
                .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        deleteAccount();
                    }
                })
                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setCancelable(true)
                .build();
        alert1.show();
    }

    //    update age range to data base
    private void deleteAccount() {

        spin_kit_delete_account.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(DeleteAccount.this);
        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(DeleteAccount.this);
        final String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "DeleteProfile");
            jsonObject.put("DeleteReason", reason);
            jsonObject.put("UID", uid);
        } catch (Exception e) {
            spin_kit_delete_account.setVisibility(View.GONE);
            e.printStackTrace();
            //dismiss();
        }

        String url = getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            final int code = response.getInt("code");
                            if (code == 1313){
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(DeleteAccount.this);
                                showToastyMessage.successMessage("Account Deleted....");
                                spin_kit_delete_account.setVisibility(View.GONE);
                                logout();
                            }
                            else {
                                spin_kit_delete_account.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            ShowToastyMessage showToastyMessage = new ShowToastyMessage(DeleteAccount.this);
                            showToastyMessage.errorMessage(e.toString());
                            // dismiss();
                            spin_kit_delete_account.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //     ShowToastyMessage showToastyMessage = new ShowToastyMessage(Settings.this);
                        //     showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                        spin_kit_delete_account.setVisibility(View.GONE);
                    }

                });

        jsonObjectRequest.setTag(4);
        requestQueue.add(jsonObjectRequest);
    }
    //    update age range to data base

    private void logout() {
        final MySharedPrefs mySharedPrefs = new MySharedPrefs(DeleteAccount.this);
        HashMap<Integer, String> hashMap;
        hashMap = mySharedPrefs.getLoggedInUserDetails();
        mySharedPrefs.clearAllPrefs();
        if (TextUtils.equals(hashMap.get(0), "facebook")){
            LoginManager.getInstance().logOut();
        }
        startActivity(new Intent(DeleteAccount.this, Login.class));
        overridePendingTransition(R.anim.no_animation, R.anim.exit);
        ActivityCompat.finishAffinity(this);
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation, R.anim.exit);
    }

//    end
}
