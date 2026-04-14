package com.example.sat.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.sat.viewmodel.ContribuyenteViewModel

@Composable
fun AppNavigation(viewModel: ContribuyenteViewModel) {
    // Usamos el sistema de estados nativo de Compose.
    // Es 100% seguro y multiplataforma.
    var pantallaActual by remember { mutableStateOf("lista") }

    when (pantallaActual) {
        "lista" -> {
            ListaContribuyentesScreen(
                viewModel = viewModel,
                onNavigateToForm = { pantallaActual = "formulario" } // Cambia el estado a formulario
            )
        }
        "formulario" -> {
            FormularioContribuyenteScreen(
                viewModel = viewModel,
                onNavigateBack = { pantallaActual = "lista" } // Cambia el estado de regreso a lista
            )
        }
    }
}