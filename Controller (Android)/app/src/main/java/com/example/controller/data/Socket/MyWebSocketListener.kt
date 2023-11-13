package com.example.controller.data.Socket

import com.example.controller.data.Models.SocketUpdate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class MyWebSocketListener: WebSocketListener() {
    val NORMAL_CLOSURE_STATUS = 1000
    val socketEventChannel: Channel<SocketUpdate> = Channel(10)

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
//        webSocket.send("Connection Opened")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("Received Message $text")
        GlobalScope.launch {
            socketEventChannel.send(SocketUpdate(text))
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("Closing Socket")
        GlobalScope.launch {
            try {
                socketEventChannel.send(SocketUpdate("Connection Closed"))
                socketEventChannel.send(SocketUpdate(exception = SocketAbortedException()))
            }
            catch (e: Exception){
                println(e.message)
            }
        }
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        socketEventChannel.close()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("Encountered Socket Failure")
        println(t.message)
        GlobalScope.launch {
            socketEventChannel.send(SocketUpdate(exception = t))
        }
    }
}

class SocketAbortedException : Exception()
