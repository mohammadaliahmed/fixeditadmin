package com.fixedit.fixitadmin.Activities;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.fixedit.fixitadmin.Activities.Coupons.ListOfCoupons;
import com.fixedit.fixitadmin.Activities.Coupons.Splash;
import com.fixedit.fixitadmin.Activities.Customers.ListOfCustomers;
import com.fixedit.fixitadmin.Activities.Orders.Orders;
import com.fixedit.fixitadmin.Activities.TimeSlotManagement.TimeSlotList;
import com.fixedit.fixitadmin.Notifications.SendNotification;
import com.fixedit.fixitadmin.R;
import com.fixedit.fixitadmin.Servicemen.ListOfServicemen;
import com.fixedit.fixitadmin.Services.ListOfServices;
import com.fixedit.fixitadmin.Services.ServiceModel;
import com.fixedit.fixitadmin.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    LinearLayout services, customers, bills, orders, notifications, settings, serviceMen, coupons, timeslots, logout;
    DatabaseReference mDatabase;
    private ArrayList<String> itemList = new ArrayList();


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
        timeslots = findViewById(R.id.timeslots);
        logout = findViewById(R.id.logout);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Vendors").child(SharedPrefs.getVendorModel().getUsername()).child("fcmKey").setValue(FirebaseInstanceId.getInstance().getToken());

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
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefs.logout();
                Intent i = new Intent(MainActivity.this, Splash.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
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

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AppSettings.class));

            }
        });
        coupons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListOfCoupons.class));

            }
        });
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SendNotification.class));

            }
        });
        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListOfCustomers.class));

            }
        });
        timeslots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();

            }
        });
        getServicesFromDB();
    }

    private void showAlert() {
        ArrayList<String> list = SharedPrefs.getServicesList();
        final String[] items = list.toArray(new String[list.size()]);
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("Choose Service");
        build.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(MainActivity.this, TimeSlotList.class);
                i.putExtra("serviceName", items[which]);
                startActivity(i);

            }
        }).create().show();
    }


    private void getServicesFromDB() {
        mDatabase.child("Services").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ServiceModel model = snapshot.getValue(ServiceModel.class);
                        if (model != null) {
                            itemList.add(model.getName());
                        }
                    }
                    if (itemList.size() > 0) {
                        SharedPrefs.setServicesList(itemList);
                    }
                } else {
                    itemList.clear();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
