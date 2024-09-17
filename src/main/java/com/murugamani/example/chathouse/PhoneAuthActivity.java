package com.murugamani.example.chathouse;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private String phoneNumber,phoneVerificationId,name,password;

    private EditText code;
    private Button verify;
    private TextView resend,extraTextview;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userReference;

    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        Intent receiveIntent = getIntent();
        phoneNumber = receiveIntent.getExtras().getString("phone");
        name = receiveIntent.getExtras().getString("name");
        password = receiveIntent.getExtras().getString("pass");

        progressDialog = new ProgressDialog(PhoneAuthActivity.this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        userReference = mFirebaseDatabase.getReference().child("users");

        sendCode();

        verify = findViewById(R.id.verify);
        code = findViewById(R.id.sms_code);
        resend = findViewById(R.id.resend);
        extraTextview = findViewById(R.id.receive);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(code.getText().toString())){
                    code.requestFocus();
                    code.setError(getString(R.string.empty_otp));
                }else {
                    verifyCode();
                }
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extraTextview.setVisibility(View.GONE);
                resend.setVisibility(View.GONE);
                resendCode();
            }
        });

    }

    private void sendCode(){

        progressDialog.setTitle("Please Wait!!!");
        progressDialog.setMessage("Sending OTP...");
        progressDialog.show();

        verificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(PhoneAuthActivity.this, "Invalid Request"+phoneNumber, Toast.LENGTH_LONG).show();
                }else if (e instanceof FirebaseTooManyRequestsException){
                    Toast.makeText(PhoneAuthActivity.this, "sms Quota Exceed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                phoneVerificationId = s;
                resendingToken = forceResendingToken;
                Toast.makeText(PhoneAuthActivity.this,"OTP Successfully sent to "+phoneNumber,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                verificationStateChangedCallbacks
        );
    }

    private void resendCode(){

        progressDialog.setTitle("Please Wait!!!");
        progressDialog.setMessage("ReSending OTP...");
        progressDialog.show();

        verificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(PhoneAuthActivity.this, "Invalid Request"+e.getMessage()+phoneNumber, Toast.LENGTH_LONG).show();
                }else if (e instanceof FirebaseTooManyRequestsException){
                    Toast.makeText(PhoneAuthActivity.this, "sms Quota Exceeded", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                phoneVerificationId = s;
                resendingToken = forceResendingToken;
                Toast.makeText(PhoneAuthActivity.this,"OTP Successfully resent to "+phoneNumber,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                verificationStateChangedCallbacks,
                resendingToken
        );
    }

    private void verifyCode() {
        String Code = code.getText().toString();

        progressDialog.setTitle("Please Wait!!!");
        progressDialog.setMessage("Verifying...");
        progressDialog.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, Code);

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            user.delete().addOnCompleteListener(PhoneAuthActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task1) {
                                    if (task1.isSuccessful()){
                                        createAccount();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(PhoneAuthActivity.this,"Error in User Creation",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void createAccount(){
        progressDialog.setMessage("Registering Account...");
        mFirebaseAuth.createUserWithEmailAndPassword(phoneNumber+"@chathouse.com",password)
                .addOnCompleteListener(PhoneAuthActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task2) {
                        if (task2.isSuccessful()){
                            FirebaseUser user1 = task2.getResult().getUser();
                            intent(user1);
                        }else {
                            Toast.makeText(PhoneAuthActivity.this,"Error in Account Creation",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }


    private void intent(FirebaseUser user1){
        progressDialog.setMessage("Creating Account...");
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user1.updateProfile(changeRequest)
                .addOnCompleteListener(PhoneAuthActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task3) {
                        if (task3.isSuccessful()){
                            Toast.makeText(PhoneAuthActivity.this,"Account Created Successfully:)",Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(PhoneAuthActivity.this,"Error in Account Details",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
        ChatHouseUsers chatHouseUsers = new ChatHouseUsers(name, phoneNumber,null, user1.getUid());
        userReference.child(user1.getUid()).setValue(chatHouseUsers);
        progressDialog.dismiss();
        Intent intent = new Intent(PhoneAuthActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are sure want to cancel OTP Verification??\n\nNote: (You cannot use "+phoneNumber+" to verify for one Hour)");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(PhoneAuthActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
