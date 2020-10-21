package com.united.lovender;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kevalpatel2106.rulerpicker.RulerValuePicker;
import com.kevalpatel2106.rulerpicker.RulerValuePickerListener;
import com.suke.widget.SwitchButton;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity implements View.OnClickListener, CalendarDatePickerDialogFragment.OnDateSetListener {

    private TextView dob_tv;
    private TextView height_number_tv;
    private RulerValuePicker heightRanger;
    private SpinKitView loading;

    private SwitchButton switch_button_man, switch_button_woman;
    private EditText name_et, about_et, job_et, company_et, school_et;
    private TextView hashtags_tv;
    private MaterialSpinner religion_spinner, zodiac_spinner, body_type_spinner;
    private String[] religionsList = {"Ahmadiyya","Aladura","Amish","Anglicanism","Asatru","Assemblies of God","Atheism","Baha'i Faith","Baptists","Bon","Buddhism","Candomble","Cao Dai","Cathari","Catholicism","Charismatic movement","Chinese Religion","Christadelphians","Christian Science","Christianity","Church of God","Church of God in Christ","Church of Satan","Confucianism","Conservative Judaism","Deism","Donatism","Dragon Rouge","Druze","Eastern Orthodox Church","Eckankar","ELCA","Epicureanism","Evangelicalism","Falun Gong","Foursquare Church","Gnosticism","Greek Religion","Hare Krishna","Hasidism","Hellenic Reconstructionism","Hinduism","Illuminati","Intelligent Design","Islam","Jainism","Jehovah's Witnesses","Judaism","Kabbalah","Kemetic Reconstructionism","Lutheranism","Mahayana Buddhism","Mayan Religion","Methodism","Mithraism","Mormonism","Neopaganism","New Age","New Thought","Nichiren","Norse Religion","Olmec Religion","Oneness Pentecostalism","Orthodox Judaism","Pentecostalism","Presbyterianism","Priory of Sion","Protestantism","Pure Land Buddhism","Quakers","Rastafarianism","Reform Judaism","Rinzai Zen Buddhism","Roman Religion","Satanism","Scientology","Seventh-Day Adventism","Shaivism","Shi'a Islam","Shinto","Sikhism","Soto Zen Buddhism","Spiritualism","Stoicism","Sufism","Sunni Islam","Taoism","Tendai Buddhism","Theravada Buddhism","Tibetan Buddhism","Typhonian Order","Umbanda","Unification Church","Unitarian Universalism","Vaishnavism","Vajrayana Buddhism","Vedanta","Vineyard Churches","Voodoo","Westboro Baptist Church","Wicca","Worldwide Church of God","Yezidi","Zen","Zionism","Zoroastrianism","not prefer to say"};
    private String[] zodiacList = {"Aquarius", "Scorpio", "Pisces","Aries","Taurus","Gemini","Cancer","Leo","Virgo","Libra","Sagittarius","Capricorn","not prefer to say"};
    private String[] bodyList = {"Lean", "Athletic", "Muscular", "Fat", "Chubby", "Slim", "fit","not prefer to say"};
    private String name, job, about,company, school, religion="", gender="man", dob="07-05-1990"
            , height="170", zodiac="", body_type="";
    private RelativeLayout main_relative_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_edit_profile);


        ImageButton back_ib = findViewById(R.id.ep_back_ib);
        dob_tv = findViewById(R.id.ep_dob_tv);
     //   TextView age_number_tv = findViewById(R.id.ep_age_number_tv);
        height_number_tv = findViewById(R.id.ep_height_number_tv);
        heightRanger = findViewById(R.id.ep_height_range);
        switch_button_man = findViewById(R.id.ep_switch_button_men);
        switch_button_woman = findViewById(R.id.ep_switch_button_women);
        CardView hashtags_cv = findViewById(R.id.ep_hashtags_cv);
        name_et = findViewById(R.id.ep_name_et);
        about_et = findViewById(R.id.ep_about_et);
        job_et = findViewById(R.id.ep_job_title_et);
        company_et = findViewById(R.id.ep_company_et);
        school_et = findViewById(R.id.ep_school_et);
        religion_spinner = findViewById(R.id.ep_religion_spinner);
        zodiac_spinner = findViewById(R.id.ep_zodiac_spinner);
        body_type_spinner = findViewById(R.id.ep_body_type_spinner);
        Button update_btn = findViewById(R.id.ep_update_btn);
        hashtags_tv = findViewById(R.id.ep_hashtags_tv);
        main_relative_layout = findViewById(R.id.ep_main_relative_layout);

        loading = findViewById(R.id.ep_spin_kit);

        religion_spinner.setItems(religionsList);
        zodiac_spinner.setItems(zodiacList);
        body_type_spinner.setItems(bodyList);

        religion_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                religion = item.toString();
                if (TextUtils.equals(religion, "not prefer to say")){
                    religion = "";
                }
            }
        });
        zodiac_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                zodiac = item.toString();
                if (TextUtils.equals(zodiac, "not prefer to say")){
                    zodiac = "";
                }
            }
        });
        body_type_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                body_type = item.toString();
                if (TextUtils.equals(body_type, "not prefer to say")){
                    body_type = "";
                }
            }
        });


        selectHeight();
        selectIAmMan();
        selectIAmWoman();

        back_ib.setOnClickListener(this);
        dob_tv.setOnClickListener(this);
        hashtags_cv.setOnClickListener(this);
        update_btn.setOnClickListener(this);
    }

    public void onStart(){
        super.onStart();
        getDataFromSharedPrefs();
        //getDataFromDataBase();
    }
    public void onResume(){
        super.onResume();
        getDataFromSharedPrefs();
        main_relative_layout.requestFocus();
    }

