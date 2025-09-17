package io.awais.cricket_championship

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform