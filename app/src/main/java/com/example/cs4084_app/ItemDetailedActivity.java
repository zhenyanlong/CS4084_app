package com.example.cs4084_app;

import static android.content.ContentValues.TAG;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ItemDetailedActivity extends AppCompatActivity {
    private EditText commentInput;
    private Button submitButton;
    private String uid;
    private String name;
    private String short_description;
    private double latitude;
    private double longitude;
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter adapter;
    private List<String> comments = new ArrayList<>();
    private FirebaseFirestore db;
    private TextView nameText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detailed);
        // Retrieve data from the intent
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String name = getIntent().getStringExtra("name");
        String shortDescription = getIntent().getStringExtra("short_description");
        String longDescription=getIntent().getStringExtra("long_description");
        float price = getIntent().getFloatExtra("price",0.0f);
        String location = getIntent().getStringExtra("location");
        String imageURl = getIntent().getStringExtra("imageURl"); // 0 is a default value
        latitude=getIntent().getDoubleExtra("latitude",0);
        longitude=getIntent().getDoubleExtra("longitude",0);
        uid=getIntent().getStringExtra("uid");
        ImageView imageView = findViewById(R.id.image);
        TextView nameView = findViewById(R.id.Name_text);
        nameView.setText(name);
        TextView shortDescriptionView = findViewById(R.id.short_description_text);
        shortDescriptionView.setText(shortDescription);
        TextView longDescriptionView = findViewById(R.id.long_description_text);
        longDescriptionView.setText(longDescription);
        TextView priceView = findViewById(R.id.price_text);
        priceView.setText(String.format(Locale.getDefault(), "£%.2f", price));
        if (imageURl != null && !imageURl.isEmpty()) {
            Glide.with(this)
                    .load(imageURl)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.d); // Default image if URL is empty
        }
        //close function
        Button returnButton = findViewById(R.id.close_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This will close the current activity and return to the previous one
                finish();
            }
        });
        initMapButton();
        initCommentSubmit();

        initComments();
        initSellerInfo();
    }
    private void initMapButton(){
        Button mapButton = findViewById(R.id.btnShowAllProductsMap);
        mapButton.setOnClickListener(view -> {
            Intent intent = new Intent(ItemDetailedActivity.this, ProductsMapActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
        });
    }
    private void initCommentSubmit(){
        commentInput = findViewById(R.id.commentEditText);
        submitButton = findViewById(R.id.commentSubmitButton);

        db = FirebaseFirestore.getInstance();

        submitButton.setOnClickListener(view -> {
            String comment = commentInput.getText().toString();
            if (!comment.isEmpty()) {
                // Get the current user's UID
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user != null ? user.getUid() : null;
                // 创建一个新的 comment 对象
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("content", comment);
                commentMap.put("UID",uid);
                commentMap.put("itemID",getIntent().getStringExtra("itemID"));
                commentMap.put("timestamp", System.currentTimeMillis());

                // 添加文档到 "comments" 集合
                db.collection("comments").add(commentMap)
                        .addOnSuccessListener(documentReference -> {
                            // 处理上传成功
                            Toast.makeText(this, "Comment uploaded!", Toast.LENGTH_SHORT).show();
                            initComments();
                        })
                        .addOnFailureListener(e -> {
                            // 处理错误情况
                            Toast.makeText(this, "Error uploading comment!", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
    private void initSellerInfo(){
        nameText=findViewById(R.id.sellerName);
        db.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // User data found, fill the EditText fields
                                //String phoneNumber = document.getString("phoneNumber");

                                String username = document.getString("username");
                                nameText.setText(username);
                                Toast.makeText(ItemDetailedActivity.this, "Get username successful", Toast.LENGTH_SHORT).show();
                            } else {
                                // User data not found, do nothing
                                Toast.makeText(ItemDetailedActivity.this, "Failed to get username", Toast.LENGTH_SHORT).show();
                                Toast.makeText(ItemDetailedActivity.this, uid, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ItemDetailedActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void initComments(){
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentsAdapter(comments);
        commentsRecyclerView.setAdapter(adapter);

        String itemId=getIntent().getStringExtra("itemID");
        //String start = itemID; // 你的 itemID 或其开始部分
        //String end = itemID + "\uf8ff"; // 使用高值字符增加范围到所有可能的后缀


        db.collection("comments")
                .whereEqualTo("itemID",itemId)
                //.orderBy("timestamp")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            comments.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(Objects.equals( document.getString("itemID"), getIntent().getStringExtra("itemID"))){
                                    //Log.d("ItemDetailedActivity", "Item ID: " + document.getString("itemID"));
                                    //Log.d("ItemDetailedActivity", "Item ID: " + itemID);
                                    String comment = document.getString("content");
                                    comments.add(comment);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            //Log.d("ItemDetailedActivity", "Item ID: " + itemID);
                            Toast.makeText(ItemDetailedActivity.this, "Error getting comments.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}