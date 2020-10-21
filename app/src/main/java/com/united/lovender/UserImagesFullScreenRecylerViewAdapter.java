package com.united.lovender;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flaviofaria.kenburnsview.KenBurnsView;

import java.util.List;


public class UserImagesFullScreenRecylerViewAdapter extends RecyclerView.Adapter<UserImagesFullScreenRecylerViewAdapter.ViewHolder> {

    private final List<DataForRecyclerView> UploadInfoList;
    private Context context;


    UserImagesFullScreenRecylerViewAdapter(Context context, List<DataForRecyclerView> TempList) {

        this.UploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewlist_user_images_full_screen, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DataForRecyclerView info = UploadInfoList.get(position);

        Glide.with(context)
                .load(info.getImage_url())
                .apply(new RequestOptions()
                        .fitCenter()
                        .error(R.drawable.ic_placeholder_profile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(holder.imageView);

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

        private ImageView imageView;
        ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ui_image_view);

        }
    }
//end
}