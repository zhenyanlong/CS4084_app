package com.example.cs4084_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cs4084_app.model.UserModel;
import com.example.cs4084_app.utills.FirebaseUtill;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private EditText  usernameInput;
    private Button submitButton;
    private String phoneNumber;
    private EditText phoneNumberEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        phoneNumberEditText = findViewById(R.id.editTextPhoneNumber);
        usernameInput = findViewById(R.id.editTextUsername);
        submitButton = findViewById(R.id.buttonSubmit);

        // Set click listener for submit button
        //submitButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
                //submitProfileChanges();
            //}
        //});
        getUsername();
        submitButton.setOnClickListener((v) ->{
            setUsername();
            finish();
        });

        // Load user data if available
        //loadUserData();
    }
    void getUsername(){
        FirebaseUtill.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    userModel = task.getResult().toObject(UserModel.class);
                    if(userModel!=null){
                        usernameInput.setText(userModel.getUsername());
                        phoneNumberEditText.setText(userModel.getPhone());
                    }
                }
            }
        });
    }
    void setUsername(){
        String username = usernameInput.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        if(username.isEmpty() || username.length() < 3){
            usernameInput.setError("Username length should be at least 3 chars");
            return ;
        }

        if(userModel!=null){
            userModel.setUsername(username);
        }
        else{
            userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtill.currentUserId());

        }

        FirebaseUtill.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                if(task.isSuccessful()){

//                    Intent intent = new Intent(EditProfileActivity.this, MainActivity2.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
                    //finish();
                }
            }
        });
    }

}