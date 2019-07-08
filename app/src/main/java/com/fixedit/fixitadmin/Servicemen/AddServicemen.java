package com.fixedit.fixitadmin.Servicemen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.fixedit.fixitadmin.R;
import com.fixedit.fixitadmin.Utils.CommonUtils;
import com.fixedit.fixitadmin.Utils.CompressImage;
import com.fixedit.fixitadmin.Utils.GifSizeFilter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddServicemen extends AppCompatActivity {
    private static final int REQUEST_CODE_CHOOSE = 23;
    Button update;
    EditText servicemanName, username, password, mobile, age, cnic;
    Spinner spinner;
    DatabaseReference mDatabase;
    List<Uri> mSelected = new ArrayList<>();
    ArrayList<String> imageUrl = new ArrayList<>();
    StorageReference mStorageRef;

    CircleImageView servicemanImage;
    String id;
    RelativeLayout wholeLayout;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_servicemen);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Add Serviceman");
        getPermissions();

        id = getIntent().getStringExtra("id");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        update = findViewById(R.id.update);

        servicemanImage = findViewById(R.id.servicemanImage);
        wholeLayout = findViewById(R.id.wholeLayout);
        servicemanName = findViewById(R.id.servicemanName);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        spinner = findViewById(R.id.spinner);
        mobile = findViewById(R.id.mobile);
        age = findViewById(R.id.age);
        cnic = findViewById(R.id.cnic);


        servicemanImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMatisse();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (servicemanName.getText().toString().trim().isEmpty()) {
                    servicemanName.setError("Enter service name");
                } else if (username.getText().toString().trim().isEmpty()) {
                    username.setError("Enter username");
                } else if (password.getText().toString().trim().isEmpty()) {
                    password.setError("Enter password");
                } else if (mobile.getText().toString().trim().isEmpty()) {
                    mobile.setError("Enter mobile");
                } else if (age.getText().toString().trim().isEmpty()) {
                    age.setError("Enter age");
                } else if (cnic.getText().toString().trim().isEmpty()) {
                    cnic.setError("Enter cnic");
                } else if (role == null) {
                    cnic.setError("Select role");
                } else {

                    if (id != null) {
                        updateDataToDB();
                    } else {
                        id = username.getText().toString();
                        sendDataToDB();
                    }
                }
            }
        });
        if (id != null) {
            getDataFromDB();
        }
        final String[] items = new String[]{"Select role", "Electrician", "Plumber"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                role = items[i];

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateDataToDB() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("name", servicemanName.getText().toString());
        map.put("username", username.getText().toString());
        map.put("password", password.getText().toString());
        map.put("mobile", mobile.getText().toString());
        map.put("age", Integer.parseInt(age.getText().toString()));
        map.put("cnic", Long.parseLong(cnic.getText().toString()));
        map.put("role", role);

        mDatabase.child("Servicemen").child(id).updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Updated");
                    }
                });

    }

    private void getDataFromDB() {
        mDatabase.child("Servicemen").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ServicemanModel model = dataSnapshot.getValue(ServicemanModel.class);
                    if (model != null) {
                        servicemanName.setText(model.getName());
                        username.setText("" + model.getUsername());
                        username.setEnabled(false);
                        password.setText("" + model.getPassword());
                        mobile.setText(model.getMobile());
                        age.setText("" + model.getAge());
                        cnic.setText("" + model.getCnic());
                        Glide.with(AddServicemen.this).load(model.getImageUrl()).into(servicemanImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendDataToDB() {
        wholeLayout.setVisibility(View.VISIBLE);
        ServicemanModel model = new ServicemanModel(
                id,
                servicemanName.getText().toString(),
                username.getText().toString(),
                password.getText().toString(),
                mobile.getText().toString(),
                role,
                true, false,
                Integer.parseInt(age.getText().toString()),
                Long.parseLong(cnic.getText().toString()),
                ""

        );
        mDatabase.child("Servicemen").child(id).setValue(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (imageUrl.size() > 0) {
                            putPictures(imageUrl.get(0));
                        } else {
                            CommonUtils.showToast("Updated");

                            wholeLayout.setVisibility(View.GONE);
                        }
                    }
                });


    }

    private void initMatisse() {
        mSelected.clear();
        imageUrl.clear();
        Matisse.from(AddServicemen.this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(1)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE && data != null) {
            mSelected = Matisse.obtainResult(data);
            for (Uri img : mSelected) {
                imageUrl.add(CompressImage.compressImage("" + img, AddServicemen.this));
            }
            Glide.with(this).load(mSelected.get(0)).into(servicemanImage);

        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void putPictures(String path) {
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        final Uri file = Uri.fromFile(new File(path));

        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        mDatabase.child("Servicemen")
                                .child(id)
                                .child("imageUrl").setValue("" + downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Updated");
                                wholeLayout.setVisibility(View.GONE);
                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        CommonUtils.showToast(exception.getMessage() + "");
                        wholeLayout.setVisibility(View.GONE);

                    }
                });


    }

    //


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

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,

        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
//        CommonUtils.showToast(PERMISSION_ALL+"");
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
