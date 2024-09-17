package com.murugamani.example.chathouse.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.murugamani.example.chathouse.Adapters.FriendRequestAdapter;
import com.murugamani.example.chathouse.ChatHouseUsers;
import com.murugamani.example.chathouse.R;

import java.util.ArrayList;

public class FriendRequestFragment extends Fragment {

    private View rootView;

    private ListView listView;

    private FriendRequestAdapter friendRequestAdapter;
    private DatabaseReference friendRequestReference;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_friend_request, container, false);

        listView = rootView.findViewById(R.id.request_list);

        friendRequestAdapter = new FriendRequestAdapter(getContext(),new ArrayList<ChatHouseUsers>());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        friendRequestReference = mFirebaseDatabase.getReference().child(user.getUid()).child("peoples").child("receivedRequests");

        listView.setAdapter(friendRequestAdapter);

        friendRequestReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatHouseUsers chatHouseUsers = dataSnapshot.getValue(ChatHouseUsers.class);
                friendRequestAdapter.add(chatHouseUsers);
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


        return rootView;
    }

}
