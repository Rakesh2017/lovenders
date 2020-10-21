package com.united.lovender;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Likings extends AppCompatActivity {

    private ChipsInput chipsInput;
    private ArrayList<String> hashTagArrayList;
    private List<LikingChip> likingChipList;
    private SpinKitView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_likings);

        SlidrConfig config = new SlidrConfig
                .Builder()
                .position(SlidrPosition.LEFT)
                .build();

        Slidr.attach(this, config);

        chipsInput = findViewById(R.id.li_chips_input);
        ImageButton back_ib = findViewById(R.id.li_back_ib);
        loading = findViewById(R.id.li_spin_kit);
        Button update_btn = findViewById(R.id.li_update_btn);
        likingChipList = new ArrayList<>();

        back_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
                new CheckNetworkConnection(Likings.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        updateData();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        dismiss();
                        startActivity(new Intent(Likings.this, NoInternetActivity.class));
                    }
                }).execute();
            }
        });

        hashTagArrayList = new ArrayList<>();
        chipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                hashTagArrayList.add(chip.getLabel().toLowerCase());
                // chip added
                // newSize is the size of the updated selected chip list
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                hashTagArrayList.remove(chip.getLabel().toLowerCase());
                // chip removed
                // newSize is the size of the updated selected chip list
            }

            @Override
            public void onTextChanged(CharSequence text) {
                // text changed
                String t = text.toString();
                if (t.contains(" ")){
                    t = t.replaceAll(" ","").replaceAll(",","");
                    if(t.trim().length() > 2) {
                        if (t.length() < 15){
                            if (!hashTagArrayList.contains(t.toLowerCase())){
                                chipsInput.addChip(t.trim().toLowerCase(), t.trim().toLowerCase());
                            }
                        }
                    }
                }
            }
        });

    }

//    update data to database
    private void updateData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(Likings.this);
        final String uid = mySharedPrefs.getUid();

        int length = hashTagArrayList.size();
        StringBuilder listOfHashTags = new StringBuilder();
        final String final_list;
        for (int i = 0 ; i< length; i++){
            listOfHashTags.append(hashTagArrayList.get(i)).append(",");
        }

        final_list = listOfHashTags.toString();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "UpdateInterest");
            jsonObject.put("UID", uid);
            jsonObject.put("Interest", final_list);
        } catch (Exception e) {
            Toast.makeText(Likings.this, "m"+e.getMessage(), Toast.LENGTH_SHORT).show();

            e.printStackTrace();
            dismiss();
        }
        String url = getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final int code = response.getInt("code");
                            if (code == 1313){
                                //showDialogBox.successMessage("Updated", "");
                                showSuccessMessage();
                                if (TextUtils.isEmpty(final_list)) {
                                    mySharedPrefs.setLikings("");
                                }else {
                                    mySharedPrefs.setLikings(final_list.substring(0,final_list.length()-1));
                                }
                                dismiss();
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(Likings.this);
                                showToastyMessage.errorMessage("Something went wrong");
                                dismiss();
                            }

                        } catch (JSONException e) {
                            ShowToastyMessage showToastyMessage = new ShowToastyMessage(Likings.this);
                            showToastyMessage.errorMessage(e.toString());
                            dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                   //     ShowToastyMessage showToastyMessage = new ShowToastyMessage(Likings.this);
                   //     showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                        dismiss();
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }
    //    update data to database

    //    show profile updated message
    private void showSuccessMessage() {
        FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(Likings.this)
                .setimageResource(R.drawable.ic_profile_updated)
                .setTextSubTitle("Updated ")
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

    public void onStart(){
        super.onStart();
        setLikingsFromPrefs();
        getHashTagList();
    }

//    set liking from prefs
    private void setLikingsFromPrefs() {
        MySharedPrefs mySharedPrefs = new MySharedPrefs(Likings.this);
        HashMap<Integer, String> hashMap = mySharedPrefs.getEditProfile();
        String likings =  hashMap.get(9);
        if (TextUtils.isEmpty(likings)){
            return;
        }
        try{
            String[] items = new String[0];
            if (likings != null) {
                items = likings.split(",");
            }
            for (String item : items)
            {
                chipsInput.addChip(item, item);
            }
        }
        catch (Exception e){
            //
        }

    }
    //    set liking from prefs

    //    get has tag list
    public void getHashTagList(){
        show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();

        MySharedPrefs mySharedPrefs = new MySharedPrefs(Likings.this);
        final String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "GetHashTags");
            jsonObject.put("UID", uid);
        } catch (Exception e) {
            Toast.makeText(Likings.this, "m"+e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            dismiss();
        }

        String url = getString(R.string.api_name);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ShowDialogBox showDialogBox = new ShowDialogBox(Likings.this);
                        try {
                            final int code = response.getInt("code");
                            if (code == 1313){
                                String result = response.getString("result");
                                try {
                                    JSONArray jsonArray = new JSONArray(result);

                                    final int length = jsonArray.length();
                                    for (int i = 0 ; i<length ; i++){
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String HashTag;
                                        HashTag = object.getString("HashTag");

                                       // likingChipList = new ArrayList<LikingChip>();
                                        LikingChip likingChip = new LikingChip(HashTag);
                                        likingChipList.add(likingChip);
                                    }
                                    chipsInput.setFilterableList(likingChipList);
                                    dismiss();
                                } catch (JSONException e) {
                                    ShowToastyMessage showToastyMessage = new ShowToastyMessage(Likings.this);
                                    showToastyMessage.errorMessage("Something went wrong");
                                    e.printStackTrace();
                                    dismiss();
                                }
                            }
                            else {
                                showDialogBox.successMessage("Something went wrong", "");
                                dismiss();
                            }

                        } catch (JSONException e) {
                            ShowToastyMessage showToastyMessage = new ShowToastyMessage(Likings.this);
                            showToastyMessage.errorMessage(e.toString());
                            dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismiss();
                    //    ShowToastyMessage showToastyMessage = new ShowToastyMessage(Likings.this);
                    //    showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }
    //    get has tag list


    public void onBackPressed(){
        super.onBackPressed();

       // mChipListText.setText(listString);
        overridePendingTransition(R.anim.no_animation, R.anim.exit);
    }

    private void show(){
        loading.setVisibility(View.VISIBLE);
    }
    private void dismiss(){
        loading.setVisibility(View.GONE);
    }

//    end
}
