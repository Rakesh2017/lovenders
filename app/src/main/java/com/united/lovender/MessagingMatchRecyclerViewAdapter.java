package com.united.lovender;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class MessagingMatchRecyclerViewAdapter extends RecyclerView.Adapter<MessagingMatchRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<DataForRecyclerView> UploadInfoList;

    MessagingMatchRecyclerViewAdapter(Context context, List<DataForRecyclerView> TempList) {

        this.UploadInfoList = TempList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewlist_matches, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DataForRecyclerView info = UploadInfoList.get(position);

        Glide.with(context)
                .load(info.getImage_url())
                .apply(new RequestOptions().placeholder(R.drawable.circle).error(R.drawable.circle))
                .into(holder.imageView);

        if (TextUtils.equals(info.getType(), "Like")){
            holder.typeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_match_));
        }
        else if (TextUtils.equals(info.getType(), "SuperLike")){
            holder.typeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.super_like_heart_match));
        }



        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckNetworkConnection(context, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        openMessaging(info);
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                        showToastyMessage.warningMessage("no internet connection");
                    }
                }).execute();

            }
        });

    }

//    open messaging activity
    private void openMessaging(final DataForRecyclerView info) {
        if (TextUtils.isEmpty(info.getMatch_UID())){
            return;
        }

        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        String uid = mySharedPrefs.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user_chat_record");

        databaseReference.child(uid).child(info.getMatch_UID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intent intent = new Intent(context, Messaging.class);
                intent.putExtra("name", info.getMatch_name());
                intent.putExtra("image_url", info.getImage_url());
                intent.putExtra("match_uid", info.getMatch_UID());
                intent.putExtra("start_at", dataSnapshot.child("start_at").getValue(String.class));
                intent.putExtra("ProfileStatus", info.getDelete_status());
                context.startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        private CircleImageView imageView;
        private ImageView typeImage;

        ViewHolder(View itemView) {
            super(itemView);

          imageView = itemView.findViewById(R.id.lm_image);
          typeImage = itemView.findViewById(R.id.lm_type);
        }
    }
//end

}