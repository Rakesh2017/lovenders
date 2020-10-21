package com.united.lovender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import mehdi.sakout.fancybuttons.FancyButton;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class Login extends AppCompatActivity implements View.OnClickListener
        , GoogleApiClient.OnConnectionFailedListener, EasyPermissions.PermissionCallbacks {

    private FancyButton phone_fb, google_fb, facebook_fb;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    private static final int DEVICE_LOCATION = 1;
    private FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;

    private SpinKitView spinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_login);

        //ids
        phone_fb = findViewById(R.id.lo_phone_fb);
        phone_fb.setClickable(false);
        google_fb = findViewById(R.id.lo_google_fb);
        facebook_fb = findViewById(R.id.lo_facebook_fb);
        loginButton = findViewById(R.id.lo_facebook_widget_button);
        spinKitView = findViewById(R.id.lo_spin_kit);
        TextView terms_tv = findViewById(R.id.lo_terms_tv);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        phone_fb.setOnClickListener(this);
        google_fb.setOnClickListener(this);
        facebook_fb.setOnClickListener(this);
        terms_tv.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        grantPermission(); // seeking location permission
        loadAnimations();
        initiateGoogleSignInServices();
        try {
            facebookLoad();
        } catch (IllegalStateException e) {
            Toast.makeText(this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
        }
    }

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

    public void onResume() {
        super.onResume();
    }

    //    facebook loading
    private void facebookLoad() {
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserProfile(AccessToken.getCurrentAccessToken(), loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                facebook_fb.setClickable(true);
                google_fb.setClickable(true);
                phone_fb.setClickable(true);
                google_fb.setOnClickListener(Login.this);
                facebook_fb.setOnClickListener(Login.this);
                phone_fb.setOnClickListener(Login.this);
                //  ShowToastyMessage showToastyMessage = new ShowToastyMessage(Login.this);
                //  showToastyMessage.warningMessage("Login Cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                facebook_fb.setClickable(true);
                google_fb.setClickable(true);
                phone_fb.setClickable(true);
                google_fb.setOnClickListener(Login.this);
                facebook_fb.setOnClickListener(Login.this);
                phone_fb.setOnClickListener(Login.this);
                Toast.makeText(Login.this, "e " + exception, Toast.LENGTH_SHORT).show();
                // App code
            }
        });
    }
    //    facebook loading

    private void getUserProfile(final AccessToken currentAccessToken, final AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        String first_name = "", last_name = "", email = "", userID, url = "";
                        try {
                            first_name = object.getString("first_name");
                            last_name = object.getString("last_name");
                            email = object.getString("email");
                            userID = object.getString("id");
                            url = "https://graph.facebook.com/" + userID + "/picture?width=" + 510 + "&height=" + 790;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (TextUtils.isEmpty(email)) {
                            email = accessToken.getUserId();
                        }

                        checkLocationBeforeLogin("FaceBookLogin"
                                , first_name + " " + last_name
                                , email
                                , "facebook"
                                , url, null, accessToken, accessToken.getUserId());

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }


    public void onStart() {
        super.onStart();
        checkLoginPrefs();
    }


    //    check if user is logged in or not
    private void checkLoginPrefs() {
        MySharedPrefs mySharedPrefs = new MySharedPrefs(Login.this);
        String logged_in_or_not = mySharedPrefs.getLoggedInOrNot();
        if (TextUtils.equals(logged_in_or_not, "true")) {
            mySharedPrefs.setProfileSettingsChanged("false");
            startActivity(new Intent(Login.this, DashBoard.class));
            Login.this.finish();
        }
    }
//    check if user is logged in or not

    //    animations
    private void loadAnimations() {
        YoYo.with(Techniques.Landing)
                .duration(3000)
                .repeat(0)
                .playOn(google_fb);
        YoYo.with(Techniques.Landing)
                .duration(3000)
                .repeat(0)
                .playOn(phone_fb);
        YoYo.with(Techniques.Landing)
                .duration(3000)
                .repeat(0)
                .playOn(facebook_fb);
    }
    //    animations

    private void initiateGoogleSignInServices() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("452556604567-mg37rqktk5e7k2s3js9pc09rtp469c48.apps.googleusercontent.com")
                .requestEmail()
                .requestScopes(new Scope(Scopes.PROFILE))
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(Login.this)
                .enableAutoManage(Login.this/* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    // [START on activity result]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result == null) {
                ShowToastyMessage showToastyMessage = new ShowToastyMessage(Login.this);
                showToastyMessage.warningMessage("server Error! try again!");
                return;
            }

            if (result.isSuccess()) {

                final GoogleSignInAccount account = result.getSignInAccount();

                String name = "", email = "", url = "", id = "";
                try {
                    email = account != null ? account.getEmail() : null;
                } catch (Exception ignored) { }
                try {
                    name = account != null ? account.getDisplayName() : null;
                } catch (Exception ignored) { }
                try {
                    url = String.valueOf(account != null ? account.getPhotoUrl() : null);
                } catch (Exception ignored) { }
                try {
                    assert account != null;
                    id = account.getId();
                } catch (Exception ignored) { }


                checkLocationBeforeLogin("GoogleLogin"
                        , name
                        , email
                        , "google"
                        , url
                        , account, null, id);
                //  firebaseAuthWithGoogle1(account);

            } else {
                //   ShowToastyMessage showToastyMessage = new ShowToastyMessage(Login.this);
                //   showToastyMessage.errorMessage("Something went wrong!");
                google_fb.setClickable(true);
                facebook_fb.setClickable(true);
                phone_fb.setClickable(true);
                google_fb.setOnClickListener(this);
                facebook_fb.setOnClickListener(Login.this);
                phone_fb.setOnClickListener(Login.this);
            }
        }
    }
    // [END on activity result]


    //    register/login user with app
    @SuppressLint("MissingPermission")
    private void checkLocationBeforeLogin(final String request, final String user_name
            , final String email, final String login_mode
            , final String photo_url, final GoogleSignInAccount account
            , final AccessToken facebookAccessToken, final String unique_id) {


        // progressDialog.show();
        show();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        getLastKnownLocationAndUpdate(location, request, user_name
                                , email, login_mode
                                , photo_url, account
                                , facebookAccessToken, unique_id);
                    }
                });
    }
//    register/login user with app



// in case if unable to get location
    @SuppressLint("MissingPermission")
    private void getLastKnownLocationAndUpdate(final Location location, final String request, final String user_name
            , final String email, final String login_mode
            , final String photo_url, final GoogleSignInAccount account
            , final AccessToken facebookAccessToken, final String unique_id) {

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
            Geocoder geocoder = new Geocoder(Login.this, Locale.getDefault());

            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(Double.valueOf(latitude),Double.valueOf(longitude), 1);
                address = addresses.get(0).getAddressLine(0);
            } catch (IOException | NullPointerException ignored) {
            }


        }
        String device_name = android.os.Build.MODEL;

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", request);
            jsonObject.put("Name", user_name);
            jsonObject.put("Email", email);
            jsonObject.put("Device", device_name);
            jsonObject.put("Profile", photo_url);
            jsonObject.put("Location", address);
            jsonObject.put("LatLog", latitude + "," + longitude);
            jsonObject.put("Token", unique_id);
        } catch (Exception e) {
            e.printStackTrace();
            //      progressDialog.dismiss();
            dismiss();
            facebook_fb.setClickable(true);
            google_fb.setClickable(true);
            google_fb.setOnClickListener(Login.this);
            facebook_fb.setOnClickListener(Login.this);
        }


        String url = getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final int code = response.getInt("code");

                            // Toast.makeText(Login.this, ""+response.toString(), Toast.LENGTH_SHORT).show();
                            if (code == 1313) {
                                String result = response.getString("result");
                                String uid, status; // status ~ if true existing user
                                JSONArray jsonArray = new JSONArray(result);

                                JSONObject object = jsonArray.getJSONObject(0);
                                uid = object.getString("UID");
                                status = object.getString("Status");

                                if (TextUtils.equals(login_mode, "google")) {
                                    firebaseAuthWithGoogle(account, login_mode
                                            , user_name, email, uid, photo_url, status);
                                } else if (TextUtils.equals(login_mode, "facebook")) {
                                    firebaseAuthWithFacebook(facebookAccessToken, login_mode
                                            , user_name, email, uid, photo_url, status);
                                }

                            } else if (code == 1920) {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(Login.this);
                                showToastyMessage.errorMessage(response.getString("msg"));
//                                progressDialog.dismiss();
                                dismiss();
                                facebook_fb.setClickable(true);
                                google_fb.setClickable(true);
                                phone_fb.setClickable(true);
                                google_fb.setOnClickListener(Login.this);
                                facebook_fb.setOnClickListener(Login.this);
                                phone_fb.setOnClickListener(Login.this);
                            } else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(Login.this);
                                showToastyMessage.errorMessage("Request not completed !");
                                //  progressDialog.dismiss();
                                dismiss();
                                facebook_fb.setClickable(true);
                                google_fb.setClickable(true);
                                phone_fb.setClickable(true);
                                google_fb.setOnClickListener(Login.this);
                                facebook_fb.setOnClickListener(Login.this);
                                phone_fb.setOnClickListener(Login.this);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //   progressDialog.dismiss();
                            dismiss();
                            facebook_fb.setClickable(true);
                            google_fb.setClickable(true);
                            phone_fb.setClickable(true);
                            google_fb.setOnClickListener(Login.this);
                            facebook_fb.setOnClickListener(Login.this);
                            phone_fb.setOnClickListener(Login.this);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //    ShowToastyMessage showToastyMessage = new ShowToastyMessage(Login.this);
                        //    showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                        //  progressDialog.dismiss();
                        dismiss();
                        facebook_fb.setClickable(true);
                        google_fb.setClickable(true);
                        phone_fb.setClickable(true);
                        google_fb.setOnClickListener(Login.this);
                        facebook_fb.setOnClickListener(Login.this);
                        phone_fb.setOnClickListener(Login.this);
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);

    }
// in case if unable to get location


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try{
            new MaterialDialog.Builder(Login.this)
                    .title("Connection Failed")
                    .content(""+connectionResult)
                    .contentColorRes(R.color.black)
                    .titleColor(getResources().getColor(R.color.googleRed))
                    .titleColor(getResources().getColor(R.color.black))
                    .positiveText("Okay")
                    .positiveColorRes(R.color.black)
                    .backgroundColor(getResources().getColor(R.color.white))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            ShowToastyMessage showToastyMessage = new ShowToastyMessage(Login.this);
                            showToastyMessage.errorMessage("connection failed ");
                        }
                    })
                    .show();
        }
        catch (NullPointerException e) { e.printStackTrace(); }
    }


//    firebase login with google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct
            , final String login_mode, final String user_name
            , final String email, final String result, final String photo_url, final String status) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            String uid = null;
                            if (user != null) {
                                uid = user.getUid();
                            }
                            if (TextUtils.isEmpty(uid)){
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(Login.this);
                                showToastyMessage.errorMessage("Server Error! try again");
                                return;
                            }

                            assert uid != null;
                            databaseReference.child("users").child(uid).child("uid")
                                    .setValue(result)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            saveLoggedInDetailsSharedPrefs(login_mode
                                                    , user_name, email, result, photo_url, status);
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                          //  progressDialog.dismiss();
                            dismiss();
                            google_fb.setClickable(true);
                            facebook_fb.setClickable(true);
                            phone_fb.setClickable(true);
                            google_fb.setOnClickListener(Login.this);
                            facebook_fb.setOnClickListener(Login.this);
                            phone_fb.setOnClickListener(Login.this);
                        }

                        // ...
                    }
                });
    }

    //    firebase login with facebook
    private void firebaseAuthWithFacebook(AccessToken acct
            , final String login_mode, final String user_name
            , final String email, final String result, final String photo_url, final String status) {

        AuthCredential credential = FacebookAuthProvider.getCredential(acct.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("dfdfd", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = null;
                            if (user != null) {
                                uid = user.getUid();
                            }
                            if (TextUtils.isEmpty(uid)){
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(Login.this);
                                showToastyMessage.errorMessage("Server Error! try again");
                                return;
                            }

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            assert uid != null;
                            databaseReference.child("users").child(uid).child("uid")
                                    .setValue(result)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            saveLoggedInDetailsSharedPrefs(login_mode
                                                    , user_name, email, result, photo_url, status);
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                         //   progressDialog.dismiss();
                            dismiss();
                            facebook_fb.setClickable(true);
                            google_fb.setClickable(true);
                            phone_fb.setClickable(true);
                            google_fb.setOnClickListener(Login.this);
                            facebook_fb.setOnClickListener(Login.this);
                            phone_fb.setOnClickListener(Login.this);
                        }

                        // ...
                    }
                });
    }

    //    on click
    @Override
    public void onClick(View v) {

//        check if all permissions are granted
        if (!grantPermission()){
           return;
        }
        final int id = v.getId();

        new CheckNetworkConnection(Login.this, new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {

//        google login
              if (id == R.id.lo_google_fb) {
//            if user register earlier but did not complete phone registration

                  google_fb.setOnClickListener(null);
                  facebook_fb.setOnClickListener(null);
                  phone_fb.setOnClickListener(null);
                  google_fb.setClickable(false);
                  facebook_fb. setClickable(false);
                  phone_fb. setClickable(false);

                  if (GoogleSignIn.getLastSignedInAccount(Login.this) != null){
                      Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                      //mGoogleApiClient.clearDefaultAccountAndReconnect();
                  }

                  Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                  startActivityForResult(signInIntent, RC_SIGN_IN);
                }

//        facebook login
                else if (id == R.id.lo_facebook_fb){
                   // facebook_fb.setClickable(false);

                  google_fb.setOnClickListener(null);
                  facebook_fb.setOnClickListener(null);
                  phone_fb.setOnClickListener(null);
                  google_fb.setClickable(false);
                  facebook_fb.setClickable(false);
                  phone_fb.setClickable(false);
                  if (LoginManager.getInstance() != null){
                      LoginManager.getInstance().logOut();
                  }

                  loginButton.performClick();

                }

//        phone login
                else if (id == R.id.lo_phone_fb){
                    startActivity(new Intent(Login.this, PhoneLogin.class));
                }


                else if (id == R.id.lo_terms_tv){
                  String urlString = "http://lovenders.com/terms-and-condition";
                  Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(urlString));
                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  intent.setPackage("com.android.chrome");
                  try {
                      startActivity(intent);
                  } catch (ActivityNotFoundException ex) {
                      // Chrome browser presumably not installed so allow user to choose instead
                      intent.setPackage(null);
                      startActivity(intent);
                  }
                 // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://lovenders.com/terms-and-condition"));
                //  startActivity(browserIntent);
              }
            }
            @Override
            public void onConnectionFail(String msg) {
               startActivity(new Intent(Login.this, NoInternetActivity.class));
            }
        }).execute();
    }

//    saving logged in details
    private void saveLoggedInDetailsSharedPrefs(String log_in_mode
            , String user_name, String email,final String uid, String photo_url, String status){

        MySharedPrefs mySharedPrefs = new MySharedPrefs(Login.this);

        if (TextUtils.equals(status, "true")){
            mySharedPrefs.setLoginPref(null, log_in_mode, user_name, email, uid, "false", "false");
            Intent intent = new Intent(Login.this, Intro.class);
            intent.putExtra("name", user_name);
            intent.putExtra("image_url", photo_url);
            startActivity(intent);
            Login.this.finish();
         //   progressDialog.dismiss();
            dismiss();
        }
        else if (TextUtils.equals(status, "false")){
            mySharedPrefs.setLoginPref("true", log_in_mode, user_name, email, uid, "true", "true");
            Intent intent = new Intent(Login.this, DashBoard.class);
            startActivity(intent);
            Login.this.finish();
          //  progressDialog.dismiss();
            dismiss();
        }
        else {
            Toast.makeText(this, "try again", Toast.LENGTH_SHORT).show();
        }
    }
//    saving logged in details

    private void turnOnGps(){
        FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(Login.this)
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
                        GpsUtils gpsUtils = new GpsUtils(Login.this);
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
                            new AppSettingsDialog.Builder(Login.this).build().show();
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

    public void onDestroy(){
        super.onDestroy();

    }

    private void show(){
        spinKitView.setVisibility(View.VISIBLE);
    }
    private void dismiss(){
        spinKitView.setVisibility(View.GONE);
    }

//    end
}


/*    new CheckNetworkConnection(Login.this, new CheckNetworkConnection.OnConnectionCallback() {
         @Override
        public void onConnectionSuccess() {

        }
         @Override
        public void onConnectionFail(String msg) {
        startActivity(new Intent(Login.this, NoInternetActivity.class));
        }
        }).execute(); */