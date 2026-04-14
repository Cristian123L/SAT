package com.example.sat.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        // En Desktop creamos un archivo físico en la carpeta del proyecto
        val databaseFile = File("sat_contribuyentes.db")
        val isNewDatabase = !databaseFile.exists()

        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")

        // Si el archivo no existía, creamos las tablas por primera vez
        if (isNewDatabase) {
            AppDatabase.Schema.create(driver)
        }

        return driver
    }
}