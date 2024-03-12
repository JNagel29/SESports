package com.example.jetpacktest

import android.util.Log
import com.example.jetpacktest.models.Player
import com.example.jetpacktest.models.TopPlayer
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DatabaseHandler {
    object Const { // We use object since only way to make them constant
        //Tag for logging
        const val TAG = "Stat Leaders Activity"
        //Constant to hold how many players to grab (top 10, top 20, etc.)
        const val MAX_PLAYERS = 30
    }
    //Executor for the database requesting (used to run in background as its own thread)
    private val executor: Executor = Executors.newSingleThreadExecutor()
    //Variables and info to connect to DB
    private val url = "jdbc:mysql://nikoarak.cikeys.com:3306/nikoarak_SESports"
    private val user = "nikoarak_420db"
    private val password = "Kozon1008$"

    //This function will run the getStatLeaders fun in its own thread, using executor
    fun executeStatLeaders(chosenStat: String, year: String,
                            onDataReceived: (MutableList<TopPlayer>) -> Unit) {
        executor.execute {
            val topPlayerList = getStatLeaders(chosenStat, year)
            // After getting list of top players, callback to calling function to send back the data
            onDataReceived(topPlayerList)
        }
    }
    //Same as last function, but this one is for the years function
    fun executeYears(playerName: String, onDataReceived: (MutableList<String>) -> Unit) {
        executor.execute {
            val yearsList = getPlayerYears(playerName)
            // After getting list of years, callback to calling function to send back the data
            onDataReceived(yearsList)
        }
    }
    //Same as last function, but for grabbing entire pieces of data for players for profile
    fun executePlayerData(playerName: String, year: String,
                          onDataReceived: (Player) -> Unit) {
        executor.execute {
            val playerObj = getPlayerData(playerName, year)
            // After getting data of player, callback to calling function to send back the data
            onDataReceived(playerObj)
        }

    }

    //This function uses DB to store all data in a player object and returns
    private fun getPlayerData(playerName: String, year: String): Player {
        val myConn: Connection
        var player = Player() //Instantiate with secondary constructor
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            val statement = myConn.createStatement()
            val sql = "SELECT* FROM PLAYER WHERE Player = '$playerName' AND Year = $year"
            val resultSet = statement.executeQuery(sql)
            //First thing to do, is check if player was on multiple teams
            var rowCount = 0
            while (resultSet.next()) {
                rowCount++
            }
            //Before anything after, reset resultSet back so it points to before first row again
            resultSet.beforeFirst()
            //If rowCount is greater than 1, that means that there is a record with
            //team "TOT" (total) that we will grab stats from, and we'll also use other rows
            //to append to team so profile shows all teams they were on that year!
            //TODO: Running into trouble here cause we can't alter data class val
            if (rowCount > 1) {
                var appendedTeam = ""
                while (resultSet.next()) {
                    if (resultSet.getString("Team") == "TOT") {
                        //Get numerical data from total row
                    }
                    else {
                        //From the other rows, just grab the team name and append
                        appendedTeam += "${resultSet.getString("Team")}/"
                    }
                }
                //Now, we've set all fields except team name, so do that now
                //player.team = appendTeam
            }
            //Otherwise, we only have one record and can just grab all our data
            else {
                //First, need to point resultSet one ahead so it actually points at first row (not b4)
                resultSet.next()
                //Give player instance all the fitting params in constructor
                player = Player(
                    name = resultSet.getString("Player"),
                    year = resultSet.getInt("Year"),
                    position = resultSet.getString("Position"),
                    team = resultSet.getString("Team"),
                    points = resultSet.getFloat("PTS"),
                    assists = resultSet.getFloat("AST"),
                    steals = resultSet.getFloat("STL"),
                    blocks = resultSet.getFloat("BLK"),
                    totalRebounds = resultSet.getFloat("TRB"),
                    turnovers = resultSet.getFloat("TOV"),
                    personalFouls = resultSet.getFloat("PF"),
                    minutesPlayed = resultSet.getFloat("MP"),
                    fieldGoals = resultSet.getFloat("FG"),
                    fieldGoalAttempts = resultSet.getFloat("FGA"),
                    fieldGoalPercent = resultSet.getFloat("FG_PERCENT"),
                    threePointers = resultSet.getFloat("3P"),
                    threePointerAttempts = resultSet.getFloat("3PA"),
                    threePointPercent = resultSet.getFloat("3P_PERCENT"),
                    twoPointers = resultSet.getFloat("2P"),
                    twoPointerAttempts = resultSet.getFloat("2PA"),
                    twoPointPercent = resultSet.getFloat("2P_PERCENT"),
                    effectiveFieldGoalPercent = resultSet.getFloat("eFG_PERCENT"),
                    offensiveRebounds = resultSet.getFloat("ORB"),
                    defensiveRebounds = resultSet.getFloat("DRB")
                )
            }
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message!!)
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return player
    }


    //This function is the one actually connecting to DB and giving us the data for stat leads
    private fun getStatLeaders(chosenStat: String, year: String): MutableList<TopPlayer> {
        val myConn: Connection
        val statData = StringBuilder()
        val topPlayerList = mutableListOf<TopPlayer>()
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            val statement = myConn.createStatement()
            val sql = "SELECT * FROM PLAYER WHERE YEAR = $year ORDER BY $chosenStat DESC"
            val resultSet = statement.executeQuery(sql)
            var counter = 0 // increment variable to count up
            statData.append("Year: ").append(year).append("\n")
                .append("\n") // append year to top for reference
            while (resultSet.next()) {
                val playerName = resultSet.getString("Player")
                val chosenStatValue = resultSet.getFloat(chosenStat)
                val topPlayer = TopPlayer(rank = counter+1, name = playerName, stat = chosenStatValue)
                topPlayerList.add(topPlayer)
                statData.append("Player Name: ").append(playerName).append(", ").append(chosenStat)
                    .append(": ").append(chosenStatValue).append("\n")
                counter++
                if (counter >= Const.MAX_PLAYERS) break // break out after grabbing top x players
            }
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message!!)
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return topPlayerList
    }

    //This function will find us the years for which a player has stats for in our DB
    private fun getPlayerYears(playerName: String): MutableList<String> {
        val yearsList = mutableListOf<String>()
        val myConn: Connection
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            val statement = myConn.createStatement()
            //This SQL selects a single column of years for that player
            val sql = "SELECT Year FROM PLAYER WHERE Player = '$playerName' ORDER BY Year DESC"
            val resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                val year = resultSet.getInt("Year").toString()
                yearsList.add(year) // Add to list of years
            }
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message!!)
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return yearsList
    }
}

