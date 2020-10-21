package com.united.lovender;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.github.zawadz88.activitychooser.MaterialActivityChooserBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;


public class SwipeDeckAdapter extends BaseAdapter {

    private List<DataForRecyclerView> data;
    private List<DataForRecyclerView> data1;
    private Context context;
    private int cust_pos;

    SwipeDeckAdapter(List<DataForRecyclerView> data, Context context) {
        this.data = data;
        this.context = context;
    }
    SwipeDeckAdapter(List<DataForRecyclerView> data,Context context, int position) {
        this.context = context;
        cust_pos = position;
        data1 = data;
    }

    SwipeDeckAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //    when left swiped by user
    void leftSwiped(){
        final DataForRecyclerView myData = data1.get(cust_pos);
       // Toast.makeText(context, "Left Swiped "+myData.getHashtag_name(), Toast.LENGTH_SHORT).show();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();

        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "ProfileSwipe");
            jsonObject.put("UID", uid);
            jsonObject.put("SwipeUID", myData.getUID());

        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = context.getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final int code = response.getInt("code");

                            if (code == 1313){
                                String result = response.getString("result");
                                if (TextUtils.equals("true", result)){
                                    Intent intent = new Intent(context, Matched.class);
                                    intent.putExtra("name", myData.getName());
                                    intent.putExtra("match_uid", myData.getUID());
                                    intent.putExtra("image_url", myData.getImage1());
                                    intent.putExtra("UID", myData.getUID());
                                    intent.putExtra("name", myData.getName());
                                    intent.putExtra("age", myData.getAge());
                                    intent.putExtra("job_title", myData.getJob());
                                    intent.putExtra("education", myData.getEducation());
                                    intent.putExtra("religion", myData.getReligion());
                                    intent.putExtra("height", myData.getHeight());
                                    intent.putExtra("gender", myData.getGender());
                                    intent.putExtra("about", myData.getAbout());
                                    intent.putExtra("zodiac", myData.getZodiac());
                                    intent.putExtra("company", myData.getCompany());
                                    intent.putExtra("body_type", myData.getBody());
                                    intent.putExtra("swipe", myData.getSwipe());
                                    intent.putExtra("image_count", myData.getImage_count());
                                    intent.putExtra("image1", myData.getImage1());
                                    intent.putExtra("image2", myData.getImage2());
                                    intent.putExtra("image3", myData.getImage3());
                                    intent.putExtra("image4", myData.getImage4());
                                    intent.putExtra("image5", myData.getImage5());
                                    intent.putStringArrayListExtra("interests", myData.getInterests());
                                    context.startActivity(intent);
                                }
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
                      //  ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                      //  showToastyMessage.errorMessage(context.getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }

    //    when right swiped by user
    public void rightSwiped(){
    }

    public void showTutorial(){


    }

//    setting image
    private void setImage(View view, DataForRecyclerView myData){
        Picasso.with(context)
                .load(myData.getProfileImage())
                .fit()
                .centerCrop()
                .into((ImageView) view.findViewById(R.id.fc_profile_image_iv));
    }
    //    setting image

//    setting data
    @SuppressLint("SetTextI18n")
    private void setData(View view, DataForRecyclerView myData){
        if (TextUtils.equals(myData.getAge(),"0")){
            String name = myData.getName().replaceAll("\n", " ");
            ((TextView) view.findViewById(R.id.fc_user_name_tv)).setText(name.concat("  "));
        } else {
            String name = myData.getName().replaceAll("\n", " ");
            ((TextView) view.findViewById(R.id.fc_user_name_tv)).setText(name.concat("  "));
            ((TextView) view.findViewById(R.id.fc_age_tv)).setText(myData.getAge());
        }

        if (!TextUtils.equals(myData.getAbout(),"")){
            ((TextView) view.findViewById(R.id.fc_about_tv)).setText(myData.getAbout());
        }
        else {
            view.findViewById(R.id.fc_about_tv).setVisibility(View.GONE);
        }

        //        distance
        if (Integer.parseInt(myData.getDistance_km()) < 1){
            ((TextView) view.findViewById(R.id.fc_distance_tv)).setText("Less than kilometer away");
        }
        else {
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String yourFormattedString = formatter.format(Integer.parseInt(myData.getDistance_km()));
            ((TextView) view.findViewById(R.id.fc_distance_tv)).setText(yourFormattedString+" kilometers away");
        }

    }
//    setting data


//    click info
    private void clickInfoButton(final View view, final int position){
        view.findViewById(R.id.fc_info_ib)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataForRecyclerView myData = data.get(position);

                        Intent intent = new Intent(context, UserProfileDetails.class);
                        intent.putExtra("UID", myData.getUID());
                        intent.putExtra("name", myData.getName());
                        intent.putExtra("age", myData.getAge());
                        intent.putExtra("job_title", myData.getJob());
                        intent.putExtra("education", myData.getEducation());
                        intent.putExtra("religion", myData.getReligion());
                        intent.putExtra("height", myData.getHeight());
                        intent.putExtra("gender", myData.getGender());
                        intent.putExtra("about", myData.getAbout());
                        intent.putExtra("zodiac", myData.getZodiac());
                        intent.putExtra("company", myData.getCompany());
                        intent.putExtra("body_type", myData.getBody());
                        intent.putExtra("swipe", myData.getSwipe());
                        intent.putExtra("image_count", myData.getImage_count());
                        intent.putExtra("image1", myData.getImage1());
                        intent.putExtra("image2", myData.getImage2());
                        intent.putExtra("image3", myData.getImage3());
                        intent.putExtra("image4", myData.getImage4());
                        intent.putExtra("image5", myData.getImage5());
                        intent.putExtra("launched_from", "info");
                        intent.putStringArrayListExtra("interests", myData.getInterests());

                        intent.putExtra("finisher", new ResultReceiver(null) {
                            @Override
                            protected void onReceiveResult(int resultCode, Bundle resultData) {
                                view.findViewById(R.id.fc_super_like_ib).setEnabled(false);
                                view.findViewById(R.id.fc_super_like_ib).setBackground(context.getResources().getDrawable(R.drawable.ic_super_like_new_));

                            }
                        });
                        ((Activity)context).startActivityForResult(intent,1);
                       // context.startActivity(intent);
                    }
                });

    }
    //    click info

