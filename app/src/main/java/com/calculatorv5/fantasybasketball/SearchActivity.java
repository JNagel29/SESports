package com.calculatorv5.fantasybasketball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//Adding new ones for game activity that ill add later
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.util.Log;





public class SearchActivity extends AppCompatActivity {
    TextView data;
    EditText inputText;
    Button searchButton;
    String basePlayerUrl = "https://nba-stats-db.herokuapp.com/api/playerdata/name/";
    // This is a flag for whether or not to finish (aka terminate) activities while switching
    // If we don't finish, then the back button can be used to go to previous page
    private final boolean shouldFinish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        data = findViewById(R.id.data);
        inputText = findViewById(R.id.inputText);
        searchButton = findViewById(R.id.searchButton);
        //Declares listener for search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlayerData(inputText.getText().toString());
            }
        });
        //Declares and calls function to set up Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        setupBottomNavigation(bottomNavigationView);
    }
    public void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setSelectedItemId(R.id.bottom_person_search);
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
                return true;
            } else if (item.getItemId() == R.id.bottom_compare) {
                startActivity(new Intent(getApplicationContext(), CompareActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                if (shouldFinish) finish();
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

    public void getPlayerData(String playerName) {
        String playerURL = basePlayerUrl + playerName;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, playerURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray resultsArr = response.getJSONArray("results"); // Grabs results of all years
                    JSONObject firstResult = resultsArr.getJSONObject(0); // Grabs most recent year
                    String playerName = firstResult.getString("player_name");
                    int age = firstResult.getInt("age");
                    String fgPercent = firstResult.getString("field_percent");
                    String threePercent = firstResult.getString("three_percent");
                    String team = firstResult.getString("team");
                    String season = firstResult.getString("season");

                    String playerData = "Player Name: " + playerName + "\nCurrent Team: " + team + "\nAge: " + age +
                            "\nField Goal Percentage: " + fgPercent + "\nThree Point Percentage: " + threePercent +
                            "\nSeason: " + season + "\n";
                    data.setText(playerData);
                } catch (JSONException e) {
                    e.printStackTrace();
                    data.setText("It's possible that " + playerName + " wasn't a valid player!");
                    //data.setText(playerURL);
                    //data.setText(R.string.error);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                data.setText("It's possible that " + playerName + " wasn't a valid player!");
            }
        });
        Volley.newRequestQueue(this).add(request);
    }


    //Small Override that makes it so back button changes selected icon
    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_person_search);
    }

}