package com.example.cs4084_app;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class CreateItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MAP_REQUEST_CODE = 100;
    private static final int LOCATION_REQUEST_CODE=2;
    private Spinner spinnerCategory;
    private Uri imageUri;
    private String UID;
    private ImageView imageViewProduct;
    private TextView locationTextView;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private double latitude;
    private double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        setCurrentUID();
        imageViewProduct = findViewById(R.id.imageViewProduct);
        imageViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        Button mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
            Intent intent = new Intent(CreateItemActivity.this, MapActivity.class);
            startActivityForResult(intent, MAP_REQUEST_CODE);
        });
        initSpinner();
        initButton();
    }
    //initialize Create item button
    private void initButton(){
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Initialize ImageView and other UI components
        imageViewProduct = findViewById(R.id.imageViewProduct);
        //initialize locationTextView
        locationTextView=(TextView)findViewById(R.id.textViewLocation);
        // Set up ImageView onClickListener and other listeners

        Button addButton = findViewById(R.id.buttonAddProduct);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate input fields (not shown here for brevity)
                // ...
                if (locationTextView.getText().toString().isEmpty() ||
                        ((EditText) findViewById(R.id.editTextProductName)).getText().toString().isEmpty() ||
                        ((EditText) findViewById(R.id.editTextProductPrice)).getText().toString().isEmpty() ||
                        ((EditText) findViewById(R.id.editTextShortDescription)).getText().toString().isEmpty() ||
                        ((EditText) findViewById(R.id.editTextLongDescription)).getText().toString().isEmpty()) {
                    Toast.makeText(CreateItemActivity.this, "Please input all of options.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // First, upload the image
                if (imageUri != null) {
                    StorageReference fileReference = storageReference.child("images/" + UUID.randomUUID().toString());
                    fileReference.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get the download URL and then add the product to Firestore
                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            addProductToFirestore(uri.toString());
                                            Intent returnIntent = new Intent();
                                            setResult(RESULT_OK, returnIntent);
                                            finish();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle unsuccessful uploads
                                    // ...

                                }
                            });
                } else {
                    // If there is no image selected, you can choose to add the product without an image
                    // or inform the user that an image is required.
                    Toast.makeText(CreateItemActivity.this, "Please choose photo.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
    private void addProductToFirestore(String imageUrl) {
        // Get the data from the input fields
        String name = ((EditText) findViewById(R.id.editTextProductName)).getText().toString();
        float price = Float.parseFloat(((EditText) findViewById(R.id.editTextProductPrice)).getText().toString());
        String shortDescription = ((EditText) findViewById(R.id.editTextShortDescription)).getText().toString();
        String longDescription = ((EditText) findViewById(R.id.editTextLongDescription)).getText().toString();
        //String location = ((EditText) findViewById(R.id.editTextLocation)).getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();

        // Get the current user's UID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user != null ? user.getUid() : null;

        // Create the product object or map
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("price", price);
        product.put("shortDescription", shortDescription);
        product.put("longDescription", longDescription);
        //product.put("location", location);
        product.put("category", category);
        product.put("is_sold",false);
        product.put("uid", uid); // Include the user's UID
        product.put("imageUrl", imageUrl); // Include the image URL
        product.put("latitude",latitude);
        product.put("longitude",longitude);
        // Add the product to Firestore
        db.collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Product added successfully
                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                        // ...
                    }
                });
    }
    //initialize spinner
    private void initSpinner(){
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.product_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewProduct.setImageURI(imageUri);
        }
        if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK) {
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
            // Use these coordinates as needed, e.g., show them on the UI or store them
            locationTextView.setText(String.format(Locale.getDefault(), "%.2f", latitude)+","+String.format(Locale.getDefault(), "%.2f", longitude));
        }
    }
    private void setCurrentUID(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UID = user.getUid(); // This is the UID you can associate with the product
        }
    }
}