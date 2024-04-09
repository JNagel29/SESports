package com.example.jetpacktest.domain.repository

import com.example.jetpacktest.data.remote.dto.games.GamesDto
import java.util.Date

interface GamesRepository {
    suspend fun getGames(date: Date): GamesDto

}