package com.friendschat.routes

import com.friendschat.room.MemberAlreadyExistsException
import com.friendschat.room.RoomController
import com.friendschat.session.ChatSession
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.ktor.ext.get

fun Route.chatSocket(roomController: RoomController) {
    // this will call every single time when clients connect to this route by web socket
    webSocket(path = "/chat-socket") {
        val session = call.sessions.get<ChatSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
            return@webSocket
        }
        try {
            roomController.onJoin(session.username, session.sessionId, this)
            // calls every time when user receives message from other user by web socket
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    roomController.sendMessage(session.username, frame.readText())
                }
            }
        } catch (e: MemberAlreadyExistsException) {
            call.respond(HttpStatusCode.Conflict)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            roomController.tryDisconnect(session.username)
        }
    }
}

fun Route.getAllMessages(roomController: RoomController) {
    get("/messages") {
        call.respond(
            HttpStatusCode.OK,
            roomController.getAllMessages()
        )
    }
}