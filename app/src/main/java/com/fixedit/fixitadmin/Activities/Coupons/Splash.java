package com.fixedit.fixitadmin.Activities.Coupons;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.fixedit.fixitadmin.Activities.MainActivity;
import com.fixedit.fixitadmin.Activities.UserManagement.LoginMenu;
import com.fixedit.fixitadmin.Activities.UserManagement.Signup;
import com.fixedit.fixitadmin.Models.VendorModel;
import com.fixedit.fixitadmin.R;
import com.fixedit.fixitadmin.Utils.CommonUtils;
import com.fixedit.fixitadmin.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Splash extends AppCompatActivity {
    public static int SPLASH_TIME_OUT = 2000;
    DatabaseReference mDatabase;

    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        logout = findViewById(R.id.logout);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        if (SharedPrefs.getVendorModel() != null) {
            if (SharedPrefs.getVendorModel().getUsername() == null) {
                Intent i = new Intent(Splash.this, LoginMenu.class);
                startActivity(i);
            } else {
                mDatabase.child("Vendors").child(SharedPrefs.getVendorModel().getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            VendorModel model = dataSnapshot.getValue(VendorModel.class);
                            if (model != null) {
                                SharedPrefs.setVendorModel(model);
                                checkDetails();
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        } else {
            Intent i = new Intent(Splash.this, LoginMenu.class);
            startActivity(i);
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefs.logout();
                Intent i = new Intent(Splash.this, Splash.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

//        new Handler().postDelayed(new Runnable() {
//
//            /*
//             * Showing splash screen with a timer. This will be useful when you
//             * want to show case your app logo / company
//             */
//
//            @Override
//            public void run() {
//                // This method will be executed once the timer is over
//                // Start your app main activity
//                if (SharedPrefs.getVendorModel() != null) {
//                    Intent i = new Intent(Splash.this, MainActivity.class);
//                    startActivity(i);
//                } else {
//                    Intent i = new Intent(Splash.this, LoginMenu.class);
//                    startActivity(i);
//                }
//
//                // close this activity
//                finish();
//            }
//        }, SPLASH_TIME_OUT);
    }

    private void checkDetails() {
        if (SharedPrefs.getVendorModel().isApproved()) {
            if (SharedPrefs.getVendorModel().isActive()) {
                Intent i = new Intent(Splash.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                CommonUtils.showToast("Your account is inactive. Please contact admin");
                logout.setVisibility(View.VISIBLE);
            }
        } else {
            CommonUtils.showToast("Waiting for admin approval");
            logout.setVisibility(View.VISIBLE);
        }

    }
}
