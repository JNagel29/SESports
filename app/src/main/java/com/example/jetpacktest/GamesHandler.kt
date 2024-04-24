package com.example.jetpacktest

//For Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
//Custom models/object
import com.example.jetpacktest.models.Game
import com.example.jetpacktest.models.GameResponse
import com.example.jetpacktest.models.TeamMaps
//For date operations
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class GamesHandler {
    //ApiInterface adds the 'games' endpoint, as well as the date query for us
    private val baseGameUrl = Keys.BDL_BASE_URL

    fun fetchGames(date: Date, onResult: (MutableList<Game>?) -> Unit) {
        //We first must convert our date into a usable format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        val gamesList = mutableListOf<Game>()
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseGameUrl)
            .build()
            .create(ApiInterface::class.java)
        val retrofitGames = retrofitBuilder.getGames(formattedDate)

        retrofitGames.enqueue(object : Callback<GameResponse?> {
            override fun onResponse(call: Call<GameResponse?>, response: Response<GameResponse?>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    for (game in responseBody.data) {
                        //Set the logos that weren't in JSON.
                        game.homeTeam.logo = TeamMaps.xmlLogos[game.homeTeam.name] ?:
                                R.drawable.baseline_arrow_back_ios_new_24
                        game.visitorTeam.logo = TeamMaps.xmlLogos[game.visitorTeam.name] ?:
                                R.drawable.baseline_arrow_back_ios_new_24
                        //gameTime set to Final means game ended, null means hasn't started
                        if (game.time.isNullOrEmpty() || game.time == "Final") game.time = ""
                        // If game status starts with 20 (year), then make it readable
                        if (game.status.startsWith("20")) {
                            game.status = fetchGameTime(game.status)
                        }
                        gamesList.add(game)
                    }
                    if (gamesList.isNotEmpty()) onResult(gamesList)
                    else onResult(null)
                }
                else { Log.d("GamesHandler", "Retrofit: Unsuccessful Response") }
            }

            override fun onFailure(call: Call<GameResponse?>, t: Throwable) {
                Log.d("GamesHandler", "Retrofit failure: $t")
            }
        })
    }

    //Used in the event game hasn't started, to convert game status to time it starts
    private fun fetchGameTime(unformattedDate: String): String {
        try {
            val utcDateTime = LocalDateTime.parse(unformattedDate, DateTimeFormatter.ISO_DATE_TIME)
            val utcZonedDateTime = ZonedDateTime.of(utcDateTime, ZoneId.of("UTC"))
            val localZoneId = ZoneId.systemDefault()
            val localZonedDateTime = utcZonedDateTime.withZoneSameInstant(localZoneId)
            //Return value in format of hour, minute, with am/pm marker
            val outputFormat = DateTimeFormatter.ofPattern("hh:mm a")
            return outputFormat.format(localZonedDateTime)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //If try failed, then return N/A to prevent compilation error
        return "N/A"
    }
}