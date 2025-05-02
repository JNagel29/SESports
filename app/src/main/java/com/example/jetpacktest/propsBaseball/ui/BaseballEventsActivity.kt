package com.example.jetpacktest.propsBaseball.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.jetpacktest.propsBaseball.ui.BaseballEventsScreen
import com.example.jetpacktest.ui.theme.JetpackTestTheme

class BaseballEventsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackTestTheme {
                BaseballEventsScreen()
            }
        }
    }
}
