package com.united.lovender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class MySharedPrefs {

    private Context context;
    private SharedPreferences sharedPreferences;

//    login
    private final static String LOGIN = "login";
    private final static String LOGGED_IN_OR_NOT = "logged_in";
    private final static String LOG_IN_MODE = "log_in_mode";
    private final static String USER_NAME = "user_name";
    private final static String EMAIL = "email";
    private final static String UID = "uid";
    private final static String FIRST_TIME_FEED = "first_time_feed";
    private final static String FIRST_TIME_PROFILE = "first_time_profile";

//    profile photo-----------------------------------------------------------
    private final static String PROFILE_PHOTO = "profile_photo";
    private final static String PHOTO_URL = "photo_url";

//    user pics---------------------------------------------------------------
    private final static String USER_IMAGES = "user_images";
    private final static String IMAGE1 = "image1";
    private final static String IMAGE2 = "image2";
    private final static String IMAGE3 = "image3";
    private final static String IMAGE4 = "image4";
    private final static String IMAGE5 = "image5";

//    edit profile data
    private final static String EDIT_PROFILE = "edit_profile";
    private final static String NAME = "name";
    private final static String ABOUT = "about";
    private final static String JOB_TITLE = "job_title";
    private final static String COMPANY = "company";
    private final static String SCHOOL = "school";
    private final static String RELIGION = "religion";
    private final static String GENDER = "gender";
    private final static String DOB = "dob";
    private final static String HEIGHT = "height";
    private final static String INTEREST = "interest";
    private final static String ZODIAC = "zodiac";
    private final static String BODY_TYPE = "body_type";
    private final static String SWIPES = "swipes";

//    settings
    private final static String SETTINGS = "settings";
    private final static String MALE = "male";
    private final static String FEMALE = "female";
    private final static String LESBIAN = "lesbian";
    private final static String GAY = "gay";
    private final static String AGE_START = "age_start";
    private final static String AGE_END = "age_end";
    private final static String MAX_DISTANCE = "max_distance";
    private final static String VISIBILITY_STATUS = "visibility_status";

//    intro profile date
    private final static String INTRO_PROFILE_DATA = "intro_profile_data";
    private final static String PROFILE_IMAGE = "profile_image";

//    view profiles settings changed
    private final static String PROFILE_SETTINGS_CHANGED = "profile_settings_changed";  // when km or preference is changed
    private final static String VALUE = "value";

//    last loaded chat key
    private final static String LAST_LOADED_CHAT_KEY = "LLCK";  // when km or preference is changed
    private final static String LAST_PUSH_KEY = "LPK";
    private final static String MATCH_UID_MESSAGING_CHAT = "MUMC";

//    chat key
    private final static String CHAT_KEY_PREF = "chat_key_pref";
    private final static String CHAT_KEY = "chat_key";

//    map location latitude and longitude
    private final static String SELECTED_MAP_LOCATION = "selected_map_location";
    private final static String ADDRESS = "address";
    private final static String LATITUDE = "latitude";
    private final static String LONGITUDE = "longitude";

//    super swipes left
    private final static String SUPER_SWIPES_LEFT = "super_swipes_left";

    //    birthday
    private final static String WISH_BIRTHDAY = "wish_birthday";
    private final static String WISHED = "wished";
    private final static String YEAR = "year";

//        is messaging activity active
    private final static String MESSAGING_ACTIVITY = "messaging_activity";
    private final static String STATUS = "status";

//        dashboard status
    private final static String DASHBOARD_STATUS = "dashboard_status";

//    pro user or not
    private final static String PRO_USER_STATUS = "pri_user_status";
    private final static String PRO_USER = "pro_user";
    private final static String EXPIRY_DATE = "expiry_date";

//    pro user or not
    private final static String SUBSCRIPTION_STATUS = "subscription_status";
    private final static String PRO_TOKEN = "pro_token";
    private final static String SUBSCRIBE_ON = "subscribe_on";
    private final static String PLAN = "plan";

//    -----------------------------------------------------------------------------------------------------------------------------------------
//    -----------------------------------------------------------------------------------------------------------------------------------------
//    -----------------------------------------------------------------------------------------------------------------------------------------

    MySharedPrefs(Context context){
        try{
            this.context = context;
        } catch (Exception e){
         //
        }
    }

    //    login, setter login page
    public void setLoginPref(String value, String log_in_mode
            ,  String user_name, String email, String uid, String first_time_feed, String first_time_profile){
        sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGGED_IN_OR_NOT, value);
        editor.putString(LOG_IN_MODE, log_in_mode);
        editor.putString(USER_NAME, user_name);
        editor.putString(EMAIL, email); // here email can be token number in case of facebook and phone number in case of phone login
        editor.putString(UID, uid);
        editor.putString(FIRST_TIME_FEED, first_time_feed);
        editor.putString(FIRST_TIME_PROFILE, first_time_profile);
        editor.apply();
    }
    public void setFirstTimeFeed(String first_time){
        sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FIRST_TIME_FEED, first_time);
        editor.apply();
    }
    public void setFirstTimeProfile(String first_time){
        sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FIRST_TIME_PROFILE, first_time);
        editor.apply();
    }

    public void setLoggedInOrNot(String value){
        sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGGED_IN_OR_NOT, value);
        editor.apply();
    }
    public String getLoggedInOrNot() {
        sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LOGGED_IN_OR_NOT, "");
    }
    public String getEmail() {
        sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        return sharedPreferences.getString(EMAIL, "");
    }
    public String getLogInMode() {
        sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LOG_IN_MODE, "");
    }
    public String getUid() {
        sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        return sharedPreferences.getString(UID, "");
    }
    public String getFirstTimeFeed() {
        sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        return sharedPreferences.getString(FIRST_TIME_FEED, "");
    }
    public String getFirstTimeProfile() {
        sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        return sharedPreferences.getString(FIRST_TIME_PROFILE, "");
    }
    public HashMap<Integer, String> getLoggedInUserDetails(){
        @SuppressLint("UseSparseArrays") HashMap<Integer, String> hashMap = new HashMap<>();
        sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        String log_in_mode = sharedPreferences.getString(LOG_IN_MODE, "");
        String user_name = sharedPreferences.getString(USER_NAME, "");
        String email = sharedPreferences.getString(EMAIL, "");
        hashMap.put(0, log_in_mode);
        hashMap.put(1, user_name);
        hashMap.put(2, email);
        return hashMap;
    }

