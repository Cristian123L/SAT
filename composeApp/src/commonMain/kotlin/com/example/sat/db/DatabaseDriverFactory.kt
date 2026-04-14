package com.example.sat.db

import app.cash.sqldelight.db.SqlDriver

// "expect" le dice a Kotlin: "Oye, cada plataforma tendrá su propia forma de hacer esto"
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}