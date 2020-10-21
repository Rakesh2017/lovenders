package com.united.lovender;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.bluzwong.swipeback.SwipeBackActivityHelper;

import java.util.List;


public class UserImagesRecyclerViewAdapter extends RecyclerView.Adapter<UserImagesRecyclerViewAdapter.ViewHolder> {

    private final List<DataForRecyclerView> UploadInfoList;
    private Context context;

    UserImagesRecyclerViewAdapter(Context context, List<DataForRecyclerView> TempList) {

        this.UploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewlist_user_images, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DataForRecyclerView info = UploadInfoList.get(position);



        Glide.with(context)
                .load(info.getImage_url())
                .apply(new RequestOptions()
                        .fitCenter()
                        //.placeholder(R.drawable.loading)
                       // .error(R.drawable.ic_placeholder_profile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(holder.imageView);



        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ShowUserImagesFullScreen.class);
                intent.putExtra("image1", info.getImage1());
                intent.putExtra("image2", info.getImage2());
                intent.putExtra("image3", info.getImage3());
                intent.putExtra("image4", info.getImage4());
                intent.putExtra("image5", info.getImage5());
                intent.putExtra("name", info.getName());
                intent.putExtra("about", info.getAbout());
                intent.putExtra("adapter_position", holder.getAdapterPosition());
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

        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ui_image_view);
        }
    }
//end
}