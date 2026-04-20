package com.example.sat.db // O el paquete donde lo tengas

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class ContribuyenteRepository(database: AppDatabase) {

    private val queries = database.appDatabaseQueries

    // ==========================================
    // INICIALIZADOR DE DATOS DE PRUEBA
    // ==========================================
    init {
        // Revisamos si la tabla de Estados está vacía (usamos executeAsList para leer de inmediato)
        val estadosActuales = queries.getAllEstados().executeAsList()

        if (estadosActuales.isEmpty()) {
            // 1. Insertamos Estados
            queries.insertEstado("Ciudad de México")
            queries.insertEstado("Jalisco")
            queries.insertEstado("Nuevo León")
            queries.insertEstado("Michoacán") // ¡Tu estado!
            queries.insertEstado("Guanajuato")

            // 2. Buscamos los IDs que SQLite les asignó automáticamente
            val estadosNuevos = queries.getAllEstados().executeAsList()
            val idCdmx = estadosNuevos.find { it.nombre == "Ciudad de México" }?.id
            val idMich = estadosNuevos.find { it.nombre == "Michoacán" }?.id
            val idGto = estadosNuevos.find { it.nombre == "Guanajuato" }?.id

            // 3. Insertamos Municipios ligados a esos Estados
            if (idCdmx != null) {
                queries.insertMunicipio(idCdmx, "Coyoacán")
                queries.insertMunicipio(idCdmx, "Tlalpan")
            }
            if (idMich != null) {
                queries.insertMunicipio(idMich, "Morelia")
                queries.insertMunicipio(idMich, "Cuitzeo")
            }
            if(idGto != null) {
                queries.insertMunicipio(idGto, "Casa De Shanty")
                queries.insertMunicipio(idGto, "Moroleón")
                queries.insertMunicipio(idGto, "Uriangato")
            }
        }
    }

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