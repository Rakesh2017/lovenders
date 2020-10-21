package com.united.lovender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsParams;
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
import com.daprlabs.cardstack.SwipeDeck;
import com.facebook.login.LoginManager;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.skyfishjy.library.RippleBackground;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.utils.SpotlightListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class Feed extends Fragment implements View.OnClickListener
        , EasyPermissions.PermissionCallbacks, PurchasesUpdatedListener {

    private SwipeDeck cardStack;
    private RippleBackground rippleBackground;
    private CircleImageView no_internet_image, rippleCircularImage;
    private RelativeLayout no_internet_rl, no_profile_left_rl, enable_location_rl;
    private RequestQueue requestQueue;
    private final ArrayList<DataForRecyclerView> testData = new ArrayList<>();
    private FragmentActivity context;
    private ImageView right_swipe_iv, left_swipe_iv;
    private static final int DEVICE_LOCATION = 1;
    private BillingClient billingClient;
    private FusedLocationProviderClient fusedLocationClient;

    public Feed() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.context = (FragmentActivity) context;
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardStack = view.findViewById(R.id.ff_swipe_deck);
        no_profile_left_rl = view.findViewById(R.id.ff_profiles_ended_rl);
        FancyButton enable_location_fb = view.findViewById(R.id.ff_enable_location_fb);
        enable_location_rl = view.findViewById(R.id.ff_enable_location_rl);

        right_swipe_iv = view.findViewById(R.id.ff_right_swipe_iv);
        left_swipe_iv = view.findViewById(R.id.ff_left_swipe_iv);

        //when loading profiles
        rippleBackground = view.findViewById(R.id.ff_ripple_background);
        rippleCircularImage = view.findViewById(R.id.ff_ripple_image);
        rippleBackground.startRippleAnimation();

        no_internet_rl = view.findViewById(R.id.ff_no_internet_rl);
        no_internet_image = view.findViewById(R.id.ff_no_internet_image);

        cardStack.setLeftImage(R.id.fc_left_image_iv);
        cardStack.setRightImage(R.id.fc_right_image_iv);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        setProfilePhoto();
        if (grantPermission()){
            checkConditionsAndSetProfile();
        } else {
            enable_location_rl.setVisibility(View.VISIBLE);
        }

        swipeCallBacks();
        checkIfSubscriptionStatusUpdatedAtOurServers();

        no_internet_image.setOnClickListener(this);
        enable_location_fb.setOnClickListener(this);
       // cardStack.swipeTopCardLeft(1);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
            String val = mySharedPrefs.getProfileSettingsChanged();
            if (TextUtils.equals(val, "true")){
                setProfilePhoto();
                checkConditionsAndSetProfile();
            }
        }
    }

    private void showTutorial() {

        left_swipe_iv.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.Shake)
                .duration(5000)
                .repeat(30)
                .playOn(left_swipe_iv);
        String id = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        SpotlightView spotlightView = new SpotlightView.Builder(context)
                .introAnimationDuration(100)
                .enableRevealAnimation(true)
                .performClick(false)
                .fadeinTextDuration(100)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("Swipe Left")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("left swipe the profile you like")
                .maskColor(Color.parseColor("#90000000"))
                .target(left_swipe_iv)
                .lineAnimDuration(100)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId(id) //UNIQUE ID
                .show();


        spotlightView.setListener(new SpotlightListener() {
            @Override
            public void onUserClicked(String s) {
              //  bottomBarLinearLayout.setVisibility(View.VISIBLE);
                String id = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
                left_swipe_iv.setVisibility(View.GONE);
                right_swipe_iv.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Shake)
                        .duration(5000)
                        .repeat(30)
                        .playOn(right_swipe_iv);

                SpotlightView spotlightView = new SpotlightView.Builder(context)
                        .introAnimationDuration(100)
                        .enableRevealAnimation(true)
                        .performClick(false)
                        .fadeinTextDuration(100)
                        .headingTvColor(Color.parseColor("#eb273f"))
                        .headingTvSize(32)
                        .headingTvText("Swipe Right")
                        .subHeadingTvText("right swipe the profile you aren't interested in")
                        .subHeadingTvColor(Color.parseColor("#ffffff"))
                        .subHeadingTvSize(16)
                        .maskColor(Color.parseColor("#90000000"))
                        .target(right_swipe_iv)
                        .lineAnimDuration(100)
                        .lineAndArcColor(Color.parseColor("#eb273f"))
                        .dismissOnTouch(true)
                        .dismissOnBackPress(true)
                        .enableDismissAfterShown(true)
                        .usageId(id) //UNIQUE ID
                        .show();

                spotlightView.setListener(new SpotlightListener() {
                    @Override
                    public void onUserClicked(String s) {
                        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                        mySharedPrefs.setFirstTimeFeed("false");
                        right_swipe_iv.setVisibility(View.GONE);
                       // bottomBarLinearLayout.setVisibility(View.GONE);
                    }
                });

            }
        });
    }

