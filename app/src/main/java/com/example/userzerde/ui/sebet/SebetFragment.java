package com.example.userzerde.ui.sebet;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.userzerde.R;
import com.example.userzerde.activities.OformlenieActivity;
import com.example.userzerde.database.StoreDatabase;
import com.example.userzerde.modules.Tovar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.example.userzerde.MainActivity.badgeDefault;
import static com.example.userzerde.MainActivity.setBagdeCount;
import static com.example.userzerde.database.StoreDatabase.COLUMN_CODE;
import static com.example.userzerde.database.StoreDatabase.COLUMN_FCOUNT;
import static com.example.userzerde.database.StoreDatabase.COLUMN_FPRICE;
import static com.example.userzerde.database.StoreDatabase.COLUMN_NAME;
import static com.example.userzerde.database.StoreDatabase.COLUMN_PHOTO;
import static com.example.userzerde.database.StoreDatabase.COLUMN_QUANTITY;
import static com.example.userzerde.database.StoreDatabase.TABLE_ORDER;
    
public class SebetFragment extends Fragment implements View.OnClickListener {

    View root;
    StoreDatabase storeDb;
    SQLiteDatabase sqdb;
    BadgeAdapter badgeAdapter;
    RecyclerView recyclerView;
    ArrayList<Tovar> listOfFoods;
    LinearLayoutManager linearLayoutManager;
    LinearLayout oformlenieLayout;
    LinearLayout notOrderedYet;
    Button submitBtn;
    Button cancelBtn;

    TextView totalPrice;
    int sumPrice;
    AlertDialog.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_sebet, container, false);

        storeDb = new StoreDatabase(getActivity());
        sqdb = storeDb.getWritableDatabase();
        listOfFoods = new ArrayList<>();
        oformlenieLayout = root.findViewById(R.id.oformlenie);
        notOrderedYet = root.findViewById(R.id.notOrderedYet);
        submitBtn = root.findViewById(R.id.submitBtn);
        cancelBtn = root.findViewById(R.id.cancelBtn);
        //initialPrice = root.findViewById(R.id.initialPrice);
        totalPrice = root.findViewById(R.id.totalPrice);

        badgeAdapter = new BadgeAdapter(getActivity(), listOfFoods);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(badgeAdapter);

        sumPrice = 0;
        oformlenieLayout.setVisibility(View.GONE);

