package com.example.controller.data.Repository

import android.os.Message
import com.example.controller.data.Models.SocketUpdate
import com.example.controller.data.Socket.WebServiceProvider
import kotlinx.coroutines.channels.Channel

class SocketRepository(private val webServiceProvider: WebServiceProvider) {

    fun startSocket(): Channel<SocketUpdate>{
        return webServiceProvider.startSocket()
    }

    fun stopSocket(){
        webServiceProvider.stopSocket()
    }

    fun sendMessage(message: String){
        webServiceProvider.sendMessage(message)
    }
}