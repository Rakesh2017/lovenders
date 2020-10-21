package com.united.lovender;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.suke.widget.SwitchButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    private RangeSeekBar age_range_sb, max_distance_sb;
    private TextView left_age_tv, signed_in_with_tv, signed_in_with_mode_tv;
    private TextView right_age_tv, subs_tv1, subs_tv, max_distance_number_tv;
    private SwitchButton switch_button_men, switch_button_women, switch_button_lesbian
            , switch_button_gay, switch_hide_profile;
    private RecyclerView recyclerView;
    private SpinKitView spin_kit_legal, spin_kit_age_range
            , spin_kit_max_distance, spin_kit_gender, spin_kit_profile_visibility;
    private ImageView signed_in_with_iv, subs_img;
    private CardView subs_cv;

    // Creating RecyclerView.Adapter.
    private RecyclerView.Adapter adapter;
    private List<DataForRecyclerView> list = new ArrayList<>();
    //private SpinKitView spinKitView

    private CardView help_cv;
    private String selectedMaxKm, minAge, maxAge;
    private RequestQueue requestQueue;
    final Handler handler = new Handler();

    private TextView profile_visibility_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_settings);

        ImageButton back_ib = findViewById(R.id.as_back_ib);
        FancyButton logout_fb = findViewById(R.id.as_logout_fb);
        FancyButton delete_account_fb = findViewById(R.id.as_delete_account_fb);
        age_range_sb = findViewById(R.id.as_age_range_sb);
        max_distance_sb = findViewById(R.id.as_max_distance_sb);
        left_age_tv = findViewById(R.id.as_left_age_tv);
        right_age_tv = findViewById(R.id.as_right_age_tv);
        max_distance_number_tv = findViewById(R.id.as_max_distance_number_tv);
        switch_button_men = findViewById(R.id.as_switch_button_men);
        switch_button_women = findViewById(R.id.as_switch_button_women);
        TextView help_tv = findViewById(R.id.as_help_tv);
        spin_kit_legal = findViewById(R.id.as_spin_kit_legal);
        spin_kit_age_range = findViewById(R.id.as_spin_kit_age_range);
        spin_kit_max_distance = findViewById(R.id.as_spin_kit_max_distance);
        spin_kit_profile_visibility = findViewById(R.id.as_spin_kit_hide_profile);
        spin_kit_gender = findViewById(R.id.as_spin_kit_gender);
        help_cv = findViewById(R.id.as_help_cv);
        switch_button_lesbian = findViewById(R.id.as_switch_button_lesbians);
        switch_button_gay = findViewById(R.id.as_switch_button_gays);
        ImageButton boost_ib = findViewById(R.id.as_boost_ib);
        ImageButton super_like_ib = findViewById(R.id.as_super_likes_ib);
        profile_visibility_tv = findViewById(R.id.as_hide_profile_tv);
        switch_hide_profile = findViewById(R.id.as_switch_hide_profile);

        signed_in_with_tv = findViewById(R.id.as_signed_in_with_tv);
        signed_in_with_mode_tv = findViewById(R.id.as_signed_in_with_mode_tv);
        signed_in_with_iv = findViewById(R.id.as_signed_in_with_iv);
        subs_tv1 = findViewById(R.id.as_subs_tv1);
        subs_tv = findViewById(R.id.as_subs_tv);
        subs_img = findViewById(R.id.as_subs_iv);
        subs_cv = findViewById(R.id.as_subs_cv);


        recyclerView = findViewById(R.id.as_legal_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(Settings.this);
        recyclerView.setLayoutManager(mLayoutManager);

        requestQueue = Volley.newRequestQueue(Settings.this);

        setAgeRange();
        setMaximumDistance();
        menSwitch();
        womenSwitch();
        profileSwitch();
        lesbianSwitch();
        gaySwitch();
        setSignInMode();
        setSubscriptionStatus();

        getLegalData();
        //startAnimations();


        back_ib.setOnClickListener(this);
        logout_fb.setOnClickListener(this);
        help_tv.setOnClickListener(this);
        help_cv.setOnClickListener(this);
        super_like_ib.setOnClickListener(this);
        boost_ib.setOnClickListener(this);
        delete_account_fb.setOnClickListener(this);
    }

//    on start
    public void onStart(){
        super.onStart();
        getDataFromSharedPrefsAndSet();
    }
    //    on start

