package com.example.controller.data.Socket

import com.example.controller.SOCKET_URL
import com.example.controller.data.Models.SocketUpdate
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

class WebServiceProvider {
    private var _webSocket: WebSocket? = null

    private val socketOkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .hostnameVerifier { _, _ -> true }
        .build()

    private var _webSocketListener: MyWebSocketListener? = null

    fun sendMessage(message: String){
        _webSocket?.send(message)
    }

    fun startSocket(): Channel<SocketUpdate> {
        val socket = MyWebSocketListener()
        setUpSocket(socket)
        return socket.socketEventChannel
    }

    private fun setUpSocket(webSocketListener: MyWebSocketListener) {
        _webSocketListener = webSocketListener
        _webSocket = socketOkHttpClient.newWebSocket(
            Request
                .Builder()
                .url("ws://$SOCKET_URL:3000/player")
//                .url("wss://ws.postman-echo.com/raw")
                .build(),
            webSocketListener
        )
//        socket
    //
    //        OkHttpClient.dispatcher.executorService.shutdown()
    }

    fun stopSocket() {
        try {
            _webSocket?.close(1000, null)
            _webSocket = null
            _webSocketListener?.socketEventChannel?.close()
            _webSocketListener = null
        } catch (ex: Exception) {
            println("Caught error in Provider class")
        }
    }
}