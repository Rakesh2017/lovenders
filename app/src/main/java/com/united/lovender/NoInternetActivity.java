package com.united.lovender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ybq.android.spinkit.SpinKitView;

import mehdi.sakout.fancybuttons.FancyButton;

public class NoInternetActivity extends AppCompatActivity {

    private FancyButton try_again_btn;
    private SpinKitView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        try_again_btn = findViewById(R.id.id_try_again_btn);
        loading = findViewById(R.id.id_spin_kit);

        try_again_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                new CheckNetworkConnection(NoInternetActivity.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        NoInternetActivity.this.finish();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        YoYo.with(Techniques.Shake)
                                .duration(500)
                                .repeat(1)
                                .playOn(try_again_btn);
                        loading.setVisibility(View.GONE);
                    }
                }).execute();
            }
        });

    }

    public void onStart(){
        super.onStart();

    }
}
