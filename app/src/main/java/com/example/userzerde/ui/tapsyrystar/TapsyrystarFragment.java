package com.example.userzerde.ui.tapsyrystar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.userzerde.R;
import com.example.userzerde.RecyclerItemClickListener;
import com.example.userzerde.activities.LoginActivity;
import com.example.userzerde.activities.ProfileActivity;
import com.example.userzerde.activities.ZakazWindowActivity;
import com.example.userzerde.adapters.TovarListAdapter;
import com.example.userzerde.adapters.ZakazListAdapter;
import com.example.userzerde.modules.NewZakaz;
import com.example.userzerde.modules.Tovar;
import com.example.userzerde.modules.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;

public class TapsyrystarFragment extends Fragment implements View.OnClickListener {
    View view;
    ImageView imgAccountBtn1, filterImgViewTapsyrys;
    LinearLayout tapsyrystarZhok;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ArrayList<NewZakaz> newZakazArrayList;
    ZakazListAdapter zakazListAdapter;

    DatabaseReference mDatabaseReference;
    FirebaseAuth mAuth;
    String user;

    ProgressDialog progressDialog;
    int i=0;
    CountDownTimer mCountDownTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tapsyrystar, container, false);
        imgAccountBtn1 = view.findViewById(R.id.accountImgView1);
        filterImgViewTapsyrys = view.findViewById(R.id.filterImgViewTapsyrys);
        tapsyrystarZhok = view.findViewById(R.id.TapsyrystarZhok);

        imgAccountBtn1.setOnClickListener(this);
        filterImgViewTapsyrys.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recyclerTapsyrys);
        newZakazArrayList = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Zakazdar");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getEmail().replace(".", "-");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressdialog_custom);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);

        getData();

        zakazListAdapter = new ZakazListAdapter(getActivity(), newZakazArrayList);
        recyclerView.setAdapter(zakazListAdapter);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int pos) {
                        Intent intent = new Intent(getContext(), ZakazWindowActivity.class);
                        intent.putExtra("zakaz", newZakazArrayList.get(pos));

                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );

//        progressDialog.setProgress(i);
//        mCountDownTimer=new CountDownTimer(5000,1000) {
//
//            @Override
//            public void onTick(long millisUntilFinished) {
//                Log.v("Log_tag", "Tick of Progress"+ i+ millisUntilFinished);
//                i++;
//                progressDialog.setProgress((int)i*100/(5000/1000));
//
//            }
//
//            @Override
//            public void onFinish() {
//                try {
//                    if (newZakazArrayList.isEmpty()){
//                        Toast.makeText(getActivity(), "Байланысты тексеріңіз", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                i++;
//                progressDialog.setProgress(100);
//            }
//        };
//        mCountDownTimer.start();

//        new CountDownTimer(5000, 1000){
//
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                if (newZakazArrayList.isEmpty()){
//                    Toast.makeText(getActivity(), "Байланысты тексеріңіз", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }.start();

        return view;
    }

    private void getData() {
        mDatabaseReference.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newZakazArrayList.clear();
                if (snapshot.exists()) {
                    new CountDownTimer(5000, 1000){

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            if (newZakazArrayList.isEmpty()){
                                Toast.makeText(getContext(), "Байланысты тексеріңіз", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.start();

                    tapsyrystarZhok.setVisibility(View.GONE);
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        NewZakaz newZakaz = snap.getValue(NewZakaz.class);

                        newZakazArrayList.add(newZakaz);
                        progressDialog.cancel();
                    }
                    zakazListAdapter.notifyDataSetChanged();
                } else {
                    tapsyrystarZhok.setVisibility(View.VISIBLE);
                    progressDialog.cancel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accountImgView1:
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    startActivity(new Intent(getContext(), ProfileActivity.class));
                } else {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
                break;
            case R.id.filterImgViewTapsyrys:
                PopupMenu popupMenu = new PopupMenu(getContext(), v);

                popupMenu.getMenuInflater().inflate(R.menu.tapsyrystar_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.allorder:
                                getData();
                                break;
                            case R.id.neworder:
                                tapsyrysQarau("new order");
                                break;
                            case R.id.accpeptedorder:
                                tapsyrysQarau("order accepted");
                                break;
                            case R.id.finishedorder:
                                tapsyrysQarau("order got");
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                break;
        }
    }

    public void tapsyrysQarau(String text){
        mDatabaseReference.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newZakazArrayList.clear();
                if (snapshot.exists()) {
                    tapsyrystarZhok.setVisibility(View.GONE);
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        NewZakaz newZakaz = snap.getValue(NewZakaz.class);

                        if (newZakaz.getTovarSituation().equals(text)){
                            newZakazArrayList.add(newZakaz);
                        }else{
                            tapsyrystarZhok.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "Бос", Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.cancel();
                    }
                    zakazListAdapter.notifyDataSetChanged();
                } else {
                    tapsyrystarZhok.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Бос", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}