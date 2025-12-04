package com.example

import io.ktor.server.application.*
import routes.clientRoutes
import routes.serviceRoutes
import routes.appointmentRoutes
import config.DatabaseConfig
import seed.seedDb

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureSecurity()
    DatabaseConfig.connect()
    seedDb()
    configureRouting()
    clientRoutes()
    serviceRoutes()
    appointmentRoutes()
}
