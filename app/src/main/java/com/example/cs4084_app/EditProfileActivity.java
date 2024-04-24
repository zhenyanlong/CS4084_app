package com.example.cs4084_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private EditText phoneNumberEditText, usernameEditText;
    private Button submitButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        phoneNumberEditText = findViewById(R.id.editTextPhoneNumber);
        usernameEditText = findViewById(R.id.editTextUsername);
        submitButton = findViewById(R.id.buttonSubmit);

        // Set click listener for submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProfileChanges();
            }
        });

        // Load user data if available
        loadUserData();
    }
    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();

        // Get user data from Firestore
        db.collection("users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // User data found, fill the EditText fields
                                String phoneNumber = document.getString("phoneNumber");
                                String username = document.getString("userName");

                                phoneNumberEditText.setText(phoneNumber);
                                usernameEditText.setText(username);
                            } else {
                                // User data not found, do nothing
                            }
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void submitProfileChanges() {
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberEditText.setError("Phone number is required");
            phoneNumberEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required");
            usernameEditText.requestFocus();
            return;
        }

        // Get the current user ID
        String userId = mAuth.getCurrentUser().getUid();

        // Create a user object with the provided data
        //User user = new User(phoneNumber, username);
        // Create the product object or map
        Map<String, Object> user = new HashMap<>();
        user.put("phoneNumber",phoneNumber);
        user.put("userName",username);
        user.put("timestamp",System.currentTimeMillis());

        // Upload the user data to Firestore
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            // Finish the activity
                            finish();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}