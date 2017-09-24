package com.atsoft.thirukuralquiz;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            manager.popBackStackImmediate();
            HomeFragment homeFragment = new HomeFragment();
            transaction.replace(R.id.container, homeFragment, "Home");
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
