package com.united.lovender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChattingRecyclerViewAdapter extends RecyclerView.Adapter<ChattingRecyclerViewAdapter.ViewHolder> {

    private final List<DataForRecyclerView> UploadInfoList;
    private Context context;
    private DatabaseReference databaseReference_CHATS = FirebaseDatabase.getInstance().getReference().child("chats");
    private DatabaseReference databaseReference_LAST_MESSAGE_STATUS = FirebaseDatabase.getInstance().getReference().child("last_message_status");
    ChattingRecyclerViewAdapter(Context context, List<DataForRecyclerView> TempList) {

        this.UploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewlist_chatting_list, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final DataForRecyclerView info = UploadInfoList.get(position);

        MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
        final String my_uid = mySharedPrefs.getUid();
        final String match_uid = info.getMatch_UID();
        final String chat_key;

        if (match_uid.compareTo(my_uid) < 0) {
            chat_key = match_uid + my_uid; // keep match uid in front
        } else chat_key = my_uid + match_uid;

        holder.name_tv.setText(info.getMatch_name());

        try {
            Glide.with(context)
                    .load(info.getMatch_profile_image_url())
                    .apply(new RequestOptions().error(R.drawable.circle).placeholder(R.drawable.circle))
                    .into(holder.circleImageView);
        } catch (Exception e) {
            //
        }

//        last message update
        Query lastQuery = databaseReference_CHATS.child(chat_key).orderByKey().limitToLast(1);
        lastQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String message = dataSnapshot.child("message").getValue(String.class);
                    holder.message_tv.setText(message);
                    String address = dataSnapshot.child("address").getValue(String.class);
                    if (!TextUtils.isEmpty(address)) {
                        holder.message_tv.setText(address);
                    }

                }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        databaseReference_LAST_MESSAGE_STATUS.child(chat_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String status = dataSnapshot.child(my_uid).getValue(String.class);

                if (TextUtils.equals(status, "seen")) {
                    holder.new_message_ib.setVisibility(View.GONE);
                } else if (TextUtils.equals(status, "sent")) {
                    holder.new_message_ib.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        open messaging activity
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckNetworkConnection(context, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        Intent intent = new Intent(context, Messaging.class);
                        intent.putExtra("name", info.getMatch_name());
                        intent.putExtra("image_url", info.getMatch_profile_image_url());
                        intent.putExtra("match_uid", match_uid);
                        intent.putExtra("start_at", info.getStart_at());
                        intent.putExtra("ProfileStatus", info.getDelete_status());
                        context.startActivity(intent);
                        holder.new_message_ib.setVisibility(View.GONE);
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                        showToastyMessage.warningMessage("no internet connection");
                    }
                }).execute();

            }
        });


       // delete message
        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                new MaterialDialog.Builder(context)
                        .title("Delete chat")
                        .titleColor(Color.BLACK)
                        .content("Do you want to delete the chat with "+info.getMatch_name()+" ?")
                        .contentColor(context.getResources().getColor(R.color.black))
                        .backgroundColor(context.getResources().getColor(R.color.white))
                        .positiveColor(context.getResources().getColor(R.color.googleRed))
                        .positiveText("Delete")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                        .getReference().child("user_chat_record");
                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance()
                                        .getReference().child("chats");

                                String chat_key;
                                if (match_uid.compareTo(my_uid) < 0){
                                    chat_key = match_uid+my_uid; // keep match uid in front
                                }
                                else chat_key = my_uid+match_uid;

                                Query query = databaseReference1.child(chat_key).limitToLast(1);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String last_push_key;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            last_push_key = snapshot.getKey();
                                            databaseReference.child(my_uid).child(match_uid).child("delete_status").setValue("deleted");
                                            databaseReference.child(my_uid).child(match_uid).child("start_at").setValue(last_push_key);
                                            databaseReference.child(my_uid).child(match_uid).child("delete_timestamps").push().child("timestamp").setValue(ServerValue.TIMESTAMP);
                                            databaseReference.child(my_uid).child(match_uid).child("delete_timestamps").push().child("last_chat_key").setValue(last_push_key);

                                            try {
                                                YoYo.with(Techniques.SlideOutRight)
                                                        .repeat(0)
                                                        .duration(500)
                                                        .playOn(holder.relativeLayout);
                                            }
                                            catch (Exception e){
                                                ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
                                                showToastyMessage.warningMessage("try again!");
                                            }


                                            holder.relativeLayout.removeView(v);
                                            UploadInfoList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, UploadInfoList.size());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        })
                        .negativeColor(context.getResources().getColor(R.color.lightGreen))
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return false;

            }
        });

    }

    // Insert a new item to the RecyclerView on a predefined position
//    public void insert(int position, Data data) {
//        list.add(position, data);
//        notifyItemInserted(position);
//    }

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

        private CircleImageView circleImageView;
        private TextView name_tv, message_tv;
        private RelativeLayout relativeLayout;
        private ImageButton new_message_ib;

        ViewHolder(View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.clx_profile_image);
            name_tv = itemView.findViewById(R.id.clx_user_name);
            message_tv = itemView.findViewById(R.id.clx_message);
            relativeLayout = itemView.findViewById(R.id.clx_relative_layout);
            new_message_ib = itemView.findViewById(R.id.clx_new_message);

        }
    }
//end
}