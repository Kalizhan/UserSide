package com.example.userzerde.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userzerde.MainActivity;
import com.example.userzerde.R;
import com.example.userzerde.modules.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.santalu.maskedittext.MaskEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistrationActivity extends AppCompatActivity  implements View.OnClickListener {
    TextView intentLoginBtn;
    EditText fullName, email, password, address, tochka;
    Spinner city;
    MaskEditText phone;
    Button registerBtn;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mDatabase;
    ProgressDialog progressDialog;

    private static final String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initId();

        intentLoginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    private void initId() {
        registerBtn = findViewById(R.id.registerBtn);
        intentLoginBtn = findViewById(R.id.intentLoginBtn);
        fullName = findViewById(R.id.etFullnameRegister);
        phone = findViewById(R.id.etPhone);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPasswordRegister);
        city = findViewById(R.id.spinnerCity);
        address = findViewById(R.id.etAddress);
        tochka = findViewById(R.id.etTochka);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerBtn:
                //String userId = mDatabase.push().getKey();
                String fullname1 = fullName.getText().toString();
                String phone1 = phone.getText().toString();
                String email1 = email.getText().toString();
                String password1 = password.getText().toString();
                String ruqsat = "no";
                String imguri = "https://www.seekpng.com/png/small/41-410093_circled-user-icon-user-profile-icon-png.png";
                String emailPath = email1.replace(".", "-");
                String City = city.getSelectedItem().toString();
                String Address = address.getText().toString();
                String Tochka = tochka.getText().toString();

                if (fullname1.isEmpty()){
                    fullName.setError("Аты-жөніңізді толтырыңыз");
                    return;
                }
                if (phone1.isEmpty()){
                    phone.setError("Нөмеріңізді толтырыңыз");
                    return;
                }
                if (email1.isEmpty()){
                    email.setError("Email толтырыңыз");
                    return;
                }
                if (password1.isEmpty()){
                    password.setError("Пароль толтырыңыз");
                    return;
                }
                if (password1.length()<=5){
                    password.setError("Пароль 6 символдан кіші болмауы керек");
                    return;
                }

                if (City.equals("Қала(қосымша)")){
                    City = "";
                }

                Pattern pattern = Pattern.compile(regex);

                Matcher matcher = pattern.matcher(email.getText().toString());

                if ((matcher.matches() ? "valid" : "invalid").equals("invalid")){
                    email.setError("Email дұрыс жазыңыз");
                    return;
                }

                progressDialog = new ProgressDialog(this);
//                progressDialog.setTitle("Енгізілуде...");
//                progressDialog.setMessage("Күте тұрыңыз");
                progressDialog.show();
                progressDialog.setContentView(R.layout.progressdialog_custom);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

//                Log.i("token", "token: " + FirebaseMessaging.getInstance().token.);

                User user = new User(fullname1, phone1, email1, password1, ruqsat, imguri, City, Address, Tochka, "null", "null");
                mDatabase.child("Users").child(emailPath).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            progressDialog.cancel();
                            Toast.makeText(RegistrationActivity.this, "Админ сізді қабылдағанын күтіңіз", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.cancel();
                        Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.intentLoginBtn:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        progressDialog.dismiss();
    }
}