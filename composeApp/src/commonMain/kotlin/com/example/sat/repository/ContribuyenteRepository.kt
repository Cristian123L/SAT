package com.example.sat.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class ContribuyenteRepository(database: AppDatabase) {

    // Accedemos a las consultas generadas por SQLDelight
    private val queries = database.appDatabaseQueries

    // --- Catálogos ---

    /**
     * Obtiene todos los estados como un Flow para observar cambios.
     */
    fun getEstados(): Flow<List<Estado>> {
        return queries.getAllEstados()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    /**
     * Obtiene los municipios correspondientes a un estado específico como un Flow.
     */
    fun getMunicipiosPorEstado(estadoId: Long): Flow<List<Municipio>> {
        return queries.getMunicipiosPorEstado(estadoId)
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    // --- CRUD Contribuyentes ---

    /**
     * Obtiene todos los contribuyentes como un Flow.
     */
    fun getContribuyentes(): Flow<List<Contribuyente>> {
        return queries.getAllContribuyentes()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    /**
     * Inserta un nuevo contribuyente. Se ejecuta como una función suspendida (corrutina)
     * porque es una operación de escritura.
     */
    suspend fun agregarContribuyente(
        tipoPersona: String,
        rfc: String,
        nombreRazonSocial: String,
        correo: String,
        estadoId: Long,
        municipioId: Long,
        codigoPostal: String
    ) {
        queries.insertContribuyente(
            tipo_persona = tipoPersona,
            rfc = rfc,
            nombre_razon_social = nombreRazonSocial,
            correo_electronico = correo,
            estado_id = estadoId,
            municipio_id = municipioId,
            codigo_postal = codigoPostal
        )
    }

    /**
     * Elimina un contribuyente por su ID.
     */
    suspend fun eliminarContribuyente(id: Long) {
        queries.deleteContribuyente(id)
    }
}