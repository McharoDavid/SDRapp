package com.sdr.android.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdr.android.Model.Users;
import com.sdr.android.Prevalent.Prevalent;
import com.sdr.android.R;
import com.sdr.android.Sellers.SellerHomeActivity;
import com.sdr.android.Sellers.SellerRegistrationActivity;

import io.paperdb.Paper;


//Creating a public class MainActivity
public class MainActivity extends AppCompatActivity {

    //Creating a private button variables for registering and login in the app
    private Button joinNowButton, loginButton;

    //Creating a private ProgressDialog variable for the loading bar.
    private ProgressDialog loadingBar;

    //Creating a private TextView variables
    private TextView sellerBegin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing paper for storage
        Paper.init(this);

        // Creating a new Loading bar.
        loadingBar = new ProgressDialog(this);

        //Connecting variable with the id
        joinNowButton = (Button) findViewById(R.id.main_join_now_btn);

        //Connecting variable with the id
        loginButton = (Button) findViewById(R.id.main_login_btn);

        //Connecting variable with the id
        sellerBegin = (TextView) findViewById(R.id.seller_begin);


        //Creating an onclick Listener to implement the method for sellerbegin
        sellerBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Creating an intent to go from mainactivity to  SellerRegistrationactivity
                Intent intent = new Intent(MainActivity.this, SellerRegistrationActivity.class);
                //Using the object intent
                startActivity(intent);
            }
        });


        //Creating an onclick Listener to implement the method for LOGINBUTTON
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating an intent to go from mainactivity to  Loginactivity
                Intent intent = new Intent(MainActivity.this, loginActivity.class);
                //Using the object intent
                startActivity(intent);
            }
        });

        //Creating an onclick Listener to implement the method for LOGINBUTTON
        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Creating an intent to go from mainactivity to  Registeractivity
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                //Using the object intent
                startActivity(intent);
            }
        });

        //Stroing the Userphonenumber in a variable
        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        //Stroing the Userphonenumber in a variable
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        //Checking if the Userphonenumber or Userpassword are not empty
        if(UserPhoneKey != "" && UserPasswordKey != ""){

            //Checking if the Userphonenumber or Userpassword are not empty
            if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){

                //Calling the function and passing the values of Userphonekey and Userpassword
                AllowAccess(UserPhoneKey, UserPasswordKey);

                loadingBar.setTitle("Already Logged In");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

            }

        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){

            Intent intent = new Intent(MainActivity.this, SellerHomeActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
    }

    private void AllowAccess(final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Users").child(phone).exists()){

                    Users userData = dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    if(userData.getPhone().equals(phone)){

                        if(userData.getPassword().equals(password)){

                            Toast.makeText(MainActivity.this, "Please wait... You are already logged in.", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = userData;
                            startActivity(intent);
                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Password is Incorrect.", Toast.LENGTH_SHORT).show();

                        }
                    }


                }
                else{
                    Toast.makeText(MainActivity.this, "Account with this "+phone+" number does not Exist!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Please register first before login.", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
