package com.example.agenda360lite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.agenda360lite.navigation.AppNavGraph
import com.example.agenda360lite.ui.theme.Agenda360LiteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Agenda360LiteTheme {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}
