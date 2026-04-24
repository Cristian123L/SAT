package com.example.sat.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.sat.viewmodel.ContribuyenteViewModel

@Composable
fun AppNavigation(viewModel: ContribuyenteViewModel) {
    var pantallaActual by remember { mutableStateOf("lista") }
    // Guardamos el ID del contribuyente a editar (null = nuevo)
    var idAEditar by remember { mutableStateOf<Long?>(null) }

    when (pantallaActual) {
        "lista" -> {
            ListaContribuyentesScreen(
                viewModel = viewModel,
                onNavigateToForm = { id ->
                    idAEditar = id           // null si es nuevo, ID si es edición
                    pantallaActual = "formulario"
                }
            )
        }
        "formulario" -> {
            FormularioContribuyenteScreen(
                viewModel = viewModel,
                contribuyenteId = idAEditar, // Pasamos el ID al formulario
                onNavigateBack = {
                    idAEditar = null          // Limpiamos al regresar
                    pantallaActual = "lista"
                }
            )
        }
    }
}