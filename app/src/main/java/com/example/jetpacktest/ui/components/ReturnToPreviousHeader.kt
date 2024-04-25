package com.example.jetpacktest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.jetpacktest.R

@Composable
fun ReturnToPreviousHeader(
    navigateBack: () -> Unit,
    label: String? = "Previous"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.White)
                .clickable {
                    navigateBack()
                }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.baseline_arrow_back_ios_new_24),
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


