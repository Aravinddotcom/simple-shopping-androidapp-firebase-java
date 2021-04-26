package com.tetradev.farmeroapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tetradev.farmeroapp.Database.Database;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

import static com.tetradev.farmeroapp.ImageListActivity.EXTRA_DESCRIPTION;
import static com.tetradev.farmeroapp.ImageListActivity.EXTRA_PRODUCTNAME;
import static com.tetradev.farmeroapp.ImageListActivity.EXTRA_PRICE;
import static com.tetradev.farmeroapp.ImageListActivity.EXTRA_CONTACT;
import static com.tetradev.farmeroapp.ImageListActivity.EXTRA_URL;

public class ProductDetail extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton btnCart;
    ElegantNumberButton numberButton;
    Button buttonCall;

    String productId="";

    ImageUpload currentproduct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        final Intent i = getIntent();

        buttonCall = findViewById(R.id.btnCall);
        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String contact = i.getStringExtra(EXTRA_CONTACT);
                String uri = "tel:" + contact.trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });





        Intent intent = getIntent();


        final String imageUrl = intent.getStringExtra(EXTRA_URL);
        final String productName = intent.getStringExtra(EXTRA_PRODUCTNAME);
        String description = intent.getStringExtra(EXTRA_DESCRIPTION);
        final String price = intent.getStringExtra(EXTRA_PRICE);
        final String contact = intent.getStringExtra(EXTRA_CONTACT);

        numberButton=(ElegantNumberButton)findViewById(R.id.number_button);
        btnCart=(FloatingActionButton)findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(


                        productId,productName,numberButton.getNumber(),price,"",imageUrl));

                Toasty.success(ProductDetail.this, "Added to Cart", Toast.LENGTH_SHORT,true).show();
            }
        });

        ImageView imageView=findViewById(R.id.image_view_detail);
        TextView textViewProductname = findViewById(R.id.text_view_creator_detail);
        TextView textViewDescription = findViewById(R.id.text_view_like_detail);
        TextView textViewPrice = findViewById(R.id.text_view_like_price);
        TextView textViewContact = findViewById(R.id.text_view_like_contact);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collasping);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        collapsingToolbarLayout.setTitle(productName);


        //get id from intent
        if(getIntent() != null)
            productId = getIntent().getStringExtra("productId");


        Picasso.with(this).load(imageUrl).fit()
                //.centerCrop()
                .into(imageView);
        textViewProductname.setText(productName);
        textViewDescription.setText(description);
        textViewPrice.setText(price);
        textViewContact.setText(contact);
    }



}
