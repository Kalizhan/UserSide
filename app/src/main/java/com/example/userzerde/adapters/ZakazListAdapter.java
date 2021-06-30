package com.example.userzerde.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.userzerde.R;
import com.example.userzerde.modules.NewZakaz;
import com.example.userzerde.modules.NewZakaz;

import java.util.ArrayList;
import java.util.List;

public class ZakazListAdapter extends RecyclerView.Adapter<ZakazListAdapter.viewHolder> {
    Context context;
    ArrayList<NewZakaz> newZakazList;

    public ZakazListAdapter(Context context, ArrayList<NewZakaz> newZakazList) {
        this.context = context;
        this.newZakazList = newZakazList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_zakaz_recycler, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        int pos = position + 1;
        NewZakaz newZakaz = newZakazList.get(position);

        holder.zakazOrder.setText("#Тапсырыс " + pos);
        holder.enteredDate.setText(newZakaz.getDate());
        holder.zakazPrice.setText("" + newZakaz.getTovarPrice() + " тг");
        if (newZakaz.getTovarSituation().equals("new order")) {
            holder.zakazViewColor.setBackgroundColor(context.getResources().getColor(R.color.neworder));
        } else if (newZakaz.getTovarSituation().equals("order accepted")) {
            holder.zakazViewColor.setBackgroundColor(context.getResources().getColor(R.color.accept));
        } else if (newZakaz.getTovarSituation().equals("order got")){
            holder.zakazViewColor.setBackgroundColor(context.getResources().getColor(R.color.finished));
        }

        if (!newZakaz.getComment().isEmpty()) {
            holder.comment.setVisibility(View.VISIBLE);
            holder.comment.setText(newZakaz.getComment());
        } else {
            holder.comment.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return newZakazList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView zakazOrder, enteredDate, zakazPrice, comment;
        View zakazViewColor;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            zakazOrder = itemView.findViewById(R.id.tvzakaz);
            enteredDate = itemView.findViewById(R.id.tventereddate);
            zakazPrice = itemView.findViewById(R.id.tvzakazprice);
            comment = itemView.findViewById(R.id.tvcomment);
            zakazViewColor = itemView.findViewById(R.id.view);
        }
    }
}
