package com.murugamani.example.chathouse.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.murugamani.example.chathouse.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.murugamani.example.chathouse.ChatHouseUsers;

import java.util.List;

/**
 * Created by Murugamani on 2/18/2018.
 */

public class FriendRequestAdapter extends ArrayAdapter<ChatHouseUsers> {

    private DatabaseReference currentUserReceivedDatabaseReference,otherUserSendDatabaseReference,currentUserFriendReference,otherUserFriendReference;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private ChatHouseUsers chatHouseUsers;
    private Button accept;
    private TextView nameTextView,mailTextView;

    public FriendRequestAdapter(@NonNull Context context, @NonNull List<ChatHouseUsers> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.list_request_item,parent,false);
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        chatHouseUsers = getItem(position);

        nameTextView = convertView.findViewById(R.id.name);
        mailTextView = convertView.findViewById(R.id.mail);

        nameTextView.setText(chatHouseUsers.getPerson_name());
        mailTextView.setText(chatHouseUsers.getUser_phone_number());

        accept = convertView.findViewById(R.id.accept);

        final View finlConvertView = convertView;

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameTextView = finlConvertView.findViewById(R.id.name);
                mailTextView = finlConvertView.findViewById(R.id.mail);
                accept = finlConvertView.findViewById(R.id.accept);

                currentUserReceivedDatabaseReference = firebaseDatabase.getReference().child(user.getUid()).child("peoples").child("receivedRequests");
                currentUserFriendReference = firebaseDatabase.getReference().child(user.getUid()).child("peoples").child("friends");

                otherUserFriendReference = firebaseDatabase.getReference().child(chatHouseUsers.getUser_id()).child("peoples").child("friends");
                otherUserSendDatabaseReference = firebaseDatabase.getReference().child(chatHouseUsers.getUser_id()).child("peoples").child("sendRequests");

                currentUserReceivedDatabaseReference.child(chatHouseUsers.getUser_id()).removeValue();
                otherUserSendDatabaseReference.child(user.getUid()).removeValue();
                currentUserFriendReference.child(chatHouseUsers.getUser_id()).setValue(chatHouseUsers);
                otherUserFriendReference.child(user.getUid()).setValue(new ChatHouseUsers(user.getDisplayName(),user.getEmail().substring(0,user.getEmail().length()-14),null,user.getUid()));

                nameTextView.setVisibility(View.GONE);
                mailTextView.setVisibility(View.GONE);
                accept.setVisibility(View.GONE);
            }
        });

        return convertView;
    }
}
