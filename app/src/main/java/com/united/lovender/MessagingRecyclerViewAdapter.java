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
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class MessagingRecyclerViewAdapter extends RecyclerView.Adapter<MessagingRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<DataForRecyclerView> UploadInfoList;

    private DatabaseReference databaseReference = FirebaseDatabase
            .getInstance().getReference().child("chats");


    MessagingRecyclerViewAdapter(Context context, List<DataForRecyclerView> TempList) {

        this.UploadInfoList = TempList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewlist_messaging, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DataForRecyclerView info = UploadInfoList.get(position);

        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);

        final String sender_uid;
        final String uid = mySharedPrefs.getUid();
        sender_uid = info.getMy_uid();  // getMy_uid contains sender_uid

        final String chat_key = mySharedPrefs.getChatKey();

        if (TextUtils.equals(sender_uid, uid)){
            holder.messageByMe.setText(info.getMessage());
            holder.relativeLayout2.setVisibility(View.VISIBLE);
            set_tick(info.getStatus(), holder);
            if (!TextUtils.isEmpty(info.getAddress())){
                holder.messageByMe.setTextColor(context.getResources().getColor(R.color.crispyBlue));
                holder.messageByMe.setText(info.getAddress());
                holder.messageByMe.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_location_placeholder), null, null, null);
            }
        }
        else {
            holder.messageReceived.setText(info.getMessage());
            holder.relativeLayout1.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(info.getAddress())){
                holder.messageReceived.setTextColor(context.getResources().getColor(R.color.crispyBlue));
                holder.messageReceived.setText(info.getAddress());
                holder.messageReceived.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_location_placeholder), null, null, null);
            }
        }

        holder.relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(info.getAddress())) {
                    Intent intent = new Intent(context, SeeLocation.class);
                    intent.putExtra("address", info.getAddress());
                    intent.putExtra("latitude", info.getLatitude());
                    intent.putExtra("longitude", info.getLongitude());
                    context.startActivity(intent);
                }
            }
        });

        holder.relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(info.getAddress())) {
                    Intent intent = new Intent(context, SeeLocation.class);
                    intent.putExtra("address", info.getAddress());
                    intent.putExtra("latitude", info.getLatitude());
                    intent.putExtra("longitude", info.getLongitude());
                    context.startActivity(intent);
                }
            }
        });

        databaseReference.child(chat_key).child(info.getPush_key())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String status = dataSnapshot.child("status").getValue(String.class);
                        if (TextUtils.equals(status, "seen")){
                            set_tick(status, holder);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


      //  setTicksREALTIME(match_uid, uid, info, holder, chat_key);

    }



    private void set_tick(String status, ViewHolder viewHolder){
        if (TextUtils.isEmpty(status))
            viewHolder.tick.setBackgroundResource(R.drawable.ic_single_tick);
        else if (TextUtils.equals(status, "seen"))
            viewHolder.tick.setBackgroundResource(R.drawable.ic_done_tick_seen);
    }


    private String getTime(long message_date){
        long offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a, d/MMM/yy");
        long now = message_date + offset;
        return formatter.format(message_date + offset);
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

        private TextView messageReceived, messageByMe, dateTime1, dateTime2, date_header;
        private RelativeLayout relativeLayout1, relativeLayout2;
        private ImageView tick;

        ViewHolder(View itemView) {
            super(itemView);

            messageByMe = itemView.findViewById(R.id.rcl_textMessageFromMe);
            messageReceived = itemView.findViewById(R.id.rcl_textMessageFromReceiver);
            dateTime1 = itemView.findViewById(R.id.rcl_dateTime1);
            dateTime2 = itemView.findViewById(R.id.rcl_dateTime2);
            tick = itemView.findViewById(R.id.rcl_tick);

            relativeLayout2 = itemView.findViewById(R.id.rcl_relativeLayout2);
            relativeLayout1 = itemView.findViewById(R.id.rcl_relativeLayout1);

        }
    }
//end
}