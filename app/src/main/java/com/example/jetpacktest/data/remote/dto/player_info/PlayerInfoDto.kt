package com.example.jetpacktest.data.remote.dto.player_info


import com.example.jetpacktest.domain.model.PlayerInfo
import com.google.gson.annotations.SerializedName

data class PlayerInfoDto(
    @SerializedName("PlayerID")
    val playerID: Int,
    @SerializedName("SportsDataID")
    val sportsDataID: String,
    @SerializedName("Status")
    val status: String,
    @SerializedName("TeamID")
    val teamID: Int,
    @SerializedName("Team")
    val team: String,
    @SerializedName("Jersey")
    val jersey: Int,
    @SerializedName("PositionCategory")
    val positionCategory: String,
    @SerializedName("Position")
    val position: String,
    @SerializedName("FirstName")
    val firstName: String,
    @SerializedName("LastName")
    val lastName: String,
    @SerializedName("Height")
    val height: Int,
    @SerializedName("Weight")
    val weight: Int,
    @SerializedName("BirthDate")
    val birthDate: String,
    @SerializedName("BirthCity")
    val birthCity: String,
    @SerializedName("BirthState")
    val birthState: String,
    @SerializedName("BirthCountry")
    val birthCountry: String,
    @SerializedName("HighSchool")
    val highSchool: String,
    @SerializedName("College")
    val college: String,
    @SerializedName("Salary")
    val salary: Int,
    @SerializedName("PhotoUrl")
    val photoUrl: String,
    @SerializedName("Experience")
    val experience: Int,
    @SerializedName("SportRadarPlayerID")
    val sportRadarPlayerID: String,
    @SerializedName("RotoworldPlayerID")
    val rotoworldPlayerID: Int,
    @SerializedName("RotoWirePlayerID")
    val rotoWirePlayerID: Int,
    @SerializedName("FantasyAlarmPlayerID")
    val fantasyAlarmPlayerID: Int,
    @SerializedName("StatsPlayerID")
    val statsPlayerID: Int,
    @SerializedName("SportsDirectPlayerID")
    val sportsDirectPlayerID: Int,
    @SerializedName("XmlTeamPlayerID")
    val xmlTeamPlayerID: Int,
    @SerializedName("InjuryStatus")
    val injuryStatus: String,
    @SerializedName("InjuryBodyPart")
    val injuryBodyPart: String,
    @SerializedName("InjuryStartDate")
    val injuryStartDate: String,
    @SerializedName("InjuryNotes")
    val injuryNotes: String,
    @SerializedName("FanDuelPlayerID")
    val fanDuelPlayerID: Int,
    @SerializedName("DraftKingsPlayerID")
    val draftKingsPlayerID: Int,
    @SerializedName("YahooPlayerID")
    val yahooPlayerID: Int,
    @SerializedName("FanDuelName")
    val fanDuelName: String,
    @SerializedName("DraftKingsName")
    val draftKingsName: String,
    @SerializedName("YahooName")
    val yahooName: String,
    @SerializedName("DepthChartPosition")
    val depthChartPosition: String,
    @SerializedName("DepthChartOrder")
    val depthChartOrder: Int,
    @SerializedName("GlobalTeamID")
    val globalTeamID: Int,
    @SerializedName("FantasyDraftName")
    val fantasyDraftName: String,
    @SerializedName("FantasyDraftPlayerID")
    val fantasyDraftPlayerID: Int,
    @SerializedName("UsaTodayPlayerID")
    val usaTodayPlayerID: Int,
    @SerializedName("UsaTodayHeadshotUrl")
    val usaTodayHeadshotUrl: String,
    @SerializedName("UsaTodayHeadshotNoBackgroundUrl")
    val usaTodayHeadshotNoBackgroundUrl: String,
    @SerializedName("UsaTodayHeadshotUpdated")
    val usaTodayHeadshotUpdated: String,
    @SerializedName("UsaTodayHeadshotNoBackgroundUpdated")
    val usaTodayHeadshotNoBackgroundUpdated: String,
    @SerializedName("NbaDotComPlayerID")
    val nbaDotComPlayerID: Int
)

fun PlayerInfoDto.toPlayerInfo(): PlayerInfo {
    return PlayerInfo(
        nbaDotComPlayerID = this.nbaDotComPlayerID
    )
}