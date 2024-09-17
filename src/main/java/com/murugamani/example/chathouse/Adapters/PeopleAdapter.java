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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.murugamani.example.chathouse.ChatHouseUsers;
import com.murugamani.example.chathouse.R;

import java.util.List;

/**
 * Created by Murugamani on 3/5/2018.
 */

public class PeopleAdapter extends ArrayAdapter<ChatHouseUsers> {

    private DatabaseReference currentUserSendDatabaseReference,currentUserReceivedDatabaseReference,otherUserSendDatabaseReference,
            otherUserReceivedDatabaseReference,currentUserFriendReference,otherUserFriendReference;
    private FirebaseUser user;
    private Button sendRequest;
    private TextView friendsTextView,nameTextView,numberTextView,idTextView;
    private FirebaseDatabase firebaseDatabase;
    private ChatHouseUsers chatHouseUsers;

    public PeopleAdapter(@NonNull Context context, @NonNull List<ChatHouseUsers> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.list_people_item,parent,false);
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        chatHouseUsers = getItem(position);

        sendRequest = convertView.findViewById(R.id.req);
        nameTextView = convertView.findViewById(R.id.person_name);
        friendsTextView = convertView.findViewById(R.id.friends);
        numberTextView = convertView.findViewById(R.id.person_number);
        idTextView = convertView.findViewById(R.id.user_id);

        nameTextView.setText(chatHouseUsers.getPerson_name());
        idTextView.setText(chatHouseUsers.getUser_id());

        currentUserSendDatabaseReference = firebaseDatabase.getReference().child(user.getUid()).child("peoples").child("sendRequests");
        currentUserReceivedDatabaseReference = firebaseDatabase.getReference().child(user.getUid()).child("peoples").child("receivedRequests");
        currentUserFriendReference = firebaseDatabase.getReference().child(user.getUid()).child("peoples").child("friends");

        friendsTextView.setVisibility(View.GONE);
        numberTextView.setVisibility(View.GONE);

        final View finalConvertView = convertView;

        currentUserSendDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatHouseUsers users = getItem(position);
                if (dataSnapshot.getKey().equals(users.getUser_id())){
                    sendRequest = finalConvertView.findViewById(R.id.req);
                    sendRequest.setText("Cancel Request");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        currentUserReceivedDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatHouseUsers users = getItem(position);
                if (dataSnapshot.getKey().equals(users.getUser_id())){
                    sendRequest = finalConvertView.findViewById(R.id.req);
                    sendRequest.setText("Accept");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        currentUserFriendReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatHouseUsers users = getItem(position);
                if (dataSnapshot.getKey().equals(users.getUser_id())){
                    sendRequest = finalConvertView.findViewById(R.id.req);
                    friendsTextView = finalConvertView.findViewById(R.id.friends);
                    numberTextView = finalConvertView.findViewById(R.id.person_number);
                    sendRequest.setVisibility(View.GONE);
                    numberTextView.setVisibility(View.VISIBLE);
                    numberTextView.setText(chatHouseUsers.getUser_phone_number());
                    friendsTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatHouseUsers = getItem(position);

                currentUserSendDatabaseReference = firebaseDatabase.getReference().child(user.getUid()).child("peoples").child("sendRequests");
                currentUserReceivedDatabaseReference = firebaseDatabase.getReference().child(user.getUid()).child("peoples").child("receivedRequests");
                currentUserFriendReference = firebaseDatabase.getReference().child(user.getUid()).child("peoples").child("friends");

                otherUserFriendReference = firebaseDatabase.getReference().child(chatHouseUsers.getUser_id()).child("peoples").child("friends");
                otherUserReceivedDatabaseReference = firebaseDatabase.getReference().child(chatHouseUsers.getUser_id()).child("peoples").child("receivedRequests");
                otherUserSendDatabaseReference = firebaseDatabase.getReference().child(chatHouseUsers.getUser_id()).child("peoples").child("sendRequests");

                sendRequest = finalConvertView.findViewById(R.id.req);
                friendsTextView = finalConvertView.findViewById(R.id.friends);
                numberTextView = finalConvertView.findViewById(R.id.person_number);

                if (sendRequest.getText().toString().equals("Send a Request")){
                    currentUserSendDatabaseReference.child(chatHouseUsers.getUser_id()).setValue(chatHouseUsers);
                    otherUserReceivedDatabaseReference.child(user.getUid()).setValue(new ChatHouseUsers(user.getDisplayName(),user.getEmail().substring(0,user.getEmail().length()-14),null,user.getUid()));
                    sendRequest.setText("Cancel Request");
                    numberTextView.setVisibility(View.GONE);
                    friendsTextView.setVisibility(View.GONE);
                }else if (sendRequest.getText().toString().equals("Cancel Request")){
                    currentUserSendDatabaseReference.child(chatHouseUsers.getUser_id()).removeValue();
                    otherUserReceivedDatabaseReference.child(user.getUid()).removeValue();
                    sendRequest.setText("Send a Request");
                    numberTextView.setVisibility(View.GONE);
                    friendsTextView.setVisibility(View.GONE);
                }else {
                    currentUserReceivedDatabaseReference.child(chatHouseUsers.getUser_id()).removeValue();
                    otherUserSendDatabaseReference.child(user.getUid()).removeValue();
                    currentUserFriendReference.child(chatHouseUsers.getUser_id()).setValue(chatHouseUsers);
                    otherUserFriendReference.child(user.getUid()).setValue(new ChatHouseUsers(user.getDisplayName(),user.getEmail().substring(0,user.getEmail().length()-14),null,user.getUid()));
                    sendRequest.setVisibility(View.GONE);
                    numberTextView.setVisibility(View.VISIBLE);
                    numberTextView.setText(chatHouseUsers.getUser_phone_number());
                    friendsTextView.setVisibility(View.VISIBLE);
                }

            }
        });


        return convertView;
    }
}
