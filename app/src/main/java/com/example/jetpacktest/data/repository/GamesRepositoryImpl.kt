package com.example.jetpacktest.data.repository

import android.util.Log
import com.example.jetpacktest.data.remote.BdlApi
import com.example.jetpacktest.data.remote.dto.games.GamesDto
import com.example.jetpacktest.domain.repository.GamesRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GamesRepositoryImpl @Inject constructor(
    private val api: BdlApi
) : GamesRepository {

    override suspend fun getGames(date: Date): GamesDto {
        val formattedDate = formatDate(date)
        Log.d("GamesRepo", "Fetching new games...")
        return api.getGames(date = formattedDate)
    }

    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }
}