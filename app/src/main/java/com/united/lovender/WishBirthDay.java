package com.united.lovender;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class WishBirthDay extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_wish_birth_day);

        TextView name = findViewById(R.id.wb_name);

        name.setText("Happy Birthday\n"+ getIntent().getStringExtra("name"));

        MySharedPrefs mySharedPrefs = new MySharedPrefs(WishBirthDay.this);
        mySharedPrefs.setWishBirthday("true", getIntent().getStringExtra("year"));
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation, R.anim.exit);
    }

}
