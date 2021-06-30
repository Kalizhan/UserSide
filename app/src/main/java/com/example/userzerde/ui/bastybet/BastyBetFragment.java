package com.example.userzerde.ui.bastybet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.userzerde.R;
import com.example.userzerde.RecyclerItemClickListener;
import com.example.userzerde.activities.LoginActivity;
import com.example.userzerde.activities.ProfileActivity;
import com.example.userzerde.activities.TovarActivity;
import com.example.userzerde.adapters.TovarListAdapter;
import com.example.userzerde.modules.Tovar;
import com.example.userzerde.modules.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class BastyBetFragment extends Fragment implements  View.OnClickListener {
    View view;
    ImageView imgFilterView, imgAccountBtn;
    SearchView searchView;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Tovar> tovarArrayList;
    TovarListAdapter tovarListAdapter;
    ProgressDialog progressDialog;

    DatabaseReference mDatabaseReference;
    StorageReference storageReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_basty_bet, container, false);
        imgFilterView = view.findViewById(R.id.filterImgView);
        imgAccountBtn = view.findViewById(R.id.accountImgView);
        searchView = view.findViewById(R.id.searchView);

        recyclerView = view.findViewById(R.id.recyclerBastyBet);
        tovarArrayList = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("UserImage");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressdialog_custom);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);

        getData();

        tovarListAdapter = new TovarListAdapter(tovarArrayList, getContext());
        recyclerView.setAdapter(tovarListAdapter);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        new CountDownTimer(5000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (tovarArrayList.isEmpty()){
                    Toast.makeText(getActivity(), "Байланысты тексеріңіз", Toast.LENGTH_SHORT).show();
                }
            }
        }.start();


        imgAccountBtn.setOnClickListener(this);
        imgFilterView.setOnClickListener(this);
        return view;
    }

    private void searchData() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tovarListAdapter.getFilter().filter(newText.toString());
                if(TextUtils.isEmpty(newText)){
                    tovarListAdapter = new TovarListAdapter(tovarArrayList, getContext());
                    recyclerView.setAdapter(tovarListAdapter);
                    tovarListAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
    }

    private void getData() {
        mDatabaseReference.child("TovarInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){



                    tovarArrayList.clear();
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Tovar tovar = snap.getValue(Tovar.class);
                        tovarArrayList.add(tovar);
                        if (tovarArrayList.isEmpty()){
                            Runnable progressRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.cancel();
                                    Toast.makeText(getActivity(), "Байланысты тексеріңіз", Toast.LENGTH_SHORT).show();
                                }
                            };

                            Handler pdCanceller = new Handler();
                            pdCanceller.postDelayed(progressRunnable, 5000);
                        }
                        progressDialog.cancel();
                    }

                    tovarListAdapter.notifyDataSetChanged();
                    searchData();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Тауар жоқ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accountImgView:
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    startActivity(new Intent(getContext(), ProfileActivity.class));
                }else{
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
                break;
            case R.id.filterImgView:
                PopupMenu popupMenu = new PopupMenu(getContext(), v);

                popupMenu.getMenuInflater().inflate(R.menu.filter_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.name_sort:
                                Collections.sort(tovarArrayList, Tovar.tovarNameComparator);
                                tovarListAdapter.notifyDataSetChanged();
                                break;
                            case R.id.price_sort:
                                Collections.sort(tovarArrayList, Tovar.tovarPriceComparator);
                                tovarListAdapter.notifyDataSetChanged();
                                break;
                            case R.id.new_sort:
                                Collections.sort(tovarArrayList, Tovar.tovarDateComparator);
                                tovarListAdapter.notifyDataSetChanged();
                                break;
                            case R.id.sale_sort:
                                Collections.sort(tovarArrayList, Tovar.tovarSatylym);
                                tovarListAdapter.notifyDataSetChanged();
                                break;
                            case R.id.available_sort:
                                Collections.sort(tovarArrayList, Tovar.tovarQuanityAvailabilityComparator);
                                tovarListAdapter.notifyDataSetChanged();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                break;
        }
    }
}