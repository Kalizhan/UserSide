package com.example.userzerde.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userzerde.R;
import com.example.userzerde.adapters.TovarInfoAdapter;
import com.example.userzerde.database.StoreDatabase;
import com.example.userzerde.modules.Tovar;

import java.util.ArrayList;

import static com.example.userzerde.MainActivity.badgeDefault;
import static com.example.userzerde.database.StoreDatabase.COLUMN_CODE;
import static com.example.userzerde.database.StoreDatabase.COLUMN_FCOUNT;
import static com.example.userzerde.database.StoreDatabase.COLUMN_FPRICE;
import static com.example.userzerde.database.StoreDatabase.COLUMN_NAME;
import static com.example.userzerde.database.StoreDatabase.COLUMN_PHOTO;
import static com.example.userzerde.database.StoreDatabase.COLUMN_QUANTITY;
import static com.example.userzerde.database.StoreDatabase.TABLE_ORDER;

public class TovarActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTovarName, tvTovarCode, tvTovarPrice, btnWatchVideo, tvDopInfo;
    CardView cardViewInfo;
    Button btnAddtoCart;
    RecyclerView recyclerView;
    ArrayList<Tovar> tovarArrayList;
    TovarInfoAdapter tovarInfoAdapter;

    StoreDatabase storeDb;
    SQLiteDatabase sqdb;

    Tovar curTovar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tovar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);


        tvTovarName = findViewById(R.id.tovarNameTovar);
        tvTovarCode = findViewById(R.id.tovarCodeTovar);
        tvTovarPrice = findViewById(R.id.tovarPriceTovar);
        btnAddtoCart = findViewById(R.id.btnAddToCart);
        btnWatchVideo = findViewById(R.id.btnWatchVideo);
        tvDopInfo = findViewById(R.id.tovarTuralyInfo);
        cardViewInfo = findViewById(R.id.cardViewDopInfo);

        String[] splitUrls = new String[0];
        try {
            Intent intent = getIntent();
            curTovar = (Tovar) intent.getSerializableExtra("tovar");

            tvTovarName.setText(curTovar.getName());
            tvTovarCode.setText("#"+curTovar.getCode());
            tvTovarPrice.setText(curTovar.getPrice()+" тг");

            if (curTovar.getDopInfo().isEmpty()){
                cardViewInfo.setVisibility(View.INVISIBLE);
            }else{
                cardViewInfo.setVisibility(View.VISIBLE);
                tvDopInfo.setText(curTovar.getDopInfo());
            }

            splitUrls = curTovar.getPhoto().split(",");
        } catch (Exception e) {
            e.printStackTrace();
        }

        actionBar.setTitle(curTovar.getName());

        recyclerView = findViewById(R.id.recyclerviewTovar);
        tovarArrayList = new ArrayList<>();
        tovarInfoAdapter = new TovarInfoAdapter(getApplicationContext(), splitUrls);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(tovarInfoAdapter);

        storeDb = new StoreDatabase(getApplicationContext());
        sqdb = storeDb.getWritableDatabase();

        btnAddtoCart.setOnClickListener(this);
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
        switch (v.getId()){
            case R.id.btnAddToCart:
                Cursor res = sqdb.rawQuery("SELECT * FROM " + TABLE_ORDER + " WHERE " +
                        COLUMN_CODE + "=?", new String[]{curTovar.getCode()});

                if (res != null && res.getCount() > 0){
                    res.moveToFirst();
                    String fCount = res.getString(res.getColumnIndex(COLUMN_FCOUNT));
                    ContentValues updateValues = new ContentValues();
//                    if (curTovar.getQuantity() == Integer.parseInt(fCount) + 1){
//                        holder.add_circle.setVisibility(View.INVISIBLE);
//                    }else{
//                        holder.add_circle.setVisibility(View.VISIBLE);
//                    }
                    updateValues.put(COLUMN_FCOUNT, (Integer.parseInt(fCount) + 1));
                    sqdb.update(TABLE_ORDER, updateValues, COLUMN_CODE + "='"+ curTovar.getCode() +"'", null);
                }else{
                    ContentValues foodValue = new ContentValues();
                    foodValue.put(COLUMN_CODE, curTovar.getCode());
                    foodValue.put(COLUMN_PHOTO, curTovar.getPhoto());
                    foodValue.put(COLUMN_NAME, curTovar.getName());
                    foodValue.put(COLUMN_QUANTITY, curTovar.getQuantity());
                    foodValue.put(COLUMN_FCOUNT, 1);
                    foodValue.put(COLUMN_FPRICE, curTovar.getPrice());

                    sqdb.insert(TABLE_ORDER, null, foodValue);
                }
                badgeDefault();

                Toast.makeText(this, "Себетке енгізілді!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnWatchVideo:
                Toast.makeText(this, "Видео ойнатылады", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}