//    getting data from shared prefs
    @SuppressLint("SetTextI18n")
    private void getDataFromSharedPrefs() {

        MySharedPrefs mySharedPrefs = new MySharedPrefs(EditProfile.this);
        HashMap<Integer, String> hashMap = mySharedPrefs.getEditProfile();
        String name1, about1, job_tile1, company1, school1, religion1
                , gender1, dob1, height1, interest1, zodiac1, body_type1;

        name1 = hashMap.get(0);
        about1 = hashMap.get(1);
        job_tile1 = hashMap.get(2);
        company1 = hashMap.get(3);
        school1 = hashMap.get(4);
        religion1 = hashMap.get(5);
        gender1 = hashMap.get(6);
        dob1 = hashMap.get(7);
        height1 = hashMap.get(8);
        interest1 = hashMap.get(9);
        zodiac1 = hashMap.get(10);
        body_type1 = hashMap.get(11);

        name_et.setText(name1);
        about_et.setText(about1);
        job_et.setText(job_tile1);
        company_et.setText(company1);
        school_et.setText(school1);
        if (!TextUtils.isEmpty(religion1)) {
            religion_spinner.setTextColor(getResources().getColor(R.color.topBarColor));
            religion_spinner.setText(religion1);
            religion = religion1;
        }
        if (!TextUtils.isEmpty(zodiac1)) {
            zodiac_spinner.setTextColor(getResources().getColor(R.color.topBarColor));
            zodiac_spinner.setText(zodiac1);
            zodiac = zodiac1;
        }
        if (!TextUtils.isEmpty(body_type1)) {
            body_type_spinner.setTextColor(getResources().getColor(R.color.topBarColor));
            body_type_spinner.setText(body_type1);
            body_type = body_type1;
        }
        hashtags_tv.setText(interest1);

        gender  = gender1;

        //setting gender
        if (TextUtils.equals(gender1, "man")){
            switch_button_man.setChecked(true);
            switch_button_woman.setChecked(false);
        }
        else if (TextUtils.equals(gender1, "woman")){
            switch_button_woman.setChecked(true);
            switch_button_man.setChecked(false);
        }

        //setting age and dob
        try{
            dob = dob1;
            assert dob1 != null;
            String cal_dab = dob1.substring(0, 2) + "-" + wordMonth(Integer.parseInt(dob1.substring(3, 5))) + "-" + dob1.substring(6, 10);
            dob_tv.setText(cal_dab);
        }
        catch (NullPointerException | StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        //        setting height
        try{
            assert height1 != null;
            Double value = Double.valueOf(height1);
            int feet = (int) Math.floor(value / 30.48);
            int inches = (int) Math.round((value / 2.54) - (feet * 12));
            if (inches == 12) inches = 11;
            height_number_tv.setText(height1+"cm/ "+feet+"'"+inches+"''");
            heightRanger.selectValue(Integer.parseInt(height1));
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    //    getting data from shared prefs


    //    I am woman
    private void selectIAmWoman() {
        switch_button_woman.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked){
                    gender = "woman";
                    switch_button_man.setChecked(false);
                }
                else {
                    switch_button_man.setChecked(true);
                }
            }
        });
    }

    //    i am man
    private void selectIAmMan() {
        switch_button_man.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked){
                    gender = "man";
                    switch_button_woman.setChecked(false);
                }
                else {
                    switch_button_woman.setChecked(true);
                }
            }
        });
    }

    //    select height
    private void selectHeight() {
        heightRanger.setValuePickerListener(new RulerValuePickerListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onValueChange(final int selectedValue) {
                Double value = (double) selectedValue;
                int feet = (int) Math.floor(value / 30.48);
                int inches = (int) Math.round((value / 2.54) - (feet * 12));
                if (inches == 12) inches = 11;
                height_number_tv.setText(selectedValue+"cm/ "+feet+"'"+inches+"''");
                height = String.valueOf(selectedValue);
                //Value changed and the user stopped scrolling the ruler.
                //Application can consider this value as final selected value.
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onIntermediateValueChange(final int selectedValue) {
                Double value = (double) selectedValue;
                int feet = (int) Math.floor(value / 30.48);
                int inches = (int) Math.round((value / 2.54) - (feet * 12));
                if (inches == 12) inches = 11;

//                String ouput = feet + "' " + inches + "\"";
//                String feet_inches  = String.valueOf(selectedValue / 30.48);
//                String feet = feet_inches.substring(0,1);
//                String inches = feet_inches.substring(2,3);
                height_number_tv.setText(selectedValue+"cm/ "+feet+"'"+inches+"''");

                //Value changed but the user is still scrolling the ruler.
                //This value is not final value. Application can utilize this value to display the current selected value.
            }
        });
    }