//    super like button
    private void clickLikeButton(View view, final DataForRecyclerView myData){
        final RelativeLayout relativeLayout = view.findViewById(R.id.fc_like_rl);
        final ImageButton imageButton =   view.findViewById(R.id.fc_like_ib);
        view.findViewById(R.id.fc_like_ib)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        YoYo.with(Techniques.FadeIn)
                                .duration(1000)
                                .repeat(0)
                                .playOn(imageButton);
                        imageButton.setEnabled(false);
                        //  gifImageView.setVisibility(View.VISIBLE);
                        relativeLayout.setVisibility(View.VISIBLE);
                        imageButton.setBackground(context.getResources().getDrawable(R.drawable.ic_cupid));
                        YoYo.with(Techniques.Landing)
                                .duration(300)
                                .repeat(0)
                                .playOn(relativeLayout);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                YoYo.with(Techniques.FadeOut)
                                        .duration(300)
                                        .repeat(0)
                                        .playOn(relativeLayout);
                                //    int item = position-1;
                                ///     final DataForRecyclerView myData = data.get(item);
                                myData.getSwipeDeck().swipeTopCardLeft(160);
                               // Toast.makeText(context, "d "+myData.getName(), Toast.LENGTH_SHORT).show();
                            }
                        },1500);
                    }
                });
    }
