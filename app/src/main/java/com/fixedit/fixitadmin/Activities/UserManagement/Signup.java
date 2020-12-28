package com.fixedit.fixitadmin.Activities.UserManagement;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.fixedit.fixitadmin.Activities.Coupons.Splash;
import com.fixedit.fixitadmin.Models.VendorModel;
import com.fixedit.fixitadmin.R;
import com.fixedit.fixitadmin.Services.AddService;
import com.fixedit.fixitadmin.Utils.CommonUtils;
import com.fixedit.fixitadmin.Utils.CompressImage;
import com.fixedit.fixitadmin.Utils.GifSizeFilter;
import com.fixedit.fixitadmin.Utils.SharedPrefs;
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

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class Signup extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE = 23;
    EditText name, email, username, password, phone, officePhone, cnic, officeAddress, homeAddress;
    Button signup;
    private VendorModel model;
    DatabaseReference mDatabase;
    List<Uri> mSelected = new ArrayList<>();
    ArrayList<String> imageUrl = new ArrayList<>();
    StorageReference mStorageRef;
    ImageView picture;
    private Uri downloadUrl;
    RelativeLayout wholeLayout;
    private HashMap<String, VendorModel> vendorsMap = new HashMap<>();
    private String cityChosen;
    Spinner citySpinner;
    private ArrayList<String> cityList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        getPermissions();
        getAllVendors();
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        picture = findViewById(R.id.picture);
        phone = findViewById(R.id.phone);
        officePhone = findViewById(R.id.officePhone);
        cnic = findViewById(R.id.cnic);
        citySpinner = findViewById(R.id.citySpinner);
        officeAddress = findViewById(R.id.officeAddress);
        homeAddress = findViewById(R.id.homeAddress);
        signup = findViewById(R.id.signup);
        wholeLayout = findViewById(R.id.wholeLayout);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMatisse();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelected.size() == 0) {
                    CommonUtils.showToast("Please select image");
                } else if (name.getText().length() == 0) {
                    name.setError("Enter name");
                } else if (email.getText().length() == 0) {
                    email.setError("Enter email");
                } else if (username.getText().length() == 0) {
                    username.setError("Enter username");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else if (phone.getText().length() == 0) {
                    phone.setError("Enter phone");
                } else if (cnic.getText().length() == 0) {
                    cnic.setError("Enter cnic");
                } else if (cityChosen.equalsIgnoreCase("Choose city")) {
                    CommonUtils.showToast("Choose city");
                } else {
                    checkLogin();
                }
            }
        });
        mDatabase.child("Cities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    cityList.add("Choose City");
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        cityList.add(snapshot.getKey());
                    }
                    setupSpinner();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void checkLogin() {
        if (vendorsMap.containsKey(username.getText().toString())) {
            CommonUtils.showToast("Username already taken");
        } else {
            putPictures(imageUrl.get(0));
        }
    }

    private void getAllVendors() {
        mDatabase.child("Vendors").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        VendorModel model = snapshot.getValue(VendorModel.class);
                        if (model.getUsername() != null) {
                            vendorsMap.put(model.getUsername(), model);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void putPictures(String path) {
        wholeLayout.setVisibility(View.VISIBLE);
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        final Uri file = Uri.fromFile(new File(path));

        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        downloadUrl = taskSnapshot.getDownloadUrl();
                        model = new VendorModel(
                                name.getText().toString(),
                                email.getText().toString(),
                                username.getText().toString(),
                                password.getText().toString(),
                                phone.getText().toString(),
                                officePhone.getText().toString(),
                                cnic.getText().toString(),
                                cityChosen,
                                "" + downloadUrl,
                                homeAddress.getText().toString(),
                                officeAddress.getText().toString(),
                                false,
                                false

                        );
                        mDatabase.child("Vendors").child(username.getText().toString()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Signup Successful\nPlease wait for admin approval");
                                SharedPrefs.setVendorModel(model);
                                wholeLayout.setVisibility(View.GONE);
                                Intent i=new Intent(Signup.this, Splash.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initMatisse() {
        mSelected.clear();
        imageUrl.clear();
        Matisse.from(this)
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
                imageUrl.add(CompressImage.compressImage("" + img, Signup.this));
            }
            Glide.with(this).load(mSelected.get(0)).into(picture);

        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupSpinner() {


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, cityList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(dataAdapter);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                gridViewAdapter.getFilter().filter(list.get(position));

                cityChosen=cityList.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

}
