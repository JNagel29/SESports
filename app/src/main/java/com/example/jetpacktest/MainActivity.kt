package com.example.jetpacktest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpacktest.navigation.AppNavigation
import com.example.jetpacktest.ui.theme.JetpackTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Instantiates nav bar (goes to Home too)
                    AppNavigation()
                }
            }
        }
    }
}

//For printing out our data, usable by any file (like Screens)
@Composable
fun ItemList(statData: String) {
    LazyColumn {
        item {
            Text(
                text = statData,
                modifier = Modifier.padding(16.dp) // Add padding for better spacing
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    JetpackTestTheme {
        ItemList("Preview Data")
    }
}