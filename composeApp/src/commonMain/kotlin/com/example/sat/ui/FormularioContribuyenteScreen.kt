package com.example.sat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

    // 1. VARIABLES COMPARTIDAS BÁSICAS
    var tipoPersona by remember { mutableStateOf("FÍSICA") }
    var rfc by remember { mutableStateOf(contribuyenteAEditar?.rfc ?: "") }
    var correo by remember { mutableStateOf(contribuyenteAEditar?.correo_electronico ?: "") }
    var telefono by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf(contribuyenteAEditar?.codigo_postal ?: "") }

    // 2. VARIABLES EXCLUSIVAS DE FÍSICA
    var curp by remember { mutableStateOf("") }
    var nombreFisica by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }

    var expandedRegimen by remember { mutableStateOf(false) }
    var regimenSeleccionado by remember { mutableStateOf("") }
    val listaRegimenes = listOf("Sueldos y Salarios", "RESICO", "Actividades Empresariales", "Arrendamiento")

    // 3. VARIABLES EXCLUSIVAS DE MORAL
    var razonSocial by remember { mutableStateOf("") }
    var fechaConstitucion by remember { mutableStateOf("") }
    var rfcRepresentante by remember { mutableStateOf("") }
    var rfcSocios by remember { mutableStateOf("") }
    var numEscritura by remember { mutableStateOf("") }

    var expandedCapital by remember { mutableStateOf(false) }
    var capitalSeleccionado by remember { mutableStateOf("") }
    val listaRegimenCapital = listOf("S.A. de C.V.", "S. de R.L. de C.V.", "S.A.P.I. de C.V.", "S.C.", "A.C.")

    // 4. CATÁLOGOS COMPARTIDOS (Base de datos y Listas)
    var expandedEstado by remember { mutableStateOf(false) }
    var expandedMunicipio by remember { mutableStateOf(false) }
    var municipioSeleccionado by remember { mutableStateOf<Municipio?>(null) }

    var expandedVialidad by remember { mutableStateOf(false) }
    var vialidadSeleccionada by remember { mutableStateOf("") }
    val listaVialidades = listOf("Calle", "Avenida", "Calzada", "Bulevar", "Privada")

    var expandedActividad by remember { mutableStateOf(false) }
    var actividadSeleccionada by remember { mutableStateOf("") }
    val listaActividades = listOf("Comercio al por menor", "Servicios Profesionales", "Manufactura", "Tecnología", "Agricultura")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (contribuyenteAEditar == null) "Nuevo Contribuyente" else "Editar Contribuyente") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- SELECCIÓN DE TIPO DE PERSONA ---
            Text("Tipo de Persona:", style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                RadioButton(selected = tipoPersona == "FÍSICA", onClick = { tipoPersona = "FÍSICA" })
                Text("Física", modifier = Modifier.padding(end = 16.dp))
                RadioButton(selected = tipoPersona == "MORAL", onClick = { tipoPersona = "MORAL" })
                Text("Moral")
            }

            Divider(modifier = Modifier.padding(bottom = 16.dp))

            // ==========================================
            // SECCIÓN DINÁMICA (EXCLUSIVOS)
            // ==========================================
            if (tipoPersona == "FÍSICA") {
                Text("Datos de Persona Física", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = curp, onValueChange = { curp = it }, label = { Text("CURP (18 caracteres)") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(value = nombreFisica, onValueChange = { nombreFisica = it }, label = { Text("Nombre(s) y Apellidos") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha de Nacimiento (DD/MM/AAAA)") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))

                ExposedDropdownMenuBox(expanded = expandedRegimen, onExpandedChange = { expandedRegimen = it }, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    OutlinedTextField(value = regimenSeleccionado, onValueChange = {}, readOnly = true, label = { Text("Régimen Fiscal") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedRegimen) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = expandedRegimen, onDismissRequest = { expandedRegimen = false }) {
                        listaRegimenes.forEach { seleccion ->
                            DropdownMenuItem(text = { Text(seleccion) }, onClick = { regimenSeleccionado = seleccion; expandedRegimen = false })
                        }
                    }
                }
            } else {
                Text("Datos de Persona Moral", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = razonSocial, onValueChange = { razonSocial = it }, label = { Text("Denominación o Razón Social") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(value = fechaConstitucion, onValueChange = { fechaConstitucion = it }, label = { Text("Fecha de Constitución") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(value = rfcRepresentante, onValueChange = { rfcRepresentante = it }, label = { Text("RFC del Representante Legal") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(value = rfcSocios, onValueChange = { rfcSocios = it }, label = { Text("RFC Socios/Accionistas") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(value = numEscritura, onValueChange = { numEscritura = it }, label = { Text("Número de Escritura/Póliza") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))

                ExposedDropdownMenuBox(expanded = expandedCapital, onExpandedChange = { expandedCapital = it }, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    OutlinedTextField(value = capitalSeleccionado, onValueChange = {}, readOnly = true, label = { Text("Régimen Capital") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCapital) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = expandedCapital, onDismissRequest = { expandedCapital = false }) {
                        listaRegimenCapital.forEach { seleccion ->
                            DropdownMenuItem(text = { Text(seleccion) }, onClick = { capitalSeleccionado = seleccion; expandedCapital = false })
                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(bottom = 16.dp))

            // ==========================================
            // SECCIÓN COMPARTIDA
            // ==========================================
            Text("Datos Compartidos y Domicilio", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = rfc, onValueChange = { rfc = it }, label = { Text("RFC del Contribuyente") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono (10 dígitos)") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))

            // Catálogo Compartido: Actividad Económica
            ExposedDropdownMenuBox(expanded = expandedActividad, onExpandedChange = { expandedActividad = it }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                OutlinedTextField(value = actividadSeleccionada, onValueChange = {}, readOnly = true, label = { Text("Actividad Económica") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedActividad) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                ExposedDropdownMenu(expanded = expandedActividad, onDismissRequest = { expandedActividad = false }) {
                    listaActividades.forEach { act -> DropdownMenuItem(text = { Text(act) }, onClick = { actividadSeleccionada = act; expandedActividad = false }) }
                }
            }

            // Catálogo Compartido: Tipo de Vialidad
            ExposedDropdownMenuBox(expanded = expandedVialidad, onExpandedChange = { expandedVialidad = it }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                OutlinedTextField(value = vialidadSeleccionada, onValueChange = {}, readOnly = true, label = { Text("Tipo de Vialidad") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedVialidad) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                ExposedDropdownMenu(expanded = expandedVialidad, onDismissRequest = { expandedVialidad = false }) {
                    listaVialidades.forEach { via -> DropdownMenuItem(text = { Text(via) }, onClick = { vialidadSeleccionada = via; expandedVialidad = false }) }
                }
            }

            // Catálogos Reactivos de Estado y Municipio
            ExposedDropdownMenuBox(expanded = expandedEstado, onExpandedChange = { expandedEstado = it }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                OutlinedTextField(value = estadoSeleccionado?.nombre ?: "Seleccione un Estado", onValueChange = {}, readOnly = true, label = { Text("Estado/Entidad") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedEstado) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                ExposedDropdownMenu(expanded = expandedEstado, onDismissRequest = { expandedEstado = false }) {
                    estados.forEach { estado -> DropdownMenuItem(text = { Text(estado.nombre) }, onClick = { viewModel.onEstadoSeleccionado(estado); municipioSeleccionado = null; expandedEstado = false }) }
                }
            }

            ExposedDropdownMenuBox(expanded = expandedMunicipio, onExpandedChange = { if (estadoSeleccionado != null) expandedMunicipio = it }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                OutlinedTextField(value = municipioSeleccionado?.nombre ?: "Seleccione un Municipio", onValueChange = {}, readOnly = true, label = { Text("Municipio o Alcaldía") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedMunicipio) }, modifier = Modifier.menuAnchor().fillMaxWidth(), enabled = estadoSeleccionado != null)
                ExposedDropdownMenu(expanded = expandedMunicipio, onDismissRequest = { expandedMunicipio = false }) {
                    municipios.forEach { municipio -> DropdownMenuItem(text = { Text(municipio.nombre) }, onClick = { municipioSeleccionado = municipio; expandedMunicipio = false }) }
                }
            }

            OutlinedTextField(value = codigoPostal, onValueChange = { codigoPostal = it }, label = { Text("Código Postal") }, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp))

            // ==========================================
            // BOTÓN GUARDAR
            // ==========================================
            Button(
                onClick = {
                    if (municipioSeleccionado != null) {
                        // Decidimos qué nombre enviar dependiendo del tipo de persona
                        val nombreFinal = if (tipoPersona == "FÍSICA") nombreFisica else razonSocial

                        if (contribuyenteAEditar == null) {
                            // Modo: NUEVO CONTRIBUYENTE
                            viewModel.guardarContribuyente(
                                tipoPersona = tipoPersona,
                                rfc = rfc,
                                nombreRazonSocial = nombreFinal,
                                correo = correo,
                                telefono = telefono,
                                curp = curp,
                                fechaNacimiento = fechaNacimiento,
                                regimenFiscal = regimenSeleccionado,
                                fechaConstitucion = fechaConstitucion,
                                rfcRepresentante = rfcRepresentante,
                                rfcSocios = rfcSocios,
                                numEscritura = numEscritura,
                                regimenCapital = capitalSeleccionado,
                                vialidad = vialidadSeleccionada,
                                actividadEconomica = actividadSeleccionada,
                                municipioId = municipioSeleccionado!!.id,
                                codigoPostal = codigoPostal
                            )
                        } else {
                            // (Si tienes tu función actualizarContribuyente lista, iría aquí con los mismos parámetros)
                            // Por ahora, con guardarContribuyente ya podemos probar la creación.
                        }
                        onNavigateBack() // Regresa a la pantalla anterior
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(if (contribuyenteAEditar == null) "Guardar Contribuyente" else "Actualizar Contribuyente")
            }
        }
    }
}