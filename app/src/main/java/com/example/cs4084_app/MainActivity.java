package com.example.cs4084_app;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private Double [] currentLocation= {0.0, 0.0};
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int ADD_PRODUCT_REQUEST = 2;  // Request code
    private static final int YOUR_PERMISSION_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE=101;
    private String[] data={"apple","pear","strawberry","Orange","watermelon","apple","pear","strawberry","Orange",
            "watermelon","apple","pear","strawberry","Orange","watermelon","apple","pear","strawberry","Orange","watermelon","apple","pear","strawberry","Orange",
            "watermelon","apple","pear","strawberry","Orange","watermelon"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 执行点击事件的代码，比如跳转到新的Activity或者显示一个Toast消息
                Toast.makeText(MainActivity.this, "FAB Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, CreateItemActivity.class);
                startActivityForResult(intent, ADD_PRODUCT_REQUEST);
            }
        });

        FirebaseApp.initializeApp(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        initCurrentLocation();
            initList();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, YOUR_PERMISSION_CODE);
//        }
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        //initFruits();
        //initdb();

        //initImage();
//        ProductAdapter adapter=new ProductAdapter(MainActivity.this,R.layout.fruit_item,productList);
//        ListView listView=(ListView) findViewById(R.id.list_view);
//        listView.setAdapter(adapter);
        //set Create button

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
                            ProductAdapter adapter = new ProductAdapter(MainActivity.this, R.layout.product_item, productList,currentLocation[0],currentLocation[1]);
                            listView = (ListView) findViewById(R.id.list_view);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Product clickedProduct = productList.get(position);

                                    Intent detailIntent = new Intent(MainActivity.this, ItemDetailedActivity.class);

                                    // Passing data to the ProductDetailActivity
                                    detailIntent.putExtra("name", clickedProduct.getName());
                                    detailIntent.putExtra("short_description", clickedProduct.getShortDescription());
                                    detailIntent.putExtra("long_description", clickedProduct.getLongDescription());
                                    detailIntent.putExtra("price", clickedProduct.getPrice());
                                    detailIntent.putExtra("location", clickedProduct.getLocation());
                                    detailIntent.putExtra("imageURl", clickedProduct.getImageUrl());
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // 使用这个 URI 进行上传操作
            uploadImageToFirebase(imageUri);
        }
        if (requestCode == ADD_PRODUCT_REQUEST) {
            // Check if the result was a success and that data might have changed
            if (resultCode == RESULT_OK) {
                // Refresh your data here
                initList();
            }
        }
    }
    private void uploadImageToFirebase(Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("uploads/" + System.currentTimeMillis() + ".jpg");
        fileRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // 上传成功
                        Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 上传失败
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void initCurrentLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);

            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {
                    locationClient.getLastLocation().addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLocation[0] = location.getLatitude();
                            currentLocation[1] = location.getLongitude();
                        } else {
                            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (SecurityException e) {
                Toast.makeText(this, "Location service error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}