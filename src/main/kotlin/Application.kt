package com.example

import com.example.plugins.DatabaseFactory
import com.example.plugins.configureApiKeyAuth
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureApiKeyAuth()
    DatabaseFactory.init()
    configureRouting()
}
