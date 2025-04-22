package com.example.jetpacktest

import android.util.Log
import com.example.jetpacktest.models.InjuredPlayer
import com.example.jetpacktest.models.Player
import com.example.jetpacktest.models.StatLeader
import com.example.jetpacktest.models.TeamMaps
import com.example.jetpacktest.models.TeamStanding
import com.example.jetpacktest.models.TeamStanding.Conference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.text.Normalizer

class DatabaseHandler {
    object Const {
        const val TAG = "DatabaseHandler"
        const val NUM_PLAYERS_TO_GRAB = 10
    }
    private val scope = CoroutineScope(Dispatchers.IO)
    private val url = "jdbc:mysql://nikoarak.cikeys.com:3306/nikoarak_SESports?useSSL=false"
    //private val url = "jdbc:mysql://nikoarak.cikeys.com:3306/nikoarak_SESports?useSSL=true&enabledTLSProtocols=TLSv1.2"


    private val url = "jdbc:mysql://nikoarak.cikeys.com:3306/nikoarak_SESports"
    private val user = Keys.DB_USER
    private val password = Keys.DB_PASS

    fun executeStatLeaders(chosenStat: String, year: String,
                           onDataReceived: (MutableList<StatLeader>) -> Unit) {
        scope.launch {
            val statLeadersList = getStatLeaders(chosenStat, year)
            withContext(Dispatchers.IO) {
                onDataReceived(statLeadersList)
            }
        }
    }
    fun executeYears(playerName: String, onDataReceived: (MutableList<String>) -> Unit) {
        scope.launch {
            val yearsList = getPlayerYears(playerName)
            withContext(Dispatchers.IO) {
                onDataReceived(yearsList)
            }
        }

    }
    fun executePlayerData(playerName: String, year: String,
                          onDataReceived: (Player) -> Unit) {
        scope.launch {
            val player = getPlayerData(playerName, year)
            withContext(Dispatchers.IO) {
                onDataReceived(player)
            }
        }
    }

    fun executePlayerSearchResults(searchResultName: String,
                                   onDataReceived: (MutableList<String>) -> Unit) {
        scope.launch {
            val playerResultsList = getPlayerSearchResults(searchResultName)
            withContext(Dispatchers.IO) {
                onDataReceived(playerResultsList)
            }
        }
    }

    fun executeRandomStat(randIndex: Int,
                          onDataReceived: (String) -> Unit) {
        scope.launch {
            val randomStat = getRandomStat(randIndex)
            withContext(Dispatchers.IO) {
                onDataReceived(randomStat)
            }
        }
    }

    fun executeNbaDotComId(playerName: String,
                           onDataReceived: (Int) -> Unit) {
        scope.launch {
            val nbaDotComId = getNbaDotComId(playerName = playerName)
            withContext(Dispatchers.IO) {
                onDataReceived(nbaDotComId)
            }
        }
    }

    fun executeStoreInjured(injuredPlayers: List<InjuredPlayer>) {
        scope.launch {
            storeInjuredPlayers(injuredPlayers)
        }
    }

    fun executeCheckInjuredStartDate(playerName: String, onDataReceived: (String) -> Unit) {
        scope.launch {
            val injuryStartDate: String = checkInjuredStartDate(playerName = playerName)
            withContext(Dispatchers.IO) {
                onDataReceived(injuryStartDate)
            }
        }
    }

