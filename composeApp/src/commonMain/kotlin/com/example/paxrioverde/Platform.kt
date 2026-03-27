package com.example.paxrioverde

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform