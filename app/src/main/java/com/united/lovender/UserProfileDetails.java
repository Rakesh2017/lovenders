package com.united.lovender;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileDetails extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    private ImageButton super_like_ib;
    private TextView intro_tv;
    TagView tagView;
    private CircleImageView img1, img2, img3, img4, img5;
    private CoordinatorLayout coordinatorLayout;

    private RecyclerView recyclerView;
    private List<DataForRecyclerView> list = new ArrayList<>();
    private TextView age_tv1, age_tv, height_tv, height_tv1, religion_tv, religion_tv1
            , education_tv1, education_tv, zodiac_tv
            , zodiac_tv1, body_type_tv1, body_type_tv, interests_tv
            , name_tv, photo_count_tv, swipes_tv, about_tv, career_tv, career_tv1
            , div3, div4, div5, div6, spam_tv;

    private ImageButton report_ib;

    private NestedScrollView nestedScrollView;
    private AppBarLayout appBarLayout;

    private RelativeLayout super_like_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_up);
        setContentView(R.layout.activity_user_profile_details);


        ImageButton back_ib = findViewById(R.id.fd_back_ib);
        super_like_ib = findViewById(R.id.fd_super_like);
        report_ib = findViewById(R.id.fd_report);

        img1 = findViewById(R.id.fd_img1);
        img2 = findViewById(R.id.fd_img2);
        img3 = findViewById(R.id.fd_img3);
        img4 = findViewById(R.id.fd_img4);
        img5 = findViewById(R.id.fd_img5);

        div3 = findViewById(R.id.fd_divider3);
        div4 = findViewById(R.id.fd_divider4);
        div5 = findViewById(R.id.fd_divider5);
        div6 = findViewById(R.id.fd_divider6);

        age_tv1 = findViewById(R.id.fd_age_tv1);
        age_tv = findViewById(R.id.fd_age_tv);
        height_tv1 = findViewById(R.id.fd_height_tv1);
        height_tv = findViewById(R.id.fd_height_tv);
        religion_tv1 = findViewById(R.id.fd_religion_tv1);
        religion_tv = findViewById(R.id.fd_religion_tv);
        education_tv1 = findViewById(R.id.fd_school_tv1);
        education_tv = findViewById(R.id.fd_school_tv);
        zodiac_tv1 = findViewById(R.id.fd_zodiac_tv1);
        zodiac_tv = findViewById(R.id.fd_zodiac_tv);
        body_type_tv1 = findViewById(R.id.fd_body_type_tv1);
        body_type_tv = findViewById(R.id.fd_body_type_tv);
        interests_tv = findViewById(R.id.fd_interests_tv);
        name_tv = findViewById(R.id.fd_name_tv);
        photo_count_tv = findViewById(R.id.fd_photos_count);
        swipes_tv = findViewById(R.id.fd_swipes_count);
        about_tv = findViewById(R.id.fd_about);
        coordinatorLayout = findViewById(R.id.fd_CoordinatorLayout);
        nestedScrollView = findViewById(R.id.pd_nested_scroll_view);
        appBarLayout = findViewById(R.id.fd_appbar);
        intro_tv = findViewById(R.id.fd_intro_tv);
        career_tv = findViewById(R.id.fd_career_tv);
        career_tv1 = findViewById(R.id.fd_career_tv1);
        spam_tv = findViewById(R.id.fd_spam_tv);
        super_like_rl = findViewById(R.id.fd_super_like_rl);

        appBarLayout.addOnOffsetChangedListener(this);
        tagView = findViewById(R.id.tag_group);

        recyclerView = findViewById(R.id.fd_slider_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        img5.setOnClickListener(this);
        report_ib.setOnClickListener(this);
        spam_tv.setOnClickListener(this);
        super_like_ib.setOnClickListener(this);
        back_ib.setOnClickListener(this);

      //  getProfileData();
        getData();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset)
    {
        if (Math.abs(offset) == appBarLayout.getTotalScrollRange()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.crispyBlue));
        }
        else {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.appColor));
        }
    }


    private void getData() {

        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String str = String.valueOf(data);
            int index= str.lastIndexOf('/');
            super_like_ib.setVisibility(View.GONE);
            report_ib.setVisibility(View.GONE);
            LoadUserData(str.substring(index+1));
            return;
        }

        String id = getIntent().getStringExtra("id");
        if (!TextUtils.isEmpty(id)){
            if (TextUtils.equals(getIntent().getStringExtra("type"), "SuperLike")){
                super_like_ib.setBackground(getResources().getDrawable(R.drawable.ic_super_like_new_));
                super_like_ib.setOnClickListener(null);
            }
            LoadUserData(id);
            return;
        }

        String name, age, about, job_tile, company, education
                , religion, height, image1, image2, image3, image4
                , image5, zodiac, body_type, swipes, image_count;
        ArrayList<String> arrayList;

        name = getIntent().getStringExtra("name");
        age = getIntent().getStringExtra("age");
        job_tile = getIntent().getStringExtra("job_title");
        company = getIntent().getStringExtra("company");
        education = getIntent().getStringExtra("education");
        religion = getIntent().getStringExtra("religion");
        height = getIntent().getStringExtra("height");
        about = getIntent().getStringExtra("about");
        zodiac = getIntent().getStringExtra("zodiac");
        body_type = getIntent().getStringExtra("body_type");
        swipes = getIntent().getStringExtra("swipe");
        image_count = getIntent().getStringExtra("image_count");
        image1 = getIntent().getStringExtra("image1");
        image2 = getIntent().getStringExtra("image2");
        image3 = getIntent().getStringExtra("image3");
        image4 = getIntent().getStringExtra("image4");
        image5 = getIntent().getStringExtra("image5");
        arrayList = getIntent().getStringArrayListExtra("interests");

        setNameAndAge();

        setImages(image1, image2, image3, image4, image5, name, about);

        setPhotoAndSwipeCount(image_count, swipes);

        setData(age, about, job_tile, company
                , education, religion, height, zodiac, body_type);

        setInterests(arrayList);
    }


//    load user data as app is opened from intent (share profile)
    private void LoadUserData(String uid) {
        RequestQueue requestQueue = Volley.newRequestQueue(UserProfileDetails.this);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "ProfileInfo");
            jsonObject.put("UID", uid);

        } catch (Exception e) {
            //  dismiss();
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
                                String result = response.getString("result");
                                JSONArray jsonArray = new JSONArray(result);

                                String name, age, about, job_tile, company, education
                                        , religion, height, image1, image2, image3, image4
                                        , image5, zodiac, body_type, swipes, interestLoop, imageCount;

                                JSONObject object = jsonArray.getJSONObject(0);

                                if (TextUtils.equals(object.getString("ProfileExist"), "false")){
                                    showMessageProfileDoNoExist();
                                    return;
                                }

                                if (TextUtils.equals(object.getString("ProfileDeleteStatus"), "true")){
                                    showMessageProfileDeleted();
                                    return;
                                }

                                name = object.getString("Name");
                                age = object.getString("Age");
                                about = object.getString("About");
                                job_tile = object.getString("JobTitle");
                                company = object.getString("Company");
                                education = object.getString("Education");
                                religion = object.getString("Religion");
                                height = object.getString("Height");
                                zodiac = object.getString("ZodiacSign");
                                body_type = object.getString("BodyType");
                                swipes = object.getString("Swipe");
                                interestLoop = object.getString("Interest");
                                imageCount = object.getString("ImageCount");
                                image1 = object.getString("Image1");
                                image2 = object.getString("Image2");
                                image3 = object.getString("Image3");
                                image4 = object.getString("Image4");
                                image5 = object.getString("Image5");

                                try {
                                    JSONArray jsonArray1 = new JSONArray(interestLoop);
                                    final int l = jsonArray1.length();
                                    ArrayList<String> arrayList = new ArrayList<>();

                                    for (int k = 0 ; k < l ; k++){
                                        JSONObject object1 = jsonArray1.getJSONObject(k);
                                        String interest = object1.getString("Interest");
                                        arrayList.add(interest);
                                    }

                                    setNameAndAge2(name, age);
                                    setPhotoAndSwipeCount(imageCount, swipes);

                                    setData(age, about, job_tile, company
                                            , education, religion, height, zodiac, body_type);

                                    setImages(image1, image2, image3, image4, image5, name, about);

                                    setInterests(arrayList);


                                }
                                catch (JSONException e){
                                    e.printStackTrace();
                                    ShowToastyMessage showToastyMessage = new ShowToastyMessage(UserProfileDetails.this);
                                    showToastyMessage.errorMessage("c4"+e);
                                }

                            }


                        } catch (JSONException e) {
                            //  dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  ShowToastyMessage showToastyMessage = new ShowToastyMessage(ReportUser.this);
                        //   showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                        // dismiss();
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }
//    load user data as app is opened from intent (share profile)

    private void showMessageProfileDeleted() {
        FancyAlertDialog.Builder alert1 = new FancyAlertDialog.Builder(UserProfileDetails.this)
                .setimageResource(R.drawable.app_logo_300px)
                .setTextTitle("Account Was Removed")
                .setTitleColor(R.color.appColor)
                .setBody("This Account was removed by the user!")
                .setPositiveColor(R.color.black)
                .setBackgroundColor(R.color.white)
                .setPositiveButtonText("Okay")
                .setCancelable(false)
                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        UserProfileDetails.this.finish();
                    }
                })
                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setCancelable(true)
                .build();
        alert1.setCancelable(false);
        alert1.show();
    }

    private void showMessageProfileDoNoExist() {
        FancyAlertDialog.Builder alert1 = new FancyAlertDialog.Builder(UserProfileDetails.this)
                .setimageResource(R.drawable.ic_heart_profile_does_not_exist)
                .setTextTitle("Profile not found")
                .setTitleColor(R.color.appColor)
                .setBody("This profile does not exist.")
                .setPositiveColor(R.color.black)
                .setBackgroundColor(R.color.white)
                .setPositiveButtonText("Okay")
                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        UserProfileDetails.this.finish();
                    }
                })
                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .build();
        alert1.setCancelable(false);
        alert1.show();
    }


    private void setInterests(ArrayList<String> arrayList) {
        if (arrayList.size() == 0){
            tagView.setVisibility(View.GONE);
            interests_tv.setVisibility(View.GONE);
            return;
        }

        for (int k = 0 ; k < arrayList.size() ; k++){
            Tag tag = new Tag(arrayList.get(k));
            tagView.addTag(tag);
        }
    }

    //    on start
    public void onStart(){
        super.onStart();
        playImageAnimations();
    }
    //    on start

//    play animation on images
    private void playImageAnimations() {
    }
    //    play animation on images


//    setting name and age from intent
    @SuppressLint("SetTextI18n")
    private void setNameAndAge() {
        String name, age;
        name = getIntent().getStringExtra("name");
        age = getIntent().getStringExtra("age");
        name_tv.setText(name+", "+age);
    }

    @SuppressLint("SetTextI18n")
    private void setNameAndAge2(String name, String age) {
        name_tv.setText(name+", "+age);
    }


//    set swipe and photo count
    private void setPhotoAndSwipeCount(String image_count, String swipes) {

        photo_count_tv.setText(String.valueOf(image_count));
        swipes_tv.setText(swipes);
    }
    //    set swipe and photo count


    //    setting data
    @SuppressLint("SetTextI18n")
    private void setData(String age, String about
            , String job_tile, String company, String education
            , String religion, String height, String zodiac, String body_type) {

        //         age
        if (!TextUtils.isEmpty(age) && !TextUtils.equals(age, "0") && !TextUtils.equals(age, "")){
            age_tv.setText(age);
        } else {
            age_tv1.setVisibility(View.GONE);
            age_tv.setVisibility(View.GONE);
        }
//        height
        if (!TextUtils.isEmpty(height) && !TextUtils.equals(height, "0") && !TextUtils.equals(height, "")){
            try{
                Double value = Double.valueOf(height);
                int feet = (int) Math.floor(value / 30.48);
                int inches = (int) Math.round((value / 2.54) - (feet * 12));
                if (inches == 12) inches = 11;
                height_tv.setText(height+"cm/ "+feet+"'"+inches+"''");
            }
            catch (Exception e){
                height_tv.setText(height);
            }
        } else {
            height_tv1.setVisibility(View.GONE);
            height_tv.setVisibility(View.GONE);
        }
//        religion
        if (!TextUtils.isEmpty(religion) && !TextUtils.equals(religion, "")){
            religion_tv.setText(religion);
        } else {
            religion_tv1.setVisibility(View.GONE);
            religion_tv.setVisibility(View.GONE);
            div3.setVisibility(View.GONE);
        }
//        education
        if (!TextUtils.isEmpty(education) && !TextUtils.equals(education, "")){
            education_tv.setText(education);
        } else {
            education_tv1.setVisibility(View.GONE);
            education_tv.setVisibility(View.GONE);
            div4.setVisibility(View.GONE);
        }
//        career
        if (!TextUtils.isEmpty(job_tile) && !TextUtils.equals(job_tile, "")
                && !TextUtils.isEmpty(company) && !TextUtils.equals(company, "")){
            career_tv.setText(job_tile+", "+company);
        } else if (!TextUtils.isEmpty(job_tile) && !TextUtils.equals(job_tile, "")
                && TextUtils.isEmpty(company) && TextUtils.equals(company, "")){
            career_tv.setText(job_tile);
        } else if (TextUtils.isEmpty(job_tile) && TextUtils.equals(job_tile, "")
                && !TextUtils.isEmpty(company) && !TextUtils.equals(company, "")){
            career_tv.setText(company);
        } else {
            career_tv.setVisibility(View.GONE);
            career_tv1.setVisibility(View.GONE);
            div5.setVisibility(View.GONE);
        }

        //        zodiac
        if (!TextUtils.isEmpty(zodiac) && !TextUtils.equals(zodiac, "")){
            setZodiac(zodiac);
        } else {
            zodiac_tv1.setVisibility(View.GONE);
            zodiac_tv.setVisibility(View.GONE);
            div6.setVisibility(View.GONE);
        }
        //        body type
        if (!TextUtils.isEmpty(body_type) && !TextUtils.equals(body_type, "")){
            body_type_tv.setText(body_type);
        } else {
            body_type_tv1.setVisibility(View.GONE);
            body_type_tv.setVisibility(View.GONE);
        }

//        about
        if (!TextUtils.isEmpty(about) && !TextUtils.equals(about, "")){
            about_tv.setText(about);
        }
        else{
            intro_tv.setVisibility(View.GONE);
            about_tv.setVisibility(View.GONE);
        }
    }
    //    setting data

//    setting zodiac with drawable
    private void setZodiac(String zodiac) {
        zodiac_tv.setText(zodiac);
        switch (zodiac){
            case "Aries": zodiac_tv.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.ic_aries), null); break;
            case "Leo": zodiac_tv.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.ic_leo), null); break;
            case "Taurus": zodiac_tv.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.ic_taurus), null); break;
            case "Gemini": zodiac_tv.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.ic_gemini), null); break;
            case "Cancer": zodiac_tv.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.ic_crab), null); break;
            case "Virgo": zodiac_tv.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.ic_virgo), null); break;
            case "Libra": zodiac_tv.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.ic_libra), null); break;
            case "Sagittarius": zodiac_tv.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.ic_sagittarius), null); break;
            case "Capricorn": zodiac_tv.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.ic_capricorn), null); break;
            case "Pisces": zodiac_tv.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.ic_pisces), null); break;
            case "Aquarius": zodiac_tv.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.ic_aquarius), null); break;
            case "Scorpio": zodiac_tv.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.ic_scorpio), null); break;
        }
    }
    //    setting zodiac with drawable


    //    setting images
    private void setImages(String image1, String image2
            , String image3, String image4, String image5, String name, String about){
        //                                        image 1
        DataForRecyclerView dataForRecyclerView = new DataForRecyclerView();
        dataForRecyclerView.setImage_url(image1);
        dataForRecyclerView.setImage1(image1);
        dataForRecyclerView.setImage2(image2);
        dataForRecyclerView.setImage3(image3);
        dataForRecyclerView.setImage4(image4);
        dataForRecyclerView.setImage5(image5);
        dataForRecyclerView.setName(name);
        dataForRecyclerView.setAbout(about);

        list.add(dataForRecyclerView);
        Glide.with(UserProfileDetails.this)
                .load(image1)
                .apply(new RequestOptions().placeholder(R.color.circular_image_placeholder).error(R.color.circular_image_placeholder))
                .into(img1);
        YoYo.with(Techniques.Landing)
                .repeat(0)
                .duration(2000)
                .playOn(img1);

//                                    image 2
        if (!TextUtils.isEmpty(image2)){
            DataForRecyclerView dataForRecyclerView2 = new DataForRecyclerView();
            dataForRecyclerView2.setImage_url(image2);

            dataForRecyclerView2.setImage1(image1);
            dataForRecyclerView2.setImage2(image2);
            dataForRecyclerView2.setImage3(image3);
            dataForRecyclerView2.setImage4(image4);
            dataForRecyclerView2.setImage5(image5);
            dataForRecyclerView2.setName(name);
            dataForRecyclerView2.setAbout(about);
            list.add(dataForRecyclerView2);
            Glide.with(UserProfileDetails.this)
                    .load(image2)
                    .apply(new RequestOptions().placeholder(R.color.circular_image_placeholder).error(R.color.circular_image_placeholder))
                    .into(img2);
            YoYo.with(Techniques.Landing)
                    .repeat(0)
                    .duration(2500)
                    .playOn(img2);
        }
        else img2.setVisibility(View.GONE);

//                                    image 3
        if (!TextUtils.isEmpty(image3)){
            DataForRecyclerView dataForRecyclerView3 = new DataForRecyclerView();
            dataForRecyclerView3.setImage_url(image3);
            dataForRecyclerView3.setImage1(image1);
            dataForRecyclerView3.setImage2(image2);
            dataForRecyclerView3.setImage3(image3);
            dataForRecyclerView3.setImage4(image4);
            dataForRecyclerView3.setImage5(image5);
            dataForRecyclerView3.setName(name);
            dataForRecyclerView3.setAbout(about);
            list.add(dataForRecyclerView3);
            Glide.with(UserProfileDetails.this)
                    .load(image3)
                    .apply(new RequestOptions().placeholder(R.color.circular_image_placeholder).error(R.color.circular_image_placeholder))
                    .into(img3);
            YoYo.with(Techniques.Landing)
                    .repeat(0)
                    .duration(2500)
                    .playOn(img3);
        }
        else img3.setVisibility(View.GONE);

//                                    image 4
        if (!TextUtils.isEmpty(image4)){
            DataForRecyclerView dataForRecyclerView4 = new DataForRecyclerView();
            dataForRecyclerView4.setImage_url(image4);
            dataForRecyclerView4.setImage1(image1);
            dataForRecyclerView4.setImage2(image2);
            dataForRecyclerView4.setImage3(image3);
            dataForRecyclerView4.setImage4(image4);
            dataForRecyclerView4.setImage5(image5);
            dataForRecyclerView4.setName(name);
            dataForRecyclerView4.setAbout(about);
            list.add(dataForRecyclerView4);
            Glide.with(UserProfileDetails.this)
                    .load(image4)
                    .apply(new RequestOptions().placeholder(R.color.circular_image_placeholder).error(R.color.circular_image_placeholder))
                    .into(img4);
            YoYo.with(Techniques.Landing)
                    .repeat(0)
                    .duration(2500)
                    .playOn(img4);
        }
        else img4.setVisibility(View.GONE);

//                                    image 5
        if (!TextUtils.isEmpty(image5)){
            DataForRecyclerView dataForRecyclerView5 = new DataForRecyclerView();
            dataForRecyclerView5.setImage_url(image5);
            dataForRecyclerView5.setImage1(image1);
            dataForRecyclerView5.setImage2(image2);
            dataForRecyclerView5.setImage3(image3);
            dataForRecyclerView5.setImage4(image4);
            dataForRecyclerView5.setImage5(image5);
            dataForRecyclerView5.setName(name);
            dataForRecyclerView5.setAbout(about);
            list.add(dataForRecyclerView5);
            Glide.with(UserProfileDetails.this)
                    .load(image5)
                    .apply(new RequestOptions().placeholder(R.color.circular_image_placeholder).error(R.color.circular_image_placeholder))
                    .into(img5);
            YoYo.with(Techniques.Landing)
                    .repeat(0)
                    .duration(2500)
                    .playOn(img5);
        }
        else img5.setVisibility(View.GONE);

        RecyclerView.Adapter adapter = new UserImagesRecyclerViewAdapter(UserProfileDetails.this, list);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }


    //    on back pressed
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        //supportFinishAfterTransition();
       // overridePendingTransition(R.anim.slide_in,R.anim.slide_up); // finish transition
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out);
    }
    //    on back pressed

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
   //     mDemoSlider.stopAutoCycle();
        super.onStop();
    }

