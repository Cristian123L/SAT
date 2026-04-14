package com.example.sat

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform