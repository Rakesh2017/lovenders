package com.united.lovender;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class OurPlans extends Fragment implements View.OnClickListener, PurchasesUpdatedListener {

    private FragmentActivity context;
    private RelativeLayout premium_rl, gold_rl, basic_rl, main_rl;
    private TextView premium_number_tv, premium_month_tv, premium_price_tv, basic_number_tv
            , basic_month_tv, basic_price_tv, gold_number_tv, gold_month_tv, gold_price_tv
            , premium_discount_tv, gold_discount_tv, basic_discount_tv;
    private ImageView display_gold_iv, display_premium_iv, display_basic_iv;
    private String premium_price = null;
    private String basic_price= null;
    private String gold_price= null;
    private CardView cardView;
    private BillingClient billingClientForDisplay, billingClientForBuy;
    private SpinKitView spinKitView;
    private int counter = 3;

    public OurPlans() {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_our_plans, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        premium_rl = view.findViewById(R.id.op_premium_rl);
        premium_discount_tv = view.findViewById(R.id.op_premium_discount);
        gold_rl = view.findViewById(R.id.op_gold_rl);
        gold_discount_tv = view.findViewById(R.id.op_gold_discount);
        basic_discount_tv = view.findViewById(R.id.op_basic_discount);
        basic_rl = view.findViewById(R.id.op_basic_rl);
        cardView = view.findViewById(R.id.op_card_layout);
        spinKitView = view.findViewById(R.id.op_spin_kit);
        FancyButton buy_btn = view.findViewById(R.id.op_buy_btn);
        main_rl = view.findViewById(R.id.op_main_rl);
        display_gold_iv = view.findViewById(R.id.op_display_gold);
        display_basic_iv = view.findViewById(R.id.op_display_basic);
        display_premium_iv = view.findViewById(R.id.op_display_premium);
        premium_number_tv = view.findViewById(R.id.op_premium_number);
        premium_month_tv = view.findViewById(R.id.op_premium_months);
        premium_price_tv = view.findViewById(R.id.op_premium_price);
        basic_number_tv = view.findViewById(R.id.op_basic_number);
        basic_month_tv = view.findViewById(R.id.op_basic_months);
        basic_price_tv = view.findViewById(R.id.op_basic_price);
        gold_number_tv = view.findViewById(R.id.op_gold_number);
        gold_month_tv = view.findViewById(R.id.op_gold_months);
        gold_price_tv = view.findViewById(R.id.op_gold_price);
        getGoogleBillingsForDisplay();
        premium_rl.setOnClickListener(this);
        gold_rl.setOnClickListener(this);
        basic_rl.setOnClickListener(this);
        buy_btn.setOnClickListener(this);
        main_rl.setOnClickListener(this);
      //  cardView.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void settingData() {

        String premium_discount = null, basic_discount = null, gold_discount = null;

        try{
            assert getArguments() != null;
            premium_discount = getArguments().getString("premium_discount");}
        catch (Exception ignored){}
        try{
            assert getArguments() != null;
            basic_discount = getArguments().getString("basic_discount");}
        catch (Exception ignored){}
        try{
            assert getArguments() != null;
            gold_discount = getArguments().getString("gold_discount");}
        catch (Exception ignored){}




        if (TextUtils.isEmpty(premium_discount)){
            premium_discount_tv.setVisibility(View.GONE);
        } if (TextUtils.isEmpty(gold_discount)){
            gold_discount_tv.setVisibility(View.GONE);
        } if (TextUtils.isEmpty(basic_discount)){
            basic_discount_tv.setVisibility(View.GONE);
        }

      //  premium_number_tv.setText(premium_month);
        premium_price_tv.setText(premium_price+"/3mo");
        premium_discount_tv.setText(premium_discount);

      //  gold_number_tv.setText(gold_month);
        gold_price_tv.setText(gold_price+"/6mo");
        gold_discount_tv.setText(gold_discount);

       // basic_number_tv.setText(basic_month);
        basic_price_tv.setText(basic_price+"/mo");
        basic_discount_tv.setText(basic_discount);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
        if (billingClientForDisplay != null && billingClientForDisplay.isReady()) {
            billingClientForDisplay.endConnection();
            billingClientForDisplay = null;
        }
        if (billingClientForBuy != null && billingClientForBuy.isReady()) {
            billingClientForBuy.endConnection();
            billingClientForBuy = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.op_premium_rl:

                YoYo.with(Techniques.ZoomIn)
                        .repeat(0)
                        .duration(200)
                        .playOn(premium_rl);

                if (counter == 1 || counter == 3){
                    if (counter == 1) {
                        YoYo.with(Techniques.SlideOutLeft)
                                .repeat(0)
                                .duration(200)
                                .playOn(display_basic_iv);
                    } else {
                        YoYo.with(Techniques.SlideOutLeft)
                                .repeat(0)
                                .duration(200)
                                .playOn(display_gold_iv);
                    }
                    display_premium_iv.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.SlideInRight)
                            .repeat(0)
                            .duration(200)
                            .playOn(display_premium_iv);
                }

                counter = 2;
                premium_rl.setBackground(context.getResources().getDrawable(R.drawable.simple_rectangle_gold));
                premium_number_tv.setTextColor(context.getResources().getColor(R.color.crispyBlue));
                premium_month_tv.setTextColor(context.getResources().getColor(R.color.crispyBlue));
                premium_price_tv.setTextColor(context.getResources().getColor(R.color.crispyBlue));
                if (!TextUtils.isEmpty(premium_discount_tv.getText().toString().trim())){
                    premium_discount_tv.setVisibility(View.VISIBLE);
                }

                gold_rl.setBackground(context.getResources().getDrawable(R.drawable.simple_rectangle));
                gold_number_tv.setTextColor(context.getResources().getColor(R.color.black90));
                gold_month_tv.setTextColor(context.getResources().getColor(R.color.black90));
                gold_price_tv.setTextColor(context.getResources().getColor(R.color.black90));
                gold_discount_tv.setVisibility(View.GONE);

                basic_rl.setBackground(context.getResources().getDrawable(R.drawable.simple_rectangle));
                basic_number_tv.setTextColor(context.getResources().getColor(R.color.black90));
                basic_month_tv.setTextColor(context.getResources().getColor(R.color.black90));
                basic_price_tv.setTextColor(context.getResources().getColor(R.color.black90));
                break;

            case R.id.op_gold_rl:

                YoYo.with(Techniques.ZoomIn)
                        .repeat(0)
                        .duration(200)
                        .playOn(gold_rl);

                if (counter == 1 || counter == 2){
                    if (counter == 1) {
                        YoYo.with(Techniques.SlideOutLeft)
                                .repeat(0)
                                .duration(200)
                                .playOn(display_basic_iv);
                    } else {
                        YoYo.with(Techniques.SlideOutLeft)
                                .repeat(0)
                                .duration(200)
                                .playOn(display_premium_iv);
                    }
                    display_gold_iv.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.SlideInRight)
                            .repeat(0)
                            .duration(200)
                            .playOn(display_gold_iv);
                }

                counter = 3;
                premium_rl.setBackground(context.getResources().getDrawable(R.drawable.simple_rectangle));
                premium_number_tv.setTextColor(context.getResources().getColor(R.color.black90));
                premium_month_tv.setTextColor(context.getResources().getColor(R.color.black90));
                premium_price_tv.setTextColor(context.getResources().getColor(R.color.black90));
                premium_discount_tv.setVisibility(View.GONE);

                gold_rl.setBackground(context.getResources().getDrawable(R.drawable.simple_rectangle_gold));
                gold_number_tv.setTextColor(context.getResources().getColor(R.color.crispyBlue));
                gold_month_tv.setTextColor(context.getResources().getColor(R.color.crispyBlue));
                gold_price_tv.setTextColor(context.getResources().getColor(R.color.crispyBlue));
                if (!TextUtils.isEmpty(gold_discount_tv.getText().toString().trim())){
                    gold_discount_tv.setVisibility(View.VISIBLE);
                }

                basic_rl.setBackground(context.getResources().getDrawable(R.drawable.simple_rectangle));
                basic_number_tv.setTextColor(context.getResources().getColor(R.color.black90));
                basic_month_tv.setTextColor(context.getResources().getColor(R.color.black90));
                basic_price_tv.setTextColor(context.getResources().getColor(R.color.black90));
                break;

            case R.id.op_basic_rl:

                YoYo.with(Techniques.ZoomIn)
                        .repeat(0)
                        .duration(200)
                        .playOn(basic_rl);

                if (counter == 2 || counter == 3){
                    if (counter == 3){
                        YoYo.with(Techniques.SlideOutLeft)
                                .repeat(0)
                                .duration(200)
                                .playOn(display_gold_iv);
                    } else {
                        YoYo.with(Techniques.SlideOutLeft)
                                .repeat(0)
                                .duration(200)
                                .playOn(display_premium_iv);
                    }
                    display_basic_iv.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.SlideInRight)
                            .repeat(0)
                            .duration(200)
                            .playOn(display_basic_iv);
                }

                counter = 1;
                premium_rl.setBackground(context.getResources().getDrawable(R.drawable.simple_rectangle));
                premium_number_tv.setTextColor(context.getResources().getColor(R.color.black90));
                premium_month_tv.setTextColor(context.getResources().getColor(R.color.black90));
                premium_price_tv.setTextColor(context.getResources().getColor(R.color.black90));
                premium_discount_tv.setVisibility(View.GONE);

                gold_rl.setBackground(context.getResources().getDrawable(R.drawable.simple_rectangle));
                gold_number_tv.setTextColor(context.getResources().getColor(R.color.black90));
                gold_month_tv.setTextColor(context.getResources().getColor(R.color.black90));
                gold_price_tv.setTextColor(context.getResources().getColor(R.color.black90));
                gold_discount_tv.setVisibility(View.GONE);

                basic_rl.setBackground(context.getResources().getDrawable(R.drawable.simple_rectangle_gold));
                basic_number_tv.setTextColor(context.getResources().getColor(R.color.crispyBlue));
                basic_month_tv.setTextColor(context.getResources().getColor(R.color.crispyBlue));
                basic_price_tv.setTextColor(context.getResources().getColor(R.color.crispyBlue));
                break;

            case R.id.op_buy_btn:
                getGoogleBillingsForBuy();
                break;

            case R.id.op_main_rl:
                back();
                break;

        }

    }

    private void getGoogleBillingsForDisplay(){

        billingClientForDisplay = BillingClient.newBuilder(context).setListener(this).build();

        billingClientForDisplay.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                    int responseCode = billingClientForDisplay.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
                    if (responseCode == BillingClient.BillingResponse.OK) {
                        getPrices();

                    } else {
                        dismiss();
                        Toast.makeText(context, "google play in app payments does not support old version of google play", Toast.LENGTH_SHORT).show();
                    }
                } else if (billingResponseCode == BillingClient.BillingResponse.BILLING_UNAVAILABLE) {
                    errorMessage("Billing Unavailable", "This generally happens if either no google account is logged in device or device does not supports google in-app subscriptions...");
                } else if (billingResponseCode == BillingClient.BillingResponse.FEATURE_NOT_SUPPORTED) {
                    errorMessage("Not Supported", "This device do not support this feature");
                } else if (billingResponseCode == BillingClient.BillingResponse.ERROR) {
                    errorMessage("Server Error", "we are facing server issues, please try again after some time. if problem still persists, please then contact us at support@lovenders.com");
                } else if (billingResponseCode == BillingClient.BillingResponse.DEVELOPER_ERROR) {
                    errorMessage("Server Error", "we are facing server issues, please try again after some time. if problem still persists, please then contact us at support@lovenders.com");
                }
                dismiss();
            }
            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(context, "connection lost with google play", Toast.LENGTH_SHORT).show();
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    private void getPrices(){
        List<String> skuList = new ArrayList<>();

        skuList.add("basic");
        skuList.add("premium");
        skuList.add("gold");

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS); // subs refer for subscriptions

        billingClientForDisplay.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                        if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {

                            for (SkuDetails skuDetails : skuDetailsList) {

                                final String sku = skuDetails.getSku();
                                final String price = skuDetails.getPrice();

                                if ("basic".equals(sku)) {
                                    basic_price = price;
                                } else if ("premium".equals(sku)) {
                                    premium_price = price;
                                } else if ("gold".equals(sku)) {
                                    gold_price = price;
                                }

                            }
                            dismiss();
                            cardView.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.SlideInUp)
                                    .repeat(0)
                                    .duration(500)
                                    .playOn(cardView);
                            settingData();

                        } else {
                            dismiss();
                        }
                    }
                });
    }

    private void getGoogleBillingsForBuy(){


        billingClientForBuy = BillingClient.newBuilder(context).setListener(this).build();

        billingClientForBuy.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                    int responseCode = billingClientForBuy.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
                    if (responseCode == BillingClient.BillingResponse.OK) {
                        queryPurchase();

                    } else {
                        dismiss();
                        Toast.makeText(context, "google play in app payments does not support old version of google play", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(context, "connection lost with google play", Toast.LENGTH_SHORT).show();
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    //    query purchase
    private void queryPurchase(){
        List<String> skuList = new ArrayList<>();

        skuList.add("basic");
        skuList.add("premium");
        skuList.add("gold");

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS); // subs refer for subscriptions


      //  Purchase.PurchasesResult purchasesResult = billingClientForBuy.queryPurchases(BillingClient.SkuType.SUBS);

        billingClientForBuy.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                        if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                            for (SkuDetails skuDetails : skuDetailsList) {
                                String sku = skuDetails.getSku();

                                if ("basic".equals(sku) && counter == 1) {
                                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetails)
                                            .build();
                                    billingClientForBuy.launchBillingFlow(context, flowParams);
                                } else if ("premium".equals(sku) && counter == 2) {
                                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetails)
                                            .build();
                                    billingClientForBuy.launchBillingFlow(context, flowParams);
                                } else if ("gold".equals(sku) && counter == 3) {
                                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetails)
                                            .build();
                                    billingClientForBuy.launchBillingFlow(context, flowParams);
                                }
                            }
                        }
                    }
                });
    }
