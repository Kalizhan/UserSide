package com.example.userzerde.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userzerde.MainActivity;
import com.example.userzerde.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    TextView intentRegisterBtn, forgetButton;

    EditText emailOrPhone, password;
    Button btnEnter;

    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        intentRegisterBtn = findViewById(R.id.intentRegisterBtn);
        forgetButton = findViewById(R.id.forgetButton);

        emailOrPhone = findViewById(R.id.etPhoneOrEmailLogin);
        password = findViewById(R.id.etPasswordLogin);
        btnEnter = findViewById(R.id.loginBtn);

        intentRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });


        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(emailOrPhone.getText())) {
                    emailOrPhone.setError("Поле бос болмауы керек");
                    return;
                }

                mAuth.sendPasswordResetEmail(emailOrPhone.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LoginActivity.this, "Почтаңызды тексеріңіз!", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(emailOrPhone.getText())) {
                    emailOrPhone.setError("Поле бос болмауы керек");
                    return;
                }
                if (TextUtils.isEmpty(password.getText())) {
                    password.setError("Поле бос болмауы керек");
                    return;
                }

                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progressdialog_custom);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


                String sEmail = emailOrPhone.getText().toString();
                String sPassword = password.getText().toString();

                mAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.cancel();
                            Toast.makeText(getApplicationContext(), "Қолданушы табылды", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("email", sEmail);
                            startActivity(intent);
                        }else{
                            progressDialog.cancel();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(getApplicationContext(), "Пароль қате", Toast.LENGTH_SHORT).show();
                            }else if (task.getException() instanceof FirebaseAuthInvalidUserException){
                                Toast.makeText(LoginActivity.this, "Бұндай email жоқ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    private void reload() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        progressDialog.dismiss();
    }
}