//    select height


    //    on click
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
//            back button
            case R.id.ep_back_ib:
                onBackPressed();
                break;
//                 date of birth
            case R.id.ep_dob_tv:
                try {
                    openDatePicker();
                }
                catch (Exception e){
                    //
                }

            break;
//            likings
            case R.id.ep_hashtags_cv:
                startActivity(new Intent(EditProfile.this, Likings.class));
                break;
//                update
            case R.id.ep_update_btn:
                show();
                new CheckNetworkConnection(EditProfile.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        hideSoftKeyboard();
                        setData();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        dismiss();
                        startActivity(new Intent(EditProfile.this, NoInternetActivity.class));

                    }
                }).execute();
                break;
        }//switch ends
    }
    //    on click

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

//    date picker
    private void openDatePicker() {

        MySharedPrefs mySharedPrefs = new MySharedPrefs(EditProfile.this);
        HashMap<Integer, String> hashMap = mySharedPrefs.getEditProfile();
        String get_dob;
        get_dob = hashMap.get(7);

        int d1=1, m1=1, y1=1990;
        try{
            assert get_dob != null;
            d1 = Integer.parseInt(get_dob.substring(0, 2));
            m1 = Integer.parseInt(get_dob.substring(3, 5));
            y1 = Integer.parseInt(get_dob.substring(6, 10));
        } catch (Exception ignored){}

        MonthAdapter.CalendarDay min = new MonthAdapter.CalendarDay();
        MonthAdapter.CalendarDay max = new MonthAdapter.CalendarDay();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18);

        min.setDay(1950,1,1);
        max.setDay(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(this)
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(y1, m1, d1)
                .setDateRange(min, max )
                .setThemeLight()
                .setDoneText("Confirm")
                .setCancelText("Cancel");
        cdp.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                int mo = monthOfYear+1;
                String day1, month1, year1;
                day1 = String.valueOf(dayOfMonth);
                month1 = String.valueOf(mo);
                year1 = String.valueOf(year);

                if (dayOfMonth < 10) day1 = "0"+dayOfMonth;
                if (mo < 10) month1 = "0"+mo;

                String selected_dob = day1+"-"+month1+"-"+year1;
                dob_tv.setText(day1+"-"+wordMonth(Integer.parseInt(month1))+"-"+year1);
                dob = selected_dob;

//                Calendar thatDay = Calendar.getInstance();
//                thatDay.set(Calendar.DAY_OF_MONTH,dayOfMonth);
//                thatDay.set(Calendar.MONTH, monthOfYear+1); // 0-11 so 1 less
//                thatDay.set(Calendar.YEAR, year);
//
//                Calendar today = Calendar.getInstance();
//                long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();
//                long years = diff / (31536000) / 1000;
              //  long years = (diff / 1000L * 60 * 60 * 24 * 365);
                //long years = diff * 60 * 60 * 60 * 12 * 365;
               // age_number_tv.setText(years+" years old");
            }
        });
        cdp.show(getSupportFragmentManager(), "fd");
    }
//    date picker

//    numeric month to word
    private String wordMonth(int month){
        switch (month){
            case 1: return "JAN";
            case 2: return "FEB";
            case 3: return "MAR";
            case 4: return "APR";
            case 5: return "MAY";
            case 6: return "JUN";
            case 7: return "JUL";
            case 8: return "AUG";
            case 9: return "SEP";
            case 10: return "OCT";
            case 11: return "NOV";
            case 12: return "DEC";
            default:return null;

        }
    }
//    numeric month to word



//    set data to database
    private void setData() {
        name = name_et.getText().toString().trim().replaceAll("\n", " ");
        about = about_et.getText().toString().trim().replaceAll("\n", " ");
        job = job_et.getText().toString().trim();
        company = company_et.getText().toString().trim();
        school = school_et.getText().toString().trim();
     //   religion = religion_spinner.getText().toString().trim();
       // dob = dob_tv.getText().toString().trim();

        if (checkValidations()){
            setDataToDataBase();
        }
        else dismiss();

    }
    //    set data to database

//    set data to database
    private void setDataToDataBase() {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();

        //        get user location
        UserCurrentLocation userCurrentLocation = new UserCurrentLocation(EditProfile.this);
        HashMap<String, String> locationHashMap;
        locationHashMap = userCurrentLocation.getUserCurrentLocation();
        String address = locationHashMap.get("address");

        MySharedPrefs mySharedPrefs = new MySharedPrefs(EditProfile.this);
        final String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "UpdateProfile");
            jsonObject.put("UID", uid);
            jsonObject.put("Name", name);
            jsonObject.put("About", about);
            jsonObject.put("JobTitle", job);
            jsonObject.put("Company", company);
            jsonObject.put("Education", school);
            jsonObject.put("Religion", religion);
            jsonObject.put("ZodiacSign", zodiac);
            jsonObject.put("BodyType", body_type);
            jsonObject.put("Gender", gender);
            jsonObject.put("Height", height);
            jsonObject.put("DOB", dob);
            jsonObject.put("Location", address);
        } catch (Exception e) {

            e.printStackTrace();
            dismiss();
        }
        String url = getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(EditProfile.this);
                        try {
                            final int code = response.getInt("code");
                            if (code == 1313){
                              //  showDialogBox.successMessageMaterialDialogs("", "Profile Updated");
                             //   showToastyMessage.successMessage("Profile updated");
                                showSuccessMessage();
                                MySharedPrefs mySharedPrefs = new MySharedPrefs(EditProfile.this);
                                mySharedPrefs.setEditProfileNameAndAbout(name, about);
                                setDataFromDataBase(name, about, job, company, school, religion
                                        , gender, dob, height, zodiac, body_type);
                                dismiss();
                            }
                            else {
                                showToastyMessage.errorMessage("Something went wrong");
                                dismiss();
                            }

                        } catch (JSONException e) {
                            showToastyMessage.errorMessage(e.toString());
                            dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismiss();
                   //     ShowToastyMessage showToastyMessage = new ShowToastyMessage(EditProfile.this);
                   //     showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }


                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }
    //    set data to database


//    show profile updated message
    private void showSuccessMessage() {
        FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(EditProfile.this)
                .setimageResource(R.drawable.ic_profile_updated)
                .setTextSubTitle("Your Profile is updated ")
                //.setBody("Profile updated")
                .setPositiveButtonText("Okay")
                .setPositiveColor(R.color.black)
                .setBackgroundColor(R.color.white)
                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                       dialog.dismiss();
                    }
                })
                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setCancelable(false)
                .build();
        alert.show();
    }
//    show profile updated message


    //    setting data from database
    private void setDataFromDataBase(String name, String about, String job, String company
            , String school, String religion, String gender, String dob, String height, String zodiac, String body_type) {

        MySharedPrefs mySharedPrefs = new MySharedPrefs(EditProfile.this);
        mySharedPrefs.setEditProfile(name, about, job, company, school, religion, gender, dob, height, zodiac, body_type);
        //getDataFromSharedPrefs();
    }
    //    setting data from database

//    validations
    private boolean checkValidations() {
        ShowToastyMessage showToastyMessage = new ShowToastyMessage(EditProfile.this);
        if (TextUtils.isEmpty(name)){
            showToastyMessage.errorMessage("enter name !");
            return false;
        }
        return true;
    }
//    validations

    public void onBackPressed(){
            super.onBackPressed();
            overridePendingTransition(R.anim.no_animation, R.anim.exit);

    }

    private void show(){
        loading.setVisibility(View.VISIBLE);
    }
    private void dismiss(){
        loading.setVisibility(View.GONE);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {

    }

//    end
}
