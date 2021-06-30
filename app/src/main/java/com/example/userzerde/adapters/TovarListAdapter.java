package com.example.userzerde.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.userzerde.R;
import com.example.userzerde.activities.TovarActivity;
import com.example.userzerde.database.StoreDatabase;
import com.example.userzerde.modules.ClickItemInterface;
import com.example.userzerde.modules.Tovar;

import java.util.ArrayList;
import java.util.List;

import static com.example.userzerde.MainActivity.badgeDefault;
import static com.example.userzerde.database.StoreDatabase.COLUMN_CODE;
import static com.example.userzerde.database.StoreDatabase.COLUMN_FCOUNT;
import static com.example.userzerde.database.StoreDatabase.COLUMN_FPRICE;
import static com.example.userzerde.database.StoreDatabase.COLUMN_NAME;
import static com.example.userzerde.database.StoreDatabase.COLUMN_PHOTO;
import static com.example.userzerde.database.StoreDatabase.COLUMN_QUANTITY;
import static com.example.userzerde.database.StoreDatabase.TABLE_ORDER;

public class TovarListAdapter extends RecyclerView.Adapter<TovarListAdapter.viewHolder> implements Filterable{
    List<Tovar> tovarList;
    List<Tovar> tovarListFull;
    Context context;
    ClickItemInterface clickItemInterface;
    StoreDatabase storeDb;
    SQLiteDatabase sqdb;

    public TovarListAdapter(List<Tovar> tovarList, Context context){
        this.tovarList = tovarList;
        this.context = context;
        tovarListFull = new ArrayList<>(tovarList);
    }

    @Override
    public Filter getFilter() {
        return FilterUser;
    }

    private Filter FilterUser = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();
            List<Tovar> filteredList = new ArrayList<>();
            if (constraint.length() == 0) {
                tovarListFull = tovarList;
            } else {
                for (Tovar row : tovarList) {
                    // name match condition. this might differ depending on your requirement
                    // here we are looking for name or phone number match
                    if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getCode().contains(constraint)) {
                        filteredList.add(row);
                    }
                }
                tovarListFull = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = tovarListFull;
            notifyDataSetChanged();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tovarList = (ArrayList<Tovar>) results.values;
            notifyDataSetChanged();
        }
    };

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView name, price, code, availability;
        TextView text_count;
        ImageView del_circle;
        ImageView add_circle;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.imgGood);
            name = itemView.findViewById(R.id.titleOfMeal);
            price = itemView.findViewById(R.id.priceNum);
            code = itemView.findViewById(R.id.idNum);
            availability = itemView.findViewById(R.id.inProcess);

            add_circle = itemView.findViewById(R.id.add_circle);
            del_circle = itemView.findViewById(R.id.del_circle);
            text_count = itemView.findViewById(R.id.text_count);
        }
    }

    public void setClickListener(ClickItemInterface clickItemInterface){
        this.clickItemInterface = clickItemInterface;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_tovar, parent, false);
        storeDb = new StoreDatabase(parent.getContext());
        sqdb = storeDb.getWritableDatabase();
        return new viewHolder(view);
    }
    int rest=0;

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Tovar tovar = tovarList.get(position);

        Glide
                .with(context)
                .load(tovar.getPhoto())
                .centerCrop()
                .placeholder(R.drawable.box)
                .into(holder.photo);

        holder.name.setText(tovar.getName());
        holder.price.setText(String.valueOf(tovar.getPrice()) + " тг/шт");
        holder.code.setText("#"+tovar.getCode());

        if (tovar.getQuantity()==0){
            holder.availability.setVisibility(View.VISIBLE);
            holder.add_circle.setVisibility(View.INVISIBLE);
        }else{
            holder.availability.setVisibility(View.INVISIBLE);
            holder.add_circle.setVisibility(View.VISIBLE);
        }