//on resume
    public void onResume(){
        super.onResume();
        if (!grantPermissionMain()){
            enable_location_rl.setVisibility(View.VISIBLE);
        } else {
            enable_location_rl.setVisibility(View.GONE);
        }
    }
//on resume

//    swipe call backs
    private void swipeCallBacks() {
        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(final int position) {
                new CheckNetworkConnection(context, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        SwipeDeckAdapter swipeDeckAdapter = new SwipeDeckAdapter(testData, context, position);
                        swipeDeckAdapter.leftSwiped();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        startActivity(new Intent(context, NoInternetActivity.class));
                    }
                }).execute();

            }

            @Override
            public void cardSwipedRight(final int position) {
                new CheckNetworkConnection(context, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        SwipeDeckAdapter swipeDeckAdapter = new SwipeDeckAdapter(testData, context, position);
                        swipeDeckAdapter.rightSwiped();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        startActivity(new Intent(context, NoInternetActivity.class));
                    }
                }).execute();

            }

            @Override
            public void cardsDepleted() {
                no_profile_left_rl.setVisibility(View.VISIBLE);
            }

            @Override
            public void cardActionDown() {
            }

            @Override
            public void cardActionUp() {
            }

        });
    }
//    swipe call backs

    private void checkConditionsAndSetProfile(){
        new CheckNetworkConnection(context, new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                setProfiles();
            }
            @Override
            public void onConnectionFail(String msg) {
                rippleBackground.stopRippleAnimation();
                rippleBackground.setVisibility(View.GONE);
                no_internet_rl.setVisibility(View.VISIBLE);
                //   startActivity(new Intent(context, NoInternetActivity.class));
            }
        }).execute();
    }

    //    set profile photo
    private void setProfilePhoto() {

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final String old_url = mySharedPrefs.getPhotoUrl();
        try {
            Glide.with(Objects.requireNonNull(getContext()))
                    .load(old_url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_placeholder_profile)
                            .error(R.drawable.ic_placeholder_profile)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .into(no_internet_image);
            Glide.with(Objects.requireNonNull(getContext()))
                    .load(old_url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_placeholder_profile)
                            .error(R.drawable.ic_placeholder_profile)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .into(rippleCircularImage);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        requestQueue = Volley.newRequestQueue(context);
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
                                try{
                                    if (!TextUtils.equals(old_url, url)) {
                                        Glide.with(context)
                                                .load(url)
                                                .apply(new RequestOptions().placeholder(R.drawable.ic_placeholder_profile))
                                                .into(no_internet_image);
                                        Glide.with(context)
                                                .load(url)
                                                .apply(new RequestOptions().placeholder(R.drawable.ic_placeholder_profile))
                                                .into(rippleCircularImage);
                                    }
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                mySharedPrefs.setProfilePhotoPrefs(url);
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
                      //  showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(4);
        requestQueue.add(jsonObjectRequest);
    }
    //    set profile photo


//    getting location and setting profiles.
    @SuppressLint("MissingPermission")
    private void setProfiles() {

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        String  latitude, longitude;
                        try{
                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                        } catch (Exception e){
                            latitude="";
                            longitude="";
                        }
                        if (TextUtils.isEmpty(longitude) || TextUtils.isEmpty(latitude)){
                            checkIfGpsIsOn();  // if unable to get location then prompt to turn on gps
                        }
                        getLastKnownLocationAndGetProfiles(latitude, longitude);

                    }
                });

    }
