package com.united.lovender;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserImages extends Fragment {


    FragmentActivity context;
    private List<DataForRecyclerView> list = new ArrayList<>();

    public UserImages() {
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
        return inflater.inflate(R.layout.fragment_user_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.ui_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);

        if (list != null) {
            list.clear();  // v v v v important (eliminate duplication of data)
        }
        DataForRecyclerView dataForRecyclerView = new DataForRecyclerView();
        dataForRecyclerView.setImage_url("https://www.thefamouspeople.com/profiles/images/amanda-cerny-3.jpg");
        list.add(dataForRecyclerView);
        // Creating RecyclerView.Adapter.
        RecyclerView.Adapter adapter = new UserImagesFullScreenRecylerViewAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
        dataForRecyclerView.setImage_url("https://www.thefamouspeople.com/profiles/images/amanda-cerny-3.jpg");
        list.add(dataForRecyclerView);
        adapter = new UserImagesFullScreenRecylerViewAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
        dataForRecyclerView.setImage_url("https://www.thefamouspeople.com/profiles/images/amanda-cerny-3.jpg");
        list.add(dataForRecyclerView);
        adapter = new UserImagesFullScreenRecylerViewAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

}