//        checkAvailabilityOfMeal(tovar, holder);

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextPage(position);
            }
        });

        holder.price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextPage(position);
            }
        });

        holder.code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextPage(position);
            }
        });

        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextPage(position);
            }
        });


        holder.add_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.text_count.setVisibility(View.VISIBLE);
                holder.del_circle.setVisibility(View.VISIBLE);

                rest = (Integer.parseInt(holder.text_count.getText().toString()) + 1);

                holder.text_count.setText("" + rest);

                Cursor res = sqdb.rawQuery("SELECT * FROM " + TABLE_ORDER + " WHERE " +
                        COLUMN_CODE + "=?", new String[]{tovar.getCode()});

                if (res != null && res.getCount() > 0){
                    res.moveToFirst();
                    String fCount = res.getString(res.getColumnIndex(COLUMN_FCOUNT));
                    ContentValues updateValues = new ContentValues();
                    if (tovar.getQuantity() == Integer.parseInt(fCount) + 1){
                        holder.add_circle.setVisibility(View.INVISIBLE);
                    }else{
                        holder.add_circle.setVisibility(View.VISIBLE);
                    }
                    updateValues.put(COLUMN_FCOUNT, (Integer.parseInt(fCount) + 1));
                    sqdb.update(TABLE_ORDER, updateValues, COLUMN_CODE + "='"+ tovar.getCode() +"'", null);
                }else{
                    ContentValues foodValue = new ContentValues();
                    foodValue.put(COLUMN_CODE, tovar.getCode());
                    foodValue.put(COLUMN_PHOTO, tovar.getPhoto());
                    foodValue.put(COLUMN_NAME, tovar.getName());
                    foodValue.put(COLUMN_QUANTITY, tovar.getQuantity());
                    foodValue.put(COLUMN_FCOUNT, 1);
                    foodValue.put(COLUMN_FPRICE, tovar.getPrice());

                    sqdb.insert(TABLE_ORDER, null, foodValue);
                }
                badgeDefault();
                //printDbStore();
            }
        });

        holder.del_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.text_count.setVisibility(View.VISIBLE);
                holder.del_circle.setVisibility(View.VISIBLE);

                int c = Integer.parseInt(holder.text_count.getText().toString());

                if (c != 0) {
                    holder.text_count.setText("" + (c - 1));

                    if ((c - 1) == 0) {
                        holder.text_count.setVisibility(View.GONE);
                        holder.del_circle.setVisibility(View.GONE);
                    }
                }

                Cursor res = sqdb.rawQuery("SELECT * FROM " + TABLE_ORDER + " WHERE " +
                        COLUMN_CODE + "=?", new String[]{tovar.getCode()});

                if (res != null && res.getCount() > 0){
                    res.moveToFirst();
                    int fCount = res.getInt(res.getColumnIndex(StoreDatabase.COLUMN_FCOUNT));

                    if (fCount - 1< tovar.getQuantity()){
                        holder.add_circle.setVisibility(View.VISIBLE);
                    }

                    if(fCount == 1){
                        sqdb.delete(TABLE_ORDER, COLUMN_CODE + "='"+ tovar.getCode() +"'", null);

                    }else {
                        ContentValues updateValues = new ContentValues();
                        updateValues.put(COLUMN_FCOUNT, (fCount - 1));

                        sqdb.update(TABLE_ORDER, updateValues, COLUMN_CODE + "='" + tovar.getCode() + "'", null);
                    }
                }
                badgeDefault();

//                printDbStore();
            }
        });
    }

    private void goToNextPage(int position) {
        Intent intent = new Intent(context, TovarActivity.class);

        intent.putExtra("tovar", tovarList.get(position));

        context.startActivity(intent);
    }

//    public void printDbStore(){
//        Cursor res = sqdb.rawQuery("SELECT * FROM " + TABLE_ORDER, null);
//
//        Log.i("FoodAdapter","printDbStore: ");
//        if(res != null && res.getCount() > 0) {
//            Log.i("FoodAdapter","moveToFirst: " + res.getCount());
//            while (res.moveToNext()) {
//
//                String fName = res.getString(res.getColumnIndex(COLUMN_NAME));
//                int fCount = res.getInt(res.getColumnIndex(COLUMN_FCOUNT));
//            }
//        }
//    }
//    public void checkAvailabilityOfMeal(Tovar product,final viewHolder holder){
//        if(!product.isAvailable()){
//            holder.availability.setBackgroundColor(holder.itemView.getResources().getColor(R.color.colorPrimaryDark));
//            holder.availability.setText("Не доступно");
//            holder.add_circle.setVisibility(View.INVISIBLE);
//        }
//    }

    @Override
    public int getItemCount() {
        return tovarList.size();
    }
}
