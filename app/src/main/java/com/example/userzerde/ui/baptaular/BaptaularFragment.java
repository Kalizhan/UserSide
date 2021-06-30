package com.example.userzerde.ui.baptaular;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userzerde.R;
import com.example.userzerde.activities.LoginActivity;
import com.example.userzerde.activities.ProfileActivity;
import com.example.userzerde.modules.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BaptaularFragment extends Fragment implements View.OnClickListener {
    View v;
    TextView btnProfile, btnAddress;
    Button btnLogOut;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_baptaular, container, false);
        btnProfile = v.findViewById(R.id.btnProfile);
        btnLogOut = v.findViewById(R.id.btnLogOut);
        btnAddress = v.findViewById(R.id.btnAddress);
        btnAddress.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            btnLogOut.setVisibility(View.VISIBLE);
        }else {
            btnLogOut.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnProfile:
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    startActivity(new Intent(getContext(), ProfileActivity.class));
                }else{
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
                break;
            case R.id.btnLogOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                break;
            case R.id.btnAddress:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_address_watch, null);

                TextView tvAddress;

                tvAddress = dialogView.findViewById(R.id.tvAddress);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "-"));

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            tvAddress.setText(user.getCity() + ", " + user.getAddress() + ", " + user.getTochka());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(true).setView(dialogView).show();

                break;
        }
    }
}