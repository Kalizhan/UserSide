package com.example.userzerde.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.userzerde.R;
import com.example.userzerde.modules.Tovar;

import java.util.ArrayList;
import java.util.List;

public class BoughtTovarListAdapter extends RecyclerView.Adapter<BoughtTovarListAdapter.viewHolder> {
    Context context;
    List<Tovar> boughtTovarArrayList;
    String quantity;

    public BoughtTovarListAdapter(Context context, List<Tovar> boughtTovarArrayList, String quantity) {
        this.context = context;
        this.boughtTovarArrayList = boughtTovarArrayList;
        this.quantity = quantity;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bought_list_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoughtTovarListAdapter.viewHolder holder, int position) {
        String split[] = quantity.split(",");
        Tovar tovar = boughtTovarArrayList.get(position);

        Glide
                .with(context)
                .load(tovar.getPhoto())
                .centerCrop()
                .placeholder(R.drawable.box)
                .into(holder.photo);

        holder.name.setText(tovar.getName());
        holder.price.setText(tovar.getPrice() + " тг/шт");
        holder.code.setText("#" + tovar.getCode());

        holder.quantit.setText(split[position] + " дана");
    }

    @Override
    public int getItemCount() {
        return boughtTovarArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView name, price, code, availability, quantit;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.imgGood);
            name = itemView.findViewById(R.id.titleOfMeal);
            price = itemView.findViewById(R.id.priceNum);
            code = itemView.findViewById(R.id.idNum);
            availability = itemView.findViewById(R.id.inProcess);
            quantit = itemView.findViewById(R.id.quant);
        }
    }
}