//    onclick
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
//            image 1
            case R.id.fd_img1:
                setSmoothScrollToTop(0);
                break;
            //            image 1
            case R.id.fd_img2:
                setSmoothScrollToTop(1);
                break;
            //            image 1
            case R.id.fd_img3:
                setSmoothScrollToTop(2);
                break;
            //            image 1
            case R.id.fd_img4:
                setSmoothScrollToTop(3);
                break;
            //            image 1
            case R.id.fd_img5:
                setSmoothScrollToTop(4);
                break;
            case R.id.fd_report:
                if (spam_tv.getVisibility() == View.VISIBLE){
                    YoYo.with(Techniques.FadeOutLeft)
                            .duration(100)
                            .repeat(0)
                            .playOn(spam_tv);
                    spam_tv.setVisibility(View.INVISIBLE);
                } else if (spam_tv.getVisibility() == View.INVISIBLE){
                    YoYo.with(Techniques.FadeInRight)
                            .duration(300)
                            .repeat(0)
                            .playOn(spam_tv);
                    spam_tv.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.fd_spam_tv:
                Intent intent = new Intent(UserProfileDetails.this, ReportUser.class);
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("report_uid", getIntent().getStringExtra("UID"));
                startActivity(intent);
                spam_tv.setVisibility(View.INVISIBLE);
                break;
            case R.id.fd_super_like:
                new CheckNetworkConnection(UserProfileDetails.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        superLike();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(UserProfileDetails.this);
                        showToastyMessage.warningMessage("no internet connection!");
                    }
                }).execute();
                break;
            case R.id.fd_back_ib:
                onBackPressed();
                break;
        }

    }
