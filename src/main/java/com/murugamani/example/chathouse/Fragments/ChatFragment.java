package com.murugamani.example.chathouse.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.murugamani.example.chathouse.Adapters.ChatPeopleAdapter;
import com.murugamani.example.chathouse.ChatActivity;
import com.murugamani.example.chathouse.ChatHouseUsers;
import com.murugamani.example.chathouse.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class ChatFragment extends Fragment {

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        return rootView;
    }


}
