package com.example.cs4084_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProductsMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double initialLat;
    private double initialLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_map);

        initialLat = getIntent().getDoubleExtra("latitude", 0);
        initialLng = getIntent().getDoubleExtra("longitude", 0);
        Button backButton=findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng initialPosition = new LatLng(initialLat, initialLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 10));

        loadProductLocations();
    }

    private void loadProductLocations() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    double lat = document.getDouble("latitude");
                    double lng = document.getDouble("longitude");
                    LatLng productLocation = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(productLocation).title(document.getString("name")));
                }
            } else {
                Log.w("MapActivity", "Error getting documents.", task.getException());
            }
        });
    }
}