package com.united.lovender;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Chatting extends Fragment implements View.OnClickListener {

    private FragmentActivity context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<DataForRecyclerView> list = new ArrayList<>();
    private RecyclerView recyclerViewMATCH;
    private RecyclerView.Adapter adapterMATCH;
    private List<DataForRecyclerView> listMATCH = new ArrayList<>();
    private ArrayList<String> matchUIDList = new ArrayList<>();
    private HashMap<String, String> STARTATHM = new HashMap<>();
    private RelativeLayout no_internet_rl, no_message_rl;
    private CircularImageView no_internet_image;
    private RelativeLayout become_pro_rl;
    private SwipeRefreshLayout pullToRefresh;
    private TextView no_match_tv;
    private SpinKitView spinKitViewMatch, spinKitViewChat, spinKitView;
    private String uid;
    private DatabaseReference databaseReferenceUSER_CHAT_RECORD = FirebaseDatabase.getInstance().getReference().child("user_chat_record");

    public Chatting() {
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
        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        uid = mySharedPrefs.getUid();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       return inflater.inflate(R.layout.fragment_chatting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.cx_recycler_view_messages);

        no_internet_rl = view.findViewById(R.id.cx_no_internet_rl);
        spinKitViewMatch = view.findViewById(R.id.cx_spin_kit_match);
        spinKitView = view.findViewById(R.id.cx_activate_pro_spin_kit);
        no_message_rl = view.findViewById(R.id.cx_no_message_rl);
        no_internet_image = view.findViewById(R.id.cx_no_internet_image);
        become_pro_rl = view.findViewById(R.id.cx_become_pro_rl);
        no_match_tv = view.findViewById(R.id.cx_no_match_tv);
        spinKitViewChat = view.findViewById(R.id.cx_spin_kit_chat);
        Button activate_pro_b = view.findViewById(R.id.cx_activate_pro_b);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerViewMATCH = view.findViewById(R.id.cx_recycler_view_match);
        recyclerViewMATCH.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        setProfilePhoto();

        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        refresh();

        pullToRefresh.setColorSchemeColors(context.getResources().getColor(R.color.red)
                , context.getResources().getColor(R.color.facebookBlue));


        checkInternetThenGetMatches();


        no_internet_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckNetworkConnection(context, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        no_internet_rl.setVisibility(View.GONE);
                        try{

                            MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                            boolean pro_status = mySharedPrefs.getProUserStatus();
                            if (pro_status){
                                become_pro_rl.setVisibility(View.GONE);
                            } else {
                                become_pro_rl.setVisibility(View.VISIBLE);
                            }
                            checkIfProUser();

                        } catch (Exception | OutOfMemoryError ignored){}

                        try {
                            getMatches();
                        } catch (Exception | OutOfMemoryError ignored){}

                        try {
                            getChatList();
                        } catch (Exception | OutOfMemoryError ignored){}

                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        no_internet_rl.setVisibility(View.VISIBLE);
                    }
                }).execute();
            }
        });

        activate_pro_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlan();
            }
        });

        try {
            checkIfProUser();
        } catch (Exception | OutOfMemoryError ignored){}


        try{
            getChatList();
        }
        catch (Exception | OutOfMemoryError ignored){ }

    }

//    refresh
    private void refresh(){
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CheckNetworkConnection(context, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        no_internet_rl.setVisibility(View.GONE);
                        // getting only matches, not chats, as chats are managed real time
                        getMatches();
                        try{
                            getChatList();
                        }
                        catch (Exception ignored){

                        }
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        no_internet_rl.setVisibility(View.VISIBLE);
                        pullToRefresh.setRefreshing(false);
                    }
                }).execute();
            }
        });
    }
    //    refresh

//    get matches
    private void checkInternetThenGetMatches(){
        new CheckNetworkConnection(context, new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                no_internet_rl.setVisibility(View.GONE);
                try{
                    getMatches();
                }
                catch (Exception | OutOfMemoryError ignored){ }
            }
            @Override
            public void onConnectionFail(String msg) {
                no_internet_rl.setVisibility(View.VISIBLE);
            }
        }).execute();
    }
//    get matches

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            setDashBoardStatus();
        }

        try{
            if (!isVisibleToUser){
                Fragment fragment = context.getSupportFragmentManager().findFragmentByTag("our_plans");
                if(fragment != null){
                    context.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
        } catch (Exception ignored){

        }

    }

//    setting dashboard status
    private void setDashBoardStatus() {
        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        mySharedPrefs.setDashboardStatus(false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("dashboard_status");
        databaseReference.child(uid).setValue("seen");
    }
//    setting dashboard status


    public void onResume(){
        super.onResume();
    }

    //    set profile photo
    private void setProfilePhoto() {

        final MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final String old_url = mySharedPrefs.getPhotoUrl();
        try {
            Glide.with(context)
                    .load(old_url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_placeholder_profile)
                            .error(R.drawable.ic_placeholder_profile)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .into(no_internet_image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    check if pro user
    private void checkIfProUser() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "ProStatus");
            jsonObject.put("UID", uid);

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

                            if (code == 1313) {
                                boolean result = response.getBoolean("result");
                                MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                                if (result){
                                    become_pro_rl.setVisibility(View.GONE);
                                    mySharedPrefs.setProUserStatus(true);
                                    pullToRefresh.setEnabled(true);
                                }
                                else {
                                    become_pro_rl.setVisibility(View.VISIBLE);
                                    mySharedPrefs.setProUserStatus(false);
                                    pullToRefresh.setEnabled(false);
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
//                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
//                        showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }
//    check if pro user


//    getting matches
    private void getMatches(){

        showMatch();
        listMATCH.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "GetSuperSwipe");
            jsonObject.put("UID", uid);

        } catch (Exception e) {
            e.printStackTrace();
            pullToRefresh.setRefreshing(false);
            dismissMatch();
        }

        String url = getString(R.string.api_name);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final int code = response.getInt("code");

                            if (code == 1313){
                                String result = response.getString("result");

                                JSONArray jsonArray = new JSONArray(result);
                                final int length = jsonArray.length();
                                for (int i=0; i<length ; i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    DataForRecyclerView dataForRecyclerView = new DataForRecyclerView();
                                    dataForRecyclerView.setImage_url(object.getString("ProfileImage"));
                                    dataForRecyclerView.setType(object.getString("Type"));
                                    dataForRecyclerView.setMatch_name(object.getString("Name"));
                                    dataForRecyclerView.setMatch_UID(object.getString("UID"));
                                    dataForRecyclerView.setDelete_status(object.getString("ProfileStatus"));
                                    listMATCH.add(dataForRecyclerView);
                                }

                                no_match_tv.setVisibility(View.GONE);
                                if (listMATCH.isEmpty()){
                                    no_match_tv.setVisibility(View.VISIBLE);
                                }

                                adapterMATCH = new MessagingMatchRecyclerViewAdapter(context, listMATCH);
                                recyclerViewMATCH.setAdapter(adapterMATCH);
                                dismissMatch();
                            }
                            else if (code == 1920){
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                showToastyMessage.errorMessage(response.getString("msg"));
                                dismissMatch();
//
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                showToastyMessage.errorMessage("Request not completed !");
                                dismissMatch();
                            }
                            pullToRefresh.setRefreshing(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pullToRefresh.setRefreshing(false);
                            dismissMatch();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pullToRefresh.setRefreshing(false);
                        dismissMatch();
//                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
//                        showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);
    }


//    getting match uids
    private void getChatList() {
       showCHAT();
       Query query = databaseReferenceUSER_CHAT_RECORD.child(uid).orderByChild("timestamp");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        STARTATHM.clear();
                        matchUIDList.clear();
                        list.clear();
                        adapter = null;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if (!TextUtils.equals(snapshot.child("delete_status").getValue(String.class), "deleted")) {
                                matchUIDList.add(snapshot.getKey());
                                STARTATHM.put(snapshot.getKey(), snapshot.child("start_at").getValue(String.class));
                            }
                        }
                        getMatchNameAndImage();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
//    getting match uids


//    get message details
    private void  getMatchNameAndImage() {

        Collections.reverse(matchUIDList);
        final int size = matchUIDList.size();

        if (size == 0){
            recyclerView.setVisibility(View.GONE);
            no_message_rl.setVisibility(View.VISIBLE);
            dismissCHAT();
            return;
        }

        no_message_rl.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

            if (context == null){
                return;
            }

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("auth", "api!luv@13_9002");
                jsonObject.put("request", "GetProfileName");
                jsonObject.put("UID", matchUIDList);
            } catch (Exception e) {
                e.printStackTrace();
                dismissCHAT();
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
                                        String match_name = object.getString("Name");
                                        String profile_image = object.getString("ProfileImage");
                                        String delete_status = object.getString("ProfileStatus");

                                        DataForRecyclerView dataForRecyclerView = new DataForRecyclerView();
                                        dataForRecyclerView.setMatch_UID(matchUIDList.get(i));
                                        dataForRecyclerView.setMatch_name(match_name);
                                        dataForRecyclerView.setMatch_profile_image_url(profile_image);
                                        dataForRecyclerView.setStart_at(STARTATHM.get(matchUIDList.get(i)));
                                        dataForRecyclerView.setDelete_status(delete_status);

                                        list.add(dataForRecyclerView);

                                    }

                                    adapter = new ChattingRecyclerViewAdapter(context, list);
                                    recyclerView.setAdapter(adapter);
                                    dismissCHAT();
                                  //  list.clear();

                                }
                                else if (code == 1920){
                                    ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                    showToastyMessage.errorMessage(response.getString("msg"));
                                    dismissCHAT();
                                }
                                else {
                                    ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                    showToastyMessage.errorMessage("Request not completed !");
                                    dismissCHAT();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                dismissCHAT();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissCHAT();
//                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
//                        showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                        }

                    });

            jsonObjectRequest.setTag(1);
            requestQueue.add(jsonObjectRequest);


    }
//    get message details

    //    get buying plans
    private void getPlan() {

        show();
        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final String uid = mySharedPrefs.getUid();

        UserCurrentLocation userCurrentLocation = new UserCurrentLocation(context);
        final String country_name = userCurrentLocation.getUserCurrentCountry();
        String symbol = "dollar";
        try {
            if (TextUtils.equals(country_name.toLowerCase(), "india")){
                symbol = "Rs";
            }
        }
        catch (Exception ignored){

        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "GetPlans");
            jsonObject.put("UID", uid);
            jsonObject.put("Symbol", symbol);

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

                            if (code == 1313){
                                String result = response.getString("result");

                                JSONArray jsonArray = new JSONArray(result);
                                String month_advance = null, price_advance = null, offer_advance = null
                                        , month_basic= null, price_basic= null, offer_basic= null
                                        , month_premium= null, price_premium= null, offer_premium= null, symbol = "dollar";

                                for (int i = 0 ; i < jsonArray.length() ; i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    symbol = object.getString("Symbol");
                                    if (TextUtils.equals(object.getString("Plan"),"Advance")){
                                        month_advance = object.getString("Month");
                                        price_advance = object.getString("Price");
                                        offer_advance = object.getString("Offer");
                                    } else if (TextUtils.equals(object.getString("Plan"),"Basic")) {
                                        month_basic = object.getString("Month");
                                        price_basic = object.getString("Price");
                                        offer_basic = object.getString("Offer");
                                    } else if (TextUtils.equals(object.getString("Plan"),"Premium")) {
                                        month_premium = object.getString("Month");
                                        price_premium = object.getString("Price");
                                        offer_premium = object.getString("Offer");
                                    }
                                }

                                Bundle bundle = new Bundle();
                                bundle.putString("month_advance", month_advance);
                                bundle.putString("price_advance", price_advance);
                                bundle.putString("offer_advance", offer_advance);
                                bundle.putString("month_basic", month_basic);
                                bundle.putString("price_basic", price_basic);
                                bundle.putString("offer_basic", offer_basic);
                                bundle.putString("month_premium", month_premium);
                                bundle.putString("price_premium", price_premium);
                                bundle.putString("offer_premium", offer_premium);
                                bundle.putString("symbol", symbol);
                                OurPlans ourPlans = new OurPlans();
                                ourPlans.setArguments(bundle);
                                context.getSupportFragmentManager().beginTransaction()
                                        .add(R.id.fragment_container_chatting, ourPlans, "our_plans").addToBackStack(null).commit();

                                dismiss();
                            }
                            else if (code == 1920){
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                showToastyMessage.errorMessage(response.getString("msg"));
                                dismiss();
//
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
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
//    get buying plans

    public void onDestroy(){
        super.onDestroy();
       // requestQueue.cancelAll(1);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    private void showMatch(){
        spinKitViewMatch.setVisibility(View.VISIBLE);
    }
    private void dismissMatch(){
        spinKitViewMatch.setVisibility(View.GONE);
    }

    private void showCHAT(){
        spinKitViewChat.setVisibility(View.VISIBLE);
    }
    private void dismissCHAT(){
        spinKitViewChat.setVisibility(View.GONE);
    }

    private void show(){
        spinKitView.setVisibility(View.VISIBLE);
    }
    private void dismiss(){
        spinKitView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

    }


//    end
}