//    profile photo-----------------------------------------------------------------------------------------
    public void setProfilePhotoPrefs(String url){
        sharedPreferences = context.getSharedPreferences(PROFILE_PHOTO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PHOTO_URL, url);
        editor.apply();
    }
    public String getPhotoUrl() {
        sharedPreferences = context.getSharedPreferences(PROFILE_PHOTO, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PHOTO_URL, "");
    }

    //    user pics------------------------------------------------------------------------------------------
    public void setUserImage1(String image1_url){
        sharedPreferences = context.getSharedPreferences(USER_IMAGES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(IMAGE1, image1_url);
        editor.apply();
    }
    public void setUserImage2(String image2_url){
        sharedPreferences = context.getSharedPreferences(USER_IMAGES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(IMAGE2, image2_url);
        editor.apply();
    }
    public void setUserImage3(String image3_url){
        sharedPreferences = context.getSharedPreferences(USER_IMAGES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(IMAGE3, image3_url);
        editor.apply();
    }
    public void setUserImage4(String image4_url){
        sharedPreferences = context.getSharedPreferences(USER_IMAGES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(IMAGE4, image4_url);
        editor.apply();
    }
    public void setUserImage5(String image5_url){
        sharedPreferences = context.getSharedPreferences(USER_IMAGES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(IMAGE5, image5_url);
        editor.apply();
    }
    public  String getIMAGE1() {
        sharedPreferences = context.getSharedPreferences(USER_IMAGES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(IMAGE1, "");
    }
    public  String getIMAGE2() {
        sharedPreferences = context.getSharedPreferences(USER_IMAGES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(IMAGE2, "");
    }
    public  String getIMAGE3() {
        sharedPreferences = context.getSharedPreferences(USER_IMAGES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(IMAGE3, "");
    }
    public  String getIMAGE4() {
        sharedPreferences = context.getSharedPreferences(USER_IMAGES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(IMAGE4, "");
    }
    public  String getIMAGE5() {
        sharedPreferences = context.getSharedPreferences(USER_IMAGES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(IMAGE5, "");
    }

//    edit profile data -------------------------------------------------------------------------------
    public void setEditProfile(String name, String about, String job_title, String company
            , String school, String religion, String gender, String dob, String height
            , String interest, String zodiac, String body_type){
        sharedPreferences = context.getSharedPreferences(EDIT_PROFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.putString(ABOUT, about);
        editor.putString(JOB_TITLE, job_title);
        editor.putString(COMPANY, company);
        editor.putString(SCHOOL, school);
        editor.putString(RELIGION, religion);
        editor.putString(GENDER, gender);
        editor.putString(DOB, dob);
        editor.putString(HEIGHT, height);
        editor.putString(INTEREST, interest);
        editor.putString(ZODIAC, zodiac);
        editor.putString(BODY_TYPE, body_type);
        editor.apply();
    }

    //    edit profile data -------------------------------------------------------------------------------
    public void setEditProfile(String name, String about, String job_title, String company
            , String school, String religion, String gender, String dob, String height, String zodiac, String body_type){
        sharedPreferences = context.getSharedPreferences(EDIT_PROFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.putString(ABOUT, about);
        editor.putString(JOB_TITLE, job_title);
        editor.putString(COMPANY, company);
        editor.putString(SCHOOL, school);
        editor.putString(RELIGION, religion);
        editor.putString(GENDER, gender);
        editor.putString(DOB, dob);
        editor.putString(HEIGHT, height);
        editor.putString(ZODIAC, zodiac);
        editor.putString(BODY_TYPE, body_type);
        editor.apply();
    }

    public void setEditProfileNameAndAbout(String name, String about){
        sharedPreferences = context.getSharedPreferences(EDIT_PROFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.putString(ABOUT, about);
        editor.apply();
    }

    public String getProfileName(){
        sharedPreferences = context.getSharedPreferences(EDIT_PROFILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(NAME, "");
    }

    public void setLikings(String likings){
        sharedPreferences = context.getSharedPreferences(EDIT_PROFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(INTEREST, likings);
        editor.apply();
    }

    public HashMap<Integer, String> getEditProfile(){
        @SuppressLint("UseSparseArrays") HashMap<Integer, String> hashMap = new HashMap<>();
        sharedPreferences = context.getSharedPreferences(EDIT_PROFILE, Context.MODE_PRIVATE);
        String name, about, job_tile, company, school, religion, gender, dob, height, interest, zodiac, body_type;
        name = sharedPreferences.getString(NAME, "");
        about = sharedPreferences.getString(ABOUT, "");
        job_tile = sharedPreferences.getString(JOB_TITLE, "");
        company = sharedPreferences.getString(COMPANY, "");
        school = sharedPreferences.getString(SCHOOL, "");
        religion = sharedPreferences.getString(RELIGION, "");
        gender = sharedPreferences.getString(GENDER, "");
        dob = sharedPreferences.getString(DOB, "");
        height = sharedPreferences.getString(HEIGHT, "");
        interest = sharedPreferences.getString(INTEREST, "");
        zodiac = sharedPreferences.getString(ZODIAC, "");
        body_type = sharedPreferences.getString(BODY_TYPE, "");

        hashMap.put(0, name);
        hashMap.put(1, about);
        hashMap.put(2, job_tile);
        hashMap.put(3, company);
        hashMap.put(4, school);
        hashMap.put(5, religion);
        hashMap.put(6, gender);
        hashMap.put(7, dob);
        hashMap.put(8, height);
        hashMap.put(9, interest);
        hashMap.put(10, zodiac);
        hashMap.put(11, body_type);

        return hashMap;
    }

    //    settings ------------------------------------------------------------------------------------------
    public void setSettings(String male, String female, String lesbian, String gay
            , String age_start, String age_end, String max_distance, String visibility_status){
        sharedPreferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MALE, male);
        editor.putString(FEMALE, female);
        editor.putString(LESBIAN, lesbian);
        editor.putString(GAY, gay);
        editor.putString(AGE_START, age_start);
        editor.putString(AGE_END, age_end);
        editor.putString(MAX_DISTANCE, max_distance);
        editor.putString(VISIBILITY_STATUS, visibility_status);
        editor.apply();
    }

    public HashMap<Integer, String> getSettings(){
        @SuppressLint("UseSparseArrays") HashMap<Integer, String> hashMap = new HashMap<>();
        sharedPreferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        String male, female, lesbian, gay, age_start, age_end, max_distance, visibility_status;
        male = sharedPreferences.getString(MALE, "");
        female = sharedPreferences.getString(FEMALE, "");
        lesbian = sharedPreferences.getString(LESBIAN, "");
        gay = sharedPreferences.getString(GAY, "");
        age_start = sharedPreferences.getString(AGE_START, "");
        age_end = sharedPreferences.getString(AGE_END, "");
        max_distance = sharedPreferences.getString(MAX_DISTANCE, "");
        visibility_status = sharedPreferences.getString(VISIBILITY_STATUS, "");

        hashMap.put(0, male);
        hashMap.put(1, female);
        hashMap.put(2, lesbian);
        hashMap.put(3, gay);
        hashMap.put(4, age_start);
        hashMap.put(5, age_end);
        hashMap.put(6, max_distance);
        hashMap.put(7, visibility_status);

        return hashMap;
    }

//    intro profile data   -----------------------------------------------------------------------------------
    public void setIntroProfileData1(String profile_image, String name, String gender){
        sharedPreferences = context.getSharedPreferences(INTRO_PROFILE_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROFILE_IMAGE, profile_image);
        editor.putString(NAME, name);
        editor.putString(GENDER, gender);
        editor.apply();
    }

    public void setIntroProfileData2(String body_type, String dob, String height){
        sharedPreferences = context.getSharedPreferences(INTRO_PROFILE_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BODY_TYPE, body_type);
        editor.putString(DOB, dob);
        editor.putString(HEIGHT, height);
        editor.apply();
    }

    public void setIntroProfileData3(String about, String school, String religion, String zodiac){
        sharedPreferences = context.getSharedPreferences(INTRO_PROFILE_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ABOUT, about);
        editor.putString(SCHOOL, school);
        editor.putString(RELIGION, religion);
        editor.putString(ZODIAC, zodiac);
        editor.apply();
    }

    public HashMap<Integer, String> getIntroProfileData(){
        @SuppressLint("UseSparseArrays") HashMap<Integer, String> hashMap = new HashMap<>();
        sharedPreferences = context.getSharedPreferences(INTRO_PROFILE_DATA, Context.MODE_PRIVATE);
        String profile_image,name,gender,body_type,dob,height,about,school,religion,zodiac;
        profile_image = sharedPreferences.getString(NAME, "");
        name = sharedPreferences.getString(NAME, "");
        gender = sharedPreferences.getString(GENDER, "");
        body_type = sharedPreferences.getString(BODY_TYPE, "");
        dob = sharedPreferences.getString(DOB, "");
        height = sharedPreferences.getString(HEIGHT, "");
        about = sharedPreferences.getString(ABOUT, "");
        school = sharedPreferences.getString(SCHOOL, "");
        religion = sharedPreferences.getString(RELIGION, "");
        zodiac = sharedPreferences.getString(ZODIAC, "");

        hashMap.put(0, profile_image);
        hashMap.put(1, name);
        hashMap.put(2, gender);
        hashMap.put(3, body_type);
        hashMap.put(4, dob);
        hashMap.put(5, height);
        hashMap.put(6, about);
        hashMap.put(7, school);
        hashMap.put(8, religion);
        hashMap.put(9, zodiac);

        return hashMap;
    }

//    view profiles settings changed----------------------------------------------------------------------------

    public void setProfileSettingsChanged(String value){
        sharedPreferences = context.getSharedPreferences(PROFILE_SETTINGS_CHANGED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(VALUE, value);
        editor.apply();
    }
    public String getProfileSettingsChanged(){
        sharedPreferences = context.getSharedPreferences(PROFILE_SETTINGS_CHANGED, Context.MODE_PRIVATE);
        return sharedPreferences.getString(VALUE, "");
    }


    //    view profiles settings changed----------------------------------------------------------------------------

    public void setChatKey(String key){
        sharedPreferences = context.getSharedPreferences(CHAT_KEY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CHAT_KEY, key);
        editor.apply();
    }
    public String getChatKey(){
        sharedPreferences = context.getSharedPreferences(CHAT_KEY_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CHAT_KEY, "");
    }

//    map location latitude and longitude--------------------------------------------------------------
    public void setSelectedMapLocation(String address, String latitude, String longitude){
        sharedPreferences = context.getSharedPreferences(SELECTED_MAP_LOCATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ADDRESS, address);
        editor.putString(LATITUDE, latitude);
        editor.putString(LONGITUDE, longitude);
        editor.apply();
    }
    public String getAddress(){
        sharedPreferences = context.getSharedPreferences(SELECTED_MAP_LOCATION, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ADDRESS, "");
    }
    public String getLatitude(){
        sharedPreferences = context.getSharedPreferences(SELECTED_MAP_LOCATION, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LATITUDE, "");
    }
    public String getLongitude(){
        sharedPreferences = context.getSharedPreferences(SELECTED_MAP_LOCATION, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LONGITUDE, "");
    }

//    super swipes left  -----------------------------------------------------------------------------------
    public void setSuperSwipesLeft(int value){
        sharedPreferences = context.getSharedPreferences(SUPER_SWIPES_LEFT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(VALUE, value);
        editor.apply();
    }
    public int getSuperSwipesLeft(){
        sharedPreferences = context.getSharedPreferences(SUPER_SWIPES_LEFT, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(VALUE, 0);
    }


    //    birthday -----------------------------------------------------------------------------------
    public void setWishBirthday(String wished, String year){
        sharedPreferences = context.getSharedPreferences(WISH_BIRTHDAY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(WISHED, wished);
        editor.putString(YEAR, year);
        editor.apply();
    }
    public String getWishBirthday(){
        sharedPreferences = context.getSharedPreferences(WISH_BIRTHDAY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(WISHED, "");
    }
    public String getWishBirthdayYear(){
        sharedPreferences = context.getSharedPreferences(WISH_BIRTHDAY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(YEAR, "");
    }

//        is messaging activity active -----------------------------------------------------------------------
    public void setMessagingActivity(String status){
        sharedPreferences = context.getSharedPreferences(MESSAGING_ACTIVITY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STATUS, status);
        editor.apply();
    }
    public String getMessagingActivityStatus(){
        sharedPreferences = context.getSharedPreferences(MESSAGING_ACTIVITY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(STATUS, "");
    }

//        dashboard status -----------------------------------------------------------------------
    public void setDashboardStatus(Boolean status){
        sharedPreferences = context.getSharedPreferences(DASHBOARD_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STATUS, status);
        editor.apply();
    }
    public Boolean getDashboardStatus(){
        sharedPreferences = context.getSharedPreferences(DASHBOARD_STATUS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(STATUS, false);
    }

    //        set my swipes -----------------------------------------------------------------------
    public void setSwipes(String number){
        sharedPreferences = context.getSharedPreferences(EDIT_PROFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SWIPES, number);
        editor.apply();
    }
    public String getSwipes(){
        sharedPreferences = context.getSharedPreferences(EDIT_PROFILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SWIPES, "");
    }

//        pro user -----------------------------------------------------------------------
    public void setProUserStatus(Boolean status){
        sharedPreferences = context.getSharedPreferences(PRO_USER_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PRO_USER, status);
        editor.apply();
    }
    public void setProUserStatus(Boolean status, String expiry_date, String plan){
        sharedPreferences = context.getSharedPreferences(PRO_USER_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PRO_USER, status);
        editor.putString(EXPIRY_DATE, expiry_date);
        editor.putString(PLAN, plan);
        editor.apply();
    }
    public boolean getProUserStatus(){
        sharedPreferences = context.getSharedPreferences(PRO_USER_STATUS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PRO_USER, false);
    }
    public String getExpiryDate(){
        sharedPreferences = context.getSharedPreferences(PRO_USER_STATUS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(EXPIRY_DATE, "");
    }
    public String getPlan(){
        sharedPreferences = context.getSharedPreferences(PRO_USER_STATUS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PLAN, "");
    }

//    subscription status
    public void setSubscriptionStatus(Boolean status, String pro_token, long subscribe_time, int plan){
        sharedPreferences = context.getSharedPreferences(PRO_USER_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STATUS, status);
        editor.putString(PRO_TOKEN, pro_token);
        editor.putLong(SUBSCRIBE_ON, subscribe_time);
        editor.putInt(PLAN, plan);
        editor.apply();
    }
    public void setSubscriptionStatus(Boolean status){
        sharedPreferences = context.getSharedPreferences(PRO_USER_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STATUS, status);
        editor.apply();
    }
    public boolean getSubscriptionStatus(){
        sharedPreferences = context.getSharedPreferences(PRO_USER_STATUS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(STATUS, true);
    }
    public String getSubscriptionToken(){
        sharedPreferences = context.getSharedPreferences(PRO_USER_STATUS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PRO_TOKEN, "");
    }
    public long getSubscriptionTiming(){
        sharedPreferences = context.getSharedPreferences(PRO_USER_STATUS, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(SUBSCRIBE_ON, 0);
    }
    public int getSubscriptionPlan(){
        sharedPreferences = context.getSharedPreferences(PRO_USER_STATUS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(PLAN, 0);
    }


//    clear all prefs----------------------------------------------------------------------------------------
    public void clearAllPrefs(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        sharedPreferences = context.getSharedPreferences(PROFILE_PHOTO, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        sharedPreferences = context.getSharedPreferences(USER_IMAGES, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        sharedPreferences = context.getSharedPreferences(EDIT_PROFILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        sharedPreferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        sharedPreferences = context.getSharedPreferences(INTRO_PROFILE_DATA, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        sharedPreferences = context.getSharedPreferences(PROFILE_SETTINGS_CHANGED, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        sharedPreferences = context.getSharedPreferences(WISH_BIRTHDAY, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        sharedPreferences = context.getSharedPreferences(DASHBOARD_STATUS, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        sharedPreferences = context.getSharedPreferences(PRO_USER_STATUS, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    //end
}