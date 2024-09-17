package com.murugamani.example.chathouse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    private Button signUp;
    private EditText ph,name,pass,repass;
    private String names,p,phone,rp;
    private ArrayList<String> users;

    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ph = findViewById(R.id.phone);
        signUp = findViewById(R.id.sign_up);
        name = findViewById(R.id.user_name);
        pass = findViewById(R.id.new_password);
        repass = findViewById(R.id.retype_password);

        users = new ArrayList<>();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userReference = mFirebaseDatabase.getReference().child("users");

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyDetails()){
                    Intent intent = new Intent(SignUpActivity.this,PhoneAuthActivity.class);
                    intent.putExtra("phone",ph.getText().toString().trim());
                    intent.putExtra("name",name.getText().toString().trim());
                    intent.putExtra("pass",pass.getText().toString().trim());
                    startActivity(intent);
                    name.setText("");
                    pass.setText("");
                    ph.setText("");
                    repass.setText("");
                    finish();
                }
            }
        });

        userReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatHouseUsers chatHouseUsers = dataSnapshot.getValue(ChatHouseUsers.class);
                users.add(chatHouseUsers.getUser_phone_number());
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
    }

    private boolean verifyDetails(){

        int smallLetter=0,capsLetter=0,number=0,symbol=0;

        names = name.getText().toString().trim();
        phone = ph.getText().toString().trim();
        p = pass.getText().toString().trim();
        rp = repass.getText().toString().trim();

        if (TextUtils.isEmpty(names)){
            name.requestFocus();
            name.setError(getString(R.string.empty_user));
            return false;
        }

        if (TextUtils.isEmpty(phone)){
            ph.requestFocus();
            ph.setError(getString(R.string.empty_phone));
            return false;
        }

        if (phone.length()!=10){
            ph.requestFocus();
            ph.setError(getString(R.string.invalid_phone));
            return false;
        }

        if (users.contains("+91"+phone)){
            ph.requestFocus();
            ph.setError(getString(R.string.acc_exists));
            return false;
        }

        if (TextUtils.isEmpty(p)){
            pass.requestFocus();
            pass.setError(getString(R.string.empty_password));
            return false;
        }

        if (p.length()>8) {
            for (int i=0;i<p.length();i++){
                if ((p.charAt(i)<=90)&&(p.charAt(i)>=65)){
                    capsLetter++;
                }else if ((p.charAt(i)>=97)&&(p.charAt(i)<=122)){
                    smallLetter++;
                }else if ((p.charAt(i)>=48)&&(p.charAt(i)<=57)){
                    number++;
                }else {
                    symbol++;
                }
            }

            if (number == 0) {
                pass.requestFocus();
                pass.setError(getString(R.string.missing_number));
                return false;
            } else if (smallLetter == 0) {
                pass.requestFocus();
                pass.setError(getString(R.string.missing_small));
                return false;
            } else if (capsLetter == 0) {
                pass.requestFocus();
                pass.setError(getString(R.string.missing_caps));
                return false;
            } else if (symbol == 0) {
                pass.requestFocus();
                pass.setError(getString(R.string.missing_symbol));
                return false;
            }
        }else {
            pass.requestFocus();
            pass.setError(getString(R.string.missing_length));
            return false;
        }

        if (!(p.equals(rp))){
            repass.requestFocus();
            repass.setError(getString(R.string.mismatch));
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
