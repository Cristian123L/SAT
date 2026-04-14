package com.example.sat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sat.viewmodel.ContribuyenteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaContribuyentesScreen(
    viewModel: ContribuyenteViewModel,
    onNavigateToForm: () -> Unit
) {
    val contribuyentes by viewModel.contribuyentes.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToForm) {
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
                            IconButton(onClick = { viewModel.eliminarContribuyente(contribuyente.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}