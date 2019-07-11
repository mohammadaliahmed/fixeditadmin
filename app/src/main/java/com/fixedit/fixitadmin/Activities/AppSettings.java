package com.fixedit.fixitadmin.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fixedit.fixitadmin.Adapters.BillsAdapter;
import com.fixedit.fixitadmin.Models.AdminModel;
import com.fixedit.fixitadmin.Models.InvoiceModel;
import com.fixedit.fixitadmin.R;
import com.fixedit.fixitadmin.Utils.CommonUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AppSettings extends AppCompatActivity {
    DatabaseReference mDatabase;

    EditText adminNumber;
    Button updateNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Settings");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        updateNumber = findViewById(R.id.updateNumber);
        adminNumber = findViewById(R.id.adminNumber);

        updateNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adminNumber.getText().length() == 0) {
                    adminNumber.setError("Enter number");
                } else {
                    mDatabase.child("Admin").child("adminNumber").setValue(adminNumber.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.showToast("Number Added");
                        }
                    });
                }
            }
        });

        getDataFromDB();

    }

    private void getDataFromDB() {
        mDatabase.child("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getValue() != null) {
                        AdminModel model = dataSnapshot.getValue(AdminModel.class);
                        if (model != null) {
                            adminNumber.setText(model.getAdminNumber());
                        }
                    }
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
