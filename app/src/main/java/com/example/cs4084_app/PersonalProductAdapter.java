package com.example.cs4084_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class PersonalProductAdapter extends ArrayAdapter<Product> {
    private int resourceId;
    public PersonalProductAdapter(Context context, int productItem, List<Product> products) {
        super(context,productItem,products);
        resourceId = productItem;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Product product = getItem(position); // Get current instance of Product class


        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        ImageView productImage = (ImageView) view.findViewById(R.id.product_image);
        TextView productName = (TextView) view.findViewById(R.id.product_name);
        TextView productDescription = (TextView) view.findViewById(R.id.product_description);
        TextView productPrice = (TextView) view.findViewById(R.id.product_price);


        //productImage.setImageResource(product.getImageResourceId());
        productName.setText(product.getName());
        productDescription.setText(product.getShortDescription());
        productPrice.setText(String.format(Locale.getDefault(), "£%.2f", product.getPrice()));

        //set URl
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(getContext())
                    .load(product.getImageUrl())
                    .into(productImage);
        } else {
            productImage.setImageResource(R.drawable.d); // Default image if URL is empty
        }
        Button soldButton = view.findViewById(R.id.soldButton);

        // 设置按钮的初始状态和文本，根据 product 的 sold 状态
        if (product.isIs_sold()) {
            soldButton.setText("Sold Out");
            soldButton.setEnabled(false); // 如果已售出，按钮不可点击
        } else {
            soldButton.setText("Mark as Sold");
            soldButton.setEnabled(true);
        }
        soldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 在这里处理点击事件
                // 更新产品状态为已售出
                product.setIs_sold(true);
                // 反馈到UI
                soldButton.setText("Sold Out");
                soldButton.setEnabled(false);
                // 这里，你还需要更新后端的Firebase数据库记录
                // 例如：
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("products").document(product.getItemID())
                        .update("is_sold", true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // 更新成功时的操作，例如通知用户
                                Toast.makeText(getContext(), "Product marked as sold.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 更新失败时的操作
                                Toast.makeText(getContext(), "Error updating product.", Toast.LENGTH_SHORT).show();
                            }
                        });

                // 通知列表视图更新显示
                notifyDataSetChanged();
            }
        });


        return view;
    }

}
