package com.example.jetpacktest

import android.system.Os.connect
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.models.BaseballPlayerSeasonStats
import com.example.jetpacktest.models.BaseballTeamStanding
import com.example.jetpacktest.models.Batter
import com.example.jetpacktest.models.InjuredPlayer
import com.example.jetpacktest.models.Pitcher
import com.example.jetpacktest.models.Player
import com.example.jetpacktest.models.StatLeader
import com.example.jetpacktest.models.TeamMaps
import com.example.jetpacktest.models.TeamStanding
import com.example.jetpacktest.models.TeamStanding.Conference
import com.example.jetpacktest.screens.PlayerDataRow
import com.example.jetpacktest.screens.StatBox
import com.example.jetpacktest.ui.components.ExpandableCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
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
    private val url = "jdbc:mysql://nikoarak.cikeys.com:3306/nikoarak_SESports?allowPublicKeyRetrieval=true&useSSL=false"
    //private val url = "jdbc:mysql://nikoarak.cikeys.com:3306/nikoarak_SESports?useSSL=true&enabledTLSProtocols=TLSv1.2"
    //private val url = "jdbc:mysql://nikoarak.cikeys.com:3306/nikoarak_SESports"
    private val user = Keys.DB_USER
    private val password = Keys.DB_PASS
    private var connection: Connection? = null
    var salaryResultSet: ResultSet? = null


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
    fun executeBaseballStandings(
        league: BaseballTeamStanding.League,
        year: String,
        onDataReceived: (MutableList<BaseballTeamStanding>) -> Unit
    ) {
        scope.launch {
            val standingsList = getBaseballStandings(league, year)
            onDataReceived(standingsList)
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
            onDataReceived(standingsList)
        }
    }
    fun executeQuery(
        query: String,
        columnName: String,
        callback: (List<String>) -> Unit
    ) {
        scope.launch {
            var connection: Connection? = null
            var statement: Statement? = null
            var resultSet: ResultSet? = null

            try {
                Log.d("DatabaseDebug", "Executing query: $query")

                connection = DriverManager.getConnection(url, user, password)
                statement = connection.createStatement()
                resultSet = statement.executeQuery(query)

                val results = mutableListOf<String>()
                while (resultSet.next()) {
                    results.add(resultSet.getString(columnName))
                }

                withContext(Dispatchers.Main) {
                    callback(results)
                }
            } catch (e: SQLException) {
                Log.e("DatabaseDebug", "SQL Exception: ${e.localizedMessage}")
                withContext(Dispatchers.Main) {
                    callback(emptyList())
                }
            } catch (e: Exception) {
                Log.e("DatabaseDebug", "Exception: ${e.localizedMessage}")
                withContext(Dispatchers.Main) {
                    callback(emptyList())
                }
            } finally {
                try { resultSet?.close() } catch (_: Exception) {}
                try { statement?.close() } catch (_: Exception) {}
                try { connection?.close() } catch (_: Exception) {}
            }
        }
    }


    fun executeBaseballStatLeaders(
        chosenStat: String,
        year: String,
        onDataReceived: (MutableList<StatLeader>) -> Unit
    ) {
        scope.launch {
            val statLeadersList = getBaseballStatLeaders(chosenStat, year)
            withContext(Dispatchers.IO) {
                onDataReceived(statLeadersList)
            }
        }
    }

    fun executePitcherSearch(searchText: String, callback: (List<String>) -> Unit) {
        val query = """
        SELECT DISTINCT player_name FROM mlb_pitchers
        WHERE player_name LIKE '%$searchText%'
        ORDER BY player_name
        LIMIT 50;
    """.trimIndent()
        executeQuery(query, "player_name", callback)
    }

    fun executePitcherYears(playerName: String, onDataReceived: (MutableList<String>) -> Unit) {
        scope.launch {
            val yearsList = getPitcherYears(playerName)
            withContext(Dispatchers.IO) {
                onDataReceived(yearsList)
            }
        }
    }
    fun executeBatterYears(playerName: String, onDataReceived: (MutableList<String>) -> Unit) {
        scope.launch {
            val yearsList = getBatterYears(playerName)
            withContext(Dispatchers.IO) {
                onDataReceived(yearsList)
            }
        }
    }

    fun executeBatterSearch(searchText: String, callback: (List<String>) -> Unit) {
        val query = """
        SELECT DISTINCT player_name FROM mlb_batters
        WHERE player_name LIKE '%$searchText%'
        ORDER BY player_name
        LIMIT 50;
    """.trimIndent()
        executeQuery(query, "player_name", callback)
    }

    fun getBatterData(name: String, year: String, callback: (Batter?) -> Unit) {
        scope.launch {
            var result: Batter? = null
            try {
                Class.forName("com.mysql.jdbc.Driver")
                val conn = DriverManager.getConnection(url, user, password)
                val stmt = conn.createStatement()
                val rs = stmt.executeQuery("""
                SELECT 
                    season, team, player_name, position, air_outs, at_bats, at_bats_per_home_run, avg, babip,
                    base_on_balls, catchers_interference, caught_stealing, doubles, games_played,
                    ground_into_double_play, ground_outs, ground_outs_to_airouts, hit_by_pitch, hits,
                    home_runs, intentional_walks, left_on_base, number_of_pitches, obp, ops,
                    plate_appearances, rbi, runs, sac_bunts, sac_flies, slg, stolen_base_percentage,
                    stolen_bases, strike_outs, total_bases, triples
                FROM mlb_batters
                WHERE player_name = "$name" AND season = $year
                LIMIT 1
            """.trimIndent())

                if (rs.next()) {
                    result = Batter(
                        season = rs.getInt("season"),
                        team = rs.getString("team"),
                        playerName = rs.getString("player_name"),
                        position = rs.getString("position"),
                        airOuts = rs.getInt("air_outs"),
                        atBats = rs.getInt("at_bats"),
                        atBatsPerHomeRun = rs.getDouble("at_bats_per_home_run"),
                        avg = rs.getDouble("avg"),
                        babip = rs.getDouble("babip"),
                        baseOnBalls = rs.getInt("base_on_balls"),
                        catchersInterference = rs.getInt("catchers_interference"),
                        caughtStealing = rs.getInt("caught_stealing"),
                        doubles = rs.getInt("doubles"),
                        gamesPlayed = rs.getInt("games_played"),
                        groundIntoDoublePlay = rs.getInt("ground_into_double_play"),
                        groundOuts = rs.getInt("ground_outs"),
                        groundOutsToAirouts = rs.getDouble("ground_outs_to_airouts"),
                        hitByPitch = rs.getInt("hit_by_pitch"),
                        hits = rs.getInt("hits"),
                        homeRuns = rs.getInt("home_runs"),
                        intentionalWalks = rs.getInt("intentional_walks"),
                        leftOnBase = rs.getInt("left_on_base"),
                        numberOfPitches = rs.getInt("number_of_pitches"),
                        obp = rs.getDouble("obp"),
                        ops = rs.getDouble("ops"),
                        plateAppearances = rs.getInt("plate_appearances"),
                        rbi = rs.getInt("rbi"),
                        runs = rs.getInt("runs"),
                        sacBunts = rs.getInt("sac_bunts"),
                        sacFlies = rs.getInt("sac_flies"),
                        slg = rs.getDouble("slg"),
                        stolenBasePercentage = rs.getDouble("stolen_base_percentage"),
                        stolenBases = rs.getInt("stolen_bases"),
                        strikeOuts = rs.getFloat("strike_outs"),
                        totalBases = rs.getInt("total_bases"),
                        triples = rs.getInt("triples"),
                        war = BigDecimal.ZERO
                    )
                }
                rs.close()
                stmt.close()
                conn.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }
    private fun getBatterYears(playerName: String): MutableList<String> {
        val yearsList = mutableListOf<String>()
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()

            val sql = """
            SELECT DISTINCT season
            FROM mlb_batters
            WHERE player_name = "$playerName"
            ORDER BY season DESC
        """.trimIndent()

            resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                yearsList.add(resultSet.getInt("season").toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeResources(myConn, resultSet, statement)
        }
        return yearsList
    }


    // This private helper will actually query the database
    private fun getPitcherYears(playerName: String): MutableList<String> {
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        val yearsList = mutableListOf<String>()
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()

            val sql = """
            SELECT DISTINCT season 
            FROM mlb_pitchers 
            WHERE player_name = "$playerName"
            ORDER BY season DESC
        """.trimIndent()

            resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                yearsList.add(resultSet.getInt("season").toString())
            }
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message ?: "SQL error during getPitcherYears")
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            closeResources(myConn, resultSet, statement)
        }
        return yearsList
    }
    fun executePitcherData(playerName: String, year: String, onDataReceived: (Pitcher) -> Unit) {
        scope.launch {
            val pitcher = getPitcherData(playerName, year)
            withContext(Dispatchers.IO) {
                onDataReceived(pitcher)
            }
        }
    }

    // This private function will actually fetch the pitcher stats
    private fun getPitcherData(playerName: String, year: String): Pitcher {
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        var pitcher = Pitcher() // this is your Pitcher model (already built)

        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()

            val sql = """
    SELECT 
        season,
        team,
        player_name,
        position,
        air_outs,
        at_bats,
        avg,
        balks,
        base_on_balls,
        batters_faced,
        blown_saves,
        catchers_interference,
        caught_stealing,
        complete_games,
        doubles,
        earned_runs,
        era,
        games_finished,
        games_pitched,
        games_played,
        games_started,
        ground_into_double_play,
        ground_outs,
        ground_outs_to_airouts,
        hit_batsmen,
        hit_by_pitch,
        hits,
        hits_per9inn,
        holds,
        home_runs,
        home_runs_per9,
        inherited_runners,
        inherited_runners_scored,
        innings_pitched,
        intentional_walks,
        losses,
        number_of_pitches,
        obp,
        ops,
        outs,
        pickoffs,
        pitches_per_inning,
        runs,
        runs_scored_per9,
        sac_bunts,
        sac_flies,
        save_opportunities,
        saves,
        shutouts,
        slg,
        stolen_base_percentage,
        stolen_bases,
        strike_outs,
        strike_percentage,
        strikeout_walk_ratio,
        strikeouts_per9inn,
        strikes,
        total_bases,
        triples,
        walks_per9inn,
        whip,
        wild_pitches,
        win_percentage,
        wins
    FROM mlb_pitchers
    WHERE player_name = "$playerName" AND season = $year
    LIMIT 1;
""".trimIndent()



            resultSet = statement.executeQuery(sql)

            if (resultSet.next()) {
                pitcher = Pitcher(
                    season = resultSet.getInt("season"),
                    team = resultSet.getString("team"),
                    playerName = resultSet.getString("player_name"),
                    position = resultSet.getString("position"),
                    airOuts = resultSet.getInt("air_outs"),
                    atBats = resultSet.getInt("at_bats"),
                    avg = resultSet.getDouble("avg"),
                    balks = resultSet.getInt("balks"),
                    baseOnBalls = resultSet.getInt("base_on_balls"),
                    battersFaced = resultSet.getInt("batters_faced"),
                    blownSaves = resultSet.getInt("blown_saves"),
                    catchersInterference = resultSet.getInt("catchers_interference"),
                    caughtStealing = resultSet.getInt("caught_stealing"),
                    completeGames = resultSet.getInt("complete_games"),
                    doubles = resultSet.getInt("doubles"),
                    earnedRuns = resultSet.getInt("earned_runs"),
                    era = resultSet.getDouble("era"),
                    gamesFinished = resultSet.getInt("games_finished"),
                    gamesPitched = resultSet.getInt("games_pitched"),
                    gamesPlayed = resultSet.getInt("games_played"),
                    gamesStarted = resultSet.getInt("games_started"),
                    groundIntoDoublePlay = resultSet.getInt("ground_into_double_play"),
                    groundOuts = resultSet.getInt("ground_outs"),
                    groundOutsToAirouts = resultSet.getDouble("ground_outs_to_airouts"),
                    hitBatsmen = resultSet.getInt("hit_batsmen"),
                    hitByPitch = resultSet.getInt("hit_by_pitch"),
                    hits = resultSet.getInt("hits"),
                    hitsPer9Inn = resultSet.getDouble("hits_per9inn"), // ✅ fixed
                    holds = resultSet.getInt("holds"),
                    homeRuns = resultSet.getInt("home_runs"),
                    homeRunsPer9 = resultSet.getDouble("home_runs_per9"), // ✅ fixed
                    inheritedRunners = resultSet.getInt("inherited_runners"),
                    inheritedRunnersScored = resultSet.getInt("inherited_runners_scored"),
                    inningsPitched = resultSet.getDouble("innings_pitched"),
                    intentionalWalks = resultSet.getInt("intentional_walks"),
                    losses = resultSet.getInt("losses"),
                    numberOfPitches = resultSet.getInt("number_of_pitches"),
                    obp = resultSet.getDouble("obp"),
                    ops = resultSet.getDouble("ops"),
                    outs = resultSet.getInt("outs"),
                    pickoffs = resultSet.getInt("pickoffs"),
                    pitchesPerInning = resultSet.getDouble("pitches_per_inning"),
                    runs = resultSet.getInt("runs"),
                    runsScoredPer9 = resultSet.getDouble("runs_scored_per9"),
                    sacBunts = resultSet.getInt("sac_bunts"),
                    sacFlies = resultSet.getInt("sac_flies"),
                    saveOpportunities = resultSet.getInt("save_opportunities"),
                    saves = resultSet.getInt("saves"),
                    shutouts = resultSet.getInt("shutouts"),
                    slg = resultSet.getDouble("slg"),
                    stolenBasePercentage = resultSet.getDouble("stolen_base_percentage"),
                    stolenBases = resultSet.getInt("stolen_bases"),
                    strikeOuts = resultSet.getInt("strike_outs"),
                    strikePercentage = resultSet.getDouble("strike_percentage"),
                    strikeoutWalkRatio = resultSet.getDouble("strikeout_walk_ratio"),
                    strikeoutsPer9Inn = resultSet.getDouble("strikeouts_per9inn"), // ✅ fixed
                    strikes = resultSet.getInt("strikes"),
                    totalBases = resultSet.getInt("total_bases"),
                    triples = resultSet.getInt("triples"),
                    walksPer9Inn = resultSet.getDouble("walks_per9inn"), // ✅ fixed
                    whip = resultSet.getDouble("whip"),
                    wildPitches = resultSet.getInt("wild_pitches"),
                    winPercentage = resultSet.getDouble("win_percentage"),
                    wins = resultSet.getInt("wins"),
                    war = BigDecimal.ZERO

                )

            }

        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message ?: "SQL error during getPitcherData")
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        finally {
            closeResources(myConn, resultSet, statement)
        }
        return pitcher
    }

    // Helper function for above
    private fun getBaseballStatLeaders(chosenStat: String, year: String): MutableList<StatLeader> {
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        val statLeadersList = mutableListOf<StatLeader>()
        val addedPlayers = mutableSetOf<String>()

        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()
            val sql = """
            SELECT Name, $chosenStat 
            FROM PlayerSeasonStats$year
            WHERE $chosenStat IS NOT NULL
            ORDER BY $chosenStat DESC
            LIMIT ${Const.NUM_PLAYERS_TO_GRAB}
        """.trimIndent()
            resultSet = statement.executeQuery(sql)

            var rank = 1
            while (resultSet.next()) {
                val playerName = resultSet.getString("Name")
                val statValue = resultSet.getFloat(chosenStat)

                if (!addedPlayers.contains(playerName)) {
                    statLeadersList.add(StatLeader(rank++, playerName, statValue))
                    addedPlayers.add(playerName)
                }
            }
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message ?: "SQL error during getBaseballStatLeaders")
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        finally {
            closeResources(myConn, resultSet, statement)
        }

        return statLeadersList
    }
    private fun isLowerBetter(statName: String): Boolean {
        return statName.lowercase() in listOf(
            "era", "losses", "walks per 9 inn", "runs scored per 9", "whip",
            "ground outs", "grounds into double play", "left on base", "at bats per hr", "wild pitches", "hit batsmen", "home runs per 9", "runs scored per 9", "hits per 9 innings"
        )
    }
    fun fetchBaseballStatLeaders(
        isPitcher: Boolean,
        chosenStat: String,
        year: String,
        callback: (List<StatLeader>) -> Unit
    ) {
        scope.launch {
            val table = if (isPitcher) "mlb_pitchers" else "mlb_batters"
            val results = mutableListOf<StatLeader>()

            try {
                Class.forName("com.mysql.jdbc.Driver")
                val conn = DriverManager.getConnection(url, user, password)
                val stmt = conn.createStatement()

                val sortDirection = if (isLowerBetter(chosenStat)) "ASC" else "DESC"

                val query = """
                SELECT player_name, $chosenStat 
                FROM $table 
                WHERE $chosenStat IS NOT NULL 
                AND season = $year 
                AND games_played >= 15 
                ORDER BY $chosenStat $sortDirection 
                LIMIT 20
            """.trimIndent()

                val rs = stmt.executeQuery(query)
                var rank = 1

                while (rs.next()) {
                    val rawName = rs.getString("player_name")
                    val name = removeAccents(rawName)
                    val statValue = rs.getFloat(chosenStat)
                    results.add(StatLeader(rank++, name, statValue))
                }

                rs.close()
                stmt.close()
                conn.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                callback(results)
            }
        }
    }






    fun executeBaseballPlayerSearch(searchText: String, callback: (List<String>) -> Unit) {
        val query = """
        SELECT DISTINCT player_name FROM mlb_pitchers
        WHERE player_name LIKE '%$searchText%'
        ORDER BY player_name
        LIMIT 50;
    """.trimIndent()

        executeQuery(query, "player_name", callback)
    }


    private fun getBaseballStandings(
        league: BaseballTeamStanding.League,
        year: String
    ): MutableList<BaseballTeamStanding> {
        val teamStandings = mutableListOf<BaseballTeamStanding>()
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()
            val sql = "SELECT * FROM BaseballTeamStanding WHERE season = $year AND league = '${league.name}' ORDER BY wins DESC"
            resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                val standing = BaseballTeamStanding(
                    rank = resultSet.getInt("ranking"),
                    name = resultSet.getString("name"),
                    wins = resultSet.getInt("wins"),
                    losses = resultSet.getInt("losses"),
                    winLossPercentage = resultSet.getFloat("winLossPercentage"),
                    league = league,
                    abbrev = resultSet.getString("abbrev"),
                    logoUrl = resultSet.getString("wikiLogoUrl"),
                    season = resultSet.getInt("season")
                )
                teamStandings.add(standing)
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
    }

    private fun getStandings(conference: Conference, year: String): MutableList<TeamStanding> {
        var myConn: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        val teamStandings = mutableListOf<TeamStanding>()
        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()
            val sql = "SELECT* FROM ${conference.name}_STANDING WHERE `Year` = $year"
            resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                val teamName = resultSet.getString("Team")
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
                    conference = conference,
                    logo = TeamMaps.xmlLogos[teamName] ?: R.drawable.baseline_arrow_back_ios_new_24,
                    abbrev = abbrev
                )
                teamStandings.add(teamStanding)
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
        var salaryResultSet: ResultSet? = null
        var player = Player()
        var salary = 20_000_000f // default fallback salary

        val playerNameNoAccents = removeAccents(playerName)

        // Normalize year to map to salary column (only allow 2023 and 2024 based on the second part of the column name)
        val salaryYear = when (year) {
            "2023", "2022_2023" -> "Year2022_2023"
            "2024", "2023_2024" -> "Year2023_2024"
            else -> null
        }

        val displayYear = salaryYear?.takeLast(4) // e.g., "2023_2024" -> "2024"

        val salaryQuery = if (salaryYear != null)
            "SELECT `$salaryYear` AS Salary FROM SALARY WHERE PlayerName = \"$playerNameNoAccents\" LIMIT 1"
        else
            ""

        try {
            Class.forName("com.mysql.jdbc.Driver")
            myConn = DriverManager.getConnection(url, user, password)
            statement = myConn.createStatement()

            if (salaryQuery.isNotEmpty()) {
                salaryResultSet = statement.executeQuery(salaryQuery)
                if (salaryResultSet?.next() == true) {
                    salary = salaryResultSet.getFloat("Salary")
                    val formattedSalary = "%.2f".format(salary / 1_000_000f)
                    Log.d("PlayerSalary", "Retrieved salary for $playerNameNoAccents in $displayYear: $$formattedSalary million")
                }
            }

            val sql = "SELECT * FROM PLAYER WHERE Player = \"$playerNameNoAccents\" AND Year = $year"
            resultSet = statement.executeQuery(sql)

            var rowCount = 0
            while (resultSet.next()) {
                rowCount++
            }
            resultSet.beforeFirst()

            if (rowCount > 1) {
                var appendedTeam = ""
                while (resultSet.next()) {
                    if (resultSet.getString("Team") == "TOT") {
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
                            defensiveRebounds = resultSet.getFloat("DRB"),
                            salary = salary
                        )
                    } else {
                        appendedTeam += "${resultSet.getString("Team")}/"
                    }
                }
                player.team = appendedTeam.dropLast(1)
            } else {
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
                    defensiveRebounds = resultSet.getFloat("DRB"),
                    salary = salary
                )
            }
        } catch (e: SQLException) {
            Log.d(Const.TAG, e.message!!)
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            salaryResultSet?.close()
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


