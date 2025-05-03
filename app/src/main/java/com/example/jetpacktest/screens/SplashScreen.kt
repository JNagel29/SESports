package com.example.jetpacktest.screens


import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.example.jetpacktest.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navigateToHomeScreen: () -> Unit) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    val opacity = remember {
        androidx.compose.animation.core.Animatable(1f)
    }
    var showImage by remember { mutableStateOf(true) }

    //AnimationEffect for Scale and Opacity
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1.75f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                }
            )
        )
        delay(2200L)
        opacity.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 300 // Fade-out duration
            )
        )
        showImage = false
        navigateToHomeScreen()
    }

    //Image
    if (showImage) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.sesports_2025),
                contentDescription = "Logo",
                modifier = Modifier
                    .scale(scale.value)
                    .graphicsLayer {
                        alpha = opacity.value
                    }
            )
        }
    }
}