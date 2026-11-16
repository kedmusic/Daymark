package com.daymark.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.daymark.app.navigation.AppNavGraph
import com.daymark.app.ui.theme.DaymarkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DaymarkTheme {
                AppNavGraph()
            }
        }
    }
}
