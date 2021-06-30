package com.example.userzerde.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.userzerde.R;
import com.example.userzerde.modules.CheckInternetStatus;
import com.example.userzerde.modules.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnNameChanger, btnImageChanger, btnPasswordChanger, btnPhoneChanger, btnAddressChanger, btnEmailChanger;
    ImageView imgView;
    TextView tvProfileName, tvProfilePhone, tvProfileAddress, tvProfileEmail, tvProfilePassword;
    AlertDialog.Builder dialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private StorageReference mstorageReference;

    String email1;
    ProgressDialog progressDialog;
    String emailPath;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imgUri;
    ImageView imgView1;

    CheckInternetStatus mCheckInternetStatus;
    boolean is_internet_connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Профиль");

        actionBar.setDisplayHomeAsUpEnabled(true);

        mCheckInternetStatus = new CheckInternetStatus(ProfileActivity.this);
        is_internet_connected = mCheckInternetStatus.isInternetConnected();
        if (!is_internet_connected) {
            Toast.makeText(this, "Интернетті тексеріңіз", Toast.LENGTH_SHORT).show();
            return;
        }

        initViews();

        btnNameChanger.setOnClickListener(this);
        btnImageChanger.setOnClickListener(this);
        btnAddressChanger.setOnClickListener(this);
        btnEmailChanger.setOnClickListener(this);
        btnPhoneChanger.setOnClickListener(this);
        btnPasswordChanger.setOnClickListener(this);

        imgView.setOnClickListener(this);

        tvProfileName.setOnClickListener(this);
        tvProfilePhone.setOnClickListener(this);
        tvProfileAddress.setOnClickListener(this);
        tvProfileEmail.setOnClickListener(this);
        tvProfilePassword.setOnClickListener(this);
    }

    private void viewData() {
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressdialog_custom);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        email1 = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.i("email", "123 " + email1);
        emailPath = email1.replace(".", "-");
        Log.i("email", "123 " + emailPath);
        mDatabaseReference.child(emailPath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);

                    if (!user.getImgUri().isEmpty()) {
                        Glide
                                .with(getApplicationContext())
                                .load(user.getImgUri())
                                .centerCrop()
                                .placeholder(R.drawable.account)
                                .into(imgView);
                    }

                    tvProfileName.setText(user.getFullName());
                    tvProfilePhone.setText(user.getPhone());
                    tvProfileAddress.setText(user.getCity() + ", " + user.getAddress() + ", " + user.getTochka());
                    tvProfileEmail.setText(user.getEmail());
                    progressDialog.cancel();
                } else {
                    Toast.makeText(getApplicationContext(), "Бұл қолданушы туралы инфо жоқ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
    }

    private void initViews() {
        btnNameChanger = findViewById(R.id.btnNameChanger);
        btnImageChanger = findViewById(R.id.btnImageChanger);
        btnAddressChanger = findViewById(R.id.btnAddressChanger);
        btnEmailChanger = findViewById(R.id.btnEmailChanger);
        btnPhoneChanger = findViewById(R.id.btnPhoneChanger);
        btnPasswordChanger = findViewById(R.id.btnPasswordChanger);

        imgView = findViewById(R.id.imgProfile);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);
        tvProfileAddress = findViewById(R.id.tvProfileAddress);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfilePassword = findViewById(R.id.tvProfilePassword);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mstorageReference = FirebaseStorage.getInstance().getReference("UserImage");

        dialog = new AlertDialog.Builder(this);

        viewData();
    }

    private void dialogViewName() {
        View dialogView = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_name, null);

        EditText name, surname;
        Button btnSave;

        name = dialogView.findViewById(R.id.name);
        surname = dialogView.findViewById(R.id.surname);
        btnSave = dialogView.findViewById(R.id.btnSave1);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty()){
                    name.setError("Толығымен толтырыңыз!");
                    return;
                }

                if (surname.getText().toString().isEmpty()){
                    surname.setError("Толығымен толтырыңыз!");
                    return;
                }

                progressDialog.show();
                progressDialog.setContentView(R.layout.progressdialog_custom);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                mDatabaseReference.child(emailPath).child("fullName").setValue(name.getText().toString() + " " + surname.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ProfileActivity.this, "Өзгертілді!", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                });
            }
        });

        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void dialogViewImage() {
        openFileChooser();
        View dialogView = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_image, null);

        Button btnUpload2;

        imgView1 = dialogView.findViewById(R.id.imgView);
        btnUpload2 = dialogView.findViewById(R.id.btnUpload2);

        mDatabaseReference.child(emailPath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);

                    if (!user.getImgUri().isEmpty()) {
                        Glide
                                .with(getApplicationContext())
                                .load(user.getImgUri())
                                .centerCrop()
                                .placeholder(R.drawable.account)
                                .into(imgView1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnUpload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void dialogViewAddress() {
        View dialogView = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_address, null);

        EditText city, street, home;
        Button btnSave3;

        city = dialogView.findViewById(R.id.city);
        street = dialogView.findViewById(R.id.street);
        home = dialogView.findViewById(R.id.home1);
        btnSave3 = dialogView.findViewById(R.id.btnSave3);

        btnSave3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city.getText().toString().isEmpty()){
                    city.setError("Толығымен толтырыңыз!");
                    return;
                }

                if (street.getText().toString().isEmpty()){
                    street.setError("Толығымен толтырыңыз!");
                    return;
                }

                if (home.getText().toString().isEmpty()){
                    home.setError("Толығымен толтырыңыз!");
                    return;
                }

                progressDialog.show();
                progressDialog.setContentView(R.layout.progressdialog_custom);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                mDatabaseReference.child(emailPath).child("address").setValue(street.getText().toString());
                mDatabaseReference.child(emailPath).child("city").setValue(city.getText().toString());
                mDatabaseReference.child(emailPath).child("tochka").setValue(home.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.cancel();
                        Toast.makeText(ProfileActivity.this, "Өзгертілді!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void dialogViewPhone() {
        View dialogView = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_phone, null);

        EditText phoneNumber;
        Button btnSave4;

        phoneNumber = dialogView.findViewById(R.id.phoneNumber);
        btnSave4 = dialogView.findViewById(R.id.btnSave4);

        btnSave4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNumber.getText().toString().isEmpty()){
                    phoneNumber.setError("Толығымен толтырыңыз!");
                    return;
                }

                progressDialog.show();
                progressDialog.setContentView(R.layout.progressdialog_custom);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                mDatabaseReference.child(emailPath).child("phone").setValue(phoneNumber.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.cancel();
                        Toast.makeText(ProfileActivity.this, "Өзгертілді!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void dialogViewPassword() {
        View dialogView = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_password, null);

        Button btnSend5;

        btnSend5 = dialogView.findViewById(R.id.btnSend5);

        btnSend5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(email1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Почтаңызды тексеріңіз!", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNameChanger:
                dialogViewName();
                break;
            case R.id.tvProfileName:
                dialogViewName();
                break;
            case R.id.btnImageChanger:
                dialogViewImage();
                break;
            case R.id.imgProfile:
                dialogViewImage();
                break;
            case R.id.btnAddressChanger:
                dialogViewAddress();
                break;
            case R.id.tvProfileAddress:
                dialogViewAddress();
                break;
            case R.id.btnPhoneChanger:
                dialogViewPhone();
                break;
            case R.id.tvProfilePhone:
                dialogViewPhone();
                break;
            case R.id.btnPasswordChanger:
                dialogViewPassword();
                break;
            case R.id.tvProfilePassword:
                dialogViewPassword();
                break;
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imgUri = data.getData();

            Glide
                    .with(this)
                    .load(imgUri)
                    .into(imgView1);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressdialog_custom);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (imgUri != null) {
            StorageReference fileReference = mstorageReference.child(System.currentTimeMillis() + "." + getFileExtension(imgUri));

            ProgressDialog progressDialog1 = new ProgressDialog(this);
            progressDialog1.setTitle("Uploading...");
            progressDialog1.show();

            fileReference.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog1.dismiss();
                                }
                            }, 500);

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imgUrl = "" + uri;
                                    Toast.makeText(getApplicationContext(), "Сурет енгізілді", Toast.LENGTH_SHORT).show();
                                    mDatabaseReference.child(emailPath).child("imgUri").setValue(imgUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog1.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog1.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Суретті таңдаңыз!", Toast.LENGTH_SHORT).show();
        }
    }
}