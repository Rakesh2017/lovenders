package com.united.lovender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rilixtech.CountryCodePicker;
import com.serhatsurguvec.continuablecirclecountdownview.ContinuableCircleCountDownView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import mehdi.sakout.fancybuttons.FancyButton;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class PhoneLogin extends AppCompatActivity
        implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private ImageView top_image;
    private CountryCodePicker countryCodePicker;
    private static final int DEVICE_LOCATION = 1;

    private EditText phone_number_et;
    private PinView codeText;
    private FancyButton verifyButton;
    private FancyButton sendButton;
    private FancyButton resendButton;
    private TextView statusText, wrong_number_tv;
    private RelativeLayout otpRelativeLayout;
    private SpinKitView spinKitView;
    private ContinuableCircleCountDownView continuableCircleCountDownView;
    private static final int OTP_TIME = 30000;
    private FirebaseAuth firebaseAuth;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FusedLocationProviderClient fusedLocationClient;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_phone_login);

//        ids
        ImageButton back_ib = findViewById(R.id.pl_back_ib);
        top_image = findViewById(R.id.pl_image_iv);
        countryCodePicker = findViewById(R.id.ccp);
        fetchIDs();
        TextWatcher();
        initialiseFirebaseAuth();

        mAuth = FirebaseAuth.getInstance();

        back_ib.setOnClickListener(this);

        loadAnimations();

    }

    //    getting ids
    private void fetchIDs() {
        phone_number_et = findViewById(R.id.phoneText);
        codeText = findViewById(R.id.codeText);
        verifyButton = findViewById(R.id.verifyButton);
        sendButton = findViewById(R.id.sendButton);
        resendButton = findViewById(R.id.resendButton);
        wrong_number_tv = findViewById(R.id.pl_wrong_number_tv);
        statusText = findViewById(R.id.pl_otp_text);
        otpRelativeLayout = findViewById(R.id.pl_otpRelativeLayout);
        spinKitView = findViewById(R.id.pl_spin_kit);
        continuableCircleCountDownView = findViewById(R.id.pl_circleCountDownView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        sendButton.setEnabled(false);
        verifyButton.setEnabled(false);
        resendButton.setEnabled(false);

        sendButton.setOnClickListener(this);
        resendButton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);
        wrong_number_tv.setOnClickListener(this);
    }


    //    text watcher
    private void TextWatcher() {
        phone_number_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 10){
                    sendButton.setEnabled(true);
                }
                else sendButton.setEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        codeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 6){
                    verifyButton.setEnabled(true);
                }
                else verifyButton.setEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
//    text watcher


    public void sendCode() {
        // ic
        String phoneNumber = phone_number_et.getText().toString();

        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10){
            Toast.makeText(PhoneLogin.this, "incorrect Phone Number", Toast.LENGTH_SHORT).show();
            spinKitView.setVisibility(View.GONE);
            return;
        }

        // spinKitView.setVisibility(View.VISIBLE);
        phoneNumber = countryCodePicker.getSelectedCountryCodeWithPlus() + phoneNumber;

        setUpVerificationCallbacksSendCode();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                30,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                PhoneLogin.this,               // Activity (for callback binding)
                verificationCallbacks);
        // ic

    }

    //    to avoid abuse
    //when send button is used
    private void setUpVerificationCallbacksSendCode() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {

                        // sign out Button.setEnabled(true);
                        // statusText.setText("Signed In");
                        resendButton.setEnabled(false);
                        verifyButton.setEnabled(false);
                        codeText.setText("");

                        // firebase login
                        signInWithPhoneAuthCredentialPhone(credential);

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            new MaterialDialog.Builder(PhoneLogin.this)
                                    .title("Invalid Credential")
                                    .titleColor(getResources().getColor(R.color.black))
                                    .content(e.getLocalizedMessage())
                                    .contentColorRes(R.color.black)
                                    .positiveText("Try Again")
                                    .positiveColorRes(R.color.green)
                                    .backgroundColor(getResources().getColor(R.color.white))
                                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                                    .show();
                            spinKitView.setVisibility(View.GONE);

                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            // Log.d(TAG, "SMS Quota exceeded.");
                            Toast.makeText(PhoneLogin.this, "Too many attempts, try after some time", Toast.LENGTH_SHORT).show();
                            spinKitView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        spinKitView.setVisibility(View.GONE);
//                        disable edit text on code sent
                        phone_number_et.setEnabled(false);
                      //  wrong_number_tv.setVisibility(View.VISIBLE);
                        phone_number_et.setTextColor(getResources().getColor(R.color.gray));

                        otpRelativeLayout.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.SlideInUp)
                                .duration(700)
                                .repeat(0)
                                .playOn(otpRelativeLayout);

                        String phoneNumber = phone_number_et.getText().toString();


                        phoneNumber = countryCodePicker.getSelectedCountryCodeWithPlus() + phoneNumber;

                        statusText.setVisibility(View.VISIBLE);

                        statusText.setText(String.format("OTP has been sent to %s. App will automatically detect the OTP if not please enter manually and verify.", phoneNumber));


                        phoneVerificationId = verificationId;
                        resendToken = token;

                        continuableCircleCountDownView.setVisibility(View.VISIBLE);
                        continuableCircleCountDownView.setTimer(OTP_TIME);
                        continuableCircleCountDownView.setListener(new ContinuableCircleCountDownView.OnCountDownCompletedListener() {
                            @Override
                            public void onTick(long passedMillis) {

                            }

                            @Override
                            public void onCompleted() {
                                resendButton.setEnabled(true);
                                wrong_number_tv.setVisibility(View.VISIBLE);
                            }
                        });
                        continuableCircleCountDownView.start();


                        // verifyButton.setEnabled(true);
                        sendButton.setEnabled(false);
                        //  resendButton.setEnabled(true);

                    }
                };
    }


    //when re-send button is used
    private void setUpVerificationCallbacksReSendCode() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {

                        // sign out Button.setEnabled(true);
                        // statusText.setText("Signed In");
                        resendButton.setEnabled(false);
                        phone_number_et.setEnabled(false);
                        verifyButton.setEnabled(false);
                        wrong_number_tv.setVisibility(View.GONE);
                        codeText.setText("");

                        // firebase login
                        signInWithPhoneAuthCredentialPhone(credential);

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            new MaterialDialog.Builder(PhoneLogin.this)
                                    .title("Invalid Credential")
                                    .titleColor(getResources().getColor(R.color.black))
                                    .content(e.getLocalizedMessage())
                                    .contentColorRes(R.color.black)
                                    .positiveText("Okay")
                                    .positiveColorRes(R.color.green)
                                    .backgroundColor(getResources().getColor(R.color.white))
                                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                                    .show();
                            spinKitView.setVisibility(View.GONE);

                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            // Log.d(TAG, "SMS Quota exceeded.");
                            Toast.makeText(PhoneLogin.this, "Too many attempts, try after some time", Toast.LENGTH_SHORT).show();
                            spinKitView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        spinKitView.setVisibility(View.GONE);

                        String phoneNumber = phone_number_et.getText().toString();


                        phoneNumber = countryCodePicker.getSelectedCountryCodeWithPlus() + phoneNumber;

                        statusText.setVisibility(View.VISIBLE);

                        continuableCircleCountDownView.cancel();
                        resendButton.setEnabled(false);
                        statusText.setText(String.format("New OTP has been re-sent to %s. App will automatically detect the OTP if not please enter manually and verify.", phoneNumber));

                        phoneVerificationId = verificationId;
                        resendToken = token;

                        continuableCircleCountDownView.setVisibility(View.VISIBLE);
                        continuableCircleCountDownView.setTimer(OTP_TIME);
                        continuableCircleCountDownView.setListener(new ContinuableCircleCountDownView.OnCountDownCompletedListener() {
                            @Override
                            public void onTick(long passedMillis) {

                            }

                            @Override
                            public void onCompleted() {
                                resendButton.setEnabled(true);
                            }
                        });
                        continuableCircleCountDownView.start();


                        // verifyButton.setEnabled(true);
                        sendButton.setEnabled(false);
                        //  resendButton.setEnabled(true);

                    }
                };
    }


    //verify code
    public void verifyCode() {
        //ic
        String code;
        try {
            code = codeText.getText().toString();
        } catch (Exception e){
            ShowToastyMessage showToastyMessage = new ShowToastyMessage(PhoneLogin.this);
            showToastyMessage.errorMessage("otp is not valid!");
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);

        // firebase login
        signInWithPhoneAuthCredentialPhone(credential);

    }
    //verify code

    // resend code
    public void resendCode() {
        //ic
        String phoneNumber = phone_number_et.getText().toString();

        // spinKitView.setVisibility(View.VISIBLE);

        phoneNumber = countryCodePicker.getSelectedCountryCodeWithPlus() + phoneNumber;
        setUpVerificationCallbacksReSendCode();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                30,
                TimeUnit.SECONDS,
                PhoneLogin.this,
                verificationCallbacks,
                resendToken);
    }
    // resend code


    //   firebase sign in with phone
    private void signInWithPhoneAuthCredentialPhone(PhoneAuthCredential credential) {
//          sign in with phone (no linking here, as google is not signed in)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneLogin.this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            codeText.setText("");
                            resendButton.setEnabled(false);
                            verifyButton.setEnabled(false);
                            fusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(PhoneLogin.this, new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            RegisterWithServer(location);
                                        }
                                    });

                        }
                    }
                });
    }
//   firebase sign in with phone


//    register with server
    @SuppressLint("MissingPermission")
    private void RegisterWithServer(Location location){

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();
        String address, latitude, longitude;
        address = "";
        try{
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        } catch (Exception e){
            latitude="";
            longitude="";
        }
        if (!TextUtils.isEmpty(longitude) && !TextUtils.isEmpty(latitude)){
            Geocoder geocoder = new Geocoder(PhoneLogin.this, Locale.getDefault());

            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(Double.valueOf(latitude),Double.valueOf(longitude), 1);
                address = addresses.get(0).getAddressLine(0);
            } catch (IOException | NullPointerException ignored) {
            }


        }

        String device_name = android.os.Build.MODEL;

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "LoginByNumber");
            jsonObject.put("Mobile", countryCodePicker.getSelectedCountryCodeWithPlus()+phone_number_et.getText().toString().trim());
            jsonObject.put("Device", device_name);
            jsonObject.put("Location", address);
            jsonObject.put("LatLog", latitude+","+longitude);
        } catch (Exception e) {
            e.printStackTrace();
            spinKitView.setVisibility(View.GONE);
        }


        String url = getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final int code = response.getInt("code");

                            // Toast.makeText(Login.this, ""+response.toString(), Toast.LENGTH_SHORT).show();
                            if (code == 1313){
                                String result = response.getString("result");
                                String uid, status; // status ~ if true existing user
                                JSONArray jsonArray = new JSONArray(result);

                                JSONObject object = jsonArray.getJSONObject(0);
                                uid = object.getString("UID");
                                status = object.getString("Status");
                                saveDataToFirebase(uid, status);

                            }
                            else if (code == 1920){
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(PhoneLogin.this);
                                showToastyMessage.errorMessage(response.getString("msg"));
                                spinKitView.setVisibility(View.GONE);
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(PhoneLogin.this);
                                showToastyMessage.errorMessage("Request not completed !");
                                spinKitView.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //   progressDialog.dismiss();
                            spinKitView.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //    ShowToastyMessage showToastyMessage = new ShowToastyMessage(Login.this);
                        //    showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                        spinKitView.setVisibility(View.GONE);
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);

    }
//    register with server


    private void saveDataToFirebase(final String server_uid, final String status){
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = null;
        if (user != null) {
            uid = user.getUid();
        }
        if (TextUtils.isEmpty(uid)){
            ShowToastyMessage showToastyMessage = new ShowToastyMessage(PhoneLogin.this);
            showToastyMessage.errorMessage("Server Error! try again");
            spinKitView.setVisibility(View.GONE);
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        assert uid != null;
        databaseReference.child("users").child(uid).child("uid")
                .setValue(server_uid)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        saveLoggedInDetailsSharedPrefs(server_uid, status);
                    }
                });
    }


    //    saving logged in details
    private void saveLoggedInDetailsSharedPrefs(String uid, String status){

        MySharedPrefs mySharedPrefs = new MySharedPrefs(PhoneLogin.this);

        if (TextUtils.equals(status, "true")){
            mySharedPrefs.setLoginPref(null, "phone", null, countryCodePicker.getSelectedCountryCodeWithPlus()+phone_number_et.getText().toString().trim(), uid, "false", "false");
            Intent intent = new Intent(PhoneLogin.this, Intro.class);
            intent.putExtra("name", "");
            intent.putExtra("image_url", "");
            startActivity(intent);
            PhoneLogin.this.finish();
            spinKitView.setVisibility(View.GONE);
        }
        else if (TextUtils.equals(status, "false")){
            mySharedPrefs.setLoginPref("true", "phone", "", countryCodePicker.getSelectedCountryCodeWithPlus()+phone_number_et.getText().toString().trim(), uid, "true", "true");
            Intent intent = new Intent(PhoneLogin.this, DashBoard.class);
            startActivity(intent);
            PhoneLogin.this.finish();
            spinKitView.setVisibility(View.GONE);
        }
        else {
            Toast.makeText(this, "try again", Toast.LENGTH_SHORT).show();
            spinKitView.setVisibility(View.GONE);
        }
    }
//    saving logged in details


    //    initializing firebase auth
    private void initialiseFirebaseAuth(){
        firebaseAuth = FirebaseAuth.getInstance();
    }
    //    initializing firebase auth



