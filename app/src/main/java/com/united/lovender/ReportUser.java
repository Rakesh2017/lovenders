package com.united.lovender;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportUser extends AppCompatActivity {

    private EditText editText;
    private String subject;
    private SpinKitView progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_report_user);

        editText = findViewById(R.id.ru_edit_text);
        Button button = findViewById(R.id.ru_submit);
        ImageButton back_button = findViewById(R.id.ru_back_ib);
        MaterialSpinner materialSpinner = findViewById(R.id.ru_spinner);
        progressDialog = findViewById(R.id.ru_spin_kit);
        TextView title_tv = findViewById(R.id.ru_title);

        materialSpinner.setItems("Spam", "Vulgarity", "Fake Profile", "Nudity", "other");
        subject = "Spam";

        materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                subject = item.toString();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        title_tv.setText("Your report will be submitted against\n"+getIntent().getStringExtra("name")+".");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = editText.getText().toString().trim();
                ShowToastyMessage showToastyMessage = new ShowToastyMessage(ReportUser.this);
                if (TextUtils.isEmpty(message)){
                    showToastyMessage.errorMessage("Write Something");
                    return;
                }
                new CheckNetworkConnection(ReportUser.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        show();
                        submit(message);
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.ru_relative_layout, new NoInternetDisplay(), "no_internet")
                                .addToBackStack(null).commit();
                    }
                }).execute();
            }
        });


    }
    ////    submit data
    private void submit(final String message) {

        RequestQueue requestQueue = Volley.newRequestQueue(ReportUser.this);
        JSONObject jsonObject = new JSONObject();

        MySharedPrefs mySharedPrefs = new MySharedPrefs(ReportUser.this);
        String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "Report");
            jsonObject.put("UID", uid);
            jsonObject.put("ReportTo", getIntent().getStringExtra("report_uid"));
            jsonObject.put("Message", message);
            jsonObject.put("Reason", subject);
        } catch (Exception e) {
            dismiss();
            e.printStackTrace();
        }

        String url = getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final int code = response.getInt("code");
                            if (code == 1313){
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(ReportUser.this);
                                showToastyMessage.successMessage("Response Submitted");
                                editText.setText(null);
                                dismiss();
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(ReportUser.this);
                                showToastyMessage.errorMessage(response.getString("error"));
                                dismiss();
                            }

                        } catch (JSONException e) {
                            dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  ShowToastyMessage showToastyMessage = new ShowToastyMessage(ReportUser.this);
                        //   showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                        dismiss();
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }


    //    progress dialog
    private void show(){
        progressDialog.setVisibility(View.VISIBLE);
    }
    private void dismiss(){
        progressDialog.setVisibility(View.GONE);
    }
//    progress dialog

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation, R.anim.exit);
    }

//    end
}
