package com.sdr.android.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdr.android.Admin.AdminHomeActivity;
import com.sdr.android.Sellers.SellerProductCategoryActivity;
import com.sdr.android.Model.Users;
import com.sdr.android.Prevalent.Prevalent;
import com.sdr.android.R;

import io.paperdb.Paper;

public class loginActivity extends AppCompatActivity {

    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private CheckBox chkBoxRememberMe;
    private TextView AdminLink, NotAdminLink, ForgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        ForgotPasswordLink = (TextView) findViewById(R.id.forget_password_link);

        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.login_password_input);

        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        loadingBar = new ProgressDialog(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });

        ForgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);
            }
        });



    }

    private void LogInUser() {

        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please write your phone number.", Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please write your password.", Toast.LENGTH_SHORT);
        }
        else{
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone, password);


        }

    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if(chkBoxRememberMe.isChecked()){

            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);

        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(parentDbName).child(phone).exists()){

                    Users userData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if(userData.getPhone().equals(phone)){

                        if(userData.getPassword().equals(password)){

                            if(parentDbName.equals("Admins")){
                                Toast.makeText(loginActivity.this, "Welcome Admin, You are Logged In Successfully!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(loginActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                            }
                            else if(parentDbName.equals("Users")){

                                Toast.makeText(loginActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(loginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = userData;
                                startActivity(intent);

                            }

                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(loginActivity.this, "Password is Incorrect.", Toast.LENGTH_SHORT).show();

                        }
                    }


                }
                else{
                    Toast.makeText(loginActivity.this, "Account with this "+phone+" number does not Exist!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(loginActivity.this, "Please register first before login.", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(loginActivity.this, "Whats Up", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
