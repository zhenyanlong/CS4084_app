package com.example.cs4084_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class ItemDetailedActivity extends AppCompatActivity {
    private String name;
    private String short_description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detailed);
        // Retrieve data from the intent
        String name = getIntent().getStringExtra("name");
        String shortDescription = getIntent().getStringExtra("short_description");
        String longDescription=getIntent().getStringExtra("long_description");
        float price = getIntent().getFloatExtra("price",0.0f);
        String location = getIntent().getStringExtra("location");
        int imageId = getIntent().getIntExtra("imageId", 0); // 0 is a default value

        ImageView imageView = findViewById(R.id.image);
        TextView nameView = findViewById(R.id.Name_text);
        nameView.setText(name);
        TextView shortDescriptionView = findViewById(R.id.short_description_text);
        shortDescriptionView.setText(shortDescription);
        TextView longDescriptionView = findViewById(R.id.long_description_text);
        longDescriptionView.setText(longDescription);
        TextView priceView = findViewById(R.id.price_text);
        priceView.setText(String.format(Locale.getDefault(), "£%.2f", price));
        //close function
        Button returnButton = findViewById(R.id.close_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This will close the current activity and return to the previous one
                finish();
            }
        });
    }
}