//    super like button

    //    super like button
    private void clickSuperLikeButton(final View view, final DataForRecyclerView myData){

        final ImageButton imageButton =   view.findViewById(R.id.fc_super_like_ib);
        view.findViewById(R.id.fc_super_like_ib)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        new CheckNetworkConnection(context, new CheckNetworkConnection.OnConnectionCallback() {
                            @Override
                            public void onConnectionSuccess() {
                                RequestQueue requestQueue = Volley.newRequestQueue(context);
                                JSONObject jsonObject = new JSONObject();

                                MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                                final String uid = mySharedPrefs.getUid();

                                try {
                                    jsonObject.put("auth", "api!luv@13_9002");
                                    jsonObject.put("request", "SuperSwipe");
                                    jsonObject.put("UID", uid);
                                    jsonObject.put("SwipeUID", myData.getUID());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                String url = context.getString(R.string.api_name);
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    final int code = response.getInt("code");
                                                    if (code == 1313){
                                                        boolean result = response.getBoolean("result");
                                                        if (result){
                                                            imageButton.setEnabled(false);
                                                            sendSuperLike(view, myData);

                                                        }
                                                        if (!result){
                                                            showSuperLikeDepletedMessage();
                                                            //  ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                                            //  showToastyMessage.errorMessage("No more super swipe left");
                                                        }
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
                                                //  ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                                //  showToastyMessage.errorMessage(context.getResources().getString(R.string.volley_error));
                                            }

                                        });

                                jsonObjectRequest.setTag(1);
                                requestQueue.add(jsonObjectRequest);


                            }
                            @Override
                            public void onConnectionFail(String msg) {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                showToastyMessage.warningMessage("no internet");
                            }
                        }).execute();

                    }
                });

    }
    //    super like button

//    super like depleted message
    private void showSuperLikeDepletedMessage() {
        FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(context)
                .setimageResource(R.drawable.ic_super_like_finished)
                .setTextSubTitle("No more super swipe left")
                .setPositiveButtonText("Buy More")
                .setPositiveColor(R.color.black)
                .setBackgroundColor(R.color.white)
                .setNegativeColor(R.color.black90)
                .setNegativeButtonText("Cancel")
                .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                    }
                })
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

//    send super like
    private void sendSuperLike(View view, final DataForRecyclerView myData) {
        final RelativeLayout relativeLayout = view.findViewById(R.id.fc_super_like_rl);
        final ImageButton imageButton =   view.findViewById(R.id.fc_super_like_ib);
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .repeat(0)
                .playOn(imageButton);
        imageButton.setEnabled(false);
        YoYo.with(Techniques.Landing)
                .duration(300)
                .repeat(0)
                .playOn(relativeLayout);

        String match_uid = myData.getUID();
        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
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
                            try {
                                showLikeSuccessMessage(myData);
                            }
                            catch (Exception ignore){

                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }

        //  gifImageView.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);
        imageButton.setBackground(context.getResources().getDrawable(R.drawable.ic_super_like_new_));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.FadeOut)
                        .duration(300)
                        .repeat(0)
                        .playOn(relativeLayout);

            }
        },1500);
    }
//send super like

    //    super like success message
    private void showLikeSuccessMessage(final DataForRecyclerView myData) {
        FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(context)
                .setimageResource(R.drawable.ic_super_like_new_)
                .setTextTitle("SUPER LIKE")
                .setTitleColor(R.color.mildBlue)
                .setBody("Your Super Like message have been sent to "+myData.getName())
                //.setTextSubTitle("No more super swipe left")
                //.setBody("Profile updated")
                .setPositiveColor(R.color.black)
                .setBackgroundColor(R.color.white)
                .setNegativeColor(R.color.black90)
                .setNegativeButtonText("Continue swiping")
                .setCancelable(false)
                .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                        myData.getSwipeDeck().swipeTopCardRight(160);
                    }
                })
                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .build();
        alert.show();
    }
