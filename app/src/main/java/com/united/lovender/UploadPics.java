package com.united.lovender;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class UploadPics extends AppCompatActivity implements View.OnClickListener{

    private ImageView upload_image_1_iv, upload_image_2_iv, upload_image_3_iv, upload_image_4_iv, upload_image_5_iv;
    private Uri ImageFilePath1, ImageFilePath2, ImageFilePath3, ImageFilePath4, ImageFilePath5;
    private int image_tag;
    private ImageButton cancel1_ib, cancel2_ib, cancel3_ib, cancel4_ib, cancel5_ib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_upload_pics);
        ImageButton back_ib = findViewById(R.id.up_back_ib);
        upload_image_1_iv = findViewById(R.id.up_upload_image_1_iv);
        upload_image_2_iv = findViewById(R.id.up_upload_image_2_iv);
        upload_image_3_iv = findViewById(R.id.up_upload_image_3_iv);
        upload_image_4_iv = findViewById(R.id.up_upload_image_4_iv);
        upload_image_5_iv = findViewById(R.id.up_upload_image_5_iv);
        cancel1_ib = findViewById(R.id.up_cancel_image_1);
        cancel2_ib = findViewById(R.id.up_cancel_image_2);
        cancel3_ib = findViewById(R.id.up_cancel_image_3);
        cancel4_ib = findViewById(R.id.up_cancel_image_4);
        cancel5_ib = findViewById(R.id.up_cancel_image_5);

        back_ib.setOnClickListener(this);
        upload_image_1_iv.setOnClickListener(this);
        upload_image_2_iv.setOnClickListener(this);
        upload_image_3_iv.setOnClickListener(this);
        upload_image_4_iv.setOnClickListener(this);
        upload_image_5_iv.setOnClickListener(this);
        cancel1_ib.setOnClickListener(this);
        cancel2_ib.setOnClickListener(this);
        cancel3_ib.setOnClickListener(this);
        cancel4_ib.setOnClickListener(this);
        cancel5_ib.setOnClickListener(this);

        getPhotos();
        checkInternetConnection();

    }

