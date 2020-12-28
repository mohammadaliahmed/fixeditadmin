package com.fixedit.fixitadmin.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fixedit.fixitadmin.Adapters.InvoiceListAdapter;
import com.fixedit.fixitadmin.Models.InvoiceModel;
import com.fixedit.fixitadmin.R;
import com.fixedit.fixitadmin.Utils.CommonUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewInvoice extends AppCompatActivity {
    TextView billNumber, orderNumber, date, dayChosen, timeChosen,
            customerName, mobileNumber, address, comments, totalTime, total;
    RecyclerView recycler;
    DatabaseReference mDatabase;
    String invoiceId;
    InvoiceModel model;

    InvoiceListAdapter adapter;
    RelativeLayout wholeLayout;
    TextView serviceName, buildingType;
    TextView couponCode, discount, perHourCost;
    LinearLayout couponArea;
    TextView materialBill, materialPercent, tax;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invoice);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        invoiceId = getIntent().getStringExtra("invoiceId");
        this.setTitle("Bill Number: " + invoiceId);

        materialPercent = findViewById(R.id.materialPercent);
        tax = findViewById(R.id.tax);
        couponCode = findViewById(R.id.couponCode);
        materialBill = findViewById(R.id.materialBill);
        discount = findViewById(R.id.discount);
        couponArea = findViewById(R.id.couponArea);
        billNumber = findViewById(R.id.billNumber);
        serviceName = findViewById(R.id.serviceName);
        orderNumber = findViewById(R.id.orderNumber);
        date = findViewById(R.id.date);
        dayChosen = findViewById(R.id.dayChosen);
        timeChosen = findViewById(R.id.timeChosen);
        buildingType = findViewById(R.id.buildingType);
        customerName = findViewById(R.id.customerName);
        mobileNumber = findViewById(R.id.mobileNumber);
        address = findViewById(R.id.address);
        comments = findViewById(R.id.comments);
        totalTime = findViewById(R.id.totalTime);
        total = findViewById(R.id.total);
        wholeLayout = findViewById(R.id.wholeLayout);
        perHourCost = findViewById(R.id.perHourCost);
        recycler = findViewById(R.id.recycler);


        getDataFromServer();
    }

    private void getDataFromServer() {
        mDatabase.child("Invoices").child(invoiceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    model = dataSnapshot.getValue(InvoiceModel.class);
                    if (model != null) {
                        billNumber.setText("Bill Number: " + model.getInvoiceId());
                        orderNumber.setText("Order Number: " + model.getOrder().getOrderId());
                        date.setText("Date: " + CommonUtils.getFormattedDate(model.getOrder().getTime()));
                        dayChosen.setText("Day: " + model.getOrder().getDate());
                        timeChosen.setText("Time: " + model.getOrder().getChosenTime());
                        customerName.setText("Customer Name: " + model.getOrder().getUser().getFullName());
                        mobileNumber.setText("Cell Number: " + model.getOrder().getUser().getMobile());
                        address.setText("Customer Address: " + model.getOrder().getOrderAddress());
                        tax.setText(model.getOrder().getTax() + "% tax");
                        materialPercent.setText("10% mat. bill: Rs " + (int) (model.getOrder().getMaterialBill() / 10));
                        comments.setText("Comments: " + model.getOrder().getInstructions());
                        total.setText("Total Bill: Rs " + model.getOrder().getTotalPrice());
                        materialBill.setText("Mat. bill: Rs " + model.getOrder().getMaterialBill());
                        totalTime.setText("Total Time: " + model.getOrder().getTotalHours() + " hours");
                        serviceName.setText("Service Type: " + model.getOrder().getServiceName());
                        buildingType.setText("Building Type: " + model.getOrder().getBuildingType());
                        perHourCost.setText("Cost: " + model.getOrder().getTotalHours() + "*" + model.getOrder().getServiceCharges());
                        if (model.getOrder().isCouponApplied()) {
                            couponArea.setVisibility(View.VISIBLE);
                            couponCode.setText("Coupon code: " + model.getOrder().getCouponCode());
                            discount.setText("Discount: " + model.getOrder().getDiscount() + "%");
                        } else {
                            couponArea.setVisibility(View.GONE);
                        }


                        recycler.setLayoutManager(new LinearLayoutManager(ViewInvoice.this, LinearLayoutManager.VERTICAL, false));
                        adapter = new InvoiceListAdapter(ViewInvoice.this, model.getOrder().getCountModelArrayList());
                        recycler.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        wholeLayout.setVisibility(View.GONE);
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
