package com.example.sat

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.sat.db.DatabaseDriverFactory

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "SAT Contribuyentes") {

        // En Desktop, el Driver no necesita contexto, solo crea el archivo localmente
        val driverFactory = DatabaseDriverFactory()

        // Llamamos a nuestra App compartida pasándole el Driver de Desktop
        App(driverFactory = driverFactory)
    }
}