package com.sdr.android.Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdr.android.Admin.AdminCheckNewProductsActivity;
import com.sdr.android.Buyers.MainActivity;
import com.sdr.android.Model.Products;
import com.sdr.android.R;
import com.sdr.android.ViewHolder.ProductViewHolder;
import com.sdr.android.ViewHolder.SellersItemViewHolder;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SellerHomeActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference ProductsRef;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent2 = new Intent(SellerHomeActivity.this, SellerHomeActivity.class);
                    startActivity(intent2);
                    return true;
                case R.id.navigation_add:
                    Intent intent1 = new Intent(SellerHomeActivity.this, SellerProductCategoryActivity.class);
                    startActivity(intent1);

                    return true;
                case R.id.navigation_logout:
                    final FirebaseAuth mAuth;
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();

                    Intent intent = new Intent(SellerHomeActivity.this, MainActivity.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");


        recyclerView = findViewById(R.id.seller_home_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("sid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()), Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, SellersItemViewHolder> adapter = new FirebaseRecyclerAdapter<Products, SellersItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SellersItemViewHolder holder, int position, final @NonNull Products model) {

                holder.txtProductName.setText(model.getPname());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductState.setText("Status: " + model.getProductState());
                holder.txtProductPrice.setText("Price = $"+model.getPrice());

                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final String productID = model.getPid();

                        CharSequence options[] = new CharSequence[]{

                                "Yes",
                                "No"

                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(SellerHomeActivity.this);
                        builder.setTitle("Do you want to Delete this product?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(i == 0){

                                    DeleteProduct(productID);

                                }
                                if(i == 1){

                                    dialogInterface.cancel();

                                }
                            }
                        });
                        builder.show();

                    }
                });

            }

            @NonNull
            @Override
            public SellersItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.seller_item_view, parent, false);

                SellersItemViewHolder holder = new SellersItemViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void DeleteProduct(String productID) {

        ProductsRef.child(productID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(SellerHomeActivity.this, "Item Deleted Successfully.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(SellerHomeActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}
