package com.indiehood.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.indiehood.app.R;
import com.indiehood.app.ui.artist_view.Artist;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public ArtistUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = this;
        demoSetup().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = (documentSnapshot.toObject(ArtistUser.class));
                assert currentUser != null;
                setContentView(R.layout.activity_main);
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                NavigationView navigationView = findViewById(R.id.nav_view);

                // Passing each menu ID as a set of Ids because each
                // menu should be considered as top level destinations.
                mAppBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.nav_listings, R.id.nav_favorites, R.id.nav_settings,
                        R.id.nav_login, R.id.nav_suggest, R.id.nav_report, R.id.nav_post)
                        .setDrawerLayout(drawer)
                        .build();
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                NavigationUI.setupActionBarWithNavController((AppCompatActivity) activity, navController, mAppBarConfiguration);
                NavigationUI.setupWithNavController(navigationView, navController);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // note: you can also use 'getSupportFragmentManager()'
        FragmentManager mgr = getFragmentManager();
        if (mgr.getBackStackEntryCount() == 0) {
            // No backstack to pop, so calling super
            super.onBackPressed();
        } else {
            mgr.popBackStack();
        }
    }

    /*
    Piece of code needed to setup the user for the demo
     */
    private Task<DocumentSnapshot> demoSetup() {
        return db.collection("UserCol").document("1111AAA").get();
    }
}