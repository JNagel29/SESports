package com.example.jetpacktest.data.remote.dto.games


import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("per_page")
    val perPage: Int
)