package com.united.lovender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wooplr.spotlight.SpotlightView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;


public class IntroFragmentOne extends Fragment implements View.OnClickListener{

    private ImageView image_iv;
    private TextView name_tv;
    ImageButton next_b;
    private RelativeLayout male_rl, female_rl;
    private ImageView male_iv, female_iv;
    private String gender;
    private String image_url;
    FragmentActivity context;


    public IntroFragmentOne() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.context = (FragmentActivity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_fragment_one, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        image_iv = view.findViewById(R.id.fo_profile_image_ci);
        name_tv = view.findViewById(R.id.fo_name_tv);
        male_rl = view.findViewById(R.id.fo_male_rl);
        female_rl = view.findViewById(R.id.fo_female_rl);
        male_iv = view.findViewById(R.id.fo_male_iv);
        female_iv = view.findViewById(R.id.fo_female_iv);
        next_b = view.findViewById(R.id.fo_next_btn);
        ImageButton plus_1_ib = view.findViewById(R.id.fo_plus_1_ib);
        male_rl.setOnClickListener(this);
        female_rl.setOnClickListener(this);
        next_b.setOnClickListener(this);
        image_iv.setOnClickListener(this);
        plus_1_ib.setOnClickListener(this);

        showTutorial();
        setNameAndImage();

        ((Intro)context).enablePaging(false);
        checkIfAllConditionsMet();

        name_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfAllConditionsMet();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showTutorial() {

        String id = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        SpotlightView spotlightView = new SpotlightView.Builder(context)
                .introAnimationDuration(200)
                .enableRevealAnimation(true)
                .performClick(false)
                .fadeinTextDuration(200)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("Upload Image")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("You can change your profile image")
                .maskColor(Color.parseColor("#90000000"))
                .target(image_iv)
                .lineAnimDuration(200)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId(id) //UNIQUE ID
                .show();

    }

    public void hideSoftKeyboard() {
        if(context.getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        }
    }



    //setting name and image
    private void setNameAndImage() {
        String name = context.getIntent().getStringExtra("name");
        image_url = context.getIntent().getStringExtra("image_url");
        name_tv.setText(name);

        Glide.with(context)
                .load(image_url)
                .into(image_iv);

    }
    //setting name and image

//    on click
    @Override
    public void onClick(View v) {
        int id = v.getId();
        hideSoftKeyboard();
        switch (id){
            case R.id.fo_next_btn:
                if (checkConditionsOnButton()){
                    String name = name_tv.getText().toString().trim();
                    MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                    mySharedPrefs.setIntroProfileData1(image_url, name, gender);
                    ((Intro)context).setCurrentItem(1, true);
                }
                break;
            case R.id.fo_male_rl:
                gender = "man";
                male_iv.setBackground(getResources().getDrawable(R.drawable.ic_male_selected));
                male_rl.setBackground(getResources().getDrawable(R.drawable.border_yello_background));
                female_iv.setBackground(getResources().getDrawable(R.drawable.ic_female));
                female_rl.setBackground(getResources().getDrawable(R.drawable.border_white_background));
                checkIfAllConditionsMet();
                break;
            case R.id.fo_female_rl:
                gender = "woman";
                male_iv.setBackground(getResources().getDrawable(R.drawable.ic_male));
                male_rl.setBackground(getResources().getDrawable(R.drawable.border_white_background));
                female_iv.setBackground(getResources().getDrawable(R.drawable.ic_female_selected));
                female_rl.setBackground(getResources().getDrawable(R.drawable.border_yello_background));
                checkIfAllConditionsMet();
                break;
            case R.id.fo_profile_image_ci:
                selectImageToUpload();
                break;
            case R.id.fo_plus_1_ib:
                selectImageToUpload();
                break;
        }

    }
    //    on click

    //    select image
    private void selectImageToUpload() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                // .setOutputCompressQuality(75)
                .setInitialCropWindowPaddingRatio(0.1f)
                .start(context, this);
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

    //wait for result after selecting image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri imageFilePath = result.getUri();
                    final InputStream imageStream;

                String path = imageFilePath.getPath();
                File f = new File(path);

                if (f.length() > 10873020){
                    ImageSizeExceeds();
                    return;
                }

                    try {
                        imageStream = context.getContentResolver().openInputStream(imageFilePath);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        String encodedImage = encodeImage(selectedImage);

                        uploadImage(encodedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                Glide.with(context)
                        .load(imageFilePath)
                        .into(image_iv);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                new MaterialDialog.Builder(context)
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
        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
        showToastyMessage.errorMessageLong("Image Upload Failed!\nImage size exceeds 10 mb limit!");
    }
//    image size exceeds limit


    //    upload image
    private void uploadImage(String encodedImage){

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        String UID = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "ImageUploader");
            jsonObject.put("UID", UID);
            jsonObject.put("Image_Number", 1);
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
                                final MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                                mySharedPrefs.setUserImage1(image_url);
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
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
                   //     ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                   //     showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }
    //    upload image


    private void checkIfAllConditionsMet() {
        String name = name_tv.getText().toString().trim();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(gender)){
            next_b.setBackground(getResources().getDrawable(R.drawable.ic_next_blue));
           // ((Intro)context).enablePaging(true);

            MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
            mySharedPrefs.setIntroProfileData1(image_url, name, gender);
        }
        else {
            next_b.setBackground(getResources().getDrawable(R.drawable.ic_next_gray));
           // ((Intro)context).enablePaging(false);
        }
    }

    private boolean checkConditionsOnButton(){
        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
        String name = name_tv.getText().toString().trim();
        if (TextUtils.isEmpty(name)){
            showToastyMessage.warningMessage("name required");
            return false;
        }
        if (TextUtils.isEmpty(gender)){
            showToastyMessage.warningMessage("gender required");
            return false;
        }
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }


}
