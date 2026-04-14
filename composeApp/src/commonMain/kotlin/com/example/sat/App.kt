package com.example.sat

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.sat.db.AppDatabase
import com.example.sat.db.ContribuyenteRepository
import com.example.sat.db.DatabaseDriverFactory
import com.example.sat.ui.AppNavigation
import com.example.sat.viewmodel.ContribuyenteViewModel

@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    MaterialTheme {
        // 1. Crear el Driver de la base de datos (se adapta según la plataforma)
        val driver = driverFactory.createDriver()

        // 2. Inicializar la Base de Datos con las tablas generadas
        val database = AppDatabase(driver)

        // 3. Crear el Repositorio (la capa de datos)
        val repository = ContribuyenteRepository(database)

        // 4. Crear el ViewModel (la capa lógica y reactiva)
        val viewModel = ContribuyenteViewModel(repository)

        // 5. Iniciar la Navegación (la interfaz gráfica)
        AppNavigation(viewModel = viewModel)
    }
}