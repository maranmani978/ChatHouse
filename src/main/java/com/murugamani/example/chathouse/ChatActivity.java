package com.murugamani.example.chathouse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.murugamani.example.chathouse.Adapters.MessageAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {

    private String chosenUser,chosenUserName,chat_messages,chat_time;

    private Calendar calendar;

    private ImageButton sendButton;
    private ImageView imageView;
    private ListView chatList;
    private EditText chatBox;

    private MessageAdapter messageAdapter;

    private ProgressDialog progressDialog;

    private FirebaseUser user;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference currentUserReference,otherUserReference;
    private ChildEventListener childEventListener;
    private StorageReference storageReference,storageReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle info = this.getIntent().getExtras();

        imageView = findViewById(R.id.pic);
        progressDialog = new ProgressDialog(this);

        user = FirebaseAuth.getInstance().getCurrentUser();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(intent,"complete action using"),2);
            }
        });

        if (info != null){
            chosenUser = info.getString("chosen_id");
            chosenUserName = info.getString("chosen_name");
        }

        sendButton = findViewById(R.id.send_message);
        chatBox = findViewById(R.id.message_writer);
        chatList = findViewById(R.id.chat_messages);

        sendButton.setEnabled(false);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        currentUserReference = mFirebaseDatabase.getReference().child(user.getUid()).child("messages").child(chosenUser);
        otherUserReference = mFirebaseDatabase.getReference().child(chosenUser).child("messages").child(user.getUid());

        storageReference = FirebaseStorage.getInstance().getReference().child("chat_photos").child(user.getUid()).child(chosenUser);
        storageReference2 = FirebaseStorage.getInstance().getReference().child("chat_photos").child(chosenUser).child(user.getUid());

        chatBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length()>0){
                    sendButton.setEnabled(true);
                }else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        setTitle(chosenUserName);

        messageAdapter = new MessageAdapter(this,chosenUser,new ArrayList<Messages>());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chat_messages = chatBox.getText().toString().trim();

                calendar = Calendar.getInstance();
                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                chat_time = format1.format(calendar.getTime());

                Messages messages = new Messages(user.getDisplayName(),chat_messages,chat_time,null,user.getUid());
                currentUserReference.push().setValue(messages);
                otherUserReference.push().setValue(messages);

                chatBox.setText("");
                chatList.setAdapter(messageAdapter);
            }
        });

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messageAdapter.add(messages);
                chatList.setAdapter(messageAdapter);
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
        };
        currentUserReference.addChildEventListener(childEventListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            StorageReference reference = storageReference.child(selectedImageUri.getLastPathSegment());
            reference.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    calendar = Calendar.getInstance();
                    SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                    chat_time = format1.format(calendar.getTime());

                    Messages messages1 = new Messages(user.getDisplayName(),null,chat_time,downloadUri.toString(),user.getUid());
                    currentUserReference.push().setValue(messages1);
                    otherUserReference.push().setValue(messages1);
                    progressDialog.dismiss();
                    Toast.makeText(ChatActivity.this,"Image Uploaded",Toast.LENGTH_LONG).show();
                }
            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatActivity.this,"ygh "+String.valueOf(e.getMessage()),Toast.LENGTH_LONG).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double process = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded"+(int)process);
                        }
                    });
        }else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(ChatActivity.this);
    }
}
