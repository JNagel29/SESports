package com.example.jetpacktest.data.remote.dto.games


import com.google.gson.annotations.SerializedName

data class Team(
    val id: Int,
    val conference: String,
    val division: String,
    val city: String,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val abbreviation: String,
    @Transient
    var logo: Int // TODO: Varring for now
)