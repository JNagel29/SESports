package com.example.jetpacktest

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() // Gives info for Hilt to work, required boilerplate