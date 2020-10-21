package com.united.lovender;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class DashBoard extends AppCompatActivity{

    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private ImageButton tabCustomImageLeft, tabCustomImageRight, tabCustomImageCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_dash_board);

        tabLayout =  findViewById(R.id.db_tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
     //   tabLayout.setTabTextColors(ColorStateList.valueOf(getResources().getColor(R.color.gray)));

        //Initializing viewPager
        viewPager =  findViewById(R.id.db_pager);


        clearNotification();
        scheduleJob();


        //Creating our pager adapter
        DashBoardPager adapter = new DashBoardPager(getSupportFragmentManager(), tabLayout.getTabCount());


        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        @SuppressLint("InflateParams") LinearLayout tabLinearLayoutRight = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_right, null);
        tabCustomImageRight = tabLinearLayoutRight.findViewById(R.id.ctr_tabContent);

        @SuppressLint("InflateParams") LinearLayout tabLinearLayoutCenter = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_center, null);
        tabCustomImageCenter = tabLinearLayoutCenter.findViewById(R.id.ctc_tabContent);

        @SuppressLint("InflateParams") LinearLayout tabLinearLayoutLeft = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_left, null);
        tabCustomImageLeft = tabLinearLayoutLeft.findViewById(R.id.ctl_tabContent);

        checkDashBoardStatus();

        try{
            viewPager.setPagingEnabled(false);
            tabLayout.getTabAt(1).select();

            tabCustomImageCenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabLayout.getTabAt(1).select();
                }
            });

            //        scroll to edit profile
            tabCustomImageLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabLayout.getTabAt(0).select();
                }
            });

            //        scroll to chatting
            tabCustomImageRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabLayout.getTabAt(2).select();
                }
            });

            changeSelectedTabIcon();
            setCustomTabs();


        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        getFireBaseAccessToken();

    }

//    check dash board status
    private void checkDashBoardStatus() {
        final MySharedPrefs mySharedPrefs = new MySharedPrefs(DashBoard.this);
        final String uid = mySharedPrefs.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("dashboard_status");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (TextUtils.equals(dataSnapshot.child(uid).getValue(String.class), "unseen")){
                    tabCustomImageRight.setBackground(getResources().getDrawable(R.drawable.ic_notification));
                    mySharedPrefs.setDashboardStatus(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//    check dash board status



    //    firebase access token
    private void getFireBaseAccessToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                         //   Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID toke

                    }
                });

    }
//    firebase access token



    private void clearNotification() {
        NotificationManager notifManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
    }

    private void scheduleJob() {

        JobScheduler jobScheduler;
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            jobScheduler = (JobScheduler) (DashBoard.this).getSystemService(JOB_SCHEDULER_SERVICE);
            ComponentName componentName = new ComponentName((DashBoard.this), MyBackgroundService.class);
            JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                    .setPeriodic(1500)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();
            jobScheduler.schedule(jobInfo);
        }
        else {
            jobScheduler = (JobScheduler) (DashBoard.this).getSystemService(JOB_SCHEDULER_SERVICE);
            ComponentName componentName = new ComponentName((DashBoard.this), MyBackgroundService.class);
            JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                    .setMinimumLatency(1500)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();
            jobScheduler.schedule(jobInfo);
        }

    }


