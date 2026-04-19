package com.example.sat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sat.db.Municipio
import com.example.sat.viewmodel.ContribuyenteViewModel
import com.example.sat.db.Contribuyente

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioContribuyenteScreen(
    viewModel: ContribuyenteViewModel,
    contribuyenteAEditar: Contribuyente? = null,
    onNavigateBack: () -> Unit
) {
    val estados by viewModel.estados.collectAsState()
    val municipios by viewModel.municipiosFiltrados.collectAsState()
    val estadoSeleccionado by viewModel.estadoSeleccionado.collectAsState()

    var tipoPersona by remember { mutableStateOf(contribuyenteAEditar?.tipo_persona ?: "") }
    var rfc by remember { mutableStateOf(contribuyenteAEditar?.rfc ?: "") }
    var nombreRazonSocial by remember { mutableStateOf(contribuyenteAEditar?.nombre_razon_social ?: "") }
    var correo by remember { mutableStateOf(contribuyenteAEditar?.correo_electronico ?: "") }
    var codigoPostal by remember { mutableStateOf(contribuyenteAEditar?.codigo_postal ?: "") }

    var expandedEstado by remember { mutableStateOf(false) }
    var expandedMunicipio by remember { mutableStateOf(false) }
    var municipioSeleccionado by remember { mutableStateOf<Municipio?>(null) }

    // Envolvemos el codigo con un scaffold, para que en nuestro topBar aparezca el boton de navegacion hacia atras
    Scaffold(
        topBar = {
            TopAppBar(
                // El título cambia según si estamos creando o editando
                title = {
                    Text(if (contribuyenteAEditar == null) "Nuevo Contribuyente" else "Editar Contribuyente")
                },
                // Aquí está el botón de regresar (la flecha)
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->

        // LA COLUMNA AHORA RECIBE EL PADDING DEL SCAFFOLD
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // <- Esto evita que la Navbar tape tus campos de texto
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // <- Permite deslizar hacia abajo
        ) {

            // TextFields básicos
            OutlinedTextField(value = tipoPersona, onValueChange = { tipoPersona = it }, label = { Text("Tipo de Persona (FÍSICA/MORAL)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = rfc, onValueChange = { rfc = it }, label = { Text("RFC") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
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
                    enabled = estadoSeleccionado != null
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = codigoPostal, onValueChange = { codigoPostal = it }, label = { Text("Código Postal") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (municipioSeleccionado != null) {
                        if (contribuyenteAEditar == null) {
                            viewModel.guardarContribuyente(
                                tipoPersona, rfc, nombreRazonSocial, correo, municipioSeleccionado!!.id, codigoPostal
                            )
                        } else {
                            viewModel.actualizarContribuyente(
                                id = contribuyenteAEditar.id,
                                tipoPersona = tipoPersona,
                                rfc = rfc,
                                nombreRazonSocial = nombreRazonSocial,
                                correo = correo,
                                municipioId = municipioSeleccionado!!.id,
                                codigoPostal = codigoPostal
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (contribuyenteAEditar == null) "Guardar Contribuyente" else "Actualizar Contribuyente")
            }
        }
    }
}