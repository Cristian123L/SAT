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
     * Revisa si la tabla de Estados está vacía. Si lo está, inserta los 32 estados de México.
     * Basicamente estamos poblando la base de datos, en este caso primero con los Estados
     */
    suspend fun poblarEstadosSiEstanVacios() {
        // Ejecutamos la consulta en el hilo correcto
        kotlinx.coroutines.withContext(Dispatchers.IO) {
            val estadosActuales = queries.getAllEstados().executeAsList()

            // Si la lista está vacía, procedemos a llenarla
            if (estadosActuales.isEmpty()) {
                val listaDeEstados = listOf(
                    "Aguascalientes", "Baja California", "Baja California Sur", "Campeche",
                    "Chiapas", "Chihuahua", "Ciudad de México", "Coahuila", "Colima",
                    "Durango", "Estado de México", "Guanajuato", "Guerrero", "Hidalgo",
                    "Jalisco", "Michoacán", "Morelos", "Nayarit", "Nuevo León", "Oaxaca",
                    "Puebla", "Querétaro", "Quintana Roo", "San Luis Potosí", "Sinaloa",
                    "Sonora", "Tabasco", "Tamaulipas", "Tlaxcala", "Veracruz",
                    "Yucatán", "Zacatecas"
                )

                // Insertamos uno por uno en la base de datos
                listaDeEstados.forEach { nombreEstado ->
                    queries.insertEstado(nombreEstado)
                }
            }
        }
    }

    /**
     * Obtiene los municipios correspondientes a un estado específico como un Flow.
     */
    fun getMunicipiosPorEstado(estadoId: Long): Flow<List<Municipio>> {
        return queries.getMunicipiosPorEstado(estadoId)
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    /**
     * Ahora debemos poblar nuesta base de datos con los municipios de cada estado que tiene México
     * Revisando si la tabla de municipios esta vacia. Si lo está, igual que en los estados
     * la llenamos con los datos
     */

    suspend fun poblarMunicipiosSiEstanVacios() {
        // Ejecutamos la consulta en el hilo correcto
        kotlinx.coroutines.withContext(Dispatchers.IO) {
            val estados = queries.getAllEstados().executeAsList()

            // Si por una muy extraña razon no llega a haber estados, cancelamos el proceso por obvias razones
            if (estados.isEmpty()) return@withContext

            // Revisamos si ya hay municipios (usando el primer estado como referencia)
            val primerEstado = estados.first()
            val municipiosPrueba = queries.getMunicipiosPorEstado(primerEstado.id).executeAsList()

            // Si está vacío, podemos comenzar a poblarla
            if (municipiosPrueba.isEmpty()) {
                val municipiosEspecificos = mapOf(
                    "Aguascalientes" to listOf("Aguascalientes", "Jesús María", "Calvillo"),
                    "Baja California" to listOf("Tijuana", "Mexicali", "Ensenada"),
                    "Baja California Sur" to listOf("La Paz", "Los Cabos", "Comondú"),
                    "Campeche" to listOf("Campeche", "Carmen", "Champotón"),
                    "Chiapas" to listOf("Tuxtla Gutiérrez", "Tapachula", "San Cristóbal de las Casas"),
                    "Chihuahua" to listOf("Juárez", "Chihuahua", "Cuauhtémoc"),
                    "Ciudad de México" to listOf("Iztapalapa", "Gustavo A. Madero", "Cuauhtémoc", "Coyoacán"),
                    "Coahuila" to listOf("Saltillo", "Torreón", "Monclova"),
                    "Colima" to listOf("Colima", "Manzanillo", "Tecomán"),
                    "Durango" to listOf("Durango", "Gómez Palacio", "Lerdo"),
                    "Estado de México" to listOf("Ecatepec", "Naucalpan", "Toluca", "Tlalnepantla"),
                    "Guanajuato" to listOf("León", "Irapuato", "Celaya", "Uriangato", "Moroleón", "Yuriria"),
                    "Guerrero" to listOf("Acapulco de Juárez", "Chilpancingo de los Bravo", "Iguala de la Independencia"),
                    "Hidalgo" to listOf("Pachuca de Soto", "Tulancingo de Bravo", "Tula de Allende"),
                    "Jalisco" to listOf("Guadalajara", "Zapopan", "Tlaquepaque", "Puerto Vallarta"),
                    "Michoacán" to listOf("Morelia", "Uruapan", "Zamora", "Cuitzeo"),
                    "Morelos" to listOf("Cuernavaca", "Jiutepec", "Cuautla"),
                    "Nayarit" to listOf("Tepic", "Bahía de Banderas", "Compostela"),
                    "Nuevo León" to listOf("Monterrey", "Apodaca", "San Pedro Garza García", "San Nicolás de los Garza"),
                    "Oaxaca" to listOf("Oaxaca de Juárez", "San Juan Bautista Tuxtepec", "Salina Cruz"),
                    "Puebla" to listOf("Puebla", "Tehuacán", "San Andrés Cholula"),
                    "Querétaro" to listOf("Querétaro", "San Juan del Río", "Corregidora"),
                    "Quintana Roo" to listOf("Benito Juárez", "Solidaridad", "Othón P. Blanco"),
                    "San Luis Potosí" to listOf("San Luis Potosí", "Soledad de Graciano Sánchez", "Ciudad Valles"),
                    "Sinaloa" to listOf("Culiacán", "Mazatlán", "Ahome"),
                    "Sonora" to listOf("Hermosillo", "Cajeme", "Nogales"),
                    "Tabasco" to listOf("Centro", "Cárdenas", "Comalcalco"),
                    "Tamaulipas" to listOf("Reynosa", "Matamoros", "Nuevo Laredo"),
                    "Tlaxcala" to listOf("Tlaxcala", "Apizaco", "Huamantla"),
                    "Veracruz" to listOf("Veracruz", "Xalapa", "Coatzacoalcos", "Boca del Río"),
                    "Yucatán" to listOf("Mérida", "Valladolid", "Progreso"),
                    "Zacatecas" to listOf("Zacatecas", "Fresnillo", "Guadalupe")
                )

                // Recorremos cada estado guardado
                estados.forEach { estado ->
                    // Buscamos si le asignamos municipios específicos
                    val municipios = municipiosEspecificos[estado.nombre]

                    if (municipios != null) {
                        // Si sí tiene, los insertamos uno por uno vinculándolos a su estado_id
                        municipios.forEach { nombreMunicipio ->
                            queries.insertMunicipio(estado.id, nombreMunicipio)
                        }
                    }
                }
            }
        }
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

    /**
     * Actualiza un contribuyente existente por su ID.
     */
    suspend fun actualizarContribuyente(
        id: Long,
        tipoPersona: String,
        rfc: String,
        nombreRazonSocial: String,
        correo: String,
        estadoId: Long,
        municipioId: Long,
        codigoPostal: String
    ) {
        queries.updateContribuyente(
            tipo_persona = tipoPersona,
            rfc = rfc,
            nombre_razon_social = nombreRazonSocial,
            correo_electronico = correo,
            estado_id = estadoId,
            municipio_id = municipioId,
            codigo_postal = codigoPostal,
            id = id
        )
    }
}