//    super like success message


    private void clickRewindButton(final View view, final int pos, final DataForRecyclerView myData) {

        view.findViewById(R.id.fc_rewind_ib)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // user is not a pro user prompt for buy plan
                        if (!myData.getPro_status()){
                            try{
                                showMessageBuySubscription(myData);
                            } catch (Exception ignored){}

                            return;
                        }

                        int position = pos -1;
                        if (pos == 0){
                            ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                            showToastyMessage.warningMessage("no rewind profile available");
                           // Toast.makeText(context, "no rewind profile available", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DataForRecyclerView myData = data.get(position);
                        Intent intent = new Intent(context, UserProfileDetails.class);
                        intent.putExtra("UID", myData.getUID());
                        intent.putExtra("name", myData.getName());
                        intent.putExtra("age", myData.getAge());
                        intent.putExtra("job_title", myData.getJob());
                        intent.putExtra("education", myData.getEducation());
                        intent.putExtra("religion", myData.getReligion());
                        intent.putExtra("height", myData.getHeight());
                        intent.putExtra("gender", myData.getGender());
                        intent.putExtra("about", myData.getAbout());
                        intent.putExtra("zodiac", myData.getZodiac());
                        intent.putExtra("company", myData.getCompany());
                        intent.putExtra("body_type", myData.getBody());
                        intent.putExtra("swipe", myData.getSwipe());
                        intent.putExtra("image_count", myData.getImage_count());
                        intent.putExtra("image1", myData.getImage1());
                        intent.putExtra("image2", myData.getImage2());
                        intent.putExtra("image3", myData.getImage3());
                        intent.putExtra("image4", myData.getImage4());
                        intent.putExtra("image5", myData.getImage5());
                        intent.putExtra("launched_from", "rewind");
                        intent.putStringArrayListExtra("interests", myData.getInterests());

                        context.startActivity(intent);
                        final ImageButton imageButton =   view.findViewById(R.id.fc_rewind_ib);
                        imageButton.setEnabled(false);
                        imageButton.setBackground(context.getResources().getDrawable(R.drawable.ic_rewind_tick));
                    }
                });
    }

//    share button
    private void clickShareButton(View view, final DataForRecyclerView myData){
        view.findViewById(R.id.fc_share_ib)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TITLE, "lovenders");
                        intent.putExtra(Intent.EXTRA_TEXT, "http://lovenders.com/profile/"+myData.getUID());
                        new MaterialActivityChooserBuilder(context)
                                .withIntent(intent)
                                .withTitle("Share "+myData.getName()+"'s Profile")
                                .show();
                    }
                });
    }
//    share button

//    buy subscription
    private void showMessageBuySubscription(final DataForRecyclerView myData){
        FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(context)
                .setimageResource(R.drawable.ic_diamond_blue)
                .setTextTitle("BECOME A PRO")
                .setTitleColor(R.color.appColor)
                .setBody("Claim unlimited rewinds with pro account.")
                .setPositiveColor(R.color.black)
                .setBackgroundColor(R.color.white)
                .setNegativeColor(R.color.black90)
                .setPositiveButtonText("Become a Pro")
                .setPositiveColor(R.color.lightGreen)
                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                       getPlan(myData);
                    }
                })
                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setCancelable(true)
                .build();
        alert.show();
    }
//    buy subscription

    //    get buying plans
    private void getPlan(final DataForRecyclerView myData) {

        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final String uid = mySharedPrefs.getUid();

        UserCurrentLocation userCurrentLocation = new UserCurrentLocation(context);
        final String country_name = userCurrentLocation.getUserCurrentCountry();
        String symbol = "dollar";
        try {
            if (TextUtils.equals(country_name.toLowerCase(), "india")){
                symbol = "Rs";
            }
        }
        catch (Exception ignored){

        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "GetPlans");
            jsonObject.put("UID", uid);
            jsonObject.put("Symbol", symbol);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = context.getString(R.string.api_name);

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

                                FragmentTransaction ft = myData.getFragmentManager().beginTransaction();
                                ft.add(R.id.fragment_container_feed, ourPlans, "our_plans").addToBackStack(null).commit();

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
//                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
//                        showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);

    }
//    get buying plans

//    get view
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        //        layout
        View view = convertView;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.feed_card_view, parent, false);
        }

        //        set data
        final DataForRecyclerView myData = data.get(position);
//        set user image
        setImage(view, myData);
//        set all text data
        setData(view, myData);
//        click info button
        clickInfoButton(view, position);
//        super like button
        clickLikeButton(view, myData);
//        rewind click
        clickRewindButton(view, position, myData);
//        super like button
        clickSuperLikeButton(view, myData);
//        share button
        clickShareButton(view, myData);

        return view;
    }

}