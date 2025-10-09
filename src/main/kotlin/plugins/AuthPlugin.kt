package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.*
import io.ktor.http.*

fun Application.configureApiKeyAuth() {
    intercept(ApplicationCallPipeline.Plugins) {
        val apiKeyHeader = call.request.headers["x-api-key"]
        val expectedKey = System.getenv("API_KEY")

        if (call.request.path().startsWith("/public")) {
            return@intercept // autoriser les routes publiques
        }

        if (apiKeyHeader == null || apiKeyHeader != expectedKey) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid or missing API key.")
            finish()
        }
    }
}
