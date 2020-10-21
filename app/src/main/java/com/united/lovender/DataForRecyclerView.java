package com.united.lovender;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;

import com.daprlabs.cardstack.SwipeDeck;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DataForRecyclerView {

    private String image_url, id, title, content, hashtag_name
            , UID, Name, About, Company, DOB, ProfileImage, age, education, religion, height
            , gender, zodiac, body
            , swipe, image1,  image2,  image3,  image4,  image5, job, image_count, page_number
            , distance_km, distance_miles, match_UID, match_message, match_message_date
            , my_uid, my_message, my_message_date, message, status, match_name, match_profile_image_url, chat_key,
              push_key, last_chat_push_key, address, latitude
            , longitude, type, start_at, delete_status;

    private Bundle savedInstance;
    private FragmentManager fragmentManager;

    private boolean pro_status;

    private long message_date;

    private SwipeDeck swipeDeck;
    private CircleImageView circleImageView, circleImageView1;
    private RecyclerView recyclerView;


    private int adapterPosition;
    private ArrayList<String> interests = new ArrayList<>();


    DataForRecyclerView(){
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setPro_status(boolean pro_status) {
        this.pro_status = pro_status;
    }

    public boolean getPro_status() {
        return pro_status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getStart_at() {
        return start_at;
    }

    public void setStart_at(String start_at) {
        this.start_at = start_at;
    }

    public String getDelete_status() {
        return delete_status;
    }

    public void setDelete_status(String delete_status) {
        this.delete_status = delete_status;
    }

    public Bundle getSavedInstance() {
        return savedInstance;
    }

    public void setSavedInstance(Bundle savedInstance) {
        this.savedInstance = savedInstance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLast_chat_push_key() {
        return last_chat_push_key;
    }

    public void setLast_chat_push_key(String last_chat_push_key) {
        this.last_chat_push_key = last_chat_push_key;
    }

    public String getChat_key() {
        return chat_key;
    }

    public void setChat_key(String chat_key) {
        this.chat_key = chat_key;
    }

    public String getPush_key() {
        return push_key;
    }

    public void setPush_key(String push_key) {
        this.push_key = push_key;
    }

    public String getMatch_name() {
        return match_name;
    }

    public void setMatch_name(String match_name) {
        this.match_name = match_name;
    }

    public String getMatch_profile_image_url() {
        return match_profile_image_url;
    }

    public void setMatch_profile_image_url(String match_profile_image_url) {
        this.match_profile_image_url = match_profile_image_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CircleImageView getCircleImageView() {
        return circleImageView;
    }

    public void setCircleImageView(CircleImageView circleImageView) {
        this.circleImageView = circleImageView;
    }

    public CircleImageView getCircleImageView1() {
        return circleImageView1;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setCircleImageView1(CircleImageView circleImageView1) {
        this.circleImageView1 = circleImageView1;
    }

    public String getMatch_message() {
        return match_message;
    }

    public void setMatch_message(String match_message) {
        this.match_message = match_message;
    }

    public String getMatch_message_date() {
        return match_message_date;
    }

    public void setMatch_message_date(String match_message_date) {
        this.match_message_date = match_message_date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMessage_date() {
        return message_date;
    }

    public void setMessage_date(long message_date) {
        this.message_date = message_date;
    }

    public String getMatch_UID() {
        return match_UID;
    }

    public void setMatch_UID(String match_UID) {
        this.match_UID = match_UID;
    }

    public String getMy_message() {
        return my_message;
    }

    public void setMy_message(String my_message) {
        this.my_message = my_message;
    }

    public String getMy_message_date() {
        return my_message_date;
    }

    public void setMy_message_date(String my_message_date) {
        this.my_message_date = my_message_date;
    }

    public String getMy_uid() {
        return my_uid;
    }

    public void setMy_uid(String my_uid) {
        this.my_uid = my_uid;
    }

    public SwipeDeck getSwipeDeck() {
        return swipeDeck;
    }

    public void setSwipeDeck(SwipeDeck swipeDeck) {
        this.swipeDeck = swipeDeck;
    }

    public String getDistance_km() {
        return distance_km;
    }

    public void setDistance_km(String distance_km) {
        this.distance_km = distance_km;
    }

    public String getDistance_miles() {
        return distance_miles;
    }

    public void setDistance_miles(String distance_miles) {
        this.distance_miles = distance_miles;
    }

    public void setPage_number(String page_number) {
        this.page_number = page_number;
    }

    public String getPage_number() {
        return page_number;
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }

    public void setAdapterPosition(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }

    public String getImage_count() {
        return image_count;
    }

    public void setImage_count(String image_count) {
        this.image_count = image_count;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getImage3() {
        return image3;
    }

    public String getImage4() {
        return image4;
    }

    public String getImage5() {
        return image5;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public void setImage5(String image5) {
        this.image5 = image5;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public void setSwipe(String swipe) {
        this.swipe = swipe;
    }

    public String getSwipe() {
        return swipe;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setZodiac(String zodiac) {
        this.zodiac = zodiac;
    }

    public String getZodiac() {
        return zodiac;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getEducation() {
        return education;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHashtag_name(String hashtag_name) {
        this.hashtag_name = hashtag_name;
    }

    public String getHashtag_name() {
        return hashtag_name;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}