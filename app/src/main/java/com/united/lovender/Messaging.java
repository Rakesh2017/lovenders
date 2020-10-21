package com.united.lovender;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;


public class Messaging extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView match_image_iv;
    private TextView match_name_tv;
    private EditText message_et;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<DataForRecyclerView> list = new ArrayList<>();
    private ChildEventListener mListener;
    private SpinKitView spinKitView;

    int count = 0, start_at_count = 0;

    private DatabaseReference databaseReference_CHATS = FirebaseDatabase
            .getInstance().getReference().child("chats");

    private DatabaseReference databaseReference_USER_CHAT_RECORD = FirebaseDatabase
            .getInstance().getReference().child("user_chat_record");
    private DatabaseReference databaseReference_LAST_MESSAGE_STATUS = FirebaseDatabase
            .getInstance().getReference().child("last_message_status");
    private DatabaseReference databaseReference_DASHBOARD_STATUS = FirebaseDatabase
            .getInstance().getReference().child("dashboard_status");

    private String match_uid, uid, chat_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_messaging);
        changeStatusBarColor();

        match_image_iv = findViewById(R.id.ac_match_image_iv);
        match_name_tv = findViewById(R.id.ac_match_name_tv);
        ImageButton send_btn = findViewById(R.id.ac_send_message);
        message_et = findViewById(R.id.ac_messageText);
        ImageButton back_ib = findViewById(R.id.ac_back_ib);
        spinKitView = findViewById(R.id.ac_spin_kit);

        recyclerView = findViewById(R.id.ac_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(Messaging.this);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);

        Toolbar toolbar =  findViewById(R.id.ac_toolbar);
        setSupportActionBar(toolbar);


        send_btn.setOnClickListener(this);
        back_ib.setOnClickListener(this);

        setNameAndImage();

        match_uid = getIntent().getStringExtra("match_uid");
        MySharedPrefs mySharedPrefs = new MySharedPrefs(Messaging.this);
        uid = mySharedPrefs.getUid();
        if (match_uid.compareTo(uid) < 0){
            chat_key = match_uid+uid; // keep match uid in front
        }
        else chat_key = uid+match_uid;

        mySharedPrefs.setChatKey(chat_key);

        databaseReference_CHATS.keepSynced(true);


        NotificationManager notifManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
        list.clear();

        if (!TextUtils.isEmpty(getIntent().getStringExtra("start_at"))){
            // if ever chat was previously deleted
            getMessagesStartAt();
        }
        else {
            getMessages();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_send_location) {
            Intent intent = new Intent(Messaging.this, LocationFromMap.class);
            intent.putExtra("finisher", new ResultReceiver(null) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    try {
                        sendMapLocation();
                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(Messaging.this);
                        showToastyMessage.successMessage("Location Sent");
                    }
                    catch (Exception e){
                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(Messaging.this);
                        showToastyMessage.warningMessage("message not sent, something went wrong");                    }
                }
            });
            startActivityForResult(intent,1);
            return true;
        }

        else if (id == R.id.menu_profile){
            if (TextUtils.equals(getIntent().getStringExtra("ProfileStatus"), "true")){
                showMessageProfileDeleted();
            } else {
                viewProfile();
            }
            return true;
        }

        else if (id == R.id.menu_report){
            Intent intent = new Intent(Messaging.this, ReportUser.class);
            intent.putExtra("name", getIntent().getStringExtra("name"));
            intent.putExtra("report_uid", match_uid);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showMessageProfileDeleted() {
        FancyAlertDialog.Builder alert1 = new FancyAlertDialog.Builder(Messaging.this)
                .setimageResource(R.drawable.ic_deletd_heart)
                .setTextTitle("Account Was Removed")
                .setTitleColor(R.color.appColor)
                .setBody("This Account was removed by the user!")
                .setPositiveColor(R.color.black)
                .setBackgroundColor(R.color.white)
                .setPositiveButtonText("Okay")
                .setCancelable(true)
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
        alert1.show();
    }


    //    view profile
    private void viewProfile() {
        RequestQueue requestQueue = Volley.newRequestQueue(Messaging.this);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "ProfileInfo");
            jsonObject.put("UID", match_uid);

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
                                Log.w("dfdser", response.toString());
                                JSONArray jsonArray = new JSONArray(result);

                                String name, age, uid, about, job_tile, company, education
                                        , religion, height, gender, image1, image2, image3, image4
                                        , image5, zodiac, body_type, swipes, interestLoop, imageCount;

                                JSONObject object = jsonArray.getJSONObject(0);

                                if (TextUtils.equals(object.getString("ProfileDeleteStatus"), "true")){
                                    showMessageProfileDeleted();
                                    return;
                                }

                                name = object.getString("Name");
                                age = object.getString("Age");
                                uid = object.getString("UID");
                                about = object.getString("About");
                                job_tile = object.getString("JobTitle");
                                company = object.getString("Company");
                                education = object.getString("Education");
                                religion = object.getString("Religion");
                                height = object.getString("Height");
                                zodiac = object.getString("ZodiacSign");
                                body_type = object.getString("BodyType");
                                swipes = object.getString("Swipe");
                                gender = object.getString("Gender");
                                interestLoop = object.getString("Interest");
                                imageCount = object.getString("ImageCount");
                                image1 = object.getString("Image1");
                                image2 = object.getString("Image2");
                                image3 = object.getString("Image3");
                                image4 = object.getString("Image4");
                                image5 = object.getString("Image5");

                                Intent intent = new Intent(Messaging.this, UserProfileDetails.class);
                                intent.putExtra("UID", uid);
                                intent.putExtra("name", name);
                                intent.putExtra("age", age);
                                intent.putExtra("job_title", job_tile);
                                intent.putExtra("education", education);
                                intent.putExtra("religion", religion);
                                intent.putExtra("height", height);
                                intent.putExtra("gender", gender);
                                intent.putExtra("about", about);
                                intent.putExtra("zodiac", zodiac);
                                intent.putExtra("company", company);
                                intent.putExtra("body_type", body_type);
                                intent.putExtra("swipe", swipes);
                                intent.putExtra("image_count", imageCount);
                                intent.putExtra("image1", image1);
                                intent.putExtra("image2", image2);
                                intent.putExtra("image3", image3);
                                intent.putExtra("image4", image4);
                                intent.putExtra("image5", image5);
                                intent.putExtra("launched_from", "messaging");

                                try {
                                    JSONArray jsonArray1 = new JSONArray(interestLoop);
                                    final int l = jsonArray1.length();
                                    ArrayList<String> arrayList = new ArrayList<>();

                                    for (int k = 0 ; k < l ; k++){
                                        JSONObject object1 = jsonArray1.getJSONObject(k);
                                        String interest = object1.getString("Interest");
                                        arrayList.add(interest);
                                    }
                                    intent.putStringArrayListExtra("interests", arrayList);
                                    startActivity(intent);
                                }
                                catch (JSONException e){

                                    e.printStackTrace();
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

                        //  ShowToastyMessage showToastyMessage = new ShowToastyMessage(ReportUser.this);
                        //   showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                       // dismiss();
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }
 //    view profile

    public void onResume(){
        super.onResume();
        MySharedPrefs mySharedPrefs = new MySharedPrefs(Messaging.this);
        mySharedPrefs.setMessagingActivity("active");
    }

    //    getting messages
    private void getMessages() {
        show();
        databaseReference_CHATS.child(chat_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String chat_push_key, message, senderUID, receiverUID, status, address, longitude, latitude;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    if (TextUtils.equals(dataSnapshot.child("senderUID").getValue(String.class), match_uid)){
                        try{
                            databaseReference_CHATS.child(chat_key).child(dataSnapshot.getKey()).child("status").setValue("seen");
                            databaseReference_CHATS.child(chat_key).child(dataSnapshot.getKey()).child("notification_status").setValue("seen");
                        }
                        catch (NullPointerException ignored){ }
                    }

                    DataForRecyclerView dataForRecyclerView = new DataForRecyclerView();

                    chat_push_key = dataSnapshot.getKey();
                    message = dataSnapshot.child("message").getValue(String.class);
                    senderUID = dataSnapshot.child("senderUID").getValue(String.class);
                    receiverUID = dataSnapshot.child("receiverUID").getValue(String.class);
                    status = dataSnapshot.child("status").getValue(String.class);
                    address = dataSnapshot.child("address").getValue(String.class);
                    latitude = dataSnapshot.child("latitude").getValue(String.class);
                    longitude = dataSnapshot.child("longitude").getValue(String.class);

                    dataForRecyclerView.setMessage(message);
                    dataForRecyclerView.setMy_uid(senderUID);
                    dataForRecyclerView.setMatch_UID(receiverUID);
                    dataForRecyclerView.setChat_key(chat_key);
                    dataForRecyclerView.setPush_key(chat_push_key);
                    dataForRecyclerView.setStatus(status);
                    dataForRecyclerView.setAddress(address);
                    dataForRecyclerView.setLatitude(latitude);
                    dataForRecyclerView.setLongitude(longitude);
                    list.add(dataForRecyclerView);
                }


                adapter = new MessagingRecyclerViewAdapter(Messaging.this, list);
                recyclerView.setAdapter(adapter);
                count=0;
                getMessagesSingleMessages();

                if (list.isEmpty()){
                    count = 1;
                }
                dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismiss();
            }
        });

    }



    //    getting messages
    private void getMessagesStartAt() {

        show();
        start_at_count = 0;
        Query query = databaseReference_CHATS.child(chat_key).orderByKey().startAt(getIntent().getStringExtra("start_at"));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String chat_push_key, message, senderUID, receiverUID, status, address, longitude, latitude;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    if (snapshot.getChildrenCount() == 1){
                        count = 0;
                        getMessagesSingleMessages();
                        dismiss();
                        return;
                    }

                    if (start_at_count == 0){
                        start_at_count = 1;
                        continue;
                    }

                    if (TextUtils.equals(dataSnapshot.child("senderUID").getValue(String.class), match_uid)){
                        try{
                            databaseReference_CHATS.child(chat_key).child(dataSnapshot.getKey()).child("status").setValue("seen");
                            databaseReference_CHATS.child(chat_key).child(dataSnapshot.getKey()).child("notification_status").setValue("seen");
                        }
                        catch (NullPointerException ignored){

                        }
                    }

                    DataForRecyclerView dataForRecyclerView = new DataForRecyclerView();

                    chat_push_key = dataSnapshot.getKey();
                    message = dataSnapshot.child("message").getValue(String.class);
                    senderUID = dataSnapshot.child("senderUID").getValue(String.class);
                    receiverUID = dataSnapshot.child("receiverUID").getValue(String.class);
                    status = dataSnapshot.child("status").getValue(String.class);
                    address = dataSnapshot.child("address").getValue(String.class);
                    latitude = dataSnapshot.child("latitude").getValue(String.class);
                    longitude = dataSnapshot.child("longitude").getValue(String.class);

                    dataForRecyclerView.setMessage(message);
                    dataForRecyclerView.setMy_uid(senderUID);
                    dataForRecyclerView.setMatch_UID(receiverUID);
                    dataForRecyclerView.setChat_key(chat_key);
                    dataForRecyclerView.setPush_key(chat_push_key);
                    dataForRecyclerView.setStatus(status);
                    dataForRecyclerView.setAddress(address);
                    dataForRecyclerView.setLatitude(latitude);
                    dataForRecyclerView.setLongitude(longitude);
                    list.add(dataForRecyclerView);
                }


                adapter = new MessagingRecyclerViewAdapter(Messaging.this, list);
                recyclerView.setAdapter(adapter);
                count=0;
                getMessagesSingleMessages();
                dismiss();

                if (list.isEmpty()){
                    count = 1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //    getting messages
    private void getMessagesSingleMessages() {
      mListener =   databaseReference_CHATS.child(chat_key).orderByKey().limitToLast(1)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        if (count==0){
                            count=1;
                            return;
                        }

                        String chat_push_key, message, senderUID, receiverUID, status, address, longitude, latitude;

                        if (TextUtils.equals(dataSnapshot.child("senderUID").getValue(String.class), match_uid)){
                           try{
                               databaseReference_CHATS.child(chat_key).child(dataSnapshot.getKey()).child("status").setValue("seen");
                               databaseReference_CHATS.child(chat_key).child(dataSnapshot.getKey()).child("notification_status").setValue("seen");
                           }
                           catch (NullPointerException ignored){

                           }
                        }

                        DataForRecyclerView dataForRecyclerView = new DataForRecyclerView();

                        chat_push_key = dataSnapshot.getKey();
                        message = dataSnapshot.child("message").getValue(String.class);
                        senderUID = dataSnapshot.child("senderUID").getValue(String.class);
                        receiverUID = dataSnapshot.child("receiverUID").getValue(String.class);
                        status = dataSnapshot.child("status").getValue(String.class);
                        address = dataSnapshot.child("address").getValue(String.class);
                        latitude = dataSnapshot.child("latitude").getValue(String.class);
                        longitude = dataSnapshot.child("longitude").getValue(String.class);

                        dataForRecyclerView.setMessage(message);
                        dataForRecyclerView.setMy_uid(senderUID);
                        dataForRecyclerView.setMatch_UID(receiverUID);
                        dataForRecyclerView.setChat_key(chat_key);
                        dataForRecyclerView.setPush_key(chat_push_key);
                        dataForRecyclerView.setStatus(status);
                        dataForRecyclerView.setAddress(address);
                        dataForRecyclerView.setLatitude(latitude);
                        dataForRecyclerView.setLongitude(longitude);

                        list.add(dataForRecyclerView);
                        adapter = new MessagingRecyclerViewAdapter(Messaging.this, list);
                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (TextUtils.equals(dataSnapshot.child("senderUID").getValue(String.class), match_uid)){
                            try{
                                databaseReference_CHATS.child(chat_key).child(dataSnapshot.getKey()).child("status").setValue("seen");
                            }
                            catch (NullPointerException ignored){

                            }
                        }

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    //    set name and image
    private void setNameAndImage() {
        match_name_tv.setText(getIntent().getStringExtra("name"));

        Glide.with(Messaging.this)
                .load(getIntent().getStringExtra("image_url"))
                .apply(new RequestOptions()
                        .error(R.drawable.ic_placeholder_profile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(match_image_iv);
    }


    private void sendMessage() {
        String message = message_et.getText().toString().trim();
        if (TextUtils.isEmpty(message)){
            return;
        }

        String push_key = databaseReference_CHATS.push().getKey();

        databaseReference_USER_CHAT_RECORD.child(uid).child(match_uid).child("delete_status").setValue(null);
        databaseReference_USER_CHAT_RECORD.child(uid).child(match_uid).child("timestamp").setValue(push_key);
        databaseReference_USER_CHAT_RECORD.child(match_uid).child(uid).child("delete_status").setValue(null);
        databaseReference_USER_CHAT_RECORD.child(match_uid).child(uid).child("timestamp").setValue(push_key);
        databaseReference_LAST_MESSAGE_STATUS.child(chat_key).child(match_uid).setValue("sent");
        databaseReference_DASHBOARD_STATUS.child(match_uid).setValue("unseen");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("senderUID", uid);
        hashMap.put("receiverUID", match_uid);
        hashMap.put("message_date_time", ServerValue.TIMESTAMP);
        hashMap.put("message", message);

        databaseReference_CHATS.child(chat_key).child(push_key).setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        message_et.setText(null);
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }

    private void sendMapLocation() {

        MySharedPrefs mySharedPrefs = new MySharedPrefs(this);
        String address = mySharedPrefs.getAddress();
        String latitude = mySharedPrefs.getLatitude();
        String longitude = mySharedPrefs.getLongitude();

        String push_key = databaseReference_CHATS.push().getKey();

        databaseReference_USER_CHAT_RECORD.child(uid).child(match_uid).child("timestamp").setValue(push_key);
        databaseReference_USER_CHAT_RECORD.child(match_uid).child(uid).child("timestamp").setValue(push_key);
        databaseReference_LAST_MESSAGE_STATUS.child(chat_key).child(match_uid).setValue("sent");
        databaseReference_DASHBOARD_STATUS.child(match_uid).setValue("unseen");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("senderUID", uid);
        hashMap.put("receiverUID", match_uid);
        hashMap.put("message_date_time", ServerValue.TIMESTAMP);
        hashMap.put("address", address);
        hashMap.put("latitude", latitude);
        hashMap.put("longitude", longitude);

        try{
            databaseReference_CHATS.child(chat_key).child(push_key).setValue(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // getMessagesSingleMessages();
                            message_et.setText(null);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        } catch (Exception ignored){}

    }

    //    changing status bar color
    private void changeStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Messaging.this,R.color.crispyBlue));
    }
    //    changing status bar color



//    on click
    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.ac_send_message){
            new CheckNetworkConnection(Messaging.this, new CheckNetworkConnection.OnConnectionCallback() {
                @Override
                public void onConnectionSuccess() {
                    try{
                        sendMessage();
                    }
                    catch (Exception ignored){
                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(Messaging.this);
                        showToastyMessage.warningMessage("message not sent, something went wrong");
                    }

                }
                @Override
                public void onConnectionFail(String msg) {
                    ShowToastyMessage showToastyMessage = new ShowToastyMessage(Messaging.this);
                    showToastyMessage.warningMessage("message not sent, no internet connection");
                }
            }).execute();

        }
        else if (id == R.id.ac_back_ib){
            onBackPressed();
        }

    }
    //    on click

    public void onStop(){
        super.onStop();
     //   list.clear();
      //  recyclerView.setAdapter(null);
        MySharedPrefs mySharedPrefs = new MySharedPrefs(Messaging.this);
        mySharedPrefs.setMessagingActivity("inactive");
        mySharedPrefs.setDashboardStatus(false);
    }

    public void onDestroy(){
        super.onDestroy();
        try{
            databaseReference_CHATS.child(chat_key).removeEventListener(mListener);
        }
        catch (Exception ignored){

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try{
         //   databaseReference_CHATS.child(chat_key).removeEventListener(mListener);
            databaseReference_LAST_MESSAGE_STATUS.child(chat_key).child(uid).setValue("seen"); // i have seen your message
        }
        catch (Exception ignored){

        }

    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation, R.anim.exit);
    }

    private void show(){
        spinKitView.setVisibility(View.VISIBLE);
    }
    private void dismiss(){
        spinKitView.setVisibility(View.GONE);
    }

//    end
}
