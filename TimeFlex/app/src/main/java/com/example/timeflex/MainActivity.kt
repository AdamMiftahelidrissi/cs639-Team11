package com.example.timeflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.timeflex.ui.MyApp
import com.example.timeflex.ui.theme.TimeFlexTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // App Theme
            TimeFlexTheme {
                // A surface container using the 'background' color from the theme
                MyApp()
            }
        }
    }
}