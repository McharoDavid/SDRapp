package com.sdr.android.Sellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdr.android.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SellerAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, Description, Price, ProductName, saveCurrentDate, saveCurrentTime;
    private Button AddProductBtn;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductDescription, InputProductPrice;

    private static final int GalleryPick = 1;
    private Uri ImageUri;

    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;

    private DatabaseReference ProductsRef, sellersRef;
    private ProgressDialog loadingBar;

    private String sName, sAddress, sPhone, sEmail, sID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_new_product);

        CategoryName = getIntent().getExtras().get("category").toString();

        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        sellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");


        AddProductBtn = (Button) findViewById(R.id.add_new_product);
        InputProductImage = (ImageView) findViewById(R.id.select_product_image);
        InputProductName = (EditText) findViewById(R.id.product_name);
        InputProductDescription = (EditText) findViewById(R.id.product_description);
        InputProductPrice = (EditText) findViewById(R.id.product_price);

        loadingBar = new ProgressDialog(this);

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        AddProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });

        sellersRef.child(FirebaseAuth.getInstance().getCurrentUser()
                .getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    sName = dataSnapshot.child("name").getValue().toString();
                    sAddress = dataSnapshot.child("address").getValue().toString();
                    sPhone = dataSnapshot.child("phone").getValue().toString();
                    sEmail = dataSnapshot.child("email").getValue().toString();
                    sID = dataSnapshot.child("sid").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




    private void OpenGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");

        startActivityForResult(galleryIntent, GalleryPick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode == RESULT_OK && data != null){

            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);

        }

    }

    private void ValidateProductData() {

        Description = InputProductDescription.getText().toString();
        Price = InputProductPrice.getText().toString();
        ProductName = InputProductName.getText().toString();

        if(ImageUri == null){

            Toast.makeText(SellerAddNewProductActivity.this, "Please Select Image for the product", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(Description)){

            Toast.makeText(SellerAddNewProductActivity.this, "Please Add Description for the product", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(Price)){

            Toast.makeText(SellerAddNewProductActivity.this, "Please Add Product Price", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(ProductName)){

            Toast.makeText(SellerAddNewProductActivity.this, "Please Add Product Name", Toast.LENGTH_SHORT).show();

        }
        else{

            StoreProductInformation();

        }



    }


    private void StoreProductInformation() {
        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Please wait while we are adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calender = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calender.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calender.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filepath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpeg");

        final UploadTask uploadTask = filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                String message = e.toString();
                Toast.makeText(SellerAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(SellerAddNewProductActivity.this, "Product Image Uploaded Successfully.", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if(!task.isSuccessful()){

                            throw task.getException();

                        }

                        downloadImageUrl = filepath.getDownloadUrl().toString();

                        return filepath.getDownloadUrl();



                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        //This is was missing, therefore causing the imageNotDisplaying Error
                        downloadImageUrl = task.getResult().toString();

                        if(task.isSuccessful()){
                            Toast.makeText(SellerAddNewProductActivity.this, "Got the Product Image Url Successfully.", Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }

                    }
                });

            }
        });


    }

    private void SaveProductInfoToDatabase() {

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", CategoryName);
        productMap.put("price", Price);
        productMap.put("pname", ProductName);

        productMap.put("sellerName", sName);
        productMap.put("sellerAddress",sAddress );
        productMap.put("sellerPhone", sPhone);
        productMap.put("sellerEmail", sEmail);
        productMap.put("sid", sID);
        productMap.put("productState", "Not Approved");

        ProductsRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Intent intent = new Intent(SellerAddNewProductActivity.this, SellerHomeActivity.class);
                    startActivity(intent);

                    loadingBar.dismiss();
                    Toast.makeText(SellerAddNewProductActivity.this, "Product is added Successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(SellerAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    Log.d("moo", message);

                }

            }
        });


    }
}
