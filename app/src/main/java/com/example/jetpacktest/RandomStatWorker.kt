package com.example.jetpacktest

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class RandomStatWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    private val sharedPreferences = applicationContext.getSharedPreferences("prefs", MODE_PRIVATE)
    private val databaseHandler = DatabaseHandler()
    private var randomStat = ""
    override suspend fun doWork(): Result {
        Log.d("RandomStatWorker", "Fetching new random stat...")

        //Gets random index from 101-112
        val randomIndex = (101 until 113).random()
        databaseHandler.executeRandomStat(randomIndex) {data ->
            randomStat = data
            saveRandomStatToSharedPreferences(randomStat)
        }
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private fun saveRandomStatToSharedPreferences(randomStat: String) {
        sharedPreferences.edit()
            .putString(KEY_RANDOM_STAT, randomStat)
            .apply()
    }
    companion object {
        const val KEY_RANDOM_STAT = "KEY_RANDOM_STAT"
    }

}