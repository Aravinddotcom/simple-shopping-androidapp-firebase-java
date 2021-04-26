package com.tetradev.farmeroapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txt_cart_name,txt_price;
    public ImageView img_cart_count;

    public ImageView cart_image;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    private ImageListAdapter.OnItemClickListener onItemClickListener;


    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(View itemView) {
        super(itemView);
        txt_cart_name=(TextView)itemView.findViewById(R.id.cart_item_name);
        txt_price=(TextView)itemView.findViewById(R.id.cart_item_Price);
        img_cart_count=(ImageView)itemView.findViewById(R.id.cart_item_count);
        cart_image=(ImageView)itemView.findViewById(R.id.cart_image);

        view_background=(RelativeLayout)itemView.findViewById(R.id.view_background);
        view_foreground=(LinearLayout)itemView.findViewById(R.id.view_foreground);
    }

    @Override
    public void onClick(View v) {

    }
}
