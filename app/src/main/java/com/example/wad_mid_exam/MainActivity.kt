package com.example.wad_mid_exam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.wad_mid_exam.data.DatabaseHelper
import com.example.wad_mid_exam.ui.StudentScreen
import com.example.wad_mid_exam.ui.theme.WadMidexamTheme

class MainActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        enableEdgeToEdge()
        setContent {
            WadMidexamTheme {
                StudentScreen(dbHelper = dbHelper)
            }
        }
    }
}
