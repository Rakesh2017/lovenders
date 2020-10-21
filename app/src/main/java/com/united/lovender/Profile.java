package com.united.lovender;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.wooplr.spotlight.SpotlightView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment implements View.OnClickListener {

    private KenBurnsView profile_image_ci;
    private ImageView previous_profiles_iv;
    private TextView user_name_tv, about_tv, total_swipes, pro_user_status_layout;
    private RequestQueue requestQueue;
    private FragmentActivity context;
    private TextView fp1, fp2, fp3;
    private SpinKitView spinKitView;
    private ImageView selector1, selector2, selector3;
    private Handler handler1, handler2, handler3;
    private ImageButton upload_images_ib;
    private RelativeLayout simple_user_status_layout;

    public Profile() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (FragmentActivity) context;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        Button edit_profile_b = view.findViewById(R.id.fp_edit_profile_b);
        Button setting_b = view.findViewById(R.id.fp_settings_b);
        profile_image_ci = view.findViewById(R.id.fp_profile_image_ci);
        user_name_tv = view.findViewById(R.id.fp_user_name_tv);
        about_tv = view.findViewById(R.id.fp_about_tv);
        previous_profiles_iv = view.findViewById(R.id.fp_previous_profiles);
        upload_images_ib = view.findViewById(R.id.fp_upload_images_ib);
        pro_user_status_layout = view.findViewById(R.id.fp_pro_user_status_layout);
        simple_user_status_layout = view.findViewById(R.id.fp_simple_user_status_layout);
        total_swipes = view.findViewById(R.id.fp_total_swipes);
        fp1 = view.findViewById(R.id.fp1);
        fp2 = view.findViewById(R.id.fp2);
        fp3 = view.findViewById(R.id.fp3);
        Button buy_plan_b = view.findViewById(R.id.fp_buy_plan_b);
        spinKitView = view.findViewById(R.id.fp_spin_kit);
        selector1 = view.findViewById(R.id.fp_selector1);
        selector2 = view.findViewById(R.id.fp_selector2);
        selector3 = view.findViewById(R.id.fp_selector3);
        handler1 = new Handler();
        handler2 = new Handler();
        handler3 = new Handler();

        edit_profile_b.setOnClickListener(this);
        setting_b.setOnClickListener(this);
        profile_image_ci.setOnClickListener(this);
        previous_profiles_iv.setOnClickListener(this);
        total_swipes.setOnClickListener(this);
        buy_plan_b.setOnClickListener(this);
        upload_images_ib.setOnClickListener(this);

    }


    private void showTutorial() {

        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        mySharedPrefs.setFirstTimeProfile("false");

        String id = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        SpotlightView spotlightView = new SpotlightView.Builder((Activity) context)
                .introAnimationDuration(200)
                .enableRevealAnimation(true)
                .performClick(false)
                .fadeinTextDuration(200)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("Upload images")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("upload your images")
                .maskColor(Color.parseColor("#90000000"))
                .target(upload_images_ib)
                .lineAnimDuration(200)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId(id) //UNIQUE ID
                .show();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            animation();
            MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
            String val = mySharedPrefs.getFirstTimeProfile();
            if (TextUtils.equals(val, "true")){
                showTutorial();
            }
        }

        try{
            if (!isVisibleToUser){
                Fragment fragment = context.getSupportFragmentManager().findFragmentByTag("our_plans");
                if(fragment != null){
                    context.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
        } catch (Exception ignored){

        }

    }

    private void loopAnimation(){

        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                fp3.setVisibility(View.GONE);
                YoYo.with(Techniques.SlideOutLeft)
                        .repeat(0)
                        .duration(500)
                        .playOn(fp1);
                fp2.setVisibility(View.VISIBLE);
                selector1.setBackground(getResources().getDrawable(R.drawable.white_circle));
                selector3.setBackground(getResources().getDrawable(R.drawable.white_circle));
                selector2.setBackground(getResources().getDrawable(R.drawable.circle_black));
                YoYo.with(Techniques.SlideInRight)
                        .repeat(0)
                        .duration(500)
                        .playOn(fp2);
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fp1.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideOutLeft)
                                .repeat(0)
                                .duration(500)
                                .playOn(fp2);

                        fp3.setVisibility(View.VISIBLE);
                        selector3.setBackground(getResources().getDrawable(R.drawable.circle_black));
                        selector2.setBackground(getResources().getDrawable(R.drawable.white_circle));
                        selector1.setBackground(getResources().getDrawable(R.drawable.white_circle));
                        YoYo.with(Techniques.SlideInRight)
                                .repeat(0)
                                .duration(500)
                                .playOn(fp3);
                        handler3.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fp2.setVisibility(View.GONE);
                                YoYo.with(Techniques.SlideOutLeft)
                                        .repeat(0)
                                        .duration(500)
                                        .playOn(fp3);
                                selector3.setBackground(getResources().getDrawable(R.drawable.white_circle));
                                selector2.setBackground(getResources().getDrawable(R.drawable.white_circle));
                                selector1.setBackground(getResources().getDrawable(R.drawable.circle_black));
                                YoYo.with(Techniques.SlideInRight)
                                        .repeat(0)
                                        .duration(500)
                                        .playOn(fp1);
                                fp1.setVisibility(View.VISIBLE);
                                if (handler1 !=null){
                                    handler1.removeCallbacksAndMessages(null);
                                }
                                if (handler2 !=null){
                                    handler2.removeCallbacksAndMessages(null);
                                }
                                if (handler3 !=null){
                                    handler3.removeCallbacksAndMessages(null);
                                }
                                loopAnimation();

                            }
                        },5000);
                    }
                },5000);
            }
        },5000);
    }

    private void animation() {
        YoYo.with(Techniques.ZoomIn)
                .repeat(0)
                .duration(500)
                .playOn(previous_profiles_iv);
        YoYo.with(Techniques.ZoomIn)
                .repeat(0)
                .duration(500)
                .playOn(total_swipes);
    }


    //    on resume
    public void onResume(){
        super.onResume();

        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);

        try {
            loopAnimation();
        } catch (Exception | OutOfMemoryError ignored){}

        try{

            boolean pro_status = mySharedPrefs.getProUserStatus();
            if (pro_status){
                pro_user_status_layout.setVisibility(View.VISIBLE);
                simple_user_status_layout.setVisibility(View.GONE);
            } else {
                pro_user_status_layout.setVisibility(View.GONE);
                simple_user_status_layout.setVisibility(View.VISIBLE);
            }

            checkIfProUser();
        } catch (Exception | OutOfMemoryError ignored){}

        try {
            getAndSetNameAndAboutFromPrefs();
        } catch (Exception | OutOfMemoryError ignored){}

        try {
            setProfilePhoto();
        } catch (Exception | OutOfMemoryError ignored){}

        try {
            getDataFromDataBase();
        } catch (Exception | OutOfMemoryError ignored){}

        try {
            getSwipesFromPrefs();
        } catch (Exception | OutOfMemoryError ignored){}

        try {
            getSwipesFromPrefs();
        } catch (Exception | OutOfMemoryError ignored){}

    }
    //    on resume


    private void getSwipesFromPrefs() {
        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        setTotalSwipes(mySharedPrefs.getSwipes());
    }

    //    check if pro user
    private void checkIfProUser() {
        requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final String uid = mySharedPrefs.getUid();

        try {

            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "ProStatus");
            jsonObject.put("UID", uid);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = getString(R.string.api_name);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final int code = response.getInt("code");

                            // Toast.makeText(Login.this, ""+response.toString(), Toast.LENGTH_SHORT).showMatch();
                            if (code == 1313) {
                                boolean result = response.getBoolean("result");
                                if (result){
                                    pro_user_status_layout.setVisibility(View.VISIBLE);
                                    simple_user_status_layout.setVisibility(View.GONE);
                                    mySharedPrefs.setProUserStatus(true);
                                }
                                else {
                                    pro_user_status_layout.setVisibility(View.GONE);
                                    simple_user_status_layout.setVisibility(View.VISIBLE);
                                    mySharedPrefs.setProUserStatus(false);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
//                        showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }
//    check if pro user


//    setting name and about from prefs
    private void getAndSetNameAndAboutFromPrefs() {
        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        HashMap<Integer, String> hashMap = mySharedPrefs.getEditProfile();
        String name, about;
        name = hashMap.get(0);
        about = hashMap.get(1);
        user_name_tv.setText(name);
        if (TextUtils.isEmpty(about)){
            about_tv.setVisibility(View.GONE);
        } else {
            about_tv.setVisibility(View.VISIBLE);
            about_tv.setText(about);
        }
    }
    //    setting name and about from prefs


    //    getting data from database
    private void getDataFromDataBase() {
        requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "GetProfile");
            jsonObject.put("UID", uid);
        } catch (Exception e) {
            Toast.makeText(context, "m"+e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        String url = getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ShowDialogBox showDialogBox = new ShowDialogBox(context);
                        try {
                            final int code = response.getInt("code");
                            if (code == 1313){
                                String result = response.getString("result");
                                try {
                                    JSONArray jsonArray = new JSONArray(result);
                                    String name = null, about = null, job = null, company = null, school = null, religion = null
                                            , gender = null, height = null, dob = null, interestLoop = null
                                            , ageStart = "18", ageEnd = "40", male = null, female = null
                                            , lesbian = null , gay = null
                                            , max_distance = "2", zodiac=null
                                            , body_type=null, visibility_status = null, swipes = null;
                                    
                                    final int length = jsonArray.length();
                                    for (int i = 0 ; i<length ; i++){
                                        JSONObject object = jsonArray.getJSONObject(i);

                                        name = object.getString("Name");
                                        about = object.getString("About");
                                        job = object.getString("JobTitle");
                                        company = object.getString("Company");
                                        school = object.getString("Education");
                                        religion = object.getString("Religion");
                                        dob = object.getString("DOB");
                                        height = object.getString("Height");
                                        gender = object.getString("Gender");
                                        zodiac = object.getString("ZodiacSign");
                                        body_type = object.getString("BodyType");
                                        interestLoop = object.getString("Interest");
                                        ageStart = object.getString("AgeStart");
                                        ageEnd = object.getString("AgeEnd");
                                        male = object.getString("Male");
                                        female = object.getString("Female");
                                        lesbian = object.getString("Lesbian");
                                        gay = object.getString("Gay");
                                        max_distance = object.getString("Distance");
                                        visibility_status = object.getString("PublicView");
                                        swipes = object.getString("Swipe");

                                        user_name_tv.setText(name);
                                        if (TextUtils.isEmpty(about)){
                                            about_tv.setVisibility(View.GONE);
                                        } else {
                                            about_tv.setVisibility(View.VISIBLE);
                                            about_tv.setText(about);
                                        }
                                        setTotalSwipes(swipes);
                                        mySharedPrefs.setSwipes(swipes);

                                    }
                                    try {
                                        JSONArray jsonArray1 = new JSONArray(interestLoop);
                                        final int l = jsonArray1.length();
                                        String interest="";
                                        for (int k = 0 ; k<l ; k++){
                                            JSONObject object1 = jsonArray1.getJSONObject(k);
                                            interest = String.format("%s,%s", object1.getString("Interest"), interest);
                                        }
                                        try {
                                            interest = interest.substring(0, interest.length()-1);
                                        }
                                        catch (Exception e) {
                                            //
                                        }
                                        setDataFromDataBase(name, about, job, company, school, religion
                                                , gender, dob, height, interest, male, female, lesbian, gay
                                                , ageStart, ageEnd, max_distance, zodiac, body_type, visibility_status);
                                    }
                                    catch (JSONException | NullPointerException e){
                                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                        showToastyMessage.errorMessage("Something went wrong");
                                        e.printStackTrace();
                                    }
                                } catch (JSONException e) {
                                    ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                    showToastyMessage.errorMessage("Something went wrong");
                                    e.printStackTrace();
                                }
                            }
                            else {
                                showDialogBox.successMessage("Something went wrong", "");
                            }

                        } catch (JSONException e) {
                            ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                            showToastyMessage.errorMessage(e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
//                        showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(2);
        requestQueue.add(jsonObjectRequest);
    }
    //    getting data from database

//    set my swipes
    private void setTotalSwipes(String swipes) {
        if (TextUtils.isEmpty(swipes)){
            return;
        }
        total_swipes.setText(swipes);
    }
//    set my swipes

    //    setting data from database
    private void setDataFromDataBase(String name, String about, String job, String company
            , String school, String religion, String gender, String dob, String height
            , String interest, String male, String female, String lesbian, String gay, String age_start
            , String age_end, String max_distance, String zodiac, String body_type, String visibility_status) {

        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        mySharedPrefs.setEditProfile(name, about, job, company, school, religion, gender, dob, height, interest, zodiac, body_type);
        mySharedPrefs.setSettings(male, female, lesbian, gay, age_start, age_end, max_distance, visibility_status);

        try{
            wishBirthDay(name, dob);
        }
        catch (Exception ignored){

        }


    }
    //    setting data from database


   // wishing birthday
    private void wishBirthDay(String s, String dob) {
        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String current_date = df.format(c);

        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);

        if (TextUtils.equals(current_date.substring(0, 5), dob.substring(0,5))){
            if (!TextUtils.equals(mySharedPrefs.getWishBirthdayYear(), current_date)){
                Intent intent = new Intent(context, WishBirthDay.class);
                intent.putExtra("name", s);
                intent.putExtra("year", current_date);
                startActivity(intent);
            }
        }

    }
    // wishing birthday


//    set profile photo
    private void setProfilePhoto() {

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final String old_url = mySharedPrefs.getPhotoUrl();
        try {
            Glide.with(Objects.requireNonNull(getContext()))
                    .load(old_url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_camera)
                            .error(R.drawable.ic_camera)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .into(profile_image_ci);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();


        String UID = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("UID", UID);
            jsonObject.put("request", "GetProfilePhoto");
        } catch (Exception e) {
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
                                String url = response.getString("result");
                                if (!TextUtils.equals(old_url, url)){
                                    try{
                                        Glide.with(context)
                                                .load(url)
                                                .apply(new RequestOptions().placeholder(R.drawable.circle))
                                                .into(profile_image_ci);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    mySharedPrefs.setProfilePhotoPrefs(url);
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
//                        showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }
    //    set profile photo

    //  on click
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.fp_edit_profile_b:
                startActivity(new Intent(getContext(), EditProfile.class));
                break;
            case R.id.fp_settings_b:
                startActivity(new Intent(getContext(), Settings.class));
                break;
            case R.id.fp_upload_images_ib:
                startActivity(new Intent(getContext(), UploadPics.class));
                break;
            case R.id.fp_previous_profiles:
                startActivity(new Intent(getContext(), PreviousProfiles.class));
                break;
            case R.id.fp_total_swipes:
                showSwipesMessage();
                break;
            case R.id.fp_buy_plan_b:
                new CheckNetworkConnection(context, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        getPlan();
                       // buy_plan_b.setOnClickListener(null);
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        startActivity(new Intent(context, NoInternetActivity.class));
                    }
                }).execute();
                break;
        }
    }

//    get buying plans
    private void getPlan() {

        show();
        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final String uid = mySharedPrefs.getUid();

        requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "GetPlans");
            jsonObject.put("UID", uid);
            jsonObject.put("Symbol", "");

        } catch (Exception e) {
            e.printStackTrace();
            dismiss();
        }

        String url = getString(R.string.api_name);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final int code = response.getInt("code");

                            if (code == 1313){
                                String result = response.getString("result");

                                JSONArray jsonArray = new JSONArray(result);
                                String month_advance = null, price_advance = null, offer_advance = null
                                        , month_basic= null, price_basic= null, offer_basic= null
                                        , month_premium= null, price_premium= null, offer_premium= null, symbol = "dollar";

                                for (int i = 0 ; i < jsonArray.length() ; i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    symbol = object.getString("Symbol");
                                    if (TextUtils.equals(object.getString("Plan"),"Advance")){
                                         month_advance = object.getString("Month");
                                         price_advance = object.getString("Price");
                                         offer_advance = object.getString("Offer");
                                    } else if (TextUtils.equals(object.getString("Plan"),"Basic")) {
                                        month_basic = object.getString("Month");
                                        price_basic = object.getString("Price");
                                        offer_basic = object.getString("Offer");
                                    } else if (TextUtils.equals(object.getString("Plan"),"Premium")) {
                                        month_premium = object.getString("Month");
                                        price_premium = object.getString("Price");
                                        offer_premium = object.getString("Offer");
                                    }
                                }

                                Bundle bundle = new Bundle();
                                bundle.putString("month_advance", month_advance);
                                bundle.putString("price_advance", price_advance);
                                bundle.putString("offer_advance", offer_advance);
                                bundle.putString("month_basic", month_basic);
                                bundle.putString("price_basic", price_basic);
                                bundle.putString("offer_basic", offer_basic);
                                bundle.putString("month_premium", month_premium);
                                bundle.putString("price_premium", price_premium);
                                bundle.putString("offer_premium", offer_premium);
                                bundle.putString("symbol", symbol);
                                OurPlans ourPlans = new OurPlans();
                                ourPlans.setArguments(bundle);

                                FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
                                ft.add(R.id.fragment_container_profile, ourPlans, "our_plans").addToBackStack(null).commit();

                                dismiss();
                            }
                            else if (code == 1920){
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                showToastyMessage.errorMessage(response.getString("msg"));
                                dismiss();
//
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                showToastyMessage.errorMessage("Request not completed !");
                                dismiss();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                            dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismiss();
//                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
//                        showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);

    }
//    get buying plans

    private void showSwipesMessage() {
        String swipes = total_swipes.getText().toString().trim();
        int min = 0;
        try {
            min = Integer.parseInt(swipes);
        } catch (Exception ignore){}

        if (min > 0 && min < 25){
            FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(context)
                    .setimageResource(R.drawable.ic_moderate_swipes)
                    .setTextTitle(swipes+" SWIPES")
                    .setTitleColor(R.color.appColor)
                    .setBody("Commendable! People are taking interest in your people.")
                    .setPositiveColor(R.color.black)
                    .setBackgroundColor(R.color.white)
                    .setNegativeColor(R.color.black90)
                    .setPositiveButtonText("Okay")
                    .setCancelable(false)
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

        else if (TextUtils.equals(swipes, "0")){
            FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(context)
                    .setimageResource(R.drawable.ic_no_swipe)
                    .setTextTitle("No Swipes")
                    .setTitleColor(R.color.appColor)
                    .setBody("Yet no one have swiped your profile. Make your profile more lively to gain people attention.")
                    .setPositiveColor(R.color.black)
                    .setBackgroundColor(R.color.white)
                    .setNegativeColor(R.color.black90)
                    .setPositiveButtonText("Okay")
                    .setCancelable(false)
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

        else if(min >= 25) {
            FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(context)
                    .setimageResource(R.drawable.ic_popular_swipes)
                    .setTextTitle(swipes+" SWIPES")
                    .setTitleColor(R.color.appColor)
                    .setBody("Bingo! People are quite interested in you. You seems to be popular in town.")
                    .setPositiveColor(R.color.black)
                    .setBackgroundColor(R.color.white)
                    .setNegativeColor(R.color.black90)
                    .setPositiveButtonText("Okay")
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
        } else {
            FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(context)
                    .setimageResource(R.drawable.ic_went_wrong_heart)
                    .setTextTitle("uh-oh")
                    .setTitleColor(R.color.appColor)
                    .setBody("something went wrong. try again after sometime!")
                    .setPositiveColor(R.color.black)
                    .setBackgroundColor(R.color.white)
                    .setNegativeColor(R.color.black90)
                    .setPositiveButtonText("Okay")
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

    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    public void onPause(){
        super.onPause();

    }

    @Override
    public void onStop () {
        super.onStop();
        if (handler1 !=null){
            handler1.removeCallbacksAndMessages(null);
        }
        if (handler2 !=null){
            handler2.removeCallbacksAndMessages(null);
        }
        if (handler3 !=null){
            handler3.removeCallbacksAndMessages(null);
        }
        if (requestQueue != null) {
            requestQueue.cancelAll(1);
            requestQueue.cancelAll(2);
        }
    }
//
    private void show(){
        spinKitView.setVisibility(View.VISIBLE);
    }
    private void dismiss(){
        spinKitView.setVisibility(View.GONE);
    }


//    end
}