//    set sign in mode
    @SuppressLint("SetTextI18n")
    private void setSignInMode(){
        MySharedPrefs mySharedPrefs = new MySharedPrefs(Settings.this);
        String login_mode = mySharedPrefs.getLogInMode();
        //facebook
        if (TextUtils.equals(login_mode, "facebook")){
            signed_in_with_tv.setText("FACEBOOK");
            signed_in_with_mode_tv.setText("");
            signed_in_with_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_facebook));
            String email = mySharedPrefs.getEmail();
            if (email.contains("@")){
                signed_in_with_mode_tv.setText("email: "+mySharedPrefs.getEmail());
            } else {
                signed_in_with_mode_tv.setText("facebook id: "+mySharedPrefs.getEmail());
            }
        } else if (TextUtils.equals(login_mode, "google")){
            signed_in_with_tv.setText("GOOGLE");
            signed_in_with_mode_tv.setText("email: "+mySharedPrefs.getEmail());
            signed_in_with_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_google));
        } if (TextUtils.equals(login_mode, "phone")){
            signed_in_with_tv.setText("PHONE");
            signed_in_with_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_phone));
            signed_in_with_mode_tv.setText("phone number: "+mySharedPrefs.getEmail());
        }

    }
//    set sign in mode

//    set subscription status
    @SuppressLint("SetTextI18n")
    private void setSubscriptionStatus(){
        MySharedPrefs mySharedPrefs = new MySharedPrefs(Settings.this);
        boolean status = mySharedPrefs.getProUserStatus();
        if (status){ // subscription true
            subs_cv.setVisibility(View.VISIBLE);
            String plan = mySharedPrefs.getPlan();
            String expiry_date = mySharedPrefs.getExpiryDate();
            if (TextUtils.equals(plan, "basic")){
                subs_tv1.setText("Subscribed to monthly plan");
                subs_img.setBackground(getResources().getDrawable(R.drawable.basic_logo));
                subs_tv.setText("Thank you for using Lovenders pro account. Your subscription will ends on "+expiry_date);
            } else if (TextUtils.equals(plan, "premium")){
                subs_tv1.setText("Subscribed to 3 months plan");
                subs_img.setBackground(getResources().getDrawable(R.drawable.premium_logo));
                subs_tv.setText("Thank you for using Lovenders pro account. Your subscription will ends on "+expiry_date);
            } else if (TextUtils.equals(plan, "gold")){
                subs_tv1.setText("Subscribed to 6 months plan");
                subs_img.setBackground(getResources().getDrawable(R.drawable.gold_logo));
                subs_tv.setText("Thank you for using Lovenders pro account. Your subscription will ends on "+expiry_date);
            }
        } else {
            subs_cv.setVisibility(View.GONE);
        }
    }
    //    set subscription status

    //    getting data from shared prefs
    @SuppressLint("SetTextI18n")
    private void getDataFromSharedPrefsAndSet() {

        MySharedPrefs mySharedPrefs = new MySharedPrefs(Settings.this);
        HashMap<Integer, String> hashMap = mySharedPrefs.getSettings();
        String male, female, lesbian, gay, age_start, age_end, max_distance, visibility_status;

        male = hashMap.get(0);
        female = hashMap.get(1);
        lesbian = hashMap.get(2);
        gay = hashMap.get(3);
        age_start = hashMap.get(4);
        age_end = hashMap.get(5);
        max_distance = hashMap.get(6);
        visibility_status = hashMap.get(7);

        if (TextUtils.equals(male, "true")) switch_button_men.setChecked(true);
        if (TextUtils.equals(female, "true")) switch_button_women.setChecked(true);
        if (TextUtils.equals(lesbian, "true")) switch_button_lesbian.setChecked(true);
        if (TextUtils.equals(gay, "true")) switch_button_gay.setChecked(true);
        if (TextUtils.equals(visibility_status, "true")){
            profile_visibility_tv.setText("VISIBLE");
            switch_hide_profile.setChecked(true);
        } else {
            profile_visibility_tv.setText("HIDDEN");
            switch_hide_profile.setChecked(false);
        }

        if (TextUtils.isEmpty(age_start) || TextUtils.equals(age_start,"0")) age_start = "18";
        if (TextUtils.isEmpty(age_end) || TextUtils.equals(age_end, "0")) age_end = "30";
        assert age_start != null;
        assert age_end != null;
        age_range_sb.setValue(Float.parseFloat(age_start), Float.parseFloat(age_end));

        if (TextUtils.isEmpty(max_distance)) max_distance = "50";
        assert max_distance != null;
        max_distance_sb.setValue(Float.parseFloat(max_distance));

    }
    //    getting data from shared prefs


    private void getLegalData() {
        spin_kit_legal.setVisibility(View.VISIBLE);
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            // jsonObject.put("request", "Privacy");
            jsonObject.put("request", "GetContent");
        } catch (Exception e) {
            spin_kit_legal.setVisibility(View.GONE);
            e.printStackTrace();
        }

        String url = getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(Legal.this, ""+response, Toast.LENGTH_SHORT).show();
                            final int code = response.getInt("code");
                            if (code == 1313){
                                String result = response.getString("result");
                                try {
                                    JSONArray jsonArray = new JSONArray(result);
                                    final int length = jsonArray.length();
                                    if (list != null) {
                                        list.clear();  // v v v v important (eliminate duplication of data)
                                    }
                                    for (int i = 0 ; i<length ; i++){
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String id = object.getString("ID");
                                        String title = object.getString("Title");
                                        String content = object.getString("Content");
                                        DataForRecyclerView dataForRecyclerView = new DataForRecyclerView();
                                        dataForRecyclerView.setId(id);
                                        dataForRecyclerView.setTitle(title);
                                        dataForRecyclerView.setContent(content);
                                        list.add(dataForRecyclerView);
                                    }
                                    adapter = new LegalListRecyclerViewAdapter(Settings.this, list);
                                    recyclerView.setAdapter(adapter);


                                    spin_kit_legal.setVisibility(View.GONE);
                                } catch (JSONException e) {
                                    spin_kit_legal.setVisibility(View.GONE);
                                    ShowToastyMessage showToastyMessage = new ShowToastyMessage(Settings.this);
                                    showToastyMessage.errorMessage("Something went wrong");
                                    e.printStackTrace();
                                }
                            }

                        } catch (JSONException e) {
                            spin_kit_legal.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spin_kit_legal.setVisibility(View.GONE);
                        //    Log.d("KUCH", error.getMessage());
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }

//    women switch
    private void womenSwitch() {
        switch_button_women.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                updateGender();
                if (!switch_button_women.isChecked() && checkIfAllAreNotUnChecked()){
                    switch_button_men.setChecked(true);
                }

            }
        });
    }

//    women switch

    //    update profile visibility
    private void profileSwitch() {
        switch_hide_profile.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (switch_hide_profile.isChecked()){
                    switch_hide_profile.setChecked(true);
                    profile_visibility_tv.setText("VISIBLE");
                    updateProfileVisibility("true");
                }
                else {
                    switch_hide_profile.setChecked(false);
                    profile_visibility_tv.setText("HIDDEN");
                    updateProfileVisibility("false");
                }
            }
        });
    }
    //    update profile visibility

    //    update age range
    public void updateProfileVisibility(String status){
        spin_kit_profile_visibility.setVisibility(View.VISIBLE);
        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(Settings.this);
        final String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "UpdatePublicView");
            jsonObject.put("UID", uid);
            jsonObject.put("PublicView", status);
        } catch (Exception e) {
            spin_kit_profile_visibility.setVisibility(View.GONE);
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
                                spin_kit_profile_visibility.setVisibility(View.GONE);
                            }
                            else {
                                spin_kit_profile_visibility.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {

                            spin_kit_profile_visibility.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  ShowToastyMessage showToastyMessage = new ShowToastyMessage(Settings.this);
                        //  showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                        spin_kit_profile_visibility.setVisibility(View.GONE);
                    }

                });

        jsonObjectRequest.setTag(3);
        requestQueue.add(jsonObjectRequest);
    }
//    update age range



    //    men switch
    private void menSwitch() {
        switch_button_men.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                updateGender();
                if (!switch_button_men.isChecked() && checkIfAllAreNotUnChecked()){
                    switch_button_women.setChecked(true);
                }
            }
        });
    }
    //    men switch

    //     lesbian
    private void lesbianSwitch() {
        switch_button_lesbian.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                updateGender();
                if (!switch_button_lesbian.isChecked() && checkIfAllAreNotUnChecked()){
                    switch_button_women.setChecked(true);
                }
            }
        });
    }

    //     gay
    private void gaySwitch() {
        switch_button_gay.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                updateGender();
                if (!switch_button_gay.isChecked() && checkIfAllAreNotUnChecked()){
                    switch_button_men.setChecked(true);
                }
            }
        });
    }

    private void updateGender(){
        spin_kit_gender.setVisibility(View.VISIBLE);
        new CheckNetworkConnection(Settings.this, new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                updateInterest(switch_button_men.isChecked()
                        , switch_button_women.isChecked()
                        , switch_button_lesbian.isChecked(), switch_button_gay.isChecked());
            }
            @Override
            public void onConnectionFail(String msg) {
                spin_kit_gender.setVisibility(View.GONE);
                ShowToastyMessage showToastyMessage = new ShowToastyMessage(Settings.this);
                showToastyMessage.warningMessage("No internet connection");
            }
        }).execute();
    }

//    update interest
    public void updateInterest(boolean men, boolean women, boolean lesbian, boolean gay){
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(Settings.this);
        final String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "UpdateInterestedIn");
            jsonObject.put("UID", uid);
            jsonObject.put("Range", men+","+women+","+lesbian+","+gay);
        } catch (Exception e) {
            spin_kit_gender.setVisibility(View.GONE);
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
                                // showDialogBox.successMessage("Profile Updated", "");
                                //dismiss();
                                mySharedPrefs.setProfileSettingsChanged("true");
                                spin_kit_gender.setVisibility(View.GONE);
                            }
                            else {
                                // showDialogBox.successMessage("Something went wrong", "");
                                // dismiss();
                                spin_kit_gender.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            //ShowToastyMessage showToastyMessage = new ShowToastyMessage(Settings.this);
                            // showToastyMessage.errorMessage(e.toString());
                            // dismiss();
                            spin_kit_gender.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // ShowToastyMessage showToastyMessage = new ShowToastyMessage(Settings.this);
                       // showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                        spin_kit_gender.setVisibility(View.GONE);
                    }

                });

        jsonObjectRequest.setTag(2);
        requestQueue.add(jsonObjectRequest);
    }
//    update interest

//    if user uncheck all then check men as default
    private boolean checkIfAllAreNotUnChecked() {
        return !switch_button_men.isChecked() && !switch_button_women.isChecked()
                && !switch_button_lesbian.isChecked() && !switch_button_gay.isChecked();
    }


    //    setting maximum distance
    private void setMaximumDistance() {
        max_distance_sb.setOnRangeChangedListener(new OnRangeChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                max_distance_number_tv.setText((int) leftValue + getString(R.string.km));
                selectedMaxKm = String.valueOf((int)leftValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //start tracking touch
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                spin_kit_max_distance.setVisibility(View.VISIBLE);
                new CheckNetworkConnection(Settings.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        updateMaxDistance(selectedMaxKm);
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        spin_kit_max_distance.setVisibility(View.GONE);
                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(Settings.this);
                        showToastyMessage.warningMessage("No internet connection");
                    }
                }).execute();
                //stop tracking touch
            }
        });
    }
//    setting maximum distance


//    update age range
    public void updateMaxDistance(String maxDistance){
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(Settings.this);
        final String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "UpdateDistanceRange");
            jsonObject.put("UID", uid);
            jsonObject.put("Range", maxDistance);
        } catch (Exception e) {
            spin_kit_max_distance.setVisibility(View.GONE);
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
                               // showDialogBox.successMessage("Profile Updated", "");
                                //dismiss();
                                mySharedPrefs.setProfileSettingsChanged("true");
                                spin_kit_max_distance.setVisibility(View.GONE);
                            }
                            else {
                               // showDialogBox.successMessage("Something went wrong", "");
                               // dismiss();
                                spin_kit_max_distance.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            //ShowToastyMessage showToastyMessage = new ShowToastyMessage(Settings.this);
                           // showToastyMessage.errorMessage(e.toString());
                           // dismiss();
                            spin_kit_max_distance.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                      //  ShowToastyMessage showToastyMessage = new ShowToastyMessage(Settings.this);
                      //  showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                        spin_kit_max_distance.setVisibility(View.GONE);
                    }

                });

        jsonObjectRequest.setTag(3);
        requestQueue.add(jsonObjectRequest);
    }
//    update age range


