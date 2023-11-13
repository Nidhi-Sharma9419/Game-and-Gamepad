package com.example.controller

import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.controller.data.Models.SocketUpdate
import com.example.controller.data.Repository.SocketRepository
import com.example.controller.data.Socket.MyWebSocketListener
import com.example.controller.data.Socket.WebServiceProvider
import com.example.controller.ui.theme.ControllerTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach

class MainActivity : ComponentActivity() {

    private lateinit var repository: SocketRepository
    private lateinit var webServiceProvider: WebServiceProvider

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return ev?.pointerCount == 1 && super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webServiceProvider = WebServiceProvider()
        repository = SocketRepository(webServiceProvider)
        setContent {
            ControllerTheme {
                TestScreen(repository)
            }
        }
    }
}

@Composable
fun TestScreen(repository: SocketRepository) {

    val interactionSource1 = remember  { MutableInteractionSource() }
    val pressed1 by interactionSource1.collectIsPressedAsState()

    val interactionSource2 = remember  { MutableInteractionSource() }
    val pressed2 by interactionSource2.collectIsPressedAsState()

    if (!pressed1 || !pressed2)
        repository.sendMessage("{\n" + "  \"direction\" : false,\n" + "  \"pressed\" : false\n" + "}")
    if(pressed1)
        repository.sendMessage("{\n" + "  \"direction\" : true,\n" + "  \"pressed\" : true\n" + "}")
    if(pressed2)
        repository.sendMessage("{\n" + "  \"direction\" : false,\n" + "  \"pressed\" : true\n" + "}")

    var url by remember{
        mutableStateOf(SOCKET_URL)
    }

    var receivedText by remember {
        mutableStateOf("")
    }
    var socketOpen by remember{
        mutableStateOf(false)
    }
    var refresher by remember{
        mutableStateOf<Boolean?>(null)
    }
    println("refresher")
    var channel by remember {
        mutableStateOf<Channel<SocketUpdate>?>(null)
    }
    LaunchedEffect(key1 = refresher) {
        println("Here $refresher")
        if(refresher != null){
            receivedText = channel!!.receive().text ?: "NONE"
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = url,
                onValueChange = {
                    url = it
                    SOCKET_URL = it
                },)
            Button(onClick = {
                channel = repository.startSocket()
                refresher = if(refresher==null) true else !refresher!!
                socketOpen = true
            }) {
                Text(text = "Start Socket")
            }
            Text(text = "Echo Will Show Here: $receivedText")
            OutlinedButton(modifier = Modifier.width(150.dp).height(150.dp), interactionSource = interactionSource1, onClick = {
                refresher = if(refresher==null) true else !refresher!!
            },
            enabled = socketOpen) {
                Text(text = "Up")
            }
            OutlinedButton(modifier = Modifier.width(150.dp).height(150.dp), interactionSource = interactionSource2, onClick = {
                refresher = if(refresher==null) true else !refresher!!

            },
                enabled = socketOpen) {
                Text(text = "Down")
            }
            Button(onClick = {
                socketOpen = false
                repository.stopSocket()
                receivedText = "Closed Connection"
//                refresher = if(refresher==null) true else !refresher!!
            }) {
                Text(text = "Stop Socket")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ControllerTheme {
//        TestScreen()
    }
}