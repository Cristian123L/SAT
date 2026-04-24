package com.example.sat.viewmodel

import com.example.sat.db.Contribuyente
import com.example.sat.db.ContribuyenteRepository
import com.example.sat.db.Estado
import com.example.sat.db.Municipio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 1. Ya NO heredamos de ViewModel() de Android
class ContribuyenteViewModel(private val repository: ContribuyenteRepository) {

    // 2. Creamos nuestro propio entorno de corrutinas compatible con Desktop y Android
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())


    /**
     * Configuramos que cuando al iniciar la aplicacion se llenen los combobox con los estados,
     * y posteriormente los municipios en base al estado seleccionado
     * Con verificaciones para saber si ya estan llenadas las tablas de estados y municipios
     * o en caso de que esten vacias las poblamos con los datos corespondientes
     */
    init {
        viewModelScope.launch {
            // Cuando la app inicie, revisamos y llenamos los estados si es necesario
            repository.poblarEstadosSiEstanVacios()
        }
    }

    init {
        viewModelScope.launch {
//            Primero debemos asegurarnos de que los estados existan
//            Si no, no tendria sentido seguir
            repository.poblarEstadosSiEstanVacios()

//             Si sí estan los estados(lo normal), procedemos con llenar los municipios
            repository.poblarMunicipiosSiEstanVacios()
        }
    }
    // ==========================================
    // 1. ESTADO DE LA UI
    // ==========================================

    val estados: StateFlow<List<Estado>> = repository.getEstados()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _estadoSeleccionado = MutableStateFlow<Estado?>(null)
    val estadoSeleccionado: StateFlow<Estado?> = _estadoSeleccionado

    private val _contribuyenteEditando = MutableStateFlow<Contribuyente?>(null)
    val contribuyenteEditando: StateFlow<Contribuyente?> = _contribuyenteEditando


    @OptIn(ExperimentalCoroutinesApi::class)
    val municipiosFiltrados: StateFlow<List<Municipio>> = _estadoSeleccionado
        .flatMapLatest { estado ->
            if (estado != null) {
                repository.getMunicipiosPorEstado(estado.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val contribuyentes: StateFlow<List<Contribuyente>> = repository.getContribuyentes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ==========================================
    // 2. ACCIONES
    // ==========================================

    fun onEstadoSeleccionado(estado: Estado) {
        _estadoSeleccionado.value = estado
    }

    fun guardarContribuyente(
        tipoPersona: String, rfc: String, nombreRazonSocial: String, correo: String,
        telefono: String, curp: String, fechaNacimiento: String, regimenFiscal: String,
        fechaConstitucion: String, rfcRepresentante: String, rfcSocios: String, numEscritura: String,
        regimenCapital: String, vialidad: String, actividadEconomica: String,
        municipioId: Long, codigoPostal: String
    ) {
        // Obtenemos el estado actualmente seleccionado
        val estadoId = _estadoSeleccionado.value?.id ?: return

        viewModelScope.launch {
            repository.agregarContribuyente(
                tipoPersona = tipoPersona,
                rfc = rfc,
                nombreRazonSocial = nombreRazonSocial,
                correo = correo,
                telefono = telefono.ifBlank { null }, // Si está vacío, manda null

                // --- LÓGICA INTELIGENTE ---
                // Si es Física, manda sus datos. Si es Moral, los anula.
                curp = if (tipoPersona == "FÍSICA") curp else null,
                fechaNacimiento = if (tipoPersona == "FÍSICA") fechaNacimiento else null,
                regimenFiscal = if (tipoPersona == "FÍSICA") regimenFiscal else null,

                // Si es Moral, manda sus datos. Si es Física, los anula.
                fechaConstitucion = if (tipoPersona == "MORAL") fechaConstitucion else null,
                rfcRepresentante = if (tipoPersona == "MORAL") rfcRepresentante else null,
                rfcSocios = if (tipoPersona == "MORAL") rfcSocios else null,
                numEscritura = if (tipoPersona == "MORAL") numEscritura else null,
                regimenCapital = if (tipoPersona == "MORAL") regimenCapital else null,

                // Datos compartidos que siempre se mandan
                vialidad = vialidad,
                actividadEconomica = actividadEconomica,
                estadoId = estadoId,
                municipioId = municipioId,
                codigoPostal = codigoPostal
            )
        }
    }

    fun eliminarContribuyente(id: Long) {
        viewModelScope.launch {
            repository.eliminarContribuyente(id)
        }
    }

    fun actualizarContribuyente(
        id: Long, tipoPersona: String, rfc: String, nombreRazonSocial: String,
        correo: String, telefono: String, curp: String, fechaNacimiento: String,
        regimenFiscal: String, fechaConstitucion: String, rfcRepresentante: String,
        rfcSocios: String, numEscritura: String, regimenCapital: String,
        vialidad: String, actividadEconomica: String,
        estadoId: Long,        // ← Recibe estadoId directo, ya no lo lee del StateFlow
        municipioId: Long, codigoPostal: String
    ) {
        viewModelScope.launch {
            repository.actualizarContribuyente(
                id = id,
                tipoPersona = tipoPersona,
                rfc = rfc,
                nombreRazonSocial = nombreRazonSocial,
                correo = correo,
                telefono = telefono.ifBlank { null },
                curp = if (tipoPersona == "FÍSICA") curp else null,
                fechaNacimiento = if (tipoPersona == "FÍSICA") fechaNacimiento else null,
                regimenFiscal = if (tipoPersona == "FÍSICA") regimenFiscal else null,
                fechaConstitucion = if (tipoPersona == "MORAL") fechaConstitucion else null,
                rfcRepresentante = if (tipoPersona == "MORAL") rfcRepresentante else null,
                rfcSocios = if (tipoPersona == "MORAL") rfcSocios else null,
                numEscritura = if (tipoPersona == "MORAL") numEscritura else null,
                regimenCapital = if (tipoPersona == "MORAL") regimenCapital else null,
                vialidad = vialidad,
                actividadEconomica = actividadEconomica,
                estadoId = estadoId,           // ← Usa el que viene del parámetro
                municipioId = municipioId,
                codigoPostal = codigoPostal
            )
        }
    }

    fun cargarContribuyenteParaEditar(id: Long) {
        viewModelScope.launch {
            _contribuyenteEditando.value = repository.getContribuyentePorId(id)
        }
    }

    fun limpiarEdicion() {
        _contribuyenteEditando.value = null
        _estadoSeleccionado.value = null
    }

}