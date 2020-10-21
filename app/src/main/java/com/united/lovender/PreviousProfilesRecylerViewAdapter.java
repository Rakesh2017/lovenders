package com.united.lovender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;

public class PreviousProfilesRecylerViewAdapter extends RecyclerView.Adapter<PreviousProfilesRecylerViewAdapter.ViewHolder> {

    private final List<DataForRecyclerView> UploadInfoList;
    private Context context;


    PreviousProfilesRecylerViewAdapter(Context context, List<DataForRecyclerView> TempList) {

        this.UploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewlist_previous_profiles, parent, false);

        return new ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DataForRecyclerView info = UploadInfoList.get(position);

        Glide.with(context)
                .load(info.getImage_url())
                .into(holder.imageView);

        holder.name.setText(info.getMatch_name());
        holder.age.setText(info.getAge());

        if (TextUtils.equals(info.getType(), "Like")){
            Glide.with(context)
                    .load(R.drawable.heart_match_white)
                    .into(holder.type);
        }
        else if (TextUtils.equals(info.getType(), "SuperLike")){
            Glide.with(context)
                    .load(R.drawable.super_like_heart_match_white)
                    .into(holder.type);
        }

        holder.image_overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileDetails.class);
                intent.putExtra("id", info.getMatch_UID());
                intent.putExtra("UID", info.getMatch_UID());
                intent.putExtra("type", info.getType());
                intent.putExtra("name", info.getMatch_name());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return UploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, age;
        private ImageView imageView, type, image_overlay;

        ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ppx_image);
            type = itemView.findViewById(R.id.ppx_like_type);
            name = itemView.findViewById(R.id.ppx_name);
            age = itemView.findViewById(R.id.ppx_age);
            image_overlay = itemView.findViewById(R.id.ppx_image_overlay);

        }
    }
//end
}