//    setting age range
    private void setAgeRange() {
        age_range_sb.setOnRangeChangedListener(new OnRangeChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                left_age_tv.setText(String.valueOf((int)leftValue));
                right_age_tv.setText(String.valueOf((int)rightValue));
                minAge = String.valueOf((int)leftValue);
                maxAge = String.valueOf((int)rightValue);
                if ((int)(rightValue) == 55){
                    right_age_tv.setText(((int)rightValue)+"+");
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //start tracking touch
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                spin_kit_age_range.setVisibility(View.VISIBLE);
                new CheckNetworkConnection(Settings.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                       // retrofitRequest(minAge, maxAge);
                          updateAgeRange(minAge, maxAge);
                        //gt();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        spin_kit_age_range.setVisibility(View.GONE);
                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(Settings.this);
                        showToastyMessage.warningMessage("No internet connection");
                    }
                }).execute();
                //stop tracking touch
            }
        });
    }
    //    setting age range

//    update age range to data base
    private void updateAgeRange(String minAge, String maxAge) {

        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(Settings.this);
        final String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "UpdateAgeRange");
            jsonObject.put("UID", uid);
            jsonObject.put("Range", minAge+","+maxAge);
        } catch (Exception e) {
            spin_kit_age_range.setVisibility(View.GONE);
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
                                // showDialogBox.successMessage("Profile Updated", "");
                                //dismiss();
                                mySharedPrefs.setProfileSettingsChanged("true");
                                spin_kit_age_range.setVisibility(View.GONE);
                               // Toast.makeText(Settings.this, "res "+response.getString("result"), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                // showDialogBox.successMessage("Something went wrong", "");
                                // dismiss();
                                spin_kit_age_range.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            //ShowToastyMessage showToastyMessage = new ShowToastyMessage(Settings.this);
                            // showToastyMessage.errorMessage(e.toString());
                            // dismiss();
                            spin_kit_age_range.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                   //     ShowToastyMessage showToastyMessage = new ShowToastyMessage(Settings.this);
                   //     showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                        spin_kit_age_range.setVisibility(View.GONE);
                    }

                });

        jsonObjectRequest.setTag(4);
        requestQueue.add(jsonObjectRequest);
    }
    //    update age range to data base


    //    onclick
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.as_back_ib:
                onBackPressed();
                break;
            case R.id.as_logout_fb:
                FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(Settings.this)
                       // .setimageResource(R.drawable.ic_log_out)
                        .setTextTitle("Logout")
                        .setTitleColor(R.color.appColor)
                        .setBody("Tap confirm to logout?")
                        .setPositiveColor(R.color.black)
                        .setBackgroundColor(R.color.white)
                        .setNegativeColor(R.color.black90)
                        .setNegativeButtonText("Cancel")
                        .setPositiveButtonText("Confirm")
                        .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                logout();
                            }
                        })
                        .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setCancelable(true)
                        .build();
                alert.show();
                break;
            case R.id.as_help_cv:
                startActivity(new Intent(Settings.this, HelpAndSupport.class));
                break;
            case R.id.as_help_tv:
                 help_cv.performClick();
                 break;
            case R.id.as_boost_ib:
               // showComingSoonMessage();
                break;
            case R.id.as_super_likes_ib:
                showComingSoonMessage();
                break;
            case R.id.as_delete_account_fb:
                startActivity(new Intent(Settings.this, DeleteAccount.class));
                break;
        }

    }
    //    onclick


    private void showComingSoonMessage() {
        FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(Settings.this)
                .setimageResource(R.drawable.ic_credit_card)
                .setTextTitle("COMING SOON")
                .setTitleColor(R.color.appColor)
                .setBody("Payments will be accepted soon...")
                .setPositiveColor(R.color.black)
                .setBackgroundColor(R.color.white)
                .setNegativeColor(R.color.black90)
                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                      dialog.dismiss();
                    }
                })
                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setCancelable(true)
                .build();
        alert.show();
    }


    private void logout() {
        final MySharedPrefs mySharedPrefs = new MySharedPrefs(Settings.this);
        HashMap<Integer, String> hashMap;
        hashMap = mySharedPrefs.getLoggedInUserDetails();
        mySharedPrefs.clearAllPrefs();
        if (TextUtils.equals(hashMap.get(0), "facebook")){
            LoginManager.getInstance().logOut();
        }
        startActivity(new Intent(Settings.this, Login.class));
        overridePendingTransition(R.anim.no_animation, R.anim.exit);
        ActivityCompat.finishAffinity(this);
    }
//    on click

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation, R.anim.exit);
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(1);
            requestQueue.cancelAll(2);
            requestQueue.cancelAll(3);
            requestQueue.cancelAll(4);
            requestQueue.cancelAll(5);
            requestQueue.stop();
            handler.removeCallbacksAndMessages(null);
        }
    }

//    end
}
