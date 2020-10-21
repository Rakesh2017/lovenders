package com.united.lovender;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import mehdi.sakout.fancybuttons.FancyButton;

public class IntroFragmentFour extends Fragment {

    FragmentActivity context;
    private SpinKitView spinKitView;

    public IntroFragmentFour() {
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
        return inflater.inflate(R.layout.fragment_intro_fragment_four, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FancyButton submit_btn = view.findViewById(R.id.ffx_continue);
        spinKitView = view.findViewById(R.id.ffx_spin_kit);

        ((Intro)context).enablePaging(false);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }


//    submit
    private void submit() {
        show();
        final MySharedPrefs mySharedPrefs = new MySharedPrefs(getContext());
        HashMap<Integer, String> hashMap = mySharedPrefs.getIntroProfileData();
        String  name, gender, body_type, dob, height, about, school, religion, zodiac;

        name = hashMap.get(1);
        gender = hashMap.get(2);
        body_type = hashMap.get(3);
        dob = hashMap.get(4);
        height = hashMap.get(5);
        about = hashMap.get(6);
        school = hashMap.get(7);
        religion = hashMap.get(8);
        zodiac = hashMap.get(9);


        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();


        final String uid = mySharedPrefs.getUid();

        try {
            jsonObject.put("auth", "api!luv@13_9002");
            jsonObject.put("request", "UpdateProfile");
            jsonObject.put("UID", uid);
            jsonObject.put("Name", name);
            jsonObject.put("About", about);
            jsonObject.put("JobTitle", "");
            jsonObject.put("Company", "");
            jsonObject.put("Education", school);
            jsonObject.put("Religion", religion);
            jsonObject.put("ZodiacSign", zodiac);
            jsonObject.put("BodyType", body_type);
            jsonObject.put("Gender", gender);
            jsonObject.put("Height", height);
            jsonObject.put("DOB", dob);
            jsonObject.put("Location", "");
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
                                dismiss();
                               mySharedPrefs.setLoggedInOrNot("true");
                               startActivity(new Intent(context, DashBoard.class));
                               context.finish();
                            }
                            else {
                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                showToastyMessage.errorMessage("Something went wrong");
                                dismiss();
                            }

                        } catch (JSONException e) {
                            ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                            showToastyMessage.errorMessage(e.toString());
                            dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismiss();
                    //    ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                    //    showToastyMessage.errorMessage(getResources().getString(R.string.volley_error));
                    }

                });

        jsonObjectRequest.setTag(1);
        requestQueue.add(jsonObjectRequest);

    }

    private void show(){
        spinKitView.setVisibility(View.VISIBLE);
    }

    private void dismiss(){
        spinKitView.setVisibility(View.GONE);
    }

//    end
}
