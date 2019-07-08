package com.fixedit.fixitadmin.Services;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.fixedit.fixitadmin.R;
import com.fixedit.fixitadmin.Utils.CommonUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListOfServices extends AppCompatActivity {
    ImageView addService;
    RecyclerView recyclerview;
    ServiceListAdapter adapter;
    private ArrayList<ServiceModel> itemList = new ArrayList<>();
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_services);
        addService = findViewById(R.id.addService);
        recyclerview = findViewById(R.id.recyclerview);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("List of services");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        addService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListOfServices.this, AddService.class));
            }

        });


        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ServiceListAdapter(this, itemList, new ServiceListAdapter.ServiceListAdapterCallbacks() {
            @Override
            public void onServiceStatusChanged(ServiceModel model, final boolean value) {
                mDatabase.child("Services").child(model.getName()).child("active").setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (value) {
                            CommonUtils.showToast("Service activated");
                        } else {
                            CommonUtils.showToast("Service Deactivated");
                        }
                    }
                });
            }

            @Override
            public void onServiceDeleted(ServiceModel model) {
                showAlert(model);
            }
        });
        recyclerview.setAdapter(adapter);

        getDataFromDB();

    }

    private void getDataFromDB() {
        mDatabase.child("Services").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    itemList.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        ServiceModel model=snapshot.getValue(ServiceModel.class);
                        if(model!=null){
                            itemList.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    itemList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showAlert(final ServiceModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to delete this service? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Services").child(model.getName()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Services Deleted");
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
