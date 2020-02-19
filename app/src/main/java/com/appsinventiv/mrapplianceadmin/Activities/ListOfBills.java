package com.appsinventiv.mrapplianceadmin.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;


import com.appsinventiv.mrapplianceadmin.Adapters.BillsAdapter;
import com.appsinventiv.mrapplianceadmin.Models.InvoiceModel;
import com.appsinventiv.mrapplianceadmin.R;
import com.appsinventiv.mrapplianceadmin.Utils.CommonUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListOfBills extends AppCompatActivity {
    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    ArrayList<InvoiceModel> itemList = new ArrayList<>();
    BillsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_bills);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Bills History");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new BillsAdapter(this, itemList, new BillsAdapter.BillsCallback() {
            @Override
            public void onDelete(InvoiceModel model) {
                showAlert(model);
            }
        });

        recyclerView.setAdapter(adapter);
        getDataFromDB();

    }

    private void showAlert(final InvoiceModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListOfBills.this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to delete this bill? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Invoices").child(model.getInvoiceId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Deleted");
                        mDatabase.child("Orders").child("" + model.getOrder().getOrderId()).child("billNumber").removeValue();
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void getDataFromDB() {
        mDatabase.child("Invoices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        InvoiceModel model = snapshot.getValue(InvoiceModel.class);
                        if (model != null) {
                            itemList.add(model);
                            Collections.sort(itemList, new Comparator<InvoiceModel>() {
                                @Override
                                public int compare(InvoiceModel listData, InvoiceModel t1) {
                                    Long ob1 = listData.getTime();
                                    Long ob2 = t1.getTime();

                                    return ob2.compareTo(ob1);

                                }
                            });
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    itemList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
