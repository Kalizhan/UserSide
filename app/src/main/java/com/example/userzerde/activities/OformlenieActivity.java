package com.example.userzerde.activities;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import com.example.userzerde.MainActivity;
import com.example.userzerde.R;
import com.example.userzerde.database.StoreDatabase;
import com.example.userzerde.modules.NewZakaz;
import com.example.userzerde.modules.User;
import com.example.userzerde.ui.tapsyrystar.TapsyrystarFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.example.userzerde.database.StoreDatabase.COLUMN_CODE;
import static com.example.userzerde.database.StoreDatabase.COLUMN_FCOUNT;
import static com.example.userzerde.database.StoreDatabase.COLUMN_FPRICE;
import static com.example.userzerde.database.StoreDatabase.COLUMN_NAME;
import static com.example.userzerde.database.StoreDatabase.TABLE_ORDER;

public class OformlenieActivity extends AppCompatActivity implements View.OnClickListener {
    StoreDatabase storeDb;
    static SQLiteDatabase sqdb;
    EditText userName, userPhone, userComment;
    Button finishBtn;
    int sumPrice = 0;
    int countOrder = 0;
    EditText address, tochka, spinnerCity;
    Spinner spinnerPayment, spinnerHowTopay;
    Switch checkDelivery;

    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    String email;
    ProgressDialog progressDialog;

    String foodCode, foodName, orderTxt;
    int foodCount, sumPrice2, sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oformlenie);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail().replace(".", "-");

        storeDb = new StoreDatabase(this);
        sqdb = storeDb.getWritableDatabase();
        userName = findViewById(R.id.userName);
        userPhone = findViewById(R.id.userPhone);
        userComment = findViewById(R.id.userComment);
        finishBtn = findViewById(R.id.finishBtn);

        spinnerCity = findViewById(R.id.spinnerCity);
        address = findViewById(R.id.spinnerAddress);
        tochka = findViewById(R.id.spinnerTochka);

        checkDelivery = findViewById(R.id.switchDeliver);
        spinnerPayment = findViewById(R.id.spinnerPay);
        spinnerHowTopay = findViewById(R.id.spinnerHowToPay);

        getFoodFromDb();
        orderTxt = "Төлем жасау\n" + countOrder + " тауар, " + sumPrice + " тг";
        sum = sumPrice;
        finishBtn.setText(orderTxt);

//        if (spinnerCity.getSelectedItem().equals("Қала(қосымша)")) {
//            spinnerCity.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_yellow));
//        } else {
//            spinnerCity.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_grey));
//        }

        databaseReference.child("Users").child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
//                if (user.getFullName().isEmpty() || user.getPhone().isEmpty() || user.getCity().isEmpty() || user.getAddress().isEmpty() || user.getTochka().isEmpty()){
//
//                }
                try {
                    userName.setText(user.getFullName());
                    userPhone.setText(user.getPhone());
                    spinnerCity.setText(user.getCity());
                    address.setText(user.getAddress());
                    tochka.setText(user.getTochka());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        finishBtn.setOnClickListener(this);
        checkDelivery.setOnClickListener(this);
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

    public void getFoodFromDb() {
        Cursor res = sqdb.rawQuery("SELECT * FROM " + TABLE_ORDER, null);
        if (res != null && res.getCount() > 0) {
            countOrder = res.getCount();

            while (res.moveToNext()) {
                foodCount = res.getInt(res.getColumnIndex(COLUMN_FCOUNT));
                int foodPrice = res.getInt(res.getColumnIndex(COLUMN_FPRICE));
                foodCode = res.getString(res.getColumnIndex(COLUMN_CODE));
                foodName = res.getString(res.getColumnIndex(COLUMN_NAME));
                Code = Code + foodCode + ",";
                Count = Count + foodCount + ",";
                Name = Name + foodName + ",";
                Log.i("foodName1", Code);

                sumPrice += foodPrice * foodCount;
            }
        }
    }

    String Code = "";
    String Count = "";
    String Name = "";
    Dialog dialogSuccess;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finishBtn:
                String checkDeliver;
                if (TextUtils.isEmpty(userName.getText())){
                    userName.setError("Аты-жөніңізді толтырыңыз");
                    return;
                }

                if (TextUtils.isEmpty(userPhone.getText())){
                    userPhone.setError("Нөмеріңізді толтырыңыз");
                    return;
                }

                if (TextUtils.isEmpty(spinnerCity.getText())){
                    spinnerCity.setError("Қаланы таңдаңыз");
                    return;
                }

                if (TextUtils.isEmpty(address.getText())){
                    address.setError("Толық толтырыңыз!");
                    return;
                }

                if (TextUtils.isEmpty(tochka.getText())){
                    tochka.setError("Толық толтырыңыз!");
                    return;
                }

                if (checkDelivery.isChecked()){
                    checkDeliver = "yes";
                    sumPrice = sumPrice2;
                }else{
                    checkDeliver = "no";
                    sumPrice = sum;
                }

                String keys = databaseReference.push().getKey();
                progressDialog = new ProgressDialog(OformlenieActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progressdialog_custom);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+6"));
                Date today = Calendar.getInstance().getTime();
                //Log.i("rightNow", ""+dateFormat.format(today));
                NewZakaz newZakaz = new NewZakaz(""+userName.getText(), "" + userPhone.getText(), email.replace("-", "."),
                        ""+spinnerCity.getText()+", "+ address.getText()+", "+tochka.getText(),
                        dateFormat.format(today), Code, "new order", Long.parseLong(String.valueOf(sumPrice)), Count, ""+userComment.getText(), Name, keys, checkDeliver,
                        spinnerPayment.getSelectedItem().toString(), spinnerHowTopay.getSelectedItem().toString());
                //Log.i("rightNow", newZakaz.getCheckDelivery() + newZakaz.getPayment() + newZakaz.getPaymentStyle() + newZakaz.getTovarPrice());
                databaseReference.child("Zakazdar").child(email).child(keys).setValue(newZakaz).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String clearDb = " DELETE FROM " + TABLE_ORDER;
                        sqdb.execSQL(clearDb);

                        progressDialog.cancel();
                        dialogSuccess = new Dialog(OformlenieActivity.this);
                        dialogSuccess.show();
                        dialogSuccess.setContentView(R.layout.custom_dialog_success);
                        dialogSuccess.getWindow().setBackgroundDrawable(getDrawable(R.drawable.bg_round));
                        dialogSuccess.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialogSuccess.setCancelable(false);

                        Button okey = dialogSuccess.findViewById(R.id.btnOkey);

                        okey.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                                Fragment mFragment = null;
//                                mFragment = new TapsyrystarFragment();
//                                FragmentManager fragmentManager = getSupportFragmentManager();
//                                fragmentManager.beginTransaction().replace(R.id.linearLayout, mFragment).commit();
                            }
                        });
                    }
                });
                break;
            case R.id.switchDeliver:
                if (checkDelivery.isChecked()){
                    sumPrice2 = sumPrice + 1000;
//                    Log.i("sumprice", String.valueOf(sumPrice2));
                    String orderTxt2 = "Төлем жасау\n" + countOrder + " тауар, " + sumPrice2 + " тг";
                    finishBtn.setText(orderTxt2);
                }else{
//                    Log.i("sumprice", ""+sumPrice);
                    finishBtn.setText(orderTxt);
                }
                break;
        }
    }


}