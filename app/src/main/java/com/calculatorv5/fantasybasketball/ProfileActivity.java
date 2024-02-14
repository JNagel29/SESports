package com.calculatorv5.fantasybasketball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    // This is a flag for whether or not to finish (aka terminate) activities while switching
    // If we don't finish, then the back button can be used to go to previous page
    private final boolean shouldFinish = true;
    //API Key for fetching game data (required)
    private final String apiKey = "";
    //Player ID, member variable b/c it needs to be used from one JSON request to another
    private int playerId;
    //TextView for testing right now
    private TextView textView;
    //Checkbox for testing seasonal stats
    private CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Declares and calls function to set up Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        setupBottomNavigation(bottomNavigationView);

        String playerName = getIntent().getStringExtra("playerName");

        textView = findViewById(R.id.textName);
        textView.setText(playerName);

        checkBox = findViewById(R.id.seasonCheckBox);

        boolean getCurrStats = checkBox.isChecked();

        if (getCurrStats) getCurrentSeasonStats(playerName);
        else getPastSeasonStats(playerName);

    }

    public void getPastSeasonStats(String playerName) {
        String basePlayerUrl = "https://nba-stats-db.herokuapp.com/api/playerdata/name/";

        String playerUrl = basePlayerUrl + playerName;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, playerUrl, null, new Response.Listener<JSONObject>() {
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
                    textView.setText(playerData);
                } catch (JSONException e) {
                    e.printStackTrace();
                    textView.setText("It's possible that " + playerName + " wasn't a valid player!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                textView.setText("It's possible that " + playerName + " wasn't a valid player!");
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    private void getCurrentSeasonStats(String playerName) {
        //String basePlayerURL = "https://www.balldontlie.io/api/v1/players?search=";
        String basePlayerIdURL ="https://api.balldontlie.io/v1/players?search=";

        String playerIdURL = basePlayerIdURL + playerName;
        //First, we make a JSON request to grab the player name's player ID
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, playerIdURL,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray playerArr;
                    JSONObject playerObj;
                    playerArr = response.getJSONArray("data"); //grabs data (not meta) obj
                    playerObj = playerArr.getJSONObject(0); // there should be 1 result only
                    playerId = playerObj.getInt("id"); // We grab that player's id
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            //Adds api key to header in format Authorization: <key>
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", apiKey);
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(request);

        textView.setText(String.valueOf(playerId));

        /*
        //Now that we have the playerId variable set, we can perform more requests
        String baseAveragesURL = "https://api.balldontlie.io/v1/season_averages?player_ids[]=";
        String averagesURL = baseAveragesURL + playerId;

        request = new JsonObjectRequest(Request.Method.GET, playerIdURL,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray playerArr;
                    JSONObject playerObj;
                    playerArr = response.getJSONArray("data"); //grabs data (not meta) obj
                    playerObj = playerArr.getJSONObject(0); // there should be 1 result only
                    playerId = playerObj.getInt("id"); // We grab that player's id
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            //Adds api key to header in format Authorization: <key>
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", apiKey);
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(request);

         */

    }




    public void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setSelectedItemId(R.id.bottom_games);
        //Note: had to upgrade gradle material to 1.11.0 for setOnItemSelectedListener to work
        bottomNavigationView.setOnItemSelectedListener(item ->  {
            if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                // This line basically says to perform the transition right after activity start
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //This line 'terminates' the current activity since we're switching
                if (shouldFinish) finish();
                return true;
            }
            else if (item.getItemId() == R.id.bottom_person_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                // This line basically says to perform the transition right after activity start
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //This line 'terminates' the current activity since we're switching
                if (shouldFinish) finish();
            }
            else if (item.getItemId() == R.id.bottom_compare) {
                startActivity(new Intent(getApplicationContext(), CompareActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                if (shouldFinish) finish();
                return true;
            }
            else if (item.getItemId() == R.id.bottom_games) {
                startActivity(new Intent(getApplicationContext(), GamesActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                if (shouldFinish) finish();
                return true;
            }
            return false;
        });
    }
}