    private fun checkInjuredStartDate(playerName: String): String {
        var injuredStartDate = ""
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()
            val sql = "SELECT `InjuryStartDate` FROM INJURED_PLAYER WHERE `Name` = \"$playerName\""
            resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                injuredStartDate = resultSet.getString("InjuryStartDate")
                Log.d("DatabaseHandler", "Player was injured on $injuredStartDate")
            }
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message!!)
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            closeResources(myConn, resultSet, statement)
        }
        return if (injuredStartDate == "") "N/A"
        else injuredStartDate
    }

    private fun storeInjuredPlayers(injuredPlayers: List<InjuredPlayer>) {
        var myConn: Connection? = null
        var statement: Statement? = null
        try {
            Log.d("DatabaseHandler", "Storing new list of injured players...")
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()
            //Prevents previous injuries from displaying
            statement.executeUpdate("DELETE FROM INJURED_PLAYER")
            //PreparedStatement for re-usability
            val preparedStatement = myConn.prepareStatement(
                "INSERT INTO INJURED_PLAYER (`Name`, `InjuryStartDate`) VALUES (?, ?)"
            )
            injuredPlayers.forEach {injuredPlayer ->
                val fullName = "${injuredPlayer.firstName} ${injuredPlayer.lastName}"
                val injuryStartDate = injuredPlayer.injuryStartDate
                preparedStatement.setString(1, fullName)
                preparedStatement.setString(2, injuryStartDate)
                preparedStatement.executeUpdate()
            }
            Log.d("DatabaseHandler", "Finished storing list...")
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message!!)
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            closeResources(myConn, null, statement)
        }
    }

    fun executeStandings(
        conference: Conference, year: String, onDataReceived: (MutableList<TeamStanding>) -> Unit
    ) {
        scope.launch {
            val standingsList = getStandings(conference =conference, year = year)

    fun executeStandings(conference: Conference, onDataReceived: (MutableList<TeamStanding>) -> Unit) {
        scope.launch {
            val standingsList = getStandings(conference)
            onDataReceived(standingsList)
        }
    }

    private fun getStandings(conference: Conference, year: String): MutableList<TeamStanding> {
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        val teamStandings = mutableListOf<TeamStanding>()
    private fun getStandings(conference: Conference): MutableList<TeamStanding> {
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        val teamPlayers = mutableListOf<TeamStanding>()
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()
            val sql = "SELECT* FROM ${conference.name}_STANDING WHERE `Year` = $year"
            resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                val teamName = resultSet.getString("Team")
            val sql = "SELECT* FROM ${conference.name}_STANDING"
            resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                val teamName = resultSet.getString("Team_Name")
                val abbrev = when (teamName) {
                    "New York Knicks" -> "NYK" // These 4 have their third letter cut off
                    "New Orleans Pelicans" -> "NOP"
                    "San Antonio Spurs" -> "SAS"
                    "Golden State Warriors" -> "GSW"
                    else -> TeamMaps.namesToAbbreviations[teamName] ?: "N/A"
                }
                val teamStanding = TeamStanding(
                    rank = resultSet.getInt("Rank"),
                    name = teamName,
                    wins = resultSet.getInt("W"),
                    losses = resultSet.getInt("L"),
                    winLossPercentage = resultSet.getFloat("W/L%"),
                    rank = resultSet.getInt("rank"),
                    name = teamName,
                    wins = resultSet.getInt("Wins"),
                    losses = resultSet.getInt("Losses"),
                    winLossPercentage = resultSet.getFloat("Win_Loss_Percentage"),
                    conference = conference,
                    logo = TeamMaps.xmlLogos[teamName] ?: R.drawable.baseline_arrow_back_ios_new_24,
                    abbrev = abbrev
                )
                teamStandings.add(teamStanding)
                teamPlayers.add(teamStanding)
            }
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message!!)
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            closeResources(myConn, resultSet, statement)
        }
        return teamStandings
        return teamPlayers
    }

    private fun getNbaDotComId(playerName: String): Int {
        var id: Int = -1
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()
            val sql = "SELECT `NbaId` FROM NBA_PLAYER_ID WHERE `Name` = \"$playerName\""
            resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                id = resultSet.getInt("NbaId")
            }
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message!!)
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            closeResources(myConn, resultSet, statement)
        }
        return id
    }

    private fun getRandomStat(randIndex: Int): String {
        var randomStat = ""
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()
            val sql = "SELECT statString FROM RANDOM_STAT WHERE statID = $randIndex"
            resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                randomStat = resultSet.getString("statString")
            }
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message!!)
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            closeResources(myConn, resultSet, statement)
        }
        return randomStat
    }

    private fun getPlayerSearchResults(searchResultName: String): MutableList<String> {
        val playerResultsList = mutableListOf<String>()
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        val searchResultsNameNoAccents = removeAccents(searchResultName)
        try {
            Log.d("DatabaseHandler", "Fetching new search results")
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()
            //Use SQL "Like" to find similar names
            //ORDER makes sure to put more recent players at the top
            val sql = "SELECT DISTINCT Player FROM PLAYER p1 WHERE Player LIKE \"%$searchResultsNameNoAccents%\" " +
                    "ORDER BY (SELECT MAX(Year) FROM PLAYER p2 WHERE p2.Player = p1.Player) DESC"
            resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                val playerName = resultSet.getString("Player")
                playerResultsList.add(playerName) // Add to list of results
            }
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message!!)
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            closeResources(myConn, resultSet, statement)
        }
        return playerResultsList
    }

    private fun getPlayerData(playerName: String, year: String): Player {
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        var player = Player()
        //Remove accents since can't have any when searching DB
        val playerNameNoAccents = removeAccents(playerName)
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()
            //NOTE: Needed to wrap in double, not single quotes, since some players have ' in name
            val sql = "SELECT* FROM PLAYER WHERE Player = \"$playerNameNoAccents\" AND Year = $year"
            resultSet = statement.executeQuery(sql)
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
            if (rowCount > 1) {
                var appendedTeam = ""
                while (resultSet.next()) {
                    if (resultSet.getString("Team") == "TOT") {
                        //Get numerical data from row (everything except team)
                        player = Player(
                            name = resultSet.getString("Player"),
                            year = resultSet.getInt("Year"),
                            position = resultSet.getString("Position"),
                            team = "",
                            points = resultSet.getFloat("PTS"),
                            assists = resultSet.getFloat("AST"),
                            steals = resultSet.getFloat("STL"),
                            blocks = resultSet.getFloat("BLK"),
                            totalRebounds = resultSet.getFloat("TRB"),
                            turnovers = resultSet.getFloat("TOV"),
                            personalFouls = resultSet.getFloat("PF"),
                            minutesPlayed = resultSet.getFloat("MP"),
                            gamesStarted = resultSet.getInt("GS"),
                            fieldGoals = resultSet.getFloat("FG"),
                            fieldGoalAttempts = resultSet.getFloat("FGA"),
                            fieldGoalPercent = resultSet.getFloat("FG_PERCENT"),
                            threePointers = resultSet.getFloat("3P"),
                            threePointerAttempts = resultSet.getFloat("3PA"),
                            threePointPercent = resultSet.getFloat("3P_PERCENT"),
                            twoPointers = resultSet.getFloat("2P"),
                            twoPointerAttempts = resultSet.getFloat("2PA"),
                            twoPointPercent = resultSet.getFloat("2P_PERCENT"),
                            freeThrows = resultSet.getFloat("FT"),
                            freeThrowAttempts = resultSet.getFloat("FTA"),
                            freeThrowPercent = resultSet.getFloat("FT_PERCENT"),
                            effectiveFieldGoalPercent = resultSet.getFloat("eFG_PERCENT"),
                            offensiveRebounds = resultSet.getFloat("ORB"),
                            defensiveRebounds = resultSet.getFloat("DRB")
                        )
                    }
                    else {
                        //From the other rows, just grab the team name and append
                        appendedTeam += "${resultSet.getString("Team")}/"
                    }
                }
                //Now, we've set all fields except team name, so do that now
                player.team = appendedTeam.dropLast(1) // Drop trailing slash
            }
            //Otherwise, we only have one record and can just grab all our data
            else {
                //Point resultSet one ahead to point at actual data
                resultSet.next()
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
                    gamesStarted = resultSet.getInt("GS"),
                    fieldGoals = resultSet.getFloat("FG"),
                    fieldGoalAttempts = resultSet.getFloat("FGA"),
                    fieldGoalPercent = resultSet.getFloat("FG_PERCENT"),
                    threePointers = resultSet.getFloat("3P"),
                    threePointerAttempts = resultSet.getFloat("3PA"),
                    threePointPercent = resultSet.getFloat("3P_PERCENT"),
                    twoPointers = resultSet.getFloat("2P"),
                    twoPointerAttempts = resultSet.getFloat("2PA"),
                    twoPointPercent = resultSet.getFloat("2P_PERCENT"),
                    freeThrows = resultSet.getFloat("FT"),
                    freeThrowAttempts = resultSet.getFloat("FTA"),
                    freeThrowPercent = resultSet.getFloat("FT_PERCENT"),
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
        } finally {
            closeResources(myConn, resultSet, statement)
        }
        return player
    }


    //This function is the one actually connecting to DB and giving us the data for stat leads
    private fun getStatLeaders(chosenStat: String, year: String): MutableList<StatLeader> {
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        val statData = StringBuilder()
        val statLeadersList = mutableListOf<StatLeader>()
        //Used to keep track of added player names to prevent duplicates
        val addedPlayers = mutableSetOf<String>()
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()
            val sql = "SELECT * FROM PLAYER WHERE YEAR = $year ORDER BY $chosenStat DESC"
            resultSet = statement.executeQuery(sql)
            var counter = 0 // increment variable to count up
            statData.append("Year: ").append(year).append("\n")
                .append("\n") // append year to top for reference
            while (resultSet.next()) {
                val playerName = resultSet.getString("Player")
                //Makes sure player name doesn't already exists in the addedPlayers set
                if (!addedPlayers.contains(playerName)) {
                    val chosenStatValue = resultSet.getFloat(chosenStat)
                    val statLeader = StatLeader(rank = counter + 1, name = playerName,
                        statValue = chosenStatValue)
                    statLeadersList.add(statLeader)
                    addedPlayers.add(playerName) // add player name to the set
                    statData.append("Player Name: ").append(playerName).append(", ")
                        .append(chosenStat).append(": ").append(chosenStatValue).append("\n")
                    counter++
                    if (counter >= Const.NUM_PLAYERS_TO_GRAB) break // break out after grabbing top x players
                }
            }
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message!!)
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            closeResources(myConn, resultSet, statement)
        }
        return statLeadersList
    }

    //This function will find us the years for which a player has stats for in our DB
    private fun getPlayerYears(playerName: String): MutableList<String> {
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        val yearsList = mutableListOf<String>()
        //We remove accents since searching DB requires none
        val playerNameNoAccents = removeAccents(playerName)
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()
            //This SQL selects a single column of years for that player
            //NOTE: We needed to wrap in double, not single quotes, since some players have ' in name
            //We use distinct since players who switched teams have multiple entries for same year
            val sql = "SELECT DISTINCT Year FROM PLAYER WHERE Player = \"$playerNameNoAccents\" ORDER BY Year DESC"
            resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                val year = resultSet.getInt("Year").toString()
                yearsList.add(year) // Add to list of years
            }
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message!!)
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            closeResources(myConn, resultSet, statement)
        }
        return yearsList
    }

    //Niko: Helper function to remove accents from playerName, since DB doesn't like them
    //Got it from https://stackoverflow.com/a/3322174
    private fun removeAccents(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }

    //Helper function to Close all our DB resources to avoid memory leak
    private fun closeResources(myConn: Connection?, resultSet: ResultSet?, statement: Statement?) {
        resultSet?.close()
        statement?.close()
        myConn?.close()
    }

}

