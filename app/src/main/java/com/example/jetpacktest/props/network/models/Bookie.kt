// app/src/main/java/com/example/sportify2/network/models/Bookie.kt
package com.example.jetpacktest.props.network.models

import com.google.gson.annotations.SerializedName

data class Bookie(
    @SerializedName("bookieId")    val id: String,
    @SerializedName("bookieTitle") val title: String
)
