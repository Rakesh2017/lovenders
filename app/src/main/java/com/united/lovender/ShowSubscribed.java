package com.united.lovender;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mehdi.sakout.fancybuttons.FancyButton;

public class ShowSubscribed extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_show_subscribed);

        ImageView imageView = findViewById(R.id.ss_img);
        FancyButton button = findViewById(R.id.ss_btn);
        TextView textView = findViewById(R.id.ss_text2);


        int plan = getIntent().getIntExtra("plan", 0);

        if (plan == 1){
            imageView.setBackground(getResources().getDrawable(R.drawable.basic_logo));
            textView.setText("Enjoy using all the paid \nservices of Lovenders for complete 1 month");
        } else if (plan == 2){
            imageView.setBackground(getResources().getDrawable(R.drawable.premium_logo));
            textView.setText("Enjoy using all the paid \nservices of Lovenders for complete 3 months");
        } else if (plan == 3){
            imageView.setBackground(getResources().getDrawable(R.drawable.gold_logo));
            textView.setText("Enjoy using all the paid \nservices of Lovenders for complete 6 months");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowSubscribed.this, DashBoard.class));
                ShowSubscribed.this.finish();
            }
        });

    }
}
