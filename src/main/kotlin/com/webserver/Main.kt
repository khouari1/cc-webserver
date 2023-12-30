package com.webserver

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket

fun main() = runBlocking {
    val port = Integer.parseInt(System.getProperty("serverPort", "80"))
    val webFolder = System.getProperty("webFolder", "www")
    val server = ServerSocket(port)
    val requestHandler = HttpRequestHandler(webFolder)
    while (true) {
        val accept = server.accept()
        launch(Dispatchers.Default) {
            accept.use { socket ->
                BufferedReader(InputStreamReader(socket.inputStream)).use { input ->
                    val requestLines = mutableListOf<String>()
                    var request = input.readLine()
                    println("Handling request...")
                    while (request != "") {
                        requestLines.add(request)
                        println(request)
                        request = input.readLine()
                    }
                    val httpResponse = requestHandler.handleRequest(requestLines)
                    BufferedWriter(OutputStreamWriter(socket.getOutputStream())).use { output ->
                        output.write(httpResponse.toResponseString())
                    }
                }
            }
        }
    }
}


