package com.example.jetpacktest.propsBaseball.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BaseballEvent(
    val id: String,
    val homeTeam: String,
    val awayTeam: String,
    val commenceTime: String
): Parcelable
