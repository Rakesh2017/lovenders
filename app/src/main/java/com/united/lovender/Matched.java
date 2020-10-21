package com.united.lovender;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.varunest.sparkbutton.SparkButton;
import mehdi.sakout.fancybuttons.FancyButton;

public class Matched extends AppCompatActivity implements View.OnClickListener {

    private CircularImageView user_image_cv, match_image_cv;
    private TextView user_name_tv, match_name_tv;
    SparkButton sparkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched);

        user_image_cv = findViewById(R.id.am_user_image);
        match_image_cv = findViewById(R.id.am_match_image);
        FancyButton message_fb = findViewById(R.id.am_send_message_fb);
        FancyButton back_fb = findViewById(R.id.am_back_fb);
        user_name_tv = findViewById(R.id.am_user_name_tv);
        match_name_tv = findViewById(R.id.am_match_name_tv);
        sparkButton = findViewById(R.id.spark_button);
        sparkButton.setEnabled(false);

        setImagesAndName();
        back_fb.setOnClickListener(this);
        message_fb.setOnClickListener(this);
        match_image_cv.setOnClickListener(this);

    }

//    on start
    public void onStart(){
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sparkButton.playAnimation();
                sparkButton.setChecked(true);
            }
        },1000);
    }
    //    on start

//    setting images
    private void setImagesAndName() {
        final MySharedPrefs mySharedPrefs = new MySharedPrefs(Matched.this);
        final String match_name = getIntent().getStringExtra("name");
        final String match_image = getIntent().getStringExtra("image_url");
        final String user_image = mySharedPrefs.getPhotoUrl();

        user_name_tv.setText(getString(R.string.you));
        match_name_tv.setText(match_name);


        Glide.with(Matched.this)
                .load(user_image)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_placeholder_profile)
                        .error(R.drawable.ic_placeholder_profile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(user_image_cv);

        Glide.with(Matched.this)
                .load(match_image)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_placeholder_profile)
                        .error(R.drawable.ic_placeholder_profile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(match_image_cv);
    }

    public void onBackPressed(){
        super.onBackPressed();
        Matched.this.finish();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out_down);
    }

//    on click
    @Override
    public void onClick(View v) {
        int id = v.getId();

//        back
        if (id == R.id.am_back_fb){
            onBackPressed();
        }

        else if (id == R.id.am_match_image){
            openMatchProfile();
        }

        else if (id == R.id.am_send_message_fb){
            openMessaging();
        }


    }
    //    on click


    private void openMessaging() {
        String match_uid = getIntent().getStringExtra("match_uid");
        MySharedPrefs mySharedPrefs = new MySharedPrefs(Matched.this);
        String uid = mySharedPrefs.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user_chat_record");

        databaseReference.child(uid).child(match_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intent intent = new Intent(Matched.this, Messaging.class);
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("image_url", getIntent().getStringExtra("image_url"));
                intent.putExtra("match_uid", getIntent().getStringExtra("match_uid"));
                intent.putExtra("start_at", dataSnapshot.child("start_at").getValue(String.class));
                startActivity(intent);
                Matched.this.finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void openMatchProfile() {
        Intent intent = new Intent(Matched.this, UserProfileDetails.class);
        intent.putExtra("UID", getIntent().getStringExtra("UID"));
        intent.putExtra("name",getIntent().getStringExtra("name"));
        intent.putExtra("age", getIntent().getStringExtra("age"));
        intent.putExtra("job_title",getIntent().getStringExtra("job_title"));
        intent.putExtra("education", getIntent().getStringExtra("education"));
        intent.putExtra("religion", getIntent().getStringExtra("religion"));
        intent.putExtra("height", getIntent().getStringExtra("height"));
        intent.putExtra("gender", getIntent().getStringExtra("gender"));
        intent.putExtra("about", getIntent().getStringExtra("about"));
        intent.putExtra("zodiac", getIntent().getStringExtra("zodiac"));
        intent.putExtra("company", getIntent().getStringExtra("company"));
        intent.putExtra("body_type", getIntent().getStringExtra("body_type"));
        intent.putExtra("swipe", getIntent().getStringExtra("swipe"));
        intent.putExtra("image_count", getIntent().getStringExtra("image_count"));
        intent.putExtra("image1", getIntent().getStringExtra("image1"));
        intent.putExtra("image2", getIntent().getStringExtra("image2"));
        intent.putExtra("image3", getIntent().getStringExtra("image3"));
        intent.putExtra("image4", getIntent().getStringExtra("image4"));
        intent.putExtra("image5", getIntent().getStringExtra("image5"));
        intent.putStringArrayListExtra("interests", getIntent().getStringArrayListExtra("interests"));
        startActivity(intent);
        Matched.this.finish();
    }


//    end
}