//        printDbStore();
        getFoodFromDb();
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        return root;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.submitBtn:
                startActivity(new Intent(getActivity(), OformlenieActivity.class));

                break;

            case R.id.cancelBtn:
                builder = new AlertDialog.Builder(getActivity());
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                sqdb.delete(TABLE_ORDER, null, null);
                                listOfFoods.clear();
                                badgeAdapter.notifyDataSetChanged();
                                emptyBadgeState();
                                setBagdeCount(0);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                builder.setMessage("Өшіру").setPositiveButton("Иа", dialogClickListener)
                        .setNegativeButton("Жоқ", dialogClickListener).show();

                break;
        }

    }

    public void emptyBadgeState() {
        oformlenieLayout.setVisibility(View.GONE);
        notOrderedYet.setVisibility(View.VISIBLE);
    }
    int foodQuantity;

    public void getFoodFromDb() {
        Cursor res = sqdb.rawQuery("SELECT * FROM " + TABLE_ORDER, null);
        if (res != null && res.getCount() > 0) {

            notOrderedYet.setVisibility(View.GONE);
            oformlenieLayout.setVisibility(View.VISIBLE);

            while (res.moveToNext()) {
                //String fKey = res.getString(res.getColumnIndex(COLUMN_FKEY));
                String foodPhoto = res.getString(res.getColumnIndex(COLUMN_PHOTO));
                String foodName = res.getString(res.getColumnIndex(COLUMN_NAME));
                String foodCode = res.getString(res.getColumnIndex(COLUMN_CODE));

                int foodCount = res.getInt(res.getColumnIndex(COLUMN_FCOUNT));
                foodQuantity = res.getInt(res.getColumnIndex(COLUMN_QUANTITY));
                Long foodPrice = res.getLong(res.getColumnIndex(COLUMN_FPRICE));

                if (foodCount > foodQuantity){
                    ContentValues newContent = new ContentValues();
                    newContent.put(COLUMN_FCOUNT, foodQuantity);
                    sqdb.update(TABLE_ORDER, newContent, COLUMN_CODE + "='"+ foodCode +"'", null);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+6"));
                Date today = Calendar.getInstance().getTime();

                listOfFoods.add(new Tovar(foodName, foodCode, foodPhoto, foodPrice, foodCount, 0, dateFormat.format(today), "qyshyma"));
                sumPrice += foodPrice * foodCount;
            }

            badgeAdapter.notifyDataSetChanged();
            setTotalSum(sumPrice);
            setBagdeCount(listOfFoods.size());
        }
    }

//    public void printDbStore() {
//        Cursor res = sqdb.rawQuery("SELECT * FROM " + TABLE_ORDER, null);
//
//        Log.i("FoodAdapter", "printDbStore: ");
//        if (res != null && res.getCount() > 0) {
//            Log.i("FoodAdapter", "moveToFirst: ");
//            while (res.moveToNext()) {
//
//                String fName = res.getString(res.getColumnIndex(COLUMN_NAME));
////                String fDesc = res.getString(res.getColumnIndex(COLUMN_FDESC));
//                int fCount = res.getInt(res.getColumnIndex(COLUMN_FCOUNT));
//
////                Log.i("BadgeFragment", "fCount: " + fCount);
////                Log.i("BadgeFragment", "fName: " + fName);
////                Log.i("BadgeFragment","fDesc: "+fDesc);
////                Log.i("BadgeFragment", "fCount2: " + fCount);
//            }
//        }
//    }

    public void setTotalSum(int sum) {
        //initialPrice.setText("" + sum + " тг");
        totalPrice.setText("" + sum + " тг");
    }

    class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.BasketProductViewHolder> {
        ArrayList<Tovar> dataList;
        Context context;

        public BadgeAdapter(Context context, ArrayList<Tovar> dataList) {
            this.context = context;
            this.dataList = dataList;
        }

        @Override
        public BasketProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_basket_product, parent, false);
            return new BasketProductViewHolder(view);
        }
        int increaseCount;

        @Override
        public void onBindViewHolder(final BasketProductViewHolder holder, final int position) {
            final Tovar tovar = dataList.get(position);

            int amount = tovar.getQuantity();

            holder.title.setText(tovar.getName());
            holder.price.setText((tovar.getPrice() * amount) + "Тг");
            holder.amount_count.setText("" + amount);

            Glide.with(holder.productImage)
                    .load(tovar.getPhoto())
                    .centerCrop()
                    .placeholder(R.drawable.box)
                    .into(holder.productImage);

//            Cursor res = sqdb.rawQuery("SELECT * FROM " + TABLE_ORDER + " WHERE " +
//                    COLUMN_CODE + "=?", new String[]{tovar.getCode()});
//
//            int fCount = res.getInt(res.getColumnIndex(StoreDatabase.COLUMN_FCOUNT));
//
//            if (foodQuantity == fCount+1){
//                holder.add_circle.setVisibility(View.INVISIBLE);
//            }

            holder.add_circle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    increaseCount = Integer.parseInt(holder.amount_count.getText().toString()) + 1;
                    Log.i("count", String.valueOf(foodQuantity));

                    holder.amount_count.setText("" + increaseCount);
                    holder.price.setText((tovar.getPrice() * increaseCount) + "Тг");

                    sumPrice += tovar.getPrice();
                    setTotalSum(sumPrice);

                    Cursor res = sqdb.rawQuery("SELECT * FROM " + TABLE_ORDER + " WHERE " +
                            COLUMN_CODE + "=?", new String[]{tovar.getCode()});

                    if (res != null && res.getCount() > 0) {
                        res.moveToFirst();

                        int fCount = res.getInt(res.getColumnIndex(StoreDatabase.COLUMN_FCOUNT));

                        if (fCount + 1 >= foodQuantity){
                            increaseCount = foodQuantity;
                            holder.add_circle.setVisibility(View.INVISIBLE);
                        }

                        ContentValues updateValues = new ContentValues();
                        updateValues.put(COLUMN_FCOUNT, increaseCount);



                        sqdb.update(TABLE_ORDER, updateValues, COLUMN_CODE + "='" + tovar.getCode() + "'", null);
                    }

                    badgeDefault();

                }
            });

            holder.del_circle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int decreaseCount = Integer.parseInt(holder.amount_count.getText().toString()) - 1;

                    sumPrice -= tovar.getPrice();
                    holder.price.setText((tovar.getPrice() * decreaseCount) + "Тг");
                    setTotalSum(sumPrice);

                    Cursor res = sqdb.rawQuery("SELECT * FROM " + TABLE_ORDER + " WHERE " +
                            COLUMN_CODE + "=?", new String[]{tovar.getCode()});

                    if (decreaseCount == 0) {
                        Toast.makeText(context, "Тауар өшірілді", Toast.LENGTH_LONG).show();
                        dataList.remove(tovar);
                        notifyDataSetChanged();


                        sqdb.delete(TABLE_ORDER, COLUMN_CODE + "='" + tovar.getCode() + "'", null);

                    } else {
                        holder.amount_count.setText("" + decreaseCount);
//                        int fCount = res.getInt(res.getColumnIndex(StoreDatabase.COLUMN_FCOUNT));
                        if (decreaseCount - 1< tovar.getQuantity()){
                            holder.add_circle.setVisibility(View.VISIBLE);
                        }

                        ContentValues updateValues = new ContentValues();
                        updateValues.put(COLUMN_FCOUNT, decreaseCount);

                        sqdb.update(TABLE_ORDER, updateValues, COLUMN_CODE + "='" + tovar.getCode() + "'", null);

                    }

                    if (dataList.size() == 0) {
                        emptyBadgeState();
                    }

                    badgeDefault();
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class BasketProductViewHolder extends RecyclerView.ViewHolder {
            ImageView productImage;
            TextView title;
            TextView price;
            TextView amount_count;
            ImageView del_circle;
            ImageView add_circle;

            BasketProductViewHolder(View itemView) {
                super(itemView);

                productImage = itemView.findViewById(R.id.productImage);
                title = itemView.findViewById(R.id.title);
                price = itemView.findViewById(R.id.price);

                add_circle = itemView.findViewById(R.id.add_circle);
                del_circle = itemView.findViewById(R.id.del_circle);
                amount_count = itemView.findViewById(R.id.text_count);

            }
        }
    }
}