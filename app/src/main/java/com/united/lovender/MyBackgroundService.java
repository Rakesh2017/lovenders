package com.united.lovender;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class MyBackgroundService extends JobService {

    private JobParameters params;
    private myTask doThisTask;

    private Context context;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user_chat_record");
    private DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("chats");



    @Override
    public boolean onStartJob(JobParameters params) {
        this.context = this;
        this.params = params;
        doThisTask = new myTask();
        doThisTask.execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (doThisTask != null)
            doThisTask.cancel(true);
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    private class myTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {

                    final MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                    if(isNetworkAvailable()){
                        return;
                    }

                    final String uid = mySharedPrefs.getUid();

                    if (TextUtils.isEmpty(uid)){
                        return;
                    }


                    databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String status = mySharedPrefs.getMessagingActivityStatus();

                            if (TextUtils.equals(status, "active")){
                                return;
                            }

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                if (!TextUtils.isEmpty(snapshot.getKey())){
                                    getLastMessageAndNotify(snapshot.getKey(), uid);
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    // Do something here
                   // stopSelf();
                }

                //        getting notification
                private void getLastMessageAndNotify(final String match_uid, final String uid) {
                    final String chat_key;
                    if (match_uid.compareTo(uid) < 0){
                        chat_key = match_uid+uid; // keep match uid in front
                    }
                    else chat_key = uid+match_uid;

                    Query query = databaseReference1.child(chat_key).orderByKey().limitToLast(1);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()){  // for

                                if (TextUtils.equals(snapshot.child("receiverUID").getValue(String.class), uid) && !TextUtils.equals(snapshot.child("status").getValue(String.class), "seen")
                                        && !snapshot.hasChild("notification_status")){

                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        OreoAndAboveNotification(match_uid, snapshot);
                                    }

                                    else {
                                        belowOreoNotification(match_uid, snapshot);
                                    }

                                    if (TextUtils.equals(snapshot.child("receiverUID").getValue(String.class), uid)){
                                        databaseReference1.child(chat_key).child(snapshot.getKey()).child("notification_status").setValue("seen");
                                    }

                                } // if

                            } // for

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

//                below oreo notification
                private void belowOreoNotification(String match_uid, final DataSnapshot snapshot) {

                    String message = snapshot.child("message").getValue(String.class);
                    if (TextUtils.isEmpty(message)){
                        message = snapshot.child("address").getValue(String.class);
                    }

                    if (TextUtils.isEmpty(message)){
                        return;
                    }

                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    JSONObject jsonObject = new JSONObject();

                    try {

                        jsonObject.put("auth", "api!luv@13_9002");
                        jsonObject.put("request", "GetProfileName");
                        jsonObject.put("UID", match_uid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String url = getString(R.string.api_name);

                    final String finalMessage = message;
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        final int code = response.getInt("code");

                                        // Toast.makeText(Login.this, ""+response.toString(), Toast.LENGTH_SHORT).show();
                                        if (code == 1313){
                                            String result = response.getString("result");

                                            JSONArray jsonArray = new JSONArray(result);

                                            JSONObject object = jsonArray.getJSONObject(0);
                                            String match_name = object.getString("Name");

                                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                                            Intent showIntent = new Intent(context, DashBoard.class);
                                            PendingIntent pIntent = PendingIntent.getActivity(context
                                                    , 0, showIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                            PugNotification.with(context)
                                                    .load()
                                                    .title(match_name)
                                                    .sound(soundUri)
                                                    .smallIcon(R.drawable.app_logo_300px)
                                                    .autoCancel(true)
                                                    .flags(Notification.FLAG_AUTO_CANCEL)
                                                    .bigTextStyle(""+ finalMessage)
                                                    .click(pIntent)
                                                    .simple()
                                                    .build();

                                            v.vibrate(300);

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
                //                below oreo notification


                //                oreo and above notifications
                private void OreoAndAboveNotification(final String match_uid, final DataSnapshot snapshot) {

                    String message = snapshot.child("message").getValue(String.class);
                    if (TextUtils.isEmpty(message)){
                        message = snapshot.child("address").getValue(String.class);
                    }

                    if (TextUtils.isEmpty(message)){
                        return;
                    }

                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    JSONObject jsonObject = new JSONObject();

                    try {

                        jsonObject.put("auth", "api!luv@13_9002");
                        jsonObject.put("request", "GetProfileName");
                        jsonObject.put("UID", match_uid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String url = getString(R.string.api_name);

                    final String finalMessage = message;
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        final int code = response.getInt("code");

                                        // Toast.makeText(Login.this, ""+response.toString(), Toast.LENGTH_SHORT).show();
                                        if (code == 1313){
                                            String result = response.getString("result");

                                            JSONArray jsonArray = new JSONArray(result);

                                            JSONObject object = jsonArray.getJSONObject(0);
                                            String match_name = object.getString("Name");
                                            String profile_image = object.getString("ProfileImage");

                                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                                            Intent showIntent = new Intent(context, DashBoard.class);
                                            showIntent.putExtra("match_uid", match_uid);
                                            PendingIntent pIntent = PendingIntent.getActivity(context
                                                    , 0, showIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                                            CharSequence name = "Lovender";// The user-visible name of the channel.
                                            int importance = NotificationManager.IMPORTANCE_HIGH;

                                            NotificationChannel mChannel = new NotificationChannel(match_name, name, importance);

                                            // Create a notification and set the notification channel.
                                            Notification notification = new Notification.Builder(context)
                                                    .setContentIntent(pIntent)
                                                    .setContentTitle(match_name)
                                                    .setContentText(""+ finalMessage)
                                                    .setSmallIcon(R.drawable.app_logo_300px)
                                                    .setChannelId(match_name)
                                                    .setSound(soundUri)
                                                    .build();

                                            NotificationManager mNotificationManager =
                                                    (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
                                            //  mNotificationManager.notify(1, notification);
                                            mNotificationManager.createNotificationChannel(mChannel);

                                            // Issue the notification.
                                            mNotificationManager.notify(0, notification);
                                            v.vibrate(300);

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
//                oreo and above notifications

            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            jobFinished(params, false);
            super.onPostExecute(aVoid);
        }
    }

    public void onDestroy(){
        super.onDestroy();
        try{
            scheduleJob();
        } catch (Exception ignore){

        }
    }


//    again start a service when current service is destroyed
    private void scheduleJob() {

        JobScheduler jobScheduler;
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            jobScheduler = (JobScheduler) (context).getSystemService(JOB_SCHEDULER_SERVICE);
            ComponentName componentName = new ComponentName((context), MyBackgroundService.class);
            JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                    .setPeriodic(1500)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();
            jobScheduler.schedule(jobInfo);
        }
        else {
            jobScheduler = (JobScheduler) (context).getSystemService(JOB_SCHEDULER_SERVICE);
            ComponentName componentName = new ComponentName((context), MyBackgroundService.class);
            JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                    .setMinimumLatency(1500)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();
            jobScheduler.schedule(jobInfo);
        }

    }
//    again start a service when current service is destroyed


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) (getApplication()).getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo == null || !activeNetworkInfo.isConnected();
    }

}
