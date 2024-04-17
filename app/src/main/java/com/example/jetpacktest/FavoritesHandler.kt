package com.example.jetpacktest

import android.content.Context
import android.content.SharedPreferences

class FavoritesHandler(context: Context) {
    companion object {
        const val FAVORITE_LIMIT = 5
    }
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)

    fun getFavoritePlayers(): Set<String> {
        return sharedPreferences.getStringSet("favorite_players", emptySet()) ?: emptySet()
    }

    fun addFavoritePlayer(playerName: String): Boolean {
        val favoritePlayers = getFavoritePlayers().toMutableSet()
        return if (favoritePlayers.size < FAVORITE_LIMIT) {
            favoritePlayers.add(playerName)
            saveFavoritePlayers(favoritePlayers)
            true
        } else false // Return false on error
    }

    fun removeFavoritePlayer(playerName: String) {
        val favoritePlayers = getFavoritePlayers().toMutableSet()
        favoritePlayers.remove(playerName)
        saveFavoritePlayers(favoritePlayers)
    }

    private fun saveFavoritePlayers(favoritePlayers: Set<String>) {
        with(sharedPreferences.edit()) {
            putStringSet("favorite_players", favoritePlayers)
            apply()
        }
    }
}