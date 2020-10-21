package com.united.lovender;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //if the message contains data payload
        //It is a map of custom keyvalues
        //we can read it easily
        if(remoteMessage.getData().size() > 0){

            //handle the data message here
        }


        //Setting up Notification channels for android O and above
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            setupChannels();
//        }
//        int notificationId = new Random().nextInt(60000);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "sd")
//                .setSmallIcon(R.drawable.app_logo_300px)  //a resource for your custom small icon
//                .setColor(getResources().getColor(R.color.white))
//                .setContentTitle(remoteMessage.getData().get("title")) //the "title" value you sent in your notification
//                .setContentText(remoteMessage.getData().get("message")) //ditto
//                .setAutoCancel(true)  //dismisses the notification on click
//                .setSound(defaultSoundUri);
//
//        notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
//




        //then here we can use the title and body to build a notification
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = "dfd";
        String adminChannelDescription = "dsfgd";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel("sd", adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

}