//    animations
    private void loadAnimations() {
        YoYo.with(Techniques.Tada)
                .duration(2000)
                .repeat(0)
                .playOn(top_image);
    }

    //    onclick
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (!grantPermission()){
            return;
        }

        switch (id){
//            send code button
            case R.id.sendButton:
                spinKitView.setVisibility(View.VISIBLE);
                new CheckNetworkConnection(PhoneLogin.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        sendCode();
                        hideSoftKeyboard();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(PhoneLogin.this);
                        showToastyMessage.warningMessage("No Internet Connection!");
                        spinKitView.setVisibility(View.GONE);
                    }
                }).execute();
                break;

//            verify code
            case R.id.verifyButton:
                spinKitView.setVisibility(View.VISIBLE);
                new CheckNetworkConnection(PhoneLogin.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        verifyCode();
                        hideSoftKeyboard();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(PhoneLogin.this);
                        showToastyMessage.warningMessage("No Internet Connection!");
                        spinKitView.setVisibility(View.GONE);
                    }
                }).execute();
                break;

//            resend code
            case R.id.resendButton:
                spinKitView.setVisibility(View.VISIBLE);
                new CheckNetworkConnection(PhoneLogin.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        resendCode();
                        hideSoftKeyboard();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(PhoneLogin.this);
                        showToastyMessage.warningMessage("No Internet Connection!");
                        spinKitView.setVisibility(View.GONE);
                    }
                }).execute();
                break;

//                wrong number
            case R.id.pl_wrong_number_tv:
                wrong_number_tv.setVisibility(View.GONE);
                phone_number_et.setText(null);
                phone_number_et.setEnabled(true);
                resendButton.setEnabled(false);
                sendButton.setEnabled(true);
                statusText.setVisibility(View.GONE);
                codeText.setText(null);
                otpRelativeLayout.setVisibility(View.GONE);
                phone_number_et.setTextColor(Color.BLACK);
                continuableCircleCountDownView.cancel();
                continuableCircleCountDownView.setVisibility(View.GONE);
                spinKitView.setVisibility(View.GONE);
                break;

//                back button
            case R.id.pl_back_ib:
                onBackPressed();
                break;

        }

    }
    //    onclick

    //    permissions
    @AfterPermissionGranted(DEVICE_LOCATION)
    private boolean grantPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.CAMERA
                , Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            checkIfGpsOn();
            return true;
        } else {
            EasyPermissions.requestPermissions(this, "Please Grant Permissions to use application.",
                    DEVICE_LOCATION, perms);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        //Toast.makeText(this, "Some permissions have been granted!", Toast.LENGTH_SHORT).show();
        // Some permissions have been granted
        // ...
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.CAMERA
                , Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.somePermissionPermanentlyDenied(this, Arrays.asList(perms))) {
            new MaterialDialog.Builder(this)
                    .title("Grant Permission")
                    .titleColor(Color.BLACK)
                    .content("Please grant permissions to proceed further.")
                    .contentColor(Color.BLACK)
                    .backgroundColor(Color.WHITE)
                    .positiveText("Grant Permission")
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .positiveColor(getResources().getColor(R.color.lightGreen))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new AppSettingsDialog.Builder(PhoneLogin.this).build().show();
                        }
                    })
                    .negativeText("Decline")
                    .negativeColor(getResources().getColor(R.color.red))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }
//    location permission

    // location is v imp, so making sure to get location
    private void checkIfGpsOn() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location == null) {
                            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                turnOnGps();
                            }
                        }
                    }
                });
    }
    // location is v imp, so making sure to get location

    private void turnOnGps(){
        FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(PhoneLogin.this)
                .setimageResource(R.drawable.ic_gps)
                .setTextTitle("Turn on gps")
                .setTitleColor(R.color.appColor)
                .setBody("Please turn on gps as app required your location to provide you with customized feed.")
                .setPositiveColor(R.color.black)
                .setBackgroundColor(R.color.white)
                .setNegativeColor(R.color.lightGreen)
                .setPositiveColor(R.color.lightCoral)
                .setPositiveButtonText("Turn on")
                .setNegativeButtonText("Cancel")
                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, final Dialog dialog) {
                        dialog.dismiss();
                        GpsUtils gpsUtils = new GpsUtils(PhoneLogin.this);
                        gpsUtils.turnGPSOn(new GpsUtils.onGpsListener() {
                            @Override
                            public void gpsStatus(boolean isGPSEnable) {
                            }
                        });
                    }
                })
                .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, final Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setCancelable(true)
                .build();
        alert.show();
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation, R.anim.exit);
    }

//    end
}
