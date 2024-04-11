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
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.jetpacktest.navigation.AppNavigation
import com.example.jetpacktest.ui.theme.JetpackTestTheme
import com.jakewharton.threetenabp.AndroidThreeTen
import java.util.concurrent.ExecutionException
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
        //Updates new randomStat value if new day has passed and fetches
        //TODO: Might not need this helper, will test tomorrow, also need to make index async
        /*
        if(!isWorkScheduled()) {
            //scheduleRandomStatWork()
        }
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

    private fun isWorkScheduled(): Boolean {
        //Checks if there's not already an instance scheduled
        val instance = WorkManager.getInstance(applicationContext)
        val statuses = instance.getWorkInfosByTag(RANDOM_STAT_TAG)
        return try {
            var running = false
            val workInfoList = statuses.get()
            for (workInfo in workInfoList) {
                val state = workInfo.state
                running = (state == WorkInfo.State.RUNNING) or (state == WorkInfo.State.ENQUEUED)
            }
            running
        } catch (e: ExecutionException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    JetpackTestTheme {
        AppNavigation(randomStat = "Placeholder")
    }
}