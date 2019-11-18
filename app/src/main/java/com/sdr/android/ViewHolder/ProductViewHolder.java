package com.sdr.android.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sdr.android.Interface.ItemClickListener;
import com.sdr.android.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDescription, txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.product_image1);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name1);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description1);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_price1);

    }


    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }


    @Override
    public void onClick(View view) {

        listener.onClick(view, getAdapterPosition(), false);

    }
}
