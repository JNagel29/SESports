package com.example.jetpacktest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ReturnToPreviousHeader(
    navigateBack: () -> Unit,
    label: String? = "Previous",
    color: Color = Color.White
) {
    Column(
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(color)
                .fillMaxWidth()
                .height(50.dp)
                .clickable {
                    navigateBack()
                }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.padding(start = 5.dp)
            )
            /*
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Return to $label",
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif
            )
             */
        }
    }
}


