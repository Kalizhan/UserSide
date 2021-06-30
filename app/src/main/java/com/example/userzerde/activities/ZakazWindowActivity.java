package com.example.userzerde.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.userzerde.MainActivity;
import com.example.userzerde.R;
import com.example.userzerde.adapters.BoughtTovarListAdapter;
import com.example.userzerde.adapters.TovarListAdapter;
import com.example.userzerde.adapters.ZakazListAdapter;
import com.example.userzerde.modules.NewZakaz;
import com.example.userzerde.modules.Tovar;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static com.example.userzerde.database.StoreDatabase.TABLE_ORDER;

public class ZakazWindowActivity extends AppCompatActivity implements View.OnClickListener {
    TextView enteredDate, fullName, address, phoneNumber, orderPrice, deliveryPrice, fullPrice, paym, paymStyle;
    CardView delivery, cardViewColor;
    LinearLayout cardLayout;
    Button btnRepeat;
    String[] codes, quantities;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    List<Tovar> tovarArrayList;
    BoughtTovarListAdapter boughtTovarListAdapter;

    DatabaseReference mDatabaseReference;
    ProgressDialog progressDialog;
    Dialog dialogRepeat, dialogSuccess;
    NewZakaz newZakaz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakaz_window);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        init();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        newZakaz = (NewZakaz) intent.getSerializableExtra("zakaz");

        enteredDate.setText(newZakaz.getDate());
        fullName.setText(newZakaz.getFullName());
        address.setText(newZakaz.getAddress());
        phoneNumber.setText(newZakaz.getPhoneNum());
        paym.setText(newZakaz.getPayment());
        paymStyle.setText(newZakaz.getPaymentStyle());

        fullPrice.setText(newZakaz.getTovarPrice().toString() + " тг");

        codes = newZakaz.getTovarCode().split(",");
        quantities = newZakaz.getTovarQuantity().split(",");
        long price = newZakaz.getTovarPrice() - 1000;
        if (newZakaz.getCheckDelivery().equals("yes")) {
            delivery.setVisibility(View.VISIBLE);
            cardLayout.setVisibility(View.VISIBLE);
            orderPrice.setText("" + price);
        } else {
            delivery.setVisibility(View.GONE);
            cardLayout.setVisibility(View.INVISIBLE);
            orderPrice.setText(newZakaz.getTovarPrice() + " тг");
        }

//        if (newZakaz.getTovarSituation().equals("new order")){
//            cardViewColor.setCardBackgroundColor(ContextCompat.getColor(this, R.color.neworder));
//        }else if (newZakaz.getTovarSituation().equals("order accepted")) {
//            cardViewColor.setCardBackgroundColor(ContextCompat.getColor(this, R.color.accept));
//        }else if (newZakaz.getTovarSituation().equals("order got")){
//            cardViewColor.setCardBackgroundColor(ContextCompat.getColor(this, R.color.finished));
//        }

        tovarArrayList = new ArrayList<>();
        boughtTovarListAdapter = new BoughtTovarListAdapter(ZakazWindowActivity.this, tovarArrayList, newZakaz.getTovarQuantity());
        recyclerView.setAdapter(boughtTovarListAdapter);
        linearLayoutManager = new LinearLayoutManager(ZakazWindowActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        progressDialog = new ProgressDialog(ZakazWindowActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressdialog_custom);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        getData();
        btnRepeat.setOnClickListener(this);
    }

    private void init() {
        recyclerView = findViewById(R.id.zakazWindowRecycler);
        enteredDate = findViewById(R.id.tvZakazDate);
        fullName = findViewById(R.id.tvZakazFullName);
        address = findViewById(R.id.tvZakazCity);
        phoneNumber = findViewById(R.id.tvZakazPhoneNumber);
        delivery = findViewById(R.id.linearLayout1);
        orderPrice = findViewById(R.id.tvZakazOrderPrice);
        deliveryPrice = findViewById(R.id.tvZakazDeliverPrice);
        fullPrice = findViewById(R.id.tvZakazFullPrice);
        btnRepeat = findViewById(R.id.zakazBtnRepeat);
        cardLayout = findViewById(R.id.cardLayout);
        paym = findViewById(R.id.paym);
        paymStyle = findViewById(R.id.paymStyle);
        cardViewColor = findViewById(R.id.cardViewColor);
    }

    private void getData() {
        mDatabaseReference.child("TovarInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Tovar tovar = snapshot1.getValue(Tovar.class);

                        for (int i = 0; i < codes.length; i++) {
                            if (tovar.getCode().equals(codes[i])) {
                                tovarArrayList.add(tovar);
                            }
                        }
                    }
                    progressDialog.cancel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zakazBtnRepeat:
                dialogRepeat = new Dialog(ZakazWindowActivity.this);
                dialogRepeat.show();
                dialogRepeat.setContentView(R.layout.custom_dialog_repeat_zakaz);
                dialogRepeat.getWindow().setBackgroundDrawable(getDrawable(R.drawable.bg_round));
                dialogRepeat.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogRepeat.setCancelable(true);

                Button okey = dialogRepeat.findViewById(R.id.btnRepeat);

                okey.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        progressDialog.setCancelable(false);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss");
                        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+6"));
                        Date today = Calendar.getInstance().getTime();

                        String key = mDatabaseReference.push().getKey();

                        NewZakaz newZakaz1 = new NewZakaz("" + newZakaz.getFullName(), "" + newZakaz.getPhoneNum(), newZakaz.getEmail(), newZakaz.getAddress(),
                                dateFormat.format(today), newZakaz.getTovarCode(), "new order", newZakaz.getTovarPrice(), newZakaz.getTovarQuantity(), newZakaz.getComment(), newZakaz.getTovarName(), key,
                                newZakaz.getCheckDelivery(), newZakaz.getPayment(), newZakaz.getPaymentStyle());
                        mDatabaseReference.child("Zakazdar").child(newZakaz.getEmail().replace(".", "-")).child(key).setValue(newZakaz1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.cancel();
                                dialogSuccess = new Dialog(ZakazWindowActivity.this);
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
                                    }
                                });
                            }
                        });
                    }
                });
                break;
        }
    }
}