//   setting custom tabs
    private void setCustomTabs() {
        tabCustomImageLeft.setBackground(getResources().getDrawable(R.drawable.ic_edit_profile_gray));
        tabLayout.getTabAt(0).setCustomView(tabCustomImageLeft);

      //  tabLayout.setTabTextColors(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        tabCustomImageCenter.setBackground(getResources().getDrawable(R.drawable.app_logo_300px));
        tabLayout.getTabAt(1).setCustomView(tabCustomImageCenter);

        tabCustomImageRight.setBackground(getResources().getDrawable(R.drawable.ic_paper_plane_white));
        tabLayout.getTabAt(2).setCustomView(tabCustomImageRight);
    }

    //    change icon of selected tab
    private void changeSelectedTabIcon() {
        final MySharedPrefs mySharedPrefs = new MySharedPrefs(DashBoard.this);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                // changing selected tab icon
                final int position = tab.getPosition();
                if (position == 0){
                    viewPager.setPagingEnabled(true);
                    tabCustomImageLeft.setBackground(getResources().getDrawable(R.drawable.ic_edit_profile_red));
                    tabLayout.getTabAt(0).setCustomView(tabCustomImageLeft);

                 //   tabLayout.setTabTextColors(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                    tabCustomImageCenter.setBackground(getResources().getDrawable(R.drawable.app_logo_bw));
                    tabLayout.getTabAt(1).setCustomView(tabCustomImageCenter);

                    if (mySharedPrefs.getDashboardStatus()){
                        tabCustomImageRight.setBackground(getResources().getDrawable(R.drawable.ic_notification));
                    }
                    else {
                        tabCustomImageRight.setBackground(getResources().getDrawable(R.drawable.ic_paper_plane_white));
                    }
                    tabLayout.getTabAt(2).setCustomView(tabCustomImageRight);
                }
                else if (position == 1){
                    viewPager.setPagingEnabled(false);
                    tabCustomImageLeft.setBackground(getResources().getDrawable(R.drawable.ic_edit_profile_gray));
                    tabLayout.getTabAt(0).setCustomView(tabCustomImageLeft);

                    //tabLayout.setTabTextColors(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    tabCustomImageCenter.setBackground(getResources().getDrawable(R.drawable.app_logo_300px));
                    tabLayout.getTabAt(1).setCustomView(tabCustomImageCenter);

                    if (mySharedPrefs.getDashboardStatus()){
                        tabCustomImageRight.setBackground(getResources().getDrawable(R.drawable.ic_notification));
                    }
                    else {
                        tabCustomImageRight.setBackground(getResources().getDrawable(R.drawable.ic_paper_plane_white));
                    }
                    tabLayout.getTabAt(2).setCustomView(tabCustomImageRight);
                }
                else if (position == 2){
                    viewPager.setPagingEnabled(true);
                    tabCustomImageLeft.setBackground(getResources().getDrawable(R.drawable.ic_edit_profile_gray));
                    tabLayout.getTabAt(0).setCustomView(tabCustomImageLeft);

                   // tabLayout.setTabTextColors(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                    tabCustomImageCenter.setBackground(getResources().getDrawable(R.drawable.app_logo_bw));
                    tabLayout.getTabAt(1).setCustomView(tabCustomImageCenter);

                    tabCustomImageRight.setBackground(getResources().getDrawable(R.drawable.ic_paper_plane_red));
                    tabLayout.getTabAt(2).setCustomView(tabCustomImageRight);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Nothing needed here.
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Nothing needed here.
            }
        });
    }

//    on back pressed
    public void onBackPressed(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("our_plans");
        if(fragment != null && fragment.isVisible()){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        } else {
            askPermissionToExit();
        }
    }
    //    on back pressed


    private void askPermissionToExit(){
        FancyAlertDialog.Builder alert1 = new FancyAlertDialog.Builder(DashBoard.this)
                //.setimageResource(R.drawable.app_logo_300px)
                .setTextTitle("Exit")
                .setTitleColor(R.color.appColor)
                .setBody("Are you sure to exit?")
                .setPositiveColor(R.color.red)
                .setBackgroundColor(R.color.white)
                .setNegativeColor(R.color.black90)
                .setNegativeButtonText("Cancel")
                .setPositiveButtonText("Yes")
                .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        DashBoard.this.finish();
                        //finishAffinity();
                    }
                })
                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setCancelable(true)
                .build();
        alert1.show();
    }

    public void onDestroy() {
        super.onDestroy();
    }


//    ends
}
