package com.friendschat

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.friendschat.plugins.*
import io.ktor.application.*

fun main(args: Array<String>): Unit =
    EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSockets()
    configureRouting()
    configureSerialization()
    configureMonitoring()
    configureSecurity()
}
