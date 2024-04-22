package com.example.cs4084_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class PersonalStoreActivity extends AppCompatActivity {
    private static final String TAG = PersonalStoreActivity.class.getSimpleName();

    private ListView listView;
    private PersonalProductAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_store);

        listView = findViewById(R.id.listViewPersonalStore);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 检查并获取位置权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            getLastLocation();
        }
        initCloseButton();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        }
    }

    // 获取最后一次位置信息
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;
                            loadProductsFromFirestore();
                        } else {
                            Toast.makeText(PersonalStoreActivity.this, "Location not detected. Make sure location is enabled on the device.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }



    private void loadProductsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Get the current user's UID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user != null ? user.getUid() : null;
        db.collection("products")
                .whereEqualTo("uid",uid )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        product.setItemID(document.getId());
                        products.add(product);
//                        String imageUrl = document.getString("imageUrl");
//                        String name = document.getString("name");
//                        String shortDescription = document.getString("shortDescription");
//                        String location = document.getString("location");
//                        Double rawPrice = document.getDouble("price");
//                        float price = rawPrice != null ? rawPrice.floatValue() : 0f;
//                        Double latitude = document.getDouble("latitude");
//                        Double longitude = document.getDouble("longitude");
//
//                        if (latitude != null && longitude != null) {
//                            Product product = new Product(
//                                    imageUrl,
//                                    name,
//                                    shortDescription,
//                                    price,
//                                    location,
//                                    latitude,
//                                    longitude
//                            );
//                            products.add(product);
//                        }
                    }
                    adapter = new PersonalProductAdapter(PersonalStoreActivity.this, R.layout.personal_product_item, products);
                    listView.setAdapter(adapter);
//                    if (currentLocation != null) {
//                        adapter = new ProductAdapter(PersonalStoreActivity.this, R.layout.product_item, products);
//                        adapter.setCurrentLatitude(currentLocation.getLatitude());
//                        adapter.setCurrentLongitude(currentLocation.getLongitude());
//                        listView.setAdapter(adapter);
//                    } else {
//                        Toast.makeText(PersonalStoreActivity.this, "Current location is not available.", Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    Log.w(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
    private void initCloseButton(){
        Button closeButton=findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
