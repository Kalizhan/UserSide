package com.example.userzerde;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.userzerde.activities.LoginActivity;
import com.example.userzerde.activities.ProfileActivity;
import com.example.userzerde.database.StoreDatabase;
import com.example.userzerde.modules.CheckInternetStatus;
import com.example.userzerde.R;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.userzerde.database.StoreDatabase.TABLE_ORDER;

public class MainActivity extends AppCompatActivity {

    BottomNavigationMenuView mbottomNavigationMenuView;
    View view;
    BottomNavigationItemView itemView;
    View cart_badge;
    static TextView cart_badgeTv;

    CheckInternetStatus mCheckInternetStatus;
    boolean is_internet_connected = false;

    StoreDatabase storeDb;
    static SQLiteDatabase sqdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_history, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        storeDb = new StoreDatabase(this);
        sqdb = storeDb.getWritableDatabase();

        mbottomNavigationMenuView = (BottomNavigationMenuView) navView.getChildAt(0);
        view = mbottomNavigationMenuView.getChildAt(2);
        itemView = (BottomNavigationItemView) view;

        cart_badge = LayoutInflater.from(this).inflate(R.layout.custom_cart_item_layout, mbottomNavigationMenuView, false);
        cart_badgeTv = cart_badge.findViewById(R.id.cart_badge);

        itemView.addView(cart_badge);

        mCheckInternetStatus = new CheckInternetStatus(MainActivity.this);
        is_internet_connected = mCheckInternetStatus.isInternetConnected();
        if (!is_internet_connected){
            Toast.makeText(this, "Интернетті тексеріңіз", Toast.LENGTH_SHORT).show();
            return;
        }

        badgeDefault();
    }

    public static void badgeDefault(){
        Cursor res = sqdb.rawQuery("SELECT * FROM " + TABLE_ORDER, null);
        setBagdeCount(res.getCount());
    }

    public static void setBagdeCount(int n){
        cart_badgeTv.setText(""+n);
    }

    public static void increaseBagdeCount(){
        int n = Integer.parseInt(""+cart_badgeTv.getText())+1;
        cart_badgeTv.setText(""+n);
    }

    public static void decreaseBagdeCount(){
        int n = Integer.parseInt(""+cart_badgeTv.getText())-1;
        cart_badgeTv.setText(""+n);
    }
}