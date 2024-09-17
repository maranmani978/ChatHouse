package com.murugamani.example.chathouse.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.murugamani.example.chathouse.R;
import com.murugamani.example.chathouse.ChatHouseUsers;

import java.util.List;

/**
 * Created by Murugamani on 2/17/2018.
 */
public class ChatPeopleAdapter extends ArrayAdapter<ChatHouseUsers> {

    public ChatPeopleAdapter(@NonNull Context context, @NonNull List<ChatHouseUsers> objects) {
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.list_chat_people_item,parent,false);
        }

        TextView nameTextView = convertView.findViewById(R.id.users_name);
        TextView mailTextView = convertView.findViewById(R.id.users_mail);

        ChatHouseUsers chatHouseUsers = getItem(position);

        nameTextView.setText(chatHouseUsers.getPerson_name());
        mailTextView.setText(chatHouseUsers.getUser_phone_number());

        return convertView;
    }
}
