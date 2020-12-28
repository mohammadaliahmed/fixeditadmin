package com.fixedit.fixitadmin.Activities.UserManagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fixedit.fixitadmin.Activities.Coupons.Splash;
import com.fixedit.fixitadmin.Activities.MainActivity;
import com.fixedit.fixitadmin.Models.VendorModel;
import com.fixedit.fixitadmin.R;
import com.fixedit.fixitadmin.Utils.CommonUtils;
import com.fixedit.fixitadmin.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class LoginMenu extends AppCompatActivity {

    Button login;
    EditText username, password;
    TextView apply;
    DatabaseReference mDatabase;
    HashMap<String, VendorModel> vendorsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        apply = findViewById(R.id.apply);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getAllVendors();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().length() == 0) {
                    username.setError("Enter username");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else {
                    checkLogin();
                }
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginMenu.this, Signup.class));
            }
        });


    }

    private void getAllVendors() {
        mDatabase.child("Vendors").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        VendorModel model = snapshot.getValue(VendorModel.class);
                        if (model.getUsername() != null) {
                            vendorsMap.put(model.getUsername(), model);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void checkLogin() {
        if (vendorsMap.containsKey(username.getText().toString())) {
            if (vendorsMap.get(username.getText().toString()).getPassword().equals(password.getText().toString())) {
                SharedPrefs.setVendorModel(vendorsMap.get(username.getText().toString()));
                checkDetails();
            } else {
                CommonUtils.showToast("Wrong password");
            }
        } else {
            CommonUtils.showToast("User does not exists. Please signup");
        }

    }

    private void checkDetails() {
        if (SharedPrefs.getVendorModel().isApproved()) {
            if (SharedPrefs.getVendorModel().isActive()) {
                CommonUtils.showToast("Successfully logged in");
                Intent i = new Intent(LoginMenu.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                CommonUtils.showToast("Your account is inactive. Please contact admin");
            }
        } else {
            CommonUtils.showToast("Waiting for admin approval");
        }
    }

}
