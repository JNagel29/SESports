package com.example.jetpacktest.models


import com.google.gson.annotations.SerializedName

data class InjuredPlayer(
    @SerializedName("FirstName")
    val firstName: String,
    @SerializedName("LastName")
    val lastName: String,
    @SerializedName("InjuryStartDate")
    val injuryStartDate: String,
)