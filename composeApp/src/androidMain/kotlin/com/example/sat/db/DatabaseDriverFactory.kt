package com.example.sat.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        // En Android, la base de datos necesita el "Context" para crearse
        return AndroidSqliteDriver(AppDatabase.Schema, context, "sat_contribuyentes.db")
    }
}