//    checking connection display toast of fail
    private void checkInternetConnection() {
        new CheckNetworkConnection(UploadPics.this, new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {

            }
            @Override
            public void onConnectionFail(String msg) {
                ShowToastyMessage showToastyMessage = new ShowToastyMessage(UploadPics.this);
                showToastyMessage.warningMessage("No or Slow internet connection!");
            }
        }).execute();
    }

    //    on start
    public void onStart(){
        super.onStart();

    }
    //    on start


    private void getPhotos() {

        try {
            setAllPicsFromPrefs();
        }
        catch (Exception e){
            e.printStackTrace();
        }


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(UploadPics.this);
        String UID = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("UID", UID);
            jsonObject.put("request", "GetProfilePhotos");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(Legal.this, ""+response, Toast.LENGTH_SHORT).show();
                            final int code = response.getInt("code");
                            if (code == 1313){
                                String result = response.getString("result");
                                try {
                                    JSONArray jsonArray = new JSONArray(result);

                                    final int length = jsonArray.length();
                                    for (int i = 0 ; i<length ; i++){
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String Image1,Image2,Image3,Image4,Image5;
                                        Image1 = object.getString("Image1");
                                        Image2 = object.getString("Image2");
                                        Image3 = object.getString("Image3");
                                        Image4 = object.getString("Image4");
                                        Image5 = object.getString("Image5");
                                        mySharedPrefs.setUserImage2("");
                                        mySharedPrefs.setUserImage3("");
                                        mySharedPrefs.setUserImage4("");
                                        mySharedPrefs.setUserImage5("");
                                        try{
                                            setImages(Image1, Image2, Image3, Image4, Image5);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }
                                } catch (JSONException e) {
                                    ShowToastyMessage showToastyMessage = new ShowToastyMessage(UploadPics.this);
                                    showToastyMessage.errorMessage("Something went wrong");
                                    e.printStackTrace();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                  ///      ShowToastyMessage showToastyMessage = new ShowToastyMessage(UploadPics.this);
                  //      showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }

//    setting all pics from prefs
    private void setAllPicsFromPrefs() {
        final MySharedPrefs mySharedPrefs = new MySharedPrefs(UploadPics.this);
        String image1_url, image2_url, image3_url, image4_url, image5_url;
        image1_url = mySharedPrefs.getIMAGE1();
        image2_url = mySharedPrefs.getIMAGE2();
        image3_url = mySharedPrefs.getIMAGE3();
        image4_url = mySharedPrefs.getIMAGE4();
        image5_url = mySharedPrefs.getIMAGE5();

        //Log.w("fdxf4", "1"+image1_url+ "\n2"+image2_url+ "\n3"+image3_url+ "\n4"+image4_url+ "\n5"+image5_url);
//        image 1
        if (!TextUtils.isEmpty(image1_url)){
            Glide.with(UploadPics.this)
                    .load(image1_url)
                    .into(upload_image_1_iv);
            ImageFilePath1 = Uri.parse(image1_url);
        }
        //        image 2
        if (!TextUtils.isEmpty(image2_url)){
            Glide.with(UploadPics.this)
                    .load(image2_url)
                    .into(upload_image_2_iv);
            ImageFilePath2 = Uri.parse(image2_url);
        }
        //        image 3
        if (!TextUtils.isEmpty(image3_url)){
            Glide.with(UploadPics.this)
                    .load(image3_url)
                    .into(upload_image_3_iv);
            ImageFilePath3 = Uri.parse(image3_url);
        }
        //        image 4
        if (!TextUtils.isEmpty(image4_url)){
            Glide.with(UploadPics.this)
                    .load(image4_url)
                    .into(upload_image_4_iv);
            ImageFilePath4 = Uri.parse(image4_url);
        }
        //        image 5
        if (!TextUtils.isEmpty(image5_url)){
            Glide.with(UploadPics.this)
                    .load(image5_url)
                    .into(upload_image_5_iv);
            ImageFilePath5 = Uri.parse(image5_url);
        }
    }
    //    setting all pics from prefs

    //    setting images
    private void setImages(String image1, String image2, String image3, String image4, String image5) {
        final MySharedPrefs mySharedPrefs = new MySharedPrefs(UploadPics.this);
        if (!TextUtils.isEmpty(image1)){
            ImageFilePath1 = Uri.parse(image1);
            Glide.with(UploadPics.this)
                    .load(image1)
                    .into(upload_image_1_iv);
            mySharedPrefs.setUserImage1(image1);
        }

        if (!TextUtils.isEmpty(image2)){
            ImageFilePath2 = Uri.parse(image2);
            cancel2_ib.setVisibility(View.VISIBLE);
            Glide.with(UploadPics.this)
                    .load(image2)
                    .into(upload_image_2_iv);
            mySharedPrefs.setUserImage2(image2);
        }
        else {
            ImageFilePath2 = null;
            upload_image_2_iv.setImageResource(0);
            cancel2_ib.setVisibility(View.GONE);
            ImageFilePath2 = null;
            mySharedPrefs.setUserImage2(null);
        }


        if (!TextUtils.isEmpty(image3)){
            ImageFilePath3 = Uri.parse(image3);
            cancel3_ib.setVisibility(View.VISIBLE);
            Glide.with(UploadPics.this)
                    .load(image3)
                    .into(upload_image_3_iv);
            mySharedPrefs.setUserImage3(image3);
        }
        else {
            upload_image_3_iv.setImageResource(0);
            cancel3_ib.setVisibility(View.GONE);
            ImageFilePath3 = null;
            mySharedPrefs.setUserImage3(null);
        }


        if (!TextUtils.isEmpty(image4)){
            ImageFilePath4 = Uri.parse(image4);
            cancel4_ib.setVisibility(View.VISIBLE);
            Glide.with(UploadPics.this)
                    .load(image4)
                    .into(upload_image_4_iv);
            mySharedPrefs.setUserImage4(image4);
        }
        else {
            upload_image_4_iv.setImageResource(0);
            cancel4_ib.setVisibility(View.GONE);
            ImageFilePath4 = null;
            mySharedPrefs.setUserImage4(null);
        }


        if (!TextUtils.isEmpty(image5)){
            ImageFilePath5 = Uri.parse(image5);
            cancel5_ib.setVisibility(View.VISIBLE);
            Glide.with(UploadPics.this)
                    .load(image5)
                    .into(upload_image_5_iv);
            mySharedPrefs.setUserImage5(image5);
        }
        else {
            upload_image_5_iv.setImageResource(0);
            cancel5_ib.setVisibility(View.GONE);
            ImageFilePath5 = null;
            mySharedPrefs.setUserImage5(null);
        }

    }
//    setting images

    //    on click
    @Override
    public void onClick(View v) {
        int id = v.getId();
        ShowToastyMessage toastyMessage = new ShowToastyMessage(UploadPics.this);
        switch (id){
            case R.id.up_back_ib:
                onBackPressed();
                break;
//                image 1 upload
            case R.id.up_upload_image_1_iv:
                image_tag = 1;
                selectImageToUpload();
                break;
//                image 2 upload
            case R.id.up_upload_image_2_iv:
                if (ImageFilePath1 == null){
                    toastyMessage.errorMessage("You may select pic 1");
                    return;
                }
                image_tag = 2;
                selectImageToUpload();
                break;
//                image 3 upload
            case R.id.up_upload_image_3_iv:
                if (ImageFilePath2 == null){
                    toastyMessage.errorMessage("You may select pic 2");
                    return;
                }
                image_tag = 3;
                selectImageToUpload();
                break;
//                image 4 upload
            case R.id.up_upload_image_4_iv:
                if (ImageFilePath3 == null){
                    toastyMessage.errorMessage("You may select pic 3");
                    return;
                }
                image_tag = 4;
                selectImageToUpload();
                break;
//                image 5 upload
            case R.id.up_upload_image_5_iv:
                if (ImageFilePath4 == null){
                    toastyMessage.errorMessage("You may select pic 4");
                    return;
                }
                image_tag = 5;
                selectImageToUpload();
                break;
//                cancel 1
            case R.id.up_cancel_image_1:
                upload_image_1_iv.setImageResource(0);
                cancel1_ib.setVisibility(View.GONE);
                ImageFilePath1 = null;
                break;
            //                cancel 2
            case R.id.up_cancel_image_2:
                upload_image_2_iv.setImageResource(0);
                cancel2_ib.setVisibility(View.GONE);
                ImageFilePath2 = null;
                removePhoto(2);
                break;
            //                cancel 3
            case R.id.up_cancel_image_3:
                upload_image_3_iv.setImageResource(0);
                cancel3_ib.setVisibility(View.GONE);
                ImageFilePath3 = null;
                removePhoto(3);
                break;
            //                cancel 4
            case R.id.up_cancel_image_4:
                upload_image_4_iv.setImageResource(0);
                cancel4_ib.setVisibility(View.GONE);
                ImageFilePath4 = null;
                removePhoto(4);
                break;
            //                cancel 5
            case R.id.up_cancel_image_5:
                upload_image_5_iv.setImageResource(0);
                cancel5_ib.setVisibility(View.GONE);
                ImageFilePath5 = null;
                removePhoto(5);
                break;
            //                plus 1
            case R.id.up_plus_1_ib:
                upload_image_1_iv.performClick();
                break;
//                plus 2
            case R.id.up_plus_2_ib:
                upload_image_2_iv.performClick();
                break;
            //                plus 3
            case R.id.up_plus_3_ib:
                upload_image_3_iv.performClick();
                break;
            //                plus 4
            case R.id.up_plus_4_ib:
                upload_image_4_iv.performClick();
                break;
            //                plus 4
            case R.id.up_plus_5_ib:
                upload_image_5_iv.performClick();
                break;
        }//switch ends

    }
//    on click

//    remove photo
    private void removePhoto(final int id) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(UploadPics.this);
        String UID = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "DeleteProfilePhotos");
            jsonObject.put("UID", UID);
            jsonObject.put("Image_Number", id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final int code = response.getInt("code");
                            if (code == 1313){
                                mySharedPrefs.setUserImage2("");
                                mySharedPrefs.setUserImage3("");
                                mySharedPrefs.setUserImage4("");
                                mySharedPrefs.setUserImage5("");
                                getPhotos();
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(UploadPics.this);
                                showToastyMessage.errorMessage("Request not completed !");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                     //   ShowToastyMessage showToastyMessage = new ShowToastyMessage(UploadPics.this);
                     //   showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }
    //    remove photo


    //    select image
    private void selectImageToUpload() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
               // .setOutputCompressQuality(75)
                .setInitialCropWindowPaddingRatio(0)
                .start(UploadPics.this);
    }
//    select image

//    convert image to base64
    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,30,baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    //    convert image to base64


//    get image size
    private boolean getImageSize(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
      //  bm.compress(Bitmap.CompressFormat.JPEG,100,baos);

        byte[] b = baos.toByteArray();
        return false;
    }
//    get image size


    //wait for result after selecting image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (image_tag == 1){
                    ImageFilePath1 = result.getUri();

                    String path = ImageFilePath1.getPath();
                    File f = new File(path);

                    if (f.length() > 10873020){
                        ImageSizeExceeds();
                        return;
                    }

                    final InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(ImageFilePath1);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        getImageSize(selectedImage);
                        String encodedImage = encodeImage(selectedImage);
                        uploadImage(encodedImage, 1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    //cancel1_ib.setVisibility(View.VISIBLE);
                    Glide.with(UploadPics.this)
                            .load(ImageFilePath1)
                            .into(upload_image_1_iv);
                }
                else if(image_tag == 2){
                    ImageFilePath2 = result.getUri();

                    String path = ImageFilePath1.getPath();
                    File f = new File(path);

                    if (f.length() > 10873020){
                        ImageSizeExceeds();
                        return;
                    }

                    final InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(ImageFilePath2);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        String encodedImage = encodeImage(selectedImage);
                        uploadImage(encodedImage, 2);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    cancel2_ib.setVisibility(View.VISIBLE);
                    Glide.with(UploadPics.this)
                            .load(ImageFilePath2)
                            .into(upload_image_2_iv);
                }
                else if(image_tag == 3){
                    ImageFilePath3 = result.getUri();

                    String path = ImageFilePath1.getPath();
                    File f = new File(path);

                    if (f.length() > 10873020){
                        ImageSizeExceeds();
                        return;
                    }

                    final InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(ImageFilePath3);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        String encodedImage = encodeImage(selectedImage);
                        uploadImage(encodedImage, 3);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    cancel3_ib.setVisibility(View.VISIBLE);
                    Glide.with(UploadPics.this)
                            .load(ImageFilePath3)
                            .into(upload_image_3_iv);
                }
                else if(image_tag == 4){
                    ImageFilePath4 = result.getUri();

                    String path = ImageFilePath1.getPath();
                    File f = new File(path);

                    if (f.length() > 10873020){
                        ImageSizeExceeds();
                        return;
                    }

                    final InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(ImageFilePath4);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        String encodedImage = encodeImage(selectedImage);
                        uploadImage(encodedImage, 4);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    cancel4_ib.setVisibility(View.VISIBLE);
                    Glide.with(UploadPics.this)
                            .load(ImageFilePath4)
                            .into(upload_image_4_iv);
                }
                else if(image_tag == 5){
                    ImageFilePath5 = result.getUri();

                    String path = ImageFilePath1.getPath();
                    File f = new File(path);

                    if (f.length() > 10873020){
                        ImageSizeExceeds();
                        return;
                    }

                    final InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(ImageFilePath5);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        String encodedImage = encodeImage(selectedImage);
                        uploadImage(encodedImage, 5);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    cancel5_ib.setVisibility(View.VISIBLE);
                    Glide.with(UploadPics.this)
                            .load(ImageFilePath5)
                            .into(upload_image_5_iv);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                new MaterialDialog.Builder(UploadPics.this)
                        .title("Failed")
                        .content(error.getLocalizedMessage())
                        .contentColorRes(R.color.black)
                        .titleColor(getResources().getColor(R.color.googleRed))
                        .titleColor(getResources().getColor(R.color.black))
                        .positiveText("Try Again")
                        .positiveColorRes(R.color.black)
                        .backgroundColor(getResources().getColor(R.color.white))
                        .icon(getResources().getDrawable(R.drawable.ic_warning))
                        .show();
            }
        }
    }
    //wait for result after selecting image

//    image size exceeds limit
    private void ImageSizeExceeds(){
        ShowToastyMessage showToastyMessage = new ShowToastyMessage(UploadPics.this);
        showToastyMessage.errorMessageLong("Image Upload Failed!\nImage size exceeds 10 mb limit!");
    }
//    image size exceeds limit

//    upload image
    private void uploadImage(String encodedImage, final int img_id){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(UploadPics.this);
        String UID = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "ImageUploader");
            jsonObject.put("UID", UID);
            jsonObject.put("Image_Number", img_id);
            jsonObject.put("Image", encodedImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final int code = response.getInt("code");
                            if (code == 1313){
                                String image_url = response.getString("result");
                                writeToSharePrefsAndNotification(img_id, image_url);
                                getPhotos();
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(UploadPics.this);
                                showToastyMessage.errorMessage("Request not completed !");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                     //   ShowToastyMessage showToastyMessage = new ShowToastyMessage(UploadPics.this);
                     //   showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }
    //    upload image

//    write shared pref
    private void writeToSharePrefsAndNotification(int img_id, String image_url) {
        final MySharedPrefs mySharedPrefs = new MySharedPrefs(UploadPics.this);
        if (img_id == 1){
            mySharedPrefs.setUserImage1(image_url);
            generateNotificationOnUpload(img_id);
        }
        else if (img_id == 2){
            mySharedPrefs.setUserImage2(image_url);
            generateNotificationOnUpload(img_id);
        }
        else if (img_id == 3){
            mySharedPrefs.setUserImage3(image_url);
            generateNotificationOnUpload(img_id);
        }
        else if (img_id == 4){
            mySharedPrefs.setUserImage4(image_url);
            generateNotificationOnUpload(img_id);
        }
        else if (img_id == 5){
            mySharedPrefs.setUserImage5(image_url);
            generateNotificationOnUpload(img_id);
        }
    }
    //    write shared pref

//    generate notification
    private void generateNotificationOnUpload(int img_id) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = String.valueOf(img_id);// The id of the channel.
        CharSequence name = "Lovender";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

// Create a notification and set the notification channel.
            Notification notification = new Notification.Builder(UploadPics.this)
                    .setContentTitle("Uploaded")
                    .setContentText("Profile Image "+img_id+" successfully uploaded")
                    .setSmallIcon(R.drawable.ic_camera)
                    .setChannelId(CHANNEL_ID)
                    .build();

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);

// Issue the notification.
            mNotificationManager.notify(img_id, notification);
        }

        PugNotification.with(UploadPics.this)
                .load()
                .identifier(img_id)
                .title("Upload Successfully")
                .message("Image  Uploaded Successfully")
                .smallIcon(R.drawable.ic_camera)
                .flags(Notification.FLAG_AUTO_CANCEL)
                .autoCancel(true)
                .ticker("1")
                .autoCancel(true)
                .simple()
                .build();

    }
//    generate notification


    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation, R.anim.exit);
    }


//    end
}