//    setting profiles


//    get profiles
    private void getLastKnownLocationAndGetProfiles(final String latitude, final String longitude){
        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(context));
        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "LoadProfiles");
            jsonObject.put("UID", uid);
            jsonObject.put("LatLog", latitude+","+longitude);
        } catch (Exception e) {
            ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
            showToastyMessage.errorMessage("lat"+e.getMessage());
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
                                String result = response.getString("result");

                                try {
                                    JSONArray jsonArrayResponse = new JSONArray(result);

                                    final int response_length = jsonArrayResponse.length();
                                    testData.clear();

                                    String profile = null, version = null, description = null, product_id = null, expiry_date = null;
                                    boolean pro_status = false, check_status = false;
                                    for (int i = 0 ; i<response_length ; i++) {
                                        JSONObject object = jsonArrayResponse.getJSONObject(i);
                                        profile = object.getString("Profiles");
                                        version = object.getString("Version");
                                        description = object.getString("Description");
                                        pro_status = object.getBoolean("ProStaus");
                                        check_status = object.getBoolean("CheckStatus");
                                        product_id = object.getString("ProductId");
                                        expiry_date = object.getString("ProExp");
                                    }

                                    // checking app version
                                    try {
                                        assert version != null;
                                        int build_version = Integer.parseInt(getResources().getString(R.string.build_version));
                                        if (Integer.parseInt(version) > build_version){
                                            showUpdateAppMessage(description);
                                        }
                                    } catch (Exception ignored){}

                                    if (pro_status){
                                        mySharedPrefs.setProUserStatus(true, expiry_date, product_id);
                                    } else {
                                        mySharedPrefs.setProUserStatus(false, "", "");
                                    }

                                    if (pro_status && check_status){
                                        checkIfSubscriptionAutoRenewed(product_id);
                                    }

                                    // getting profiles
                                    JSONArray jsonArrayProfiles = new JSONArray(profile);
                                    final int profile_length = jsonArrayProfiles.length();
                                    for (int i = 0 ; i<profile_length ; i++){ // profile loop starts
                                        String name, age, uid, about, job_tile, company, education
                                                , religion, height, gender, image1, image2, image3, image4
                                                , image5, zodiac, body_type, swipes, interestLoop, imageCount
                                                , distance_km, distance_mile;

                                        JSONObject objectProfiles = jsonArrayProfiles.getJSONObject(i);
                                        DataForRecyclerView dataForRecyclerView = new DataForRecyclerView();

                                        name = objectProfiles.getString("Name");
                                        age = objectProfiles.getString("Age");
                                        uid = objectProfiles.getString("UID");
                                        about = objectProfiles.getString("About");
                                        job_tile = objectProfiles.getString("JobTitle");
                                        company = objectProfiles.getString("Company");
                                        education = objectProfiles.getString("Education");
                                        religion = objectProfiles.getString("Religion");
                                        height = objectProfiles.getString("Height");
                                        zodiac = objectProfiles.getString("ZodiacSign");
                                        body_type = objectProfiles.getString("BodyType");
                                        swipes = objectProfiles.getString("Swipe");
                                        gender = objectProfiles.getString("Gender");
                                        distance_km = objectProfiles.getString("Distance_KM");
                                        distance_mile = objectProfiles.getString("Distance_M");
                                        interestLoop = objectProfiles.getString("Interest");
                                        imageCount = objectProfiles.getString("ImageCount");
                                        image1 = objectProfiles.getString("Image1");
                                        image2 = objectProfiles.getString("Image2");
                                        image3 = objectProfiles.getString("Image3");
                                        image4 = objectProfiles.getString("Image4");
                                        image5 = objectProfiles.getString("Image5");

                                        dataForRecyclerView.setName(name);
                                        dataForRecyclerView.setProfileImage(image1);
                                        dataForRecyclerView.setAge(age);
                                        dataForRecyclerView.setUID(uid);
                                        dataForRecyclerView.setAbout(about);
                                        dataForRecyclerView.setJob(job_tile);
                                        dataForRecyclerView.setCompany(company);
                                        dataForRecyclerView.setHeight(height);
                                        dataForRecyclerView.setZodiac(zodiac);
                                        dataForRecyclerView.setBody(body_type);
                                        dataForRecyclerView.setEducation(education);
                                        dataForRecyclerView.setReligion(religion);
                                        dataForRecyclerView.setSwipe(swipes);
                                        dataForRecyclerView.setGender(gender);
                                        dataForRecyclerView.setDistance_km(distance_km);
                                        dataForRecyclerView.setDistance_miles(distance_mile);
                                        dataForRecyclerView.setSwipeDeck(cardStack);

                                        dataForRecyclerView.setImage_count(imageCount);
                                        dataForRecyclerView.setImage1(image1);
                                        dataForRecyclerView.setImage2(image2);
                                        dataForRecyclerView.setImage3(image3);
                                        dataForRecyclerView.setImage4(image4);
                                        dataForRecyclerView.setImage5(image5);

                                        try {
                                            JSONArray jsonArrayInterest = new JSONArray(interestLoop);
                                            final int interest_length = jsonArrayInterest.length();
                                            ArrayList<String> arrayList = new ArrayList<>();

                                            for (int k = 0 ; k < interest_length ; k++){
                                                JSONObject object1 = jsonArrayInterest.getJSONObject(k);
                                                String interest = object1.getString("Interest");
                                                arrayList.add(interest);
                                            }


                                            dataForRecyclerView.setInterests(arrayList);
                                        }
                                        catch (JSONException ignored){
                                        }
                                        // checking is user is pro or not
                                        dataForRecyclerView.setFragmentManager(context.getSupportFragmentManager());
                                        if (pro_status){
                                            dataForRecyclerView.setPro_status(pro_status);
                                        }
                                        testData.add(dataForRecyclerView);
                                    }  // profile loop ends

                                    // setting adapter
                                    final SwipeDeckAdapter adapter = new SwipeDeckAdapter(testData, getContext());
                                    cardStack.setAdapter(adapter);

                                    rippleBackground.setVisibility(View.GONE);
                                    no_internet_rl.setVisibility(View.GONE);
                                    rippleBackground.stopRippleAnimation();

                                    mySharedPrefs.setProfileSettingsChanged("false");
                                    String val = mySharedPrefs.getFirstTimeFeed();
                                    if (TextUtils.equals(val, "true")){
                                        showTutorial(); // showing tutorial
                                    }

                                } catch (JSONException e) {
                                    ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                    showToastyMessage.errorMessage("c1"+e.getMessage());
                                    e.printStackTrace();
                                }

                            }
                            else if (code == 1920){
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                showToastyMessage.errorMessage(response.getString("msg"));
                                logout();
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                showToastyMessage.errorMessage(response.getString("code"));
                            }

                        } catch (JSONException ignored) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //    ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                        //   showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(2);
        requestQueue.add(jsonObjectRequest);
    }
//    get profiles



//    check if subscription renewed
    private void checkIfSubscriptionAutoRenewed(String product_id){
       getGoogleBillingsForBuy(product_id);
    }

    // getting billing of the user and checking subscription validity
    // [important points: this billing will show all the billings
    // of current google accounts logged in to device, and if purchase token saved at our
    // server matches with any one of the purchase token received from device will indicates the validity of
    // new subscription, as we will check the next subscription validity after the end of current subscription
    // (here purchase token of subscription always remains same while order ID will always be different)
    // ]
    private void getGoogleBillingsForBuy(final String product_id){

        billingClient = BillingClient.newBuilder(context).setListener(this).build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                    int responseCode = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
                    if (responseCode == BillingClient.BillingResponse.OK) {
                        queryPurchase(product_id);
                    }
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
            }
        });
    }
// getting billing of the user and checking subscription validity

    private void queryPurchase(String product_id){
        List<String> skuList = new ArrayList<>();

        skuList.add("basic");
        skuList.add("premium");
        skuList.add("gold");

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS); // subs refer for subscriptions

        Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
        updateServerValueOfAutoSubscription(purchasesResult.getPurchasesList(), product_id);
    }

    // updating our server, did user renew subscription or not
    private void updateServerValueOfAutoSubscription(List<Purchase> purchasesList, String product_id){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();

        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final String uid = mySharedPrefs.getUid();
        String token = null, data = null;
        long subscription_time = 0;
        int counter = 0;
        if (purchasesList.isEmpty()){
            token = "";
            subscription_time = 0;
            counter = 0;
            data = "";
        } else {
            int length = purchasesList.size();
            for(int i=0; i<length ; i++){
                Purchase purchase = purchasesList.get(i);
                String renewed_product_id = purchase.getSku();
                if (TextUtils.equals(renewed_product_id, product_id)){
                    token = purchase.getPurchaseToken();
                    data = Base64.encodeToString(purchase.getOriginalJson().getBytes(), Base64.NO_WRAP);
                    subscription_time = purchase.getPurchaseTime();
                    if (TextUtils.equals(renewed_product_id, "basic")){
                        counter = 1;
                    } else if (TextUtils.equals(renewed_product_id, "premium")){
                        counter = 3;
                    }  else if (TextUtils.equals(renewed_product_id, "gold")){
                        counter = 6;
                    } else {
                        counter = 0;
                    }
                }
            }
        }

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "ReProAccount");
            jsonObject.put("UID", uid);
            jsonObject.put("ProToken", data);
            jsonObject.put("SubscribeOn", subscription_time);
            jsonObject.put("Plan", counter);
            jsonObject.put("ProductToken", token);

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

                            // Toast.makeText(Login.this, ""+response.toString(), Toast.LENGTH_SHORT).show();
                            if (code == 1313){
                                boolean result = response.getBoolean("result");
                                MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                                if (result){
                                    mySharedPrefs.setProUserStatus(true);
                                }
                                else {
                                    mySharedPrefs.setProUserStatus(false);
                                }

                            }
                            else if (code == 1920){
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                showToastyMessage.errorMessage(response.getString("msg"));
//
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
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

                    }

                });

        jsonObjectRequest.setTag(3);
        requestQueue.add(jsonObjectRequest);
    }
    // updating our server, did user renew subscription or not

