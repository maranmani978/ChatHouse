package com.murugamani.example.chathouse;

/**
 * Created by Murugamani on 3/4/2018.
 */

public class ChatHouseUsers {

    private String person_name;
    private String user_phone_number;
    private String user_id;
    private String pro_img_url;

    private ChatHouseUsers(){ }

    public ChatHouseUsers(String p_name, String phone, String img, String uid){
        person_name = p_name;
        user_phone_number = phone;
        pro_img_url = img;
        user_id = uid;
    }

    public String getPerson_name(){ return person_name; }
    public String getUser_phone_number(){ return user_phone_number; }
    public String getPro_img_url(){ return pro_img_url; }
    public String getUser_id(){ return user_id; }

}
