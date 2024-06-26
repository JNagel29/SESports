package com.example.jetpacktest

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.jetpacktest.navigation.AppNavigation
import com.example.jetpacktest.ui.theme.JetpackTestTheme
import com.jakewharton.threetenabp.AndroidThreeTen
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    companion object {
        private const val RANDOM_STAT_TAG = "RandomStatWork"
        private const val INJURY_TAG = "InjuryWork"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        //Debugs memory leaks
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build()
        )
         */
        //For obtaining random stat (if new day has passed)
        scheduleRandomStatWork()
        sharedPreferences = applicationContext.getSharedPreferences("prefs", MODE_PRIVATE)
        val randomStat = sharedPreferences.getString(RandomStatWorker.KEY_RANDOM_STAT, "test") ?: ""
        //For storing new injury data (if week has passed)
        scheduleInjuredWork()
        //Initializes timezone used in GamesScreen.kt
        AndroidThreeTen.init(this)
        setContent {
            JetpackTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Instantiates nav bar (goes to Home too)
                    AppNavigation(randomStat)
                }
            }
        }
    }

    private fun scheduleRandomStatWork() {
        //Sets up and submits work request, only runs every 24 hours
        val randomStatRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<RandomStatWorker>(24, TimeUnit.HOURS)
                .build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniquePeriodicWork(
            RANDOM_STAT_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            randomStatRequest
        )
    }

    private fun scheduleInjuredWork() {
        //Sets up and submits work request, only runs every week
        val injuredPlayerRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<InjuryWorker>(7, TimeUnit.DAYS)
                .build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniquePeriodicWork(
            INJURY_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            injuredPlayerRequest
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    JetpackTestTheme {
        AppNavigation(randomStat = "Placeholder")
    }
}