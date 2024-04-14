package com.example.jetpacktest.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerPersonalInfo(
    val `data`: List<Data>,
) : Parcelable

@Parcelize
data class Data(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val position: String,
    val height: String,
    val weight: String,
    @SerializedName("jersey_number")
    val jerseyNumber: String,
    val college: String,
    val country: String,
    @SerializedName("draft_year")
    val draftYear: Int,
    @SerializedName("draft_round")
    val draftRound: Int,
    @SerializedName("draft_number")
    val draftNumber: Int,
) : Parcelable