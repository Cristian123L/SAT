package com.example.sat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sat.db.DatabaseDriverFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // En Android, el Driver necesita el "Context" (this) para crear el archivo .db
        val driverFactory = DatabaseDriverFactory(this)

        setContent {
            // Llamamos a nuestra App compartida pasándole el Driver de Android
            App(driverFactory = driverFactory)
        }
    }
}