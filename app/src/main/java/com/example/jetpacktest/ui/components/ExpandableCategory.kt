package com.example.jetpacktest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpandableCategory(title: String, content: @Composable () -> Unit) {
    var expandedCategory by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {expandedCategory = !expandedCategory }
    ){
        Text(
            text = title,
            modifier = Modifier.padding(8.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = if (expandedCategory) Icons.Filled.ArrowDropUp
            else Icons.Filled.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
    AnimatedVisibility(visible = expandedCategory) {
        content() // Lambda of rows that we passed in for that specific category
    }
}