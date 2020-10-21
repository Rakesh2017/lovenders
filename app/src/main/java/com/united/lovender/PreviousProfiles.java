package com.united.lovender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ybq.android.spinkit.SpinKitView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PreviousProfiles extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private List<DataForRecyclerView> list = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private SpinKitView spinKitView;
    private TextView no_match_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_previous_profiles);

        recyclerView = findViewById(R.id.pp_recyclerView);
        no_match_tv = findViewById(R.id.pp_no_match_tv);
        spinKitView = findViewById(R.id.pp_spin_kit);
        ImageButton back_ib = findViewById(R.id.pp_back_ib);
        recyclerView.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();

        GridLayoutManager staggeredGridLayoutManager = new GridLayoutManager(PreviousProfiles.this, 2);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        int spanCount = 2; // 3 columns
        int spacing = 20; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        back_ib.setOnClickListener(this);

        getData();
    }

    private void getData(){

        show();
        RequestQueue requestQueue = Volley.newRequestQueue(PreviousProfiles.this);
        JSONObject jsonObject = new JSONObject();

        MySharedPrefs mySharedPrefs = new MySharedPrefs(this);
        final String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "GetAllSwipe");
            jsonObject.put("UID", uid);
        } catch (Exception e) {
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

                            list.clear();
                            if (code == 1313){
                                String result = response.getString("result");

                                JSONArray jsonArray = new JSONArray(result);
                                final int size = jsonArray.length();
                                for (int i =0 ; i<size ; i++){
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    DataForRecyclerView dataForRecyclerView = new DataForRecyclerView();
                                    dataForRecyclerView.setImage_url(object.getString("ProfileImage"));
                                    dataForRecyclerView.setType(object.getString("Type"));
                                    dataForRecyclerView.setMatch_name(object.getString("Name"));
                                    dataForRecyclerView.setMatch_UID(object.getString("UID"));
                                    dataForRecyclerView.setAge(object.getString("Age"));

                                    list.add(dataForRecyclerView);
                                    adapter = new PreviousProfilesRecylerViewAdapter(PreviousProfiles.this, list);

                                }
                                recyclerView.setAdapter(adapter);

                                if (list.isEmpty()){
                                    no_match_tv.setVisibility(View.VISIBLE);
                                    YoYo.with(Techniques.ZoomIn)
                                            .repeat(0)
                                            .duration(500)
                                            .playOn(no_match_tv);
                                }

                                dismiss();

                            }
                            else if (code == 1920){
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(PreviousProfiles.this);
                                showToastyMessage.errorMessage(response.getString("msg"));
                                dismiss();
//
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(PreviousProfiles.this);
                                showToastyMessage.errorMessage("Request not completed !");
                                dismiss();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismiss();
//                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
//                        showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation, R.anim.exit);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.pp_back_ib){
            onBackPressed();
        }

    }

    private void show(){
        spinKitView.setVisibility(View.VISIBLE);
    }
    private void dismiss(){
        spinKitView.setVisibility(View.GONE);
    }

//    end
}
