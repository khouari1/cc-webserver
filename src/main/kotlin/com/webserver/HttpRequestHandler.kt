package com.webserver

import java.io.File

class HttpRequestHandler(
    private val webFolder: String,
) {

    fun handleRequest(request: String): HttpResponse {
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
        val resourceUrl = File(webFolder).resolve(httpRequest.path.drop(1))
        if (!resourceUrl.exists()) {
            return HttpResponse.Fail(
                status = 404,
                content = "Resource not found",
            )
        }
        val text = resourceUrl.readText()
        return HttpResponse.Success(
            status = 200,
            contentType = "text/html",
            content = text,
        )
    }
}