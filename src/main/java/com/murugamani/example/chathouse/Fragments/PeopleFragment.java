package com.murugamani.example.chathouse.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.murugamani.example.chathouse.Adapters.PeopleAdapter;
import com.murugamani.example.chathouse.ChatActivity;
import com.murugamani.example.chathouse.ChatHouseUsers;
import com.murugamani.example.chathouse.MainActivity;
import com.murugamani.example.chathouse.R;

import java.util.ArrayList;


public class PeopleFragment extends Fragment {

    private View rootView;

    private ListView listView;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userReference;
    private FirebaseUser user;
    private PeopleAdapter peopleAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_people, container, false);

        listView = rootView.findViewById(R.id.list_people);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        userReference = mFirebaseDatabase.getReference().child("users");

        user = FirebaseAuth.getInstance().getCurrentUser();

        peopleAdapter = new PeopleAdapter(getContext(),new ArrayList<ChatHouseUsers>());
        listView.setAdapter(peopleAdapter);


        userReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatHouseUsers chatHouseUsers = dataSnapshot.getValue(ChatHouseUsers.class);
                if (!(dataSnapshot.getKey().equals(user.getUid()))){
                    peopleAdapter.add(chatHouseUsers);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if ((view.findViewById(R.id.friends)).getVisibility()==View.VISIBLE){
                    TextView nameTextView = ((TextView)view.findViewById(R.id.person_name));
                    TextView idTextView = ((TextView)view.findViewById(R.id.user_id));
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("chosen_id",idTextView.getText().toString());
                    bundle.putString("chosen_name",nameTextView.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });


        return rootView;
    }

}