//    query purchase

    @Override
    public void onPurchasesUpdated(@BillingClient.BillingResponse int responseCode, List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                updateDataToServer(purchase);
            }
        }
    }

    private void updateDataToServer(Purchase purchase){

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        mySharedPrefs.setSubscriptionStatus(true); // save locally purchase data

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();


        final String uid = mySharedPrefs.getUid();
        final String data = Base64.encodeToString(purchase.getOriginalJson().getBytes(), Base64.NO_WRAP);
        final long subscription_time = purchase.getPurchaseTime();
        final String product_token = purchase.getPurchaseToken();

        // in case subscription status is not updated on our server, the store it in local storage and also check for every time when user logs in

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "ProAccount");
            jsonObject.put("UID", uid);
            jsonObject.put("ProToken", data);
            jsonObject.put("SubscribeOn", subscription_time);
            jsonObject.put("Plan", counter);
            jsonObject.put("ProductToken", product_token);
        } catch (Exception e) {
            e.printStackTrace();
            mySharedPrefs.setSubscriptionStatus(false, data, subscription_time, counter);
        }

        String url = getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final int code = response.getInt("code");
                            if (code == 1313){
                                boolean result = response.getBoolean("result");
                                if (result){
                                    Intent intent = new Intent(context, ShowSubscribed.class);
                                    intent.putExtra("plan", counter);
                                    startActivity(intent);
                                    context.finish();
                                } else {
                                    mySharedPrefs.setSubscriptionStatus(false, data, subscription_time, counter);
                                }
                            }
                            else if (code == 1920){
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                showToastyMessage.errorMessage(response.getString("msg"));
                                mySharedPrefs.setSubscriptionStatus(false, data, subscription_time, counter);
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                showToastyMessage.errorMessage("Request not completed !");
                                mySharedPrefs.setSubscriptionStatus(false, data, subscription_time, counter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mySharedPrefs.setSubscriptionStatus(false, data, subscription_time, counter);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mySharedPrefs.setSubscriptionStatus(false, data, subscription_time, counter);
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }

    private void back(){
        final Fragment fragment = context.getSupportFragmentManager().findFragmentByTag("our_plans");
        if(fragment != null){
            YoYo.with(Techniques.SlideOutDown)
                    .repeat(0)
                    .duration(500)
                    .playOn(cardView);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    YoYo.with(Techniques.FadeOut)
                            .repeat(0)
                            .duration(200)
                            .playOn(main_rl);
                    context.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }, 700);
        }
    }

    private void errorMessage(String title, String message){
        FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(context)
                .setimageResource(R.drawable.ic_warning)
                .setTextTitle(title)
                .setTitleColor(R.color.appColor)
                .setBody(message)
                .setPositiveColor(R.color.black)
                .setBackgroundColor(R.color.white)
                .setNegativeColor(R.color.black90)
                .setPositiveButtonText("Okay")
                .setCancelable(false)
                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        back();
                    }
                })
                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                .setCancelable(false)
                .build();
        alert.show();
    }

    private void show(){
        spinKitView.setVisibility(View.VISIBLE);
    }
    private void dismiss(){
        spinKitView.setVisibility(View.GONE);
    }


    // end
}
