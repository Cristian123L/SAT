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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioContribuyenteScreen(
    viewModel: ContribuyenteViewModel,
    contribuyenteId: Long? = null,   // ← Cambiamos: ahora recibe ID, no el objeto
    onNavigateBack: () -> Unit
) {
    val esEdicion = contribuyenteId != null
    val estados by viewModel.estados.collectAsState()
    val municipios by viewModel.municipiosFiltrados.collectAsState()
    val estadoSeleccionado by viewModel.estadoSeleccionado.collectAsState()

    // Cargamos los datos del contribuyente si estamos editando
    LaunchedEffect(contribuyenteId) {
        if (contribuyenteId != null) {
            viewModel.cargarContribuyenteParaEditar(contribuyenteId)
        } else {
            viewModel.limpiarEdicion()
        }
    }

    // Leemos el contribuyente que el ViewModel cargó
    val contribuyenteEditando by viewModel.contribuyenteEditando.collectAsState()

    // ── Variables del formulario ──────────────────────────────────────────
    var tipoPersona by remember { mutableStateOf("FÍSICA") }
    var rfc by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }

    // Física
    var curp by remember { mutableStateOf("") }
    var nombreFisica by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var regimenSeleccionado by remember { mutableStateOf("") }
    var expandedRegimen by remember { mutableStateOf(false) }
    val listaRegimenes = listOf(
        "Sueldos y Salarios", "RESICO",
        "Actividades Empresariales", "Arrendamiento"
    )

    // Moral
    var razonSocial by remember { mutableStateOf("") }
    var fechaConstitucion by remember { mutableStateOf("") }
    var rfcRepresentante by remember { mutableStateOf("") }
    var rfcSocios by remember { mutableStateOf("") }
    var numEscritura by remember { mutableStateOf("") }
    var capitalSeleccionado by remember { mutableStateOf("") }
    var expandedCapital by remember { mutableStateOf(false) }
    val listaRegimenCapital = listOf(
        "S.A. de C.V.", "S. de R.L. de C.V.",
        "S.A.P.I. de C.V.", "S.C.", "A.C."
    )

    // Compartidos
    var expandedEstado by remember { mutableStateOf(false) }
    var expandedMunicipio by remember { mutableStateOf(false) }
    var municipioSeleccionado by remember { mutableStateOf<Municipio?>(null) }
    var vialidadSeleccionada by remember { mutableStateOf("") }
    var expandedVialidad by remember { mutableStateOf(false) }
    val listaVialidades = listOf("Calle", "Avenida", "Calzada", "Bulevar", "Privada")
    var actividadSeleccionada by remember { mutableStateOf("") }
    var expandedActividad by remember { mutableStateOf(false) }
    val listaActividades = listOf(
        "Comercio al por menor", "Servicios Profesionales",
        "Manufactura", "Tecnología", "Agricultura"
    )

    // ── Precargar campos cuando llegan los datos del ViewModel ────────────
    LaunchedEffect(contribuyenteEditando, estados, municipios) {
        val c = contribuyenteEditando ?: return@LaunchedEffect

        tipoPersona = c.tipo_persona
        rfc = c.rfc
        correo = c.correo_electronico
        telefono = c.telefono ?: ""
        codigoPostal = c.codigo_postal

        // Física
        curp = c.curp ?: ""
        nombreFisica = if (c.tipo_persona == "FÍSICA") c.nombre_razon_social else ""
        fechaNacimiento = c.fecha_nacimiento ?: ""
        regimenSeleccionado = c.regimen_fiscal ?: ""

        // Moral
        razonSocial = if (c.tipo_persona == "MORAL") c.nombre_razon_social else ""
        fechaConstitucion = c.fecha_constitucion ?: ""
        rfcRepresentante = c.rfc_representante ?: ""
        rfcSocios = c.rfc_socios ?: ""
        numEscritura = c.num_escritura ?: ""
        capitalSeleccionado = c.regimen_capital ?: ""

        // Compartidos
        vialidadSeleccionada = c.vialidad ?: ""
        actividadSeleccionada = c.actividad_economica ?: ""

        // Estado y Municipio — buscamos el objeto completo por ID
        val estadoEncontrado = estados.find { it.id == c.estado_id }
        if (estadoEncontrado != null) {
            viewModel.onEstadoSeleccionado(estadoEncontrado)
        }
        // El municipio lo buscamos en la lista ya filtrada por estado
        municipioSeleccionado = municipios.find { it.id == c.municipio_id }
    }

    // Si el municipio aún no está porque los municipios cargaron después del estado,
    // lo buscamos de nuevo cuando la lista de municipios se actualice
    LaunchedEffect(municipios) {
        val c = contribuyenteEditando ?: return@LaunchedEffect
        if (municipioSeleccionado == null) {
            municipioSeleccionado = municipios.find { it.id == c.municipio_id }
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (esEdicion) "Editar Contribuyente" else "Nuevo Contribuyente")
                },
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
            // Tipo de Persona
            Text("Tipo de Persona:", style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                RadioButton(
                    selected = tipoPersona == "FÍSICA",
                    onClick = { tipoPersona = "FÍSICA" }
                )
                Text("Física", modifier = Modifier.padding(end = 16.dp))
                RadioButton(
                    selected = tipoPersona == "MORAL",
                    onClick = { tipoPersona = "MORAL" }
                )
                Text("Moral")
            }

            Divider(modifier = Modifier.padding(bottom = 16.dp))

            // ── Sección dinámica ──────────────────────────────────────────
            if (tipoPersona == "FÍSICA") {
                Text("Datos de Persona Física",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(curp, { curp = it },
                    label = { Text("CURP (18 caracteres)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(nombreFisica, { nombreFisica = it },
                    label = { Text("Nombre(s) y Apellidos") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(fechaNacimiento, { fechaNacimiento = it },
                    label = { Text("Fecha de Nacimiento (DD/MM/AAAA)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedRegimen,
                    onExpandedChange = { expandedRegimen = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        value = regimenSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Régimen Fiscal") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedRegimen) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRegimen,
                        onDismissRequest = { expandedRegimen = false }
                    ) {
                        listaRegimenes.forEach { seleccion ->
                            DropdownMenuItem(
                                text = { Text(seleccion) },
                                onClick = { regimenSeleccionado = seleccion; expandedRegimen = false }
                            )
                        }
                    }
                }
            } else {
                Text("Datos de Persona Moral",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(razonSocial, { razonSocial = it },
                    label = { Text("Denominación o Razón Social") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(fechaConstitucion, { fechaConstitucion = it },
                    label = { Text("Fecha de Constitución") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(rfcRepresentante, { rfcRepresentante = it },
                    label = { Text("RFC del Representante Legal") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(rfcSocios, { rfcSocios = it },
                    label = { Text("RFC Socios/Accionistas") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(numEscritura, { numEscritura = it },
                    label = { Text("Número de Escritura/Póliza") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedCapital,
                    onExpandedChange = { expandedCapital = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        value = capitalSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Régimen Capital") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCapital) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCapital,
                        onDismissRequest = { expandedCapital = false }
                    ) {
                        listaRegimenCapital.forEach { seleccion ->
                            DropdownMenuItem(
                                text = { Text(seleccion) },
                                onClick = { capitalSeleccionado = seleccion; expandedCapital = false }
                            )
                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(bottom = 16.dp))

            // ── Sección compartida ────────────────────────────────────────
            Text("Datos Compartidos y Domicilio",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(rfc, { rfc = it },
                label = { Text("RFC del Contribuyente") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            OutlinedTextField(correo, { correo = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            OutlinedTextField(telefono, { telefono = it },
                label = { Text("Teléfono (10 dígitos)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))

            ExposedDropdownMenuBox(
                expanded = expandedActividad,
                onExpandedChange = { expandedActividad = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                OutlinedTextField(
                    value = actividadSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Actividad Económica") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedActividad) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedActividad,
                    onDismissRequest = { expandedActividad = false }
                ) {
                    listaActividades.forEach { act ->
                        DropdownMenuItem(
                            text = { Text(act) },
                            onClick = { actividadSeleccionada = act; expandedActividad = false }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expandedVialidad,
                onExpandedChange = { expandedVialidad = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                OutlinedTextField(
                    value = vialidadSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Vialidad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedVialidad) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedVialidad,
                    onDismissRequest = { expandedVialidad = false }
                ) {
                    listaVialidades.forEach { via ->
                        DropdownMenuItem(
                            text = { Text(via) },
                            onClick = { vialidadSeleccionada = via; expandedVialidad = false }
                        )
                    }
                }
            }

            // Estado — reactivo
            ExposedDropdownMenuBox(
                expanded = expandedEstado,
                onExpandedChange = { expandedEstado = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                OutlinedTextField(
                    value = estadoSeleccionado?.nombre ?: "Seleccione un Estado",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado/Entidad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedEstado) },
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
                                municipioSeleccionado = null  // Reset municipio al cambiar estado
                                expandedEstado = false
                            }
                        )
                    }
                }
            }

            // Municipio — filtrado por estado
            ExposedDropdownMenuBox(
                expanded = expandedMunicipio,
                onExpandedChange = { if (estadoSeleccionado != null) expandedMunicipio = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                OutlinedTextField(
                    value = municipioSeleccionado?.nombre ?: "Seleccione un Municipio",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Municipio o Alcaldía") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedMunicipio) },
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
                            onClick = { municipioSeleccionado = municipio; expandedMunicipio = false }
                        )
                    }
                }
            }

            OutlinedTextField(codigoPostal, { codigoPostal = it },
                label = { Text("Código Postal") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp))

            // ── Botón Guardar / Actualizar ────────────────────────────────
            Button(
                onClick = {
                    if (municipioSeleccionado != null && estadoSeleccionado != null) {
                        val nombreFinal = if (tipoPersona == "FÍSICA") nombreFisica else razonSocial

                        if (esEdicion) {
                            viewModel.actualizarContribuyente(
                                id = contribuyenteId!!,
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
                                estadoId = estadoSeleccionado!!.id,  // ← directo del ComboBox
                                municipioId = municipioSeleccionado!!.id,
                                codigoPostal = codigoPostal
                            )
                        } else {
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
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(if (esEdicion) "Actualizar Contribuyente" else "Guardar Contribuyente")
            }
        }
    }
}