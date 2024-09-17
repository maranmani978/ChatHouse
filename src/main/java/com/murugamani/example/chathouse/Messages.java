package com.murugamani.example.chathouse;

/**
 * Created by Murugamani on 3/6/2018.
 */

public class Messages {

    private String user_name;
    private String chat_msg;
    private String chat_time;
    private String photo_url;
    private String user_id;

    public Messages(){}

    public Messages(String name,String msg,String time,String photo,String id){
        user_name = name;
        chat_msg = msg;
        chat_time = time;
        photo_url = photo;
        user_id = id;
    }

    public String getUser_name(){ return  user_name; }
    public String getChat_msg(){ return chat_msg; }
    public String getChat_time(){ return chat_time; }
    public String getPhoto_url(){ return  photo_url; }
    public String getUser_id(){ return user_id; }

}
