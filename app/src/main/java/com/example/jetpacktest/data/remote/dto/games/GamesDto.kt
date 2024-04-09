package com.example.jetpacktest.data.remote.dto.games

import com.example.jetpacktest.domain.model.Games


data class GamesDto(
    val `data`: List<Game>,
    val meta: Meta
)

fun GamesDto.toGames(): Games {
    return Games(
        data = this.data
    )
}
