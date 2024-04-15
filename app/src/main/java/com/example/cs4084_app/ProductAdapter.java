package com.example.cs4084_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends ArrayAdapter<Product> {
    private int resourceId;

    public ProductAdapter(Context context, int textViewResourceId, List<Product> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
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

        productImage.setImageResource(product.getImageResourceId());
        productName.setText(product.getName());
        productDescription.setText(product.getShort_description());
        productPrice.setText(String.format(Locale.getDefault(), "£%.2f", product.getPrice()));
        productLocation.setText(product.getLocation());

        return view;
    }
}