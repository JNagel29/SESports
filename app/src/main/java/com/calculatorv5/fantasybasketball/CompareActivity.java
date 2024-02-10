package com.calculatorv5.fantasybasketball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CompareActivity extends AppCompatActivity {

    // This is a flag for whether or not to finish (aka terminate) activities while switching
    // If we don't finish, then the back button can be used to go to previous page
    private final boolean shouldFinish = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        //Declares and calls function to set up Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        setupBottomNavigation(bottomNavigationView);
    }

    public void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setSelectedItemId(R.id.bottom_compare);
        //Note: had to upgrade gradle material to 1.11.0 for setOnItemSelectedListener to work
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                // This line basically says to perform the transition right after activity start
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //This line 'terminates' the current activity since we're switching
                if (shouldFinish) finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_person_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                // This line basically says to perform the transition right after activity start
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //This line 'terminates' the current activity since we're switching
                if (shouldFinish) finish();
            } else if (item.getItemId() == R.id.bottom_compare) {
                return true;
            } else if (item.getItemId() == R.id.bottom_games) {
                startActivity(new Intent(getApplicationContext(), GamesActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                if (shouldFinish) finish();
                return true;
            }
            return false;
        });
    }

    //Small Override that makes it so back button changes selected icon
    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_compare);
    }

}