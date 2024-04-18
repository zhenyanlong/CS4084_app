package com.example.cs4084_app;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends ArrayAdapter<Product> {
    private int resourceId;



    private double currentLatitude;



    private double currentLongitude;

    public ProductAdapter(Context context, int textViewResourceId, List<Product> objects,Double latitude,Double longitude) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        currentLatitude=latitude;
        currentLongitude=longitude;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = getItem(position); // Get current instance of Product class

        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        ImageView productImage = (ImageView) view.findViewById(R.id.product_image);
        TextView productName = (TextView) view.findViewById(R.id.product_name);
        TextView productDescription = (TextView) view.findViewById(R.id.product_description);
        TextView productPrice = (TextView) view.findViewById(R.id.product_price);
        TextView productLocation = (TextView) view.findViewById(R.id.product_location);

        //productImage.setImageResource(product.getImageResourceId());
        productName.setText(product.getName());
        productDescription.setText(product.getShortDescription());
        productPrice.setText(String.format(Locale.getDefault(), "£%.2f", product.getPrice()));
        //productLocation.setText(product.getLocation());
        //set URl
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(getContext())
                    .load(product.getImageUrl())
                    .into(productImage);
        } else {
            productImage.setImageResource(R.drawable.d); // Default image if URL is empty
        }
        //set location
        float distance = calculateDistance(currentLatitude, currentLongitude,
                product.getLatitude(), product.getLongitude());
        productLocation.setText(String.format(Locale.getDefault(), "%.2f km Away", distance));
        return view;
    }
    public static float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0] / 1000; // returns distance in kilometers
    }
    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }
    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }
}