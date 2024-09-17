package com.murugamani.example.chathouse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private TextView signUp;
    private EditText phoneNo,pass;
    private Button Login;

    private FirebaseAuth mFirebaseAuth;
    private String phoneNumber,password;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUp = findViewById(R.id.create);
        Login = findViewById(R.id.log_in);
        phoneNo = findViewById(R.id.phone_number);
        pass = findViewById(R.id.user_password);

        progressDialog = new ProgressDialog(LoginActivity.this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (networkAccess()) {
                    if (verify()) {
                        progressDialog.setMessage("Verifying Details");
                        progressDialog.show();

                        phoneNumber = phoneNo.getText().toString().trim() + "@chathouse.com";
                        password = pass.getText().toString().trim();

                        mFirebaseAuth.signInWithEmailAndPassword(phoneNumber, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivityForResult(intent,1);
                                            progressDialog.dismiss();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                }else {
                    Snackbar snackbar = Snackbar.make(view,"Please Check your Network Connectivity!!!",Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1){
            if (resultCode == RESULT_CANCELED){
                finish();
            }
        }
    }

    private boolean verify(){
        phoneNumber = phoneNo.getText().toString().trim();
        password = pass.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)){
            phoneNo.requestFocus();
            phoneNo.setError(getString(R.string.empty_phone_number));
            return false;
        }

        if (phoneNumber.length()!=10){
            phoneNo.requestFocus();
            phoneNo.setError(getString(R.string.invalid_phone));
            return false;
        }

        if (TextUtils.isEmpty(password)){
            pass.requestFocus();
            pass.setError(getString(R.string.empty_login_password));
            return false;
        }
        return true;
    }


    private boolean networkAccess() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
