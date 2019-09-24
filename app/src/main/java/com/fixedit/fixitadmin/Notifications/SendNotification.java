package com.fixedit.fixitadmin.Notifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fixedit.fixitadmin.ChangePolicy;
import com.fixedit.fixitadmin.Models.AdminModel;
import com.fixedit.fixitadmin.Models.User;
import com.fixedit.fixitadmin.R;
import com.fixedit.fixitadmin.Utils.CommonUtils;
import com.fixedit.fixitadmin.Utils.NotificationAsync;
import com.fixedit.fixitadmin.Utils.NotificationObserver;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SendNotification extends AppCompatActivity implements NotificationObserver {
    EditText notificationMessage, notificationTitle;

    Button send;

    DatabaseReference mDatabase;
    Button sent;
    private ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        this.setTitle("Send Notification");
        notificationMessage = findViewById(R.id.notificationMessage);
        notificationTitle = findViewById(R.id.notificationTitle);
        send = findViewById(R.id.send);
        send = findViewById(R.id.send);
        sent = findViewById(R.id.sent);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User customer = snapshot.getValue(User.class);
                        if (customer != null) {
                            arrayList.add(customer.getFcmKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SendNotification.this, NotificationHistory.class);
                startActivity(i);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notificationTitle.getText().length() == 0) {
                    notificationTitle.setError("Enter Title");
                } else if (notificationMessage.getText().length() == 0) {
                    notificationMessage.setError("Enter Message");
                } else if (arrayList.size() == 0) {
                    CommonUtils.showToast("No Users");

                } else {

                    for (String keys : arrayList) {
                        sendNotification(keys);
                    }
                    updateNotificationInDB(notificationTitle.getText().toString(),
                            notificationMessage.getText().toString(), "marketing", "");
                    notificationTitle.setText("");
                    notificationMessage.setText("");
                    CommonUtils.showToast("Sent notification to all");
//                    finish();
                }
            }
        });
    }

    private void updateNotificationInDB(String title, String message, String type, String id) {
        String key = mDatabase.push().getKey();
        mDatabase.child("CustomerNotifications").child(key).setValue(new CustomerNotificationModel(
                key, title, message, type, id, System.currentTimeMillis()
        )).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    private void sendNotification(String keys) {
        NotificationAsync notificationAsync = new NotificationAsync(SendNotification.this);
        String notification_title = notificationTitle.getText().toString();
        String notification_message = notificationMessage.getText().toString();
        notificationAsync.execute("ali", keys, notification_title, notification_message, "marketing", "abc");
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

    @Override
    public void onSuccess(String chatId) {

    }

    @Override
    public void onFailure() {

    }
}
