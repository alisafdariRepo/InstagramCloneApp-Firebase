package com.mehrsoft.myinstagram.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mehrsoft.myinstagram.Fragments.HomeFragment;
import com.mehrsoft.myinstagram.Fragments.NotificationFragment;
import com.mehrsoft.myinstagram.Fragments.ProfileFragment;
import com.mehrsoft.myinstagram.Fragments.SearchFragment;
import com.mehrsoft.myinstagram.R;
import com.mehrsoft.myinstagram.Utilis.Utility;
import com.orhanobut.hawk.Hawk;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottomNavViewBar)
    BottomNavigationViewEx bottomNavigationView;


    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Hawk.init(this).build();

        handleBottomNavigationView();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String publisherid = bundle.getString("publisherid");
            Hawk.put(Utility.HawkKey.PUBLISHER, publisherid);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

    }

    private void handleBottomNavigationView() {

        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.setTextVisibility(false);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.ic_home:

                        selectedFragment = new HomeFragment();

                        break;
                    case R.id.ic_search:
                        selectedFragment = new SearchFragment();

                        break;
                    case R.id.ic_heart:

                        selectedFragment = new NotificationFragment();

                        break;
                    case R.id.ic_add:

                        selectedFragment = null;

                        startActivity(new Intent(MainActivity.this, PostActivity.class));

                        break;
                    case R.id.ic_profile:

                        Hawk.put("profiled", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        selectedFragment = new ProfileFragment();

                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            }
        });
    }


}
