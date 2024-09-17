package com.murugamani.example.chathouse.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.murugamani.example.chathouse.Messages;
import com.murugamani.example.chathouse.R;

import java.util.List;

/**
 * Created by Murugamani on 3/6/2018.
 */

public class MessageAdapter extends ArrayAdapter<Messages> {

    String n;
    Messages messages;

    public MessageAdapter(@NonNull Context context,String choice, @NonNull List<Messages> objects) {
        super(context, 0, objects);
        n = choice;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        messages = getItem(position);

        if (convertView == null){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_message_right,parent,false);
            if (messages.getUser_id().equals(n)){
                convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_message_left,parent,false);
            }
        }

        TextView chatTextView = convertView.findViewById(R.id.msg);
        TextView timeTextView = convertView.findViewById(R.id.msg_time);
        ImageView imageView = convertView.findViewById(R.id.photoImageView);

        boolean isPhoto = messages.getPhoto_url() != null;
        if (isPhoto){
            imageView.setVisibility(View.VISIBLE);
            chatTextView.setVisibility(View.INVISIBLE);
            Glide.with(imageView.getContext())
                    .load(messages.getPhoto_url())
                    .into(imageView);
        }else {
            chatTextView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            chatTextView.setText(messages.getChat_msg());
        }
        timeTextView.setText(messages.getChat_time());

        return convertView;

    }
}
