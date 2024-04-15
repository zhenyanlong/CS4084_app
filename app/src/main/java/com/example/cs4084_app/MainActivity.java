package com.example.cs4084_app;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private List<Product> productList;
    private ListView listView;
    private String[] data={"apple","pear","strawberry","Orange","watermelon","apple","pear","strawberry","Orange",
            "watermelon","apple","pear","strawberry","Orange","watermelon","apple","pear","strawberry","Orange","watermelon","apple","pear","strawberry","Orange",
            "watermelon","apple","pear","strawberry","Orange","watermelon"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        //initFruits();
        //initdb();
        initList();
        //initImage();
//        ProductAdapter adapter=new ProductAdapter(MainActivity.this,R.layout.fruit_item,productList);
//        ListView listView=(ListView) findViewById(R.id.list_view);
//        listView.setAdapter(adapter);
    }
    private void initFruits(){
        for (int i = 0; i < data.length; i++) {
//            Product product = new Product( R.drawable.e,data[i], "Description for " + data[i], "$9.99", "Location for " + data[i]);
//            productList.add(product);
        }
    }
    private void initdb(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Create a new product with attributes
        Map<String, Object> product = new HashMap<>();
        product.put("name", "Apple");
        product.put("short_description", "Fresh apple");
        product.put("long_description","long_long_description...........");
        product.put("price", 1.99);
        product.put("category","food");
        product.put("is_sold",false);
        product.put("location", "Orchard");
        product.put("imageUrl","example.jpg");

// Add a new document with a generated ID
        db.collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
    private void initList(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .whereEqualTo("is_sold", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            productList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Product product = document.toObject(Product.class);
                                productList.add(product);
                            }
                            // Update your adapter with this productList
                            ProductAdapter adapter = new ProductAdapter(MainActivity.this, R.layout.product_item, productList);
                            listView = (ListView) findViewById(R.id.list_view);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Product clickedProduct = productList.get(position);

                                    Intent detailIntent = new Intent(MainActivity.this, ItemDetailedActivity.class);

                                    // Passing data to the ProductDetailActivity
                                    detailIntent.putExtra("name", clickedProduct.getName());
                                    detailIntent.putExtra("short_description", clickedProduct.getShort_description());
                                    detailIntent.putExtra("long_description", clickedProduct.getLong_description());
                                    detailIntent.putExtra("price", clickedProduct.getPrice());
                                    detailIntent.putExtra("location", clickedProduct.getLocation());
//                                    // If you're passing an image ID, make sure it's passed correctly and received in the ProductDetailActivity.
//                                    // If your images are stored as resource IDs (like R.drawable.image_name), you can pass them directly.
//                                    detailIntent.putExtra("imageId", clickedProduct.getImageId());

                                    startActivity(detailIntent);
                                }
                            });
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }
    public void initImage(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("drawable/a.jpg");

        Uri file = Uri.fromFile(new File("I:/AndroidExample/CS4084_app/app/src/main/res/drawable/a.jpg"));
        UploadTask uploadTask = imageRef.putFile(file);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        saveImageUrlToFirestore(imageUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        });
    }
    public void saveImageUrlToFirestore(String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", imageUrl);

        db.collection("images").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("Firestore", "DocumentSnapshot written with ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firestore", "Error adding document", e);
            }
        });
    }

}