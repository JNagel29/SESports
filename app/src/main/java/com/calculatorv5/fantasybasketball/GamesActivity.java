package com.calculatorv5.fantasybasketball;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class GamesActivity extends AppCompatActivity {

    // This is a flag for whether or not to finish (aka terminate) activities while switching
    // If we don't finish, then the back button can be used to go to previous page
    private final boolean shouldFinish = true;
    //These three will be used to list out the games via a scrollable recyclerView
    RecyclerView recyclerView;
    List<Game> myGames = new ArrayList<>();
    CustomAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        //Declares and calls function to set up Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        setupBottomNavigation(bottomNavigationView);
        //Gets all the games on current day
        getDailyGames();
        //Used this video to implement RecyclerView for all the daily games
        //CustomAdapter, CustomViewHolder, and Game class all part of it to display items
        //https://www.youtube.com/watch?v=DlaSiftrWeA
        //Can use this short follow-up if we need to implement click listeners on each game
        //https://www.youtube.com/watch?v=-gs1hllisG4
        displayItems();

    }

    public void displayItems() {
        recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        //Then, we declare the adapter and set it to the recycler view
        customAdapter = new CustomAdapter(this, myGames);
        recyclerView.setAdapter(customAdapter);

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
                return true;
            }
            return false;
        });
    }

    public void getDailyGames() {
        String baseGameUrl = "https://www.balldontlie.io/api/v1/games";
        //Get current date
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentTime);
        //Create correct URL using today's date
        //Later on we can add a selector for a certain date?
        String dailyGameUrl = baseGameUrl + "?dates[]=" + formattedDate; // request for curr games
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, dailyGameUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray arrGames = response.getJSONArray("data"); // results of all games that day
                    JSONObject game, homeTeam, awayTeam;
                    String homeTeamName, awayTeamName, homeTeamScore, awayTeamScore, gameStatus;
                    for (int i=0; i < arrGames.length(); i++) { //Loop through each game
                        game = arrGames.getJSONObject(i);
                        //Inside each game, there's another JSON object for home/visitor info
                        homeTeam = game.getJSONObject("home_team");
                        awayTeam = game.getJSONObject("visitor_team");
                        //Grab the (shortened) name for each
                        homeTeamName = homeTeam.getString("name");
                        awayTeamName = awayTeam.getString("name");
                        //Grab the scores for each, and status of the game (quarter or finished)
                        homeTeamScore = String.valueOf(game.getInt("home_team_score"));
                        awayTeamScore = String.valueOf(game.getInt("visitor_team_score"));
                        gameStatus = game.getString("status");
                        //However, gameStatus returns a long string if the game hasn't started:
                        if (gameStatus.startsWith("20")) { // checks if it starts with year
                            gameStatus = getGameTime(gameStatus);
                        }
                        //Add to list of games that will be later displayed out
                        myGames.add(new Game(homeTeamName, awayTeamName, homeTeamScore, awayTeamScore, gameStatus));
                    }
                    // Here, we update view with most recent data, very important since its async
                    updateRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    //Small Override that makes it so back button changes selected icon
    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_games);
    }
    //Small function that will update the view, without this it wasn't showing all the games
    private void updateRecyclerView() {
        customAdapter = new CustomAdapter(this, myGames);
        recyclerView.setAdapter(customAdapter);
    }
    //Used in the event game hasn't started, since we need to convert game status to time game starts
    private String getGameTime(String unformattedDate) {
        String[] dateArr = unformattedDate.split("T");
        String utcTime = dateArr[1]; //Gets string after T (the time)
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
        //First, we set timezone to UTC for the conversion
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        //Now, we convert the string to a Date object
        try {
            Date originalTime = inputFormat.parse(utcTime);
            //Finally, we convert to local timezone and change the format too
            outputFormat.setTimeZone(TimeZone.getDefault()); //sets format to local time
            return outputFormat.format(originalTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

}