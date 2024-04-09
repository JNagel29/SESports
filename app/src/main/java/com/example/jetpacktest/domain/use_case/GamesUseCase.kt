package com.example.jetpacktest.domain.use_case

import android.net.http.HttpException
import com.example.jetpacktest.R
import com.example.jetpacktest.common.Resource
import com.example.jetpacktest.data.remote.dto.games.Game
import com.example.jetpacktest.data.remote.dto.games.toGames
import com.example.jetpacktest.domain.model.Games
import com.example.jetpacktest.domain.repository.GamesRepository
import com.example.jetpacktest.models.NbaTeam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.IOException
import java.util.Date
import javax.inject.Inject

class GamesUseCase @Inject constructor(
    private val repository: GamesRepository
) {
    operator fun invoke(date: Date): Flow<Resource<Games>> = flow {
        try {
            //Emit loading for progress bar
            emit(Resource.Loading())
            val games = repository.getGames(date).toGames()
            games.data.forEach { game ->
                processGame(game)
            }
            emit(Resource.Success(games))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check internet connection"))
        }
    }

    private fun processGame(game: Game) {
        if (game.time.isNullOrEmpty() || game.time == "Final") game.time = ""
        if (game.status.startsWith("20")) { // Game hasn't started yet
            game.status = formatStartGameTime(game.status)
        }
        game.homeTeam.logo = NbaTeam.xmlLogos[game.homeTeam.name]
            ?: R.drawable.baseline_arrow_back_ios_new_24
        game.visitorTeam.logo = NbaTeam.xmlLogos[game.visitorTeam.name]
            ?: R.drawable.baseline_arrow_back_ios_new_24
    }

    private fun formatStartGameTime(unformattedDate: String): String {
        try {
            val utcDateTime = LocalDateTime.parse(unformattedDate, DateTimeFormatter.ISO_DATE_TIME)
            val utcZonedDateTime = ZonedDateTime.of(utcDateTime, ZoneId.of("UTC"))
            val localZoneId = ZoneId.systemDefault()
            val localZonedDateTime = utcZonedDateTime.withZoneSameInstant(localZoneId)
            //Return time in format of hour, minute, with am/pm marker
            val outputFormat = DateTimeFormatter.ofPattern("hh:mm a")
            return outputFormat.format(localZonedDateTime)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "N/A"
    }

}