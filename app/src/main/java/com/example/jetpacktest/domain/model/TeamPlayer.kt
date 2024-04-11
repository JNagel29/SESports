package com.example.jetpacktest.domain.model

data class TeamPlayer(
    val team: String,
    val jersey: Int?,
    val position: String?,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val birthCity: String?,
    val birthState: String?,
    val birthCountry: String?,
    val height: Int?,
    val weight: Int?
)