//    check if subscription renewed


//    checking if update is available
    private void showUpdateAppMessage(String message) {
        FancyAlertDialog.Builder alert1 = new FancyAlertDialog.Builder(context)
                .setimageResource(R.drawable.ic_update)
                .setTextTitle("Update Available")
                .setTitleColor(R.color.appColor)
                .setBody(message)
                .setPositiveColor(R.color.black)
                .setBackgroundColor(R.color.white)
                .setPositiveButtonText("update now")
                .setNegativeButtonText("update later")
                .setPositiveButtonText("update now")
                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                        final String appPackageName = "com.united.lovender";
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException exception) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setBodyGravity(FancyAlertDialog.TextGravity.LEFT)
                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .build();
        alert1.setCancelable(true);
        alert1.show();
    }
    //    checking if update is available


//    here checking, whether subscription status is also update at our servers, once the user buy subscription
//    (this can rarely happen in case of internet access lost at very instant at which payment is made to google
//   )
    private void checkIfSubscriptionStatusUpdatedAtOurServers(){
        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final boolean status = mySharedPrefs.getSubscriptionStatus();
        if (!status){
            updateDataToServer();
        }
    }

    private void updateDataToServer(){
        requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final String uid = mySharedPrefs.getUid();
        final String data = mySharedPrefs.getSubscriptionToken();
        final long subscription_time = mySharedPrefs.getSubscriptionTiming();
        final int counter = mySharedPrefs.getSubscriptionPlan();

        if (TextUtils.isEmpty(data) || subscription_time < 0 || counter < 1 || counter > 3){
            return;
        }

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "ProAccount");
            jsonObject.put("UID", uid);
            jsonObject.put("ProToken", data);
            jsonObject.put("SubscribeOn", subscription_time);
            jsonObject.put("Plan", counter);
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
                                    mySharedPrefs.setSubscriptionStatus(true, null, 0, 0);
                                }
                            }
                            else if (code == 1920){
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                showToastyMessage.errorMessage(response.getString("msg"));
//
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
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
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }
//    here checking, whether subscription status is also update at our servers, once the user buy subscription

    @Override
    public void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(1);
            requestQueue.cancelAll(2);
            requestQueue.cancelAll(3);
            requestQueue.cancelAll(4);
        }
    }