//    onclick

    private void superLike(){
        RequestQueue requestQueue = Volley.newRequestQueue(UserProfileDetails.this);
        JSONObject jsonObject = new JSONObject();

        MySharedPrefs mySharedPrefs = new MySharedPrefs(UserProfileDetails.this);
        final String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "SuperSwipe");
            jsonObject.put("UID", uid);
            jsonObject.put("SwipeUID", getIntent().getStringExtra("UID"));

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
                                boolean result = response.getBoolean("result");
                                if (result){
                                    super_like_ib.setEnabled(false);
                                    sendSuperLike();
                                    if (TextUtils.equals(getIntent().getStringExtra("launched_from"), "info")){
                                        ((ResultReceiver)getIntent().getParcelableExtra("finisher")).send(1, new Bundle());
                                    }
                                } else {
                                    showSuperLikeDepletedMessage();
                                }
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(UserProfileDetails.this);
                                showToastyMessage.errorMessage("Request not completed !");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                        //  showToastyMessage.errorMessage(context.getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }

    //    send super like
    private void sendSuperLike() {
        String match_uid = getIntent().getStringExtra("UID");
        MySharedPrefs mySharedPrefs = new MySharedPrefs(UserProfileDetails.this);
        String uid = mySharedPrefs.getUid();
        String chat_key;
        if (match_uid.compareTo(uid) < 0){
            chat_key = match_uid+uid; // keep match uid in front
        }
        else chat_key = uid+match_uid;

        DatabaseReference databaseReference2 = FirebaseDatabase
                .getInstance().getReference().child("chats");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user_chat_record");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("last_message_status");
        String push_key = databaseReference.push().getKey();
        databaseReference.child(uid).child(match_uid).child("timestamp").setValue(push_key);
        databaseReference.child(match_uid).child(uid).child("timestamp").setValue(push_key);
        databaseReference1.child(chat_key).child(match_uid).setValue("sent");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("senderUID", uid);
        hashMap.put("receiverUID", match_uid);
        hashMap.put("message_date_time", ServerValue.TIMESTAMP);
        hashMap.put("message", "hi, I just super liked you ♥");

        if (push_key != null) {
            databaseReference2.child(chat_key).child(push_key).setValue(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            YoYo.with(Techniques.FadeIn)
                                    .duration(1000)
                                    .repeat(0)
                                    .playOn(super_like_ib);
                            YoYo.with(Techniques.Landing)
                                    .duration(300)
                                    .repeat(0)
                                    .playOn(super_like_rl);

                            //  gifImageView.setVisibility(View.VISIBLE);
                            super_like_rl.setVisibility(View.VISIBLE);
                            super_like_ib.setBackground(getResources().getDrawable(R.drawable.ic_super_like_new_));

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    YoYo.with(Techniques.FadeOut)
                                            .duration(300)
                                            .repeat(0)
                                            .playOn(super_like_rl);
                                  showLikeSuccessMessage();
                                }
                            },1500);

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }

    }
//send super like

    //    super like success message
    private void showLikeSuccessMessage() {
        FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(UserProfileDetails.this)
                .setimageResource(R.drawable.ic_super_like_new_)
                .setTextTitle("SUPER LIKE")
                .setTitleColor(R.color.mildBlue)
                .setBody("Your Super Like message have been sent to "+getIntent().getStringExtra("name"))
                .setPositiveColor(R.color.black)
                .setBackgroundColor(R.color.white)
                .setNegativeColor(R.color.black90)
                .setNegativeButtonText("Okay")
                .setCancelable(false)
                .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
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
//    super like success message

    //    super like depleted message
    private void showSuperLikeDepletedMessage() {
        FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(UserProfileDetails.this)
                .setimageResource(R.drawable.ic_super_like_finished)
                .setTextSubTitle("No more super swipe left")
                //.setBody("Profile updated")
                .setPositiveButtonText("Okay")
                .setPositiveColor(R.color.black)
                .setBackgroundColor(R.color.white)
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
//    super like depleted message



    //    set smooth scroll
    private void setSmoothScrollToTop(int position){
        recyclerView.smoothScrollToPosition(position);
        nestedScrollView.smoothScrollTo(0,0);
        appBarLayout.setExpanded(true, true);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if(behavior !=null) {
            behavior.setTopAndBottomOffset(0);
            behavior.onNestedPreScroll(coordinatorLayout, appBarLayout, null, 0, 0, new int[2]);
            recyclerView.setPreserveFocusAfterLayout(true);
        }
    }
//    set smooth scroll

}
