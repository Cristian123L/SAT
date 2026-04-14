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

    // 2. Creamos nuestro propio entorno de corrutinas 100% compatible con Desktop y Android
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

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
        tipoPersona: String,
        rfc: String,
        nombreRazonSocial: String,
        correo: String,
        municipioId: Long,
        codigoPostal: String
    ) {
        val estadoId = _estadoSeleccionado.value?.id ?: return

        viewModelScope.launch {
            repository.agregarContribuyente(
                tipoPersona = tipoPersona,
                rfc = rfc,
                nombreRazonSocial = nombreRazonSocial,
                correo = correo,
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
}