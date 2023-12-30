package com.webserver

import java.io.File

class HttpRequestHandler(
    private val webFolder: String,
) {

    fun handleRequest(request: List<String>): HttpResponse {
        val httpRequestResult = HttpRequest.from(request)
        return httpRequestResult.fold(
            ifLeft = { (error, code) ->
                HttpResponse.Fail(
                    status = code,
                    content = error,
                )
            },
            ifRight = { httpRequest ->
                handleRequest(httpRequest)
            }
        )
    }

    private fun handleRequest(httpRequest: HttpRequest): HttpResponse {
        return if (httpRequest.path.startsWith("/cgi-bin")) {
            handleCgiRequest(httpRequest)
        } else {
            val resourceUrl = File(webFolder).resolve(httpRequest.path)
            if (!resourceUrl.exists()) {
                HttpResponse.Fail(
                    status = 404,
                    content = "Resource not found",
                )
            } else {
                val text = resourceUrl.readText()
                HttpResponse.Success(
                    status = 200,
                    contentType = "text/html",
                    content = text,
                )
            }
        }
    }

    private fun handleCgiRequest(httpRequest: HttpRequest): HttpResponse.Success {
        val process = Runtime.getRuntime().exec(webFolder + httpRequest.path)
        val builder = StringBuilder()
        var message = process.inputReader().readLine()
        while (message != null) {
            builder.append(message)
            message = process.inputReader().readLine()
        }
        if (process.isAlive) {
            process.destroy()
        }
        return HttpResponse.Success(
            status = 200,
            contentType = "text/html",
            content = builder.toString(),
        )
    }
}