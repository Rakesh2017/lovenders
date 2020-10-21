package com.united.lovender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import java.util.ArrayList;
import java.util.List;

public class ShowUserImagesFullScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<DataForRecyclerView> list = new ArrayList<>();
    private TextView name_tv, about_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_up);
        setContentView(R.layout.activity_show_user_images_full_screen);

        SlidrConfig config = new SlidrConfig
                                .Builder()
                                .position(SlidrPosition.BOTTOM)
                                .build();

        Slidr.attach(this, config);

        name_tv = findViewById(R.id.fs_name_tv);
        about_tv = findViewById(R.id.fs_about_tv);
        recyclerView = findViewById(R.id.fs_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        String image1, image2, image3, image4, image5, name, about;
        int adapter_position;
        image1 = getIntent().getStringExtra("image1");
        image2 = getIntent().getStringExtra("image2");
        image3 = getIntent().getStringExtra("image3");
        image4 = getIntent().getStringExtra("image4");
        image5 = getIntent().getStringExtra("image5");
        name = getIntent().getStringExtra("name");
        about = getIntent().getStringExtra("about");

        adapter_position = getIntent().getIntExtra("adapter_position", 1);
        setNameAndProfileImage(image1, name, about);
        setImages(image1, image2, image3, image4, image5, adapter_position);

    }



    private void setNameAndProfileImage(String image1, String name, String about) {
        name_tv.setText(name);
        about_tv.setText(about);
        if (TextUtils.isEmpty(about) || TextUtils.equals(about, "")) about_tv.setVisibility(View.GONE);
    }

    //    setting images
    private void setImages(String image1, String image2, String image3, String image4, String image5, int adapter_position){
        //                                        image 1
        DataForRecyclerView dataForRecyclerView = new DataForRecyclerView();
        dataForRecyclerView.setImage_url(image1);
        dataForRecyclerView.setPage_number("1");
        list.add(dataForRecyclerView);

//                                    image 2
        if (!TextUtils.isEmpty(image2)){
            DataForRecyclerView dataForRecyclerView2 = new DataForRecyclerView();
            dataForRecyclerView2.setPage_number("2");
            dataForRecyclerView2.setImage_url(image2);
            list.add(dataForRecyclerView2);
        }

//                                    image 3
        if (!TextUtils.isEmpty(image3)){
            DataForRecyclerView dataForRecyclerView3 = new DataForRecyclerView();
            dataForRecyclerView3.setPage_number("3");
            dataForRecyclerView3.setImage_url(image3);
            list.add(dataForRecyclerView3);
        }

//                                    image 4
        if (!TextUtils.isEmpty(image4)){
            DataForRecyclerView dataForRecyclerView4 = new DataForRecyclerView();
            dataForRecyclerView4.setPage_number("4");
            dataForRecyclerView4.setImage_url(image4);
            list.add(dataForRecyclerView4);
        }

//                                    image 5
        if (!TextUtils.isEmpty(image5)){
            DataForRecyclerView dataForRecyclerView5 = new DataForRecyclerView();
            dataForRecyclerView5.setPage_number("5");
            dataForRecyclerView5.setImage_url(image5);
            list.add(dataForRecyclerView5);
        }

        RecyclerView.Adapter adapter = new UserImagesFullScreenRecylerViewAdapter(ShowUserImagesFullScreen.this, list);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter_position);
        adapter.notifyDataSetChanged();
    }

    //    on back pressed
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_up_custom);
    }
    //    on back pressed

}
