package com.example.sat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sat.viewmodel.ContribuyenteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaContribuyentesScreen(
    viewModel: ContribuyenteViewModel,
    onNavigateToForm: (Long?) -> Unit
) {
    val contribuyentes by viewModel.contribuyentes.collectAsState()

    // Vamos a agregar una alerta para que el boton de borrar no sea presionar y matar
    // El interruptor de la alerta (Inicia apagado / false)
    var mostrarAlerta by remember { mutableStateOf(false) }
    // La memoria de a quién vamos a borrar (Inicia vacía / null)
    var idRegistroABorrar by remember { mutableStateOf<Long?>(null) }

    // Dibujamos la alerta (Solo si el interruptor está encendido)
    if (mostrarAlerta) {
        AlertDialog(
            onDismissRequest = {
                // Si el usuario toca fuera de la caja, se cancela
                mostrarAlerta = false
                idRegistroABorrar = null
            },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar a este contribuyente? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Sí confirma: Borramos usando el ID guardado y apagamos la alerta
                        idRegistroABorrar?.let { viewModel.eliminarContribuyente(it) }
                        mostrarAlerta = false
                        idRegistroABorrar = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // Sí cancela: Solo apagamos la alerta
                        mostrarAlerta = false
                        idRegistroABorrar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToForm(null) }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Text(
                "Contribuyentes Registrados",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(contribuyentes) { contribuyente ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(contribuyente.nombre_razon_social, style = MaterialTheme.typography.titleMedium)
                                Text("RFC: ${contribuyente.rfc}", style = MaterialTheme.typography.bodyMedium)
                                Text("Tipo: ${contribuyente.tipo_persona}", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { onNavigateToForm(contribuyente.id) }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                            }

                            // Modificamos el boton de eliminar que teniamos para que funcione ahora con la alerta que diujamos
                            IconButton(
                                onClick = {
                                    // Guardamos el ID del registro que tocaron
                                    idRegistroABorrar = contribuyente.id
                                    // Prendemos el interruptor de la alerta
                                    mostrarAlerta = true
                                }
                            ) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}