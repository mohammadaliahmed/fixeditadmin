package com.fixedit.fixitadmin.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fixedit.fixitadmin.Activities.Orders.Orders;
import com.fixedit.fixitadmin.R;
import com.fixedit.fixitadmin.Servicemen.ListOfServicemen;
import com.fixedit.fixitadmin.Services.ListOfServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {
    LinearLayout services, customers, bills, orders, notifications, settings, serviceMen, coupons;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        services = findViewById(R.id.services);
        customers = findViewById(R.id.customers);
        bills = findViewById(R.id.bills);
        orders = findViewById(R.id.orders);
        notifications = findViewById(R.id.notifications);
        settings = findViewById(R.id.settings);
        serviceMen = findViewById(R.id.serviceMen);
        coupons = findViewById(R.id.coupons);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Admin").child("fcmKey").setValue(FirebaseInstanceId.getInstance().getToken());

        serviceMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListOfServicemen.class));

            }
        });

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Orders.class));
            }
        });


        services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListOfServices.class));

            }
        });
        bills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListOfBills.class));

            }
        });
    }
}
