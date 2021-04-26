package com.tetradev.farmeroapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tetradev.farmeroapp.Database.Database;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;


public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    Button btnPlace;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;

    RelativeLayout rootLayout;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        //firebase
        database = FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");



        //init
        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);






        //Swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        txtTotalPrice=(TextView)findViewById(R.id.total);
        btnPlace=(Button)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              showAlertDialog();
            }
        });

        loadListProduct();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);

        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your details:");


        LayoutInflater inflater = this.getLayoutInflater();
        View order_phone = inflater.inflate(R.layout.order_phone,null);



        final MaterialEditText editName = (MaterialEditText)order_phone.findViewById(R.id.editName);
        final MaterialEditText editAddress = (MaterialEditText)order_phone.findViewById(R.id.editAddress);
        final MaterialEditText editPhone = (MaterialEditText)order_phone.findViewById(R.id.editPhone);



        alertDialog.setView(order_phone);
        alertDialog.setIcon(R.drawable.ic_add_shopping_cart_black_24dp);




        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {




            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


               // password_layout = (TextInputLayout) findViewById(R.id.password_layout);
                //request
                Request request = new Request(
                        editName.getText().toString(),
                        editAddress.getText().toString(),
                        editPhone.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart
                );
                if (editName.getText().toString().equals("")) {


                    Toast.makeText(getApplicationContext(), "Name is mandatory", Toast.LENGTH_LONG).show();

                } else if (editAddress.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Address is mandatory", Toast.LENGTH_LONG).show();
                } else if (editPhone.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Phone is mandatory", Toast.LENGTH_LONG).show();
                } else {


                    //submit to firebase
                    requests.child(String.valueOf(System.currentTimeMillis()))
                            .setValue(request);
                    //delete cart
                    new Database(getBaseContext()).cleanCart();
                    Toasty.success(Cart.this, "Thank you,Order Placed", Toast.LENGTH_SHORT,true).show();
                    finish();
                }

            }


        });






        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();


    }

    private void loadListProduct() {
        cart=new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        recyclerView.setAdapter(adapter);

        //calculate total price
        int total = 0;
        for(Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));

        Locale locale = new Locale("en","IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));

    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder)
        {
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();
            final Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductName());

            //total
            int total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts();
            for (Order item : orders)
                total += (Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
            Locale locale = new Locale("en","IN");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));

            //snackbar
            Snackbar snackbar = Snackbar.make(rootLayout,name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);
                    //total
                    int total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts();
                    for (Order item : orders)
                        total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

                    Locale locale = new Locale("en", "IN");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                    txtTotalPrice.setText(fmt.format(total));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
        }
    }
