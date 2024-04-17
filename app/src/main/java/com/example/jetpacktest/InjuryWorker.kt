package com.example.jetpacktest

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InjuryWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {
    private val databaseHandler = DatabaseHandler()
    private val injuryApiBaseUrl = "https://api.sportsdata.io/v3/nba/projections/json/"
    override suspend fun doWork(): Result {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(injuryApiBaseUrl)
            .build()
            .create(ApiInterface::class.java)
        //Fetch list of injuredPlayers
        Log.d("InjuryWorker", "InjuryWorker is active, calling API")
        val injuredPlayers = retrofitBuilder.getInjuredPlayers(Keys.SPORTS_DATA_IO_KEY)
        //Store in database if found
        injuredPlayers.let {
            databaseHandler.executeStoreInjured(injuredPlayers = injuredPlayers)
        }
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}