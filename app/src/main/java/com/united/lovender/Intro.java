package com.united.lovender;

import android.content.res.ColorStateList;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class Intro extends AppCompatActivity {

    private CustomViewPager viewPager;
    private ImageView heart1, heart2, heart3, heart4;
    private TextView toolbar_header_tv;
    private ImageButton back_ib;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        TabLayout tabLayout = findViewById(R.id.ai_tabLayout);
        heart1 =  findViewById(R.id.ai_heart1);
        heart2 =  findViewById(R.id.ai_heart2);
        heart3 =  findViewById(R.id.ai_heart3);
        heart4 =  findViewById(R.id.ai_heart4);
        back_ib =  findViewById(R.id.ai_back_ib);
        toolbar =  findViewById(R.id.ai_toolbar);

        toolbar_header_tv =  findViewById(R.id.ai_toolbar_header_tv);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab().setText("LOVENDERS"));
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(ColorStateList.valueOf(getResources().getColor(R.color.gray)));

        //Initializing viewPager
        viewPager =  findViewById(R.id.ai_pager);


        //Creating our pager adapter
        IntroPager adapter = new IntroPager(getSupportFragmentManager(), 4);

        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        heart1.setBackground(getResources().getDrawable(R.drawable.ic_heart_white));
        changeSelectedTabIcon();

    }

    private void changeSelectedTabIcon() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                //  Toast.makeText(Intro.this, "e "+position, Toast.LENGTH_SHORT).show();
                back_ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            setCurrentItem(position-1, true);
                        }
                        catch (Exception ignored){
                        }

                    }
                });
                if (position == 0){
                    heart1.setBackground(getResources().getDrawable(R.drawable.ic_heart_white));
                    heart2.setBackground(getResources().getDrawable(R.drawable.ic_heart_off));
                    heart3.setBackground(getResources().getDrawable(R.drawable.ic_heart_off));
                    heart4.setBackground(getResources().getDrawable(R.drawable.ic_heart_off));
                    toolbar_header_tv.setText(getString(R.string.profile));
                }
                else if (position == 1){
                    heart1.setBackground(getResources().getDrawable(R.drawable.ic_heart_off));
                    heart2.setBackground(getResources().getDrawable(R.drawable.ic_heart_white));
                    heart3.setBackground(getResources().getDrawable(R.drawable.ic_heart_off));
                    heart4.setBackground(getResources().getDrawable(R.drawable.ic_heart_off));
                    toolbar_header_tv.setText(getString(R.string.personal_details));
                }
                else if (position == 2){
                    heart1.setBackground(getResources().getDrawable(R.drawable.ic_heart_off));
                    heart2.setBackground(getResources().getDrawable(R.drawable.ic_heart_off));
                    heart3.setBackground(getResources().getDrawable(R.drawable.ic_heart_white));
                    heart4.setBackground(getResources().getDrawable(R.drawable.ic_heart_off));
                    toolbar_header_tv.setText(getString(R.string.general_details));
                }
                else if (position == 3){
                    heart1.setBackground(getResources().getDrawable(R.drawable.ic_heart_off));
                    heart2.setBackground(getResources().getDrawable(R.drawable.ic_heart_off));
                    heart3.setBackground(getResources().getDrawable(R.drawable.ic_heart_off));
                    heart4.setBackground(getResources().getDrawable(R.drawable.ic_heart_white));
                    toolbar_header_tv.setText("");
                    toolbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setCurrentItem (int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }

    public void enablePaging (Boolean value) {
        viewPager.setPagingEnabled(value);
    }

//    end
}
