package com.example.sat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sat.db.Municipio
import com.example.sat.viewmodel.ContribuyenteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioContribuyenteScreen(
    viewModel: ContribuyenteViewModel,
    onNavigateBack: () -> Unit
) {
    // Observamos los estados (StateFlow) desde el ViewModel
    val estados by viewModel.estados.collectAsState()
    val municipios by viewModel.municipiosFiltrados.collectAsState()
    val estadoSeleccionado by viewModel.estadoSeleccionado.collectAsState()

    // Variables locales para los TextFields
    var tipoPersona by remember { mutableStateOf("") }
    var rfc by remember { mutableStateOf("") }
    var nombreRazonSocial by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }

    // Variables para controlar si los ComboBox están abiertos
    var expandedEstado by remember { mutableStateOf(false) }
    var expandedMunicipio by remember { mutableStateOf(false) }
    var municipioSeleccionado by remember { mutableStateOf<Municipio?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Nuevo Contribuyente", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // TextFields básicos
        OutlinedTextField(value = tipoPersona, onValueChange = { tipoPersona = it }, label = { Text("Tipo de Persona (FÍSICA/MORAL)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = rfc, onValueChange = { rfc = it }, label = { Text("RFC") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = nombreRazonSocial, onValueChange = { nombreRazonSocial = it }, label = { Text("Nombre o Razón Social") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        // --- COMBOBOX: ESTADO ---
        ExposedDropdownMenuBox(
            expanded = expandedEstado,
            onExpandedChange = { expandedEstado = it }
        ) {
            OutlinedTextField(
                value = estadoSeleccionado?.nombre ?: "Seleccione un Estado",
                onValueChange = {},
                readOnly = true,
                label = { Text("Estado/Entidad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstado) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedEstado,
                onDismissRequest = { expandedEstado = false }
            ) {
                estados.forEach { estado ->
                    DropdownMenuItem(
                        text = { Text(estado.nombre) },
                        onClick = {
                            viewModel.onEstadoSeleccionado(estado)
                            municipioSeleccionado = null // Limpiamos el municipio si cambia el estado
                            expandedEstado = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- COMBOBOX: MUNICIPIO (Reactivo) ---
        ExposedDropdownMenuBox(
            expanded = expandedMunicipio,
            onExpandedChange = {
                // Solo se puede abrir si ya hay un estado seleccionado
                if (estadoSeleccionado != null) expandedMunicipio = it
            }
        ) {
            OutlinedTextField(
                value = municipioSeleccionado?.nombre ?: "Seleccione un Municipio",
                onValueChange = {},
                readOnly = true,
                label = { Text("Municipio o Alcaldía") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMunicipio) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                enabled = estadoSeleccionado != null // Se habilita según el estado
            )
            ExposedDropdownMenu(
                expanded = expandedMunicipio,
                onDismissRequest = { expandedMunicipio = false }
            ) {
                municipios.forEach { municipio ->
                    DropdownMenuItem(
                        text = { Text(municipio.nombre) },
                        onClick = {
                            municipioSeleccionado = municipio
                            expandedMunicipio = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = codigoPostal, onValueChange = { codigoPostal = it }, label = { Text("Código Postal") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (municipioSeleccionado != null) {
                    viewModel.guardarContribuyente(
                        tipoPersona, rfc, nombreRazonSocial, correo, municipioSeleccionado!!.id, codigoPostal
                    )
                    onNavigateBack() // Regresar a la lista tras guardar
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Contribuyente")
        }
    }
}