//    on click
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.ff_no_internet_image) {
            rippleBackground.setVisibility(View.VISIBLE);
            rippleBackground.startRippleAnimation();
            checkConditionsAndSetProfile();

            // granting permission for location
        } else if (id == R.id.ff_enable_location_fb) {
            grantPermission();
        }
    }
    //    on click

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    private void logout() {
        final MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        HashMap<Integer, String> hashMap;
        hashMap = mySharedPrefs.getLoggedInUserDetails();
        mySharedPrefs.clearAllPrefs();
        if (TextUtils.equals(hashMap.get(0), "facebook")){ // logging out facebook instance
            LoginManager.getInstance().logOut();
        }
        startActivity(new Intent(context, Login.class));
        ActivityCompat.finishAffinity(context);
    }


    private boolean grantPermissionMain() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION };
        //            EasyPermissions.requestPermissions(this, "We need to know your location to give best service.",
//                    DEVICE_LOCATION, perms);
        return EasyPermissions.hasPermissions(context, perms);
    }

    //    permissions
    @AfterPermissionGranted(DEVICE_LOCATION)
    private boolean grantPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION };
        if (EasyPermissions.hasPermissions(context, perms)) {
            return true;
        } else {
            EasyPermissions.requestPermissions(this, "We need to know your location to give best service.",
                    DEVICE_LOCATION, perms);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        checkConditionsAndSetProfile();
        enable_location_rl.setVisibility(View.GONE);
        //Toast.makeText(this, "Some permissions have been granted!", Toast.LENGTH_SHORT).show();
        // Some permissions have been granted
        // ...
    }

    private void checkIfGpsIsOn(){
        GpsUtils gpsUtils = new GpsUtils(context);
        gpsUtils.turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
            }
        });
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION };
        if (EasyPermissions.somePermissionPermanentlyDenied(this, Arrays.asList(perms))) {
            new MaterialDialog.Builder(context)
                    .title("Grant Permission")
                    .titleColor(Color.BLACK)
                    .content("We need to know your location to give best service.")
                    .contentColor(Color.BLACK)
                    .backgroundColor(Color.WHITE)
                    .positiveText("Grant Permission")
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .positiveColor(getResources().getColor(R.color.lightGreen))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new AppSettingsDialog.Builder(context).build().show();
                        }
                    })
                    .negativeText("Decline")
                    .negativeColor(getResources().getColor(R.color.red))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {

    }
//    permission
}
