package com.example.jetpacktest.domain.use_case

import com.example.jetpacktest.common.Resource
import com.example.jetpacktest.domain.repository.HeadshotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.text.Normalizer
import javax.inject.Inject

class HeadshotUseCase @Inject constructor(
    private val repository: HeadshotRepository
) {
    operator fun invoke(playerName: String, apiKey: String): Flow<Resource<Int>> = flow {
        try {
            emit(Resource.Loading())
            val noAccentedPlayerName = removeAccents(playerName)
            val (firstName, lastName) = splitPlayerName(noAccentedPlayerName)
            val activePlayersList = repository.getActivePlayers(apiKey)
            val matchingPlayer = activePlayersList.find {
                it.firstName == firstName && it.lastName == lastName
            }
            val nbaDotComPlayerId = matchingPlayer?.let {
                repository.getPlayerById(it.playerID, apiKey).nbaDotComPlayerID
            } ?: -1
            emit(Resource.Success(nbaDotComPlayerId))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check internet connection"))
        }
    }

    private fun splitPlayerName(playerName: String): Pair<String, String> {
        val splitNameParts = playerName.split(" ".toRegex(), limit = 2)
        val firstName = splitNameParts.getOrNull(0) ?: ""
        val lastName = splitNameParts.getOrNull(1) ?: ""
        return Pair(firstName, lastName)
    }

    //Niko: Helper function to remove accents from playerName, since API we use for headshots doesn't use accent
    //Got it from https://stackoverflow.com/a/3322174
    private fun removeAccents(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }
}