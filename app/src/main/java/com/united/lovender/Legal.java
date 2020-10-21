package com.united.lovender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

public class Legal extends AppCompatActivity {

    private TextView title_tv;
    private HtmlTextView htmlTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_legal);

        title_tv = findViewById(R.id.le_header_ib);
        htmlTextView = findViewById(R.id.le_html_text);
        ImageButton back_ib = findViewById(R.id.le_back_ib);



        String title, content;
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");

        setData(title, content);

        back_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

//    set data
    private void setData(String title, String content) {
        title_tv.setText(title);
        htmlTextView.setHtml(content,
                new HtmlHttpImageGetter(htmlTextView));
    }
//    aet data


//    back pressed
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation, R.anim.exit);
    }
//    back pressed

}
