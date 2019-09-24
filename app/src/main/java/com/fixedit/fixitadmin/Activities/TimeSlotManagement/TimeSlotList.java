package com.fixedit.fixitadmin.Activities.TimeSlotManagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fixedit.fixitadmin.Activities.Customers.CustomersListAdapter;
import com.fixedit.fixitadmin.Activities.MainActivity;
import com.fixedit.fixitadmin.Adapters.TimeslotsAdapter;
import com.fixedit.fixitadmin.Models.OrderModel;
import com.fixedit.fixitadmin.Models.User;
import com.fixedit.fixitadmin.R;
import com.fixedit.fixitadmin.Services.ListOfSubServices;
import com.fixedit.fixitadmin.Utils.CommonUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVWriter;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSlotList extends AppCompatActivity {
    DatabaseReference mDatabase;
    public static String timeSelected;
    public String daySelected;
    RadioButton day1, day2, day3;
    HashMap<String, ArrayList<String>> map = new HashMap<>();
    String dayNo1, dayNo2, dayNo3;


    RecyclerView recyclerview;
    TimeslotsAdapter adapter;
    ArrayList<String> itemList = new ArrayList<>();
    String serviceName;

    TextView servicename;
    int dayy = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_slot_management);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);

        }
        this.setTitle("Time Slots");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        servicename = findViewById(R.id.serviceName);

        serviceName = getIntent().getStringExtra("serviceName");
        servicename.setText("Time slot for: " + serviceName);

        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        day3 = findViewById(R.id.day3);
        recyclerview = findViewById(R.id.recyclerview);


        day1.setText(CommonUtils.getDay(System.currentTimeMillis()) + "\n" + CommonUtils.getDayName(System.currentTimeMillis()));
        day2.setText(CommonUtils.getDay(System.currentTimeMillis() + 86400000) + "\n" + CommonUtils.getDayName(System.currentTimeMillis() + 86400000));
        day3.setText(CommonUtils.getDay(System.currentTimeMillis() + 86400000 + 86400000) + "\n" + CommonUtils.getDayName(System.currentTimeMillis() + 86400000 + 86400000));

        addTime();
        adapter = new TimeslotsAdapter(this, itemList, new ArrayList<String>());
        adapter.setCallback(new TimeslotsAdapter.TimeSlotsCallback() {
            @Override
            public void optionSelected(String timeChosen) {
//                timeSelected = timeChosen;
                showBookAlert(timeChosen);

            }

            @Override
            public void optionUnblock(String timeChosen) {
                showUnblockSlotAlert(timeChosen);
            }
        });
        recyclerview.setLayoutManager(new GridLayoutManager(this, 3));

        recyclerview.setAdapter(adapter);


        dayNo1 = CommonUtils.getDay(System.currentTimeMillis()) + CommonUtils.getDayName(System.currentTimeMillis());
        dayNo2 = CommonUtils.getDay(System.currentTimeMillis() + 86400000) + CommonUtils.getDayName(System.currentTimeMillis() + 86400000);
        dayNo3 = CommonUtils.getDay(System.currentTimeMillis() + 86400000 + 86400000) + CommonUtils.getDayName(System.currentTimeMillis() + 86400000 + 86400000);
        daySelected = day1.getText().toString();
        day1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    dayy = 1;
                    daySelected = day1.getText().toString();
                    ArrayList<String> list = map.get(dayNo1) == null ? new ArrayList<String>() : map.get(dayNo1);
                    adapter.setUnavailableTime(list);
                    adapter.setavailableTime(itemList);
                    itemList.clear();
                    addTime();
                    adapter.setavailableTime(itemList);


                }
            }
        });
        day2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    dayy = 2;
                    daySelected = day2.getText().toString();
                    ArrayList<String> list = map.get(dayNo2) == null ? new ArrayList<String>() : map.get(dayNo2);
                    adapter.setUnavailableTime(list);
                    itemList.clear();
                    itemList.add("10:00 am");
                    itemList.add("11:00 am");
                    itemList.add("12:00 pm");
                    itemList.add("1:00 pm");
                    itemList.add("2:00 pm");
                    itemList.add("3:00 pm");
                    itemList.add("4:00 pm");
                    itemList.add("5:00 pm");
                    itemList.add("6:00 pm");
                    itemList.add("7:00 pm");
                    itemList.add("8:00 pm");
                    itemList.add("9:00 pm");
                    itemList.add("10:00 pm");
                    itemList.add("11:00 pm");
                    itemList.add("12:00 am");
                    adapter.setavailableTime(itemList);


                }
            }
        });
        day3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    dayy = 3;
                    daySelected = day3.getText().toString();
                    ArrayList<String> list = map.get(dayNo3) == null ? new ArrayList<String>() : map.get(dayNo3);
                    adapter.setUnavailableTime(list);
                    itemList.clear();
                    itemList.add("10:00 am");
                    itemList.add("11:00 am");
                    itemList.add("12:00 pm");
                    itemList.add("1:00 pm");
                    itemList.add("2:00 pm");
                    itemList.add("3:00 pm");
                    itemList.add("4:00 pm");
                    itemList.add("5:00 pm");
                    itemList.add("6:00 pm");
                    itemList.add("7:00 pm");
                    itemList.add("8:00 pm");
                    itemList.add("9:00 pm");
                    itemList.add("10:00 pm");
                    itemList.add("11:00 pm");
                    itemList.add("12:00 am");
                    adapter.setavailableTime(itemList);

                }
            }
        });
        day1.setChecked(true);
        daySelected = day1.getText().toString();


        getTimeSlotsFromDB();

    }

    private void showBookAlert(final String timeChosen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to book this slot? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String dayy = daySelected.replace("\n", "");

                mDatabase.child("TimeSlots").child(serviceName)
                        .child(CommonUtils.getYear(System.currentTimeMillis()))
                        .child(CommonUtils.getMonthName(System.currentTimeMillis()))
                        .child(dayy).child(timeChosen).setValue(timeChosen).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Slot booked");
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUnblockSlotAlert(final String timeChosen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to un book this slot? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String dayy = daySelected.replace("\n", "");

                mDatabase.child("TimeSlots").child(serviceName)
                        .child(CommonUtils.getYear(System.currentTimeMillis()))
                        .child(CommonUtils.getMonthName(System.currentTimeMillis()))
                        .child(dayy).child(timeChosen)
                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Slot un-booked");
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getTimeSlotsFromDB() {
        mDatabase.child("TimeSlots").child(serviceName)
                .child(CommonUtils.getYear(System.currentTimeMillis()))
                .child(CommonUtils.getMonthName(System.currentTimeMillis()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            ArrayList<String> day1List = new ArrayList<>();

                            for (DataSnapshot snapshot : dataSnapshot.child(dayNo1).getChildren()) {
                                day1List.add(snapshot.getKey());

                            }
                            map.put(dayNo1, day1List);


                            ArrayList<String> day1List2 = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.child(dayNo2).getChildren()) {
                                day1List2.add(snapshot.getKey());

                            }
                            map.put(dayNo2, day1List2);


                            ArrayList<String> day1Lis3 = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.child(dayNo3).getChildren()) {
                                day1Lis3.add(snapshot.getKey());

                            }
                            map.put(dayNo3, day1Lis3);

//                            day1.setChecked(true);
                            if(dayy==1){
                                adapter.setUnavailableTime(day1List);
                                itemList.clear();
                                addTime();
                                adapter.setavailableTime(itemList);
                            }else if(dayy==2){
                                adapter.setUnavailableTime(day1List2);
                                itemList.clear();
                                addTime();
                                adapter.setavailableTime(itemList);
                            }else {
                                adapter.setUnavailableTime(day1Lis3);
                                itemList.clear();
                                addTime();
                                adapter.setavailableTime(itemList);
                            }



                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addTime() {
        itemList.add("10:00 am");
        itemList.add("11:00 am");
        itemList.add("12:00 pm");
        itemList.add("1:00 pm");
        itemList.add("2:00 pm");
        itemList.add("3:00 pm");
        itemList.add("4:00 pm");
        itemList.add("5:00 pm");
        itemList.add("6:00 pm");
        itemList.add("7:00 pm");
        itemList.add("8:00 pm");
        itemList.add("9:00 pm");
        itemList.add("10:00 pm");
        itemList.add("11:00 pm");
        itemList.add("12:00 am");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
