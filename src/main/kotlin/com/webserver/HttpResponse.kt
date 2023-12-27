package com.webserver

sealed class HttpResponse {
    abstract val status: Int
    abstract val contentType: String
    abstract val content: String

    fun toResponseString(): String {
        val builder = StringBuilder()
        builder.append("HTTP/1.1 $status ${httpStatusCode(status)}\r\n")
        builder.append("Content-Type: $contentType\r\n")
        builder.append("Content-Length: ${content.length}\r\n")
        builder.append("\r\n")
        builder.append("$content\r\n")
        builder.append("\r\n")
        return builder.toString()
    }

    private fun httpStatusCode(status: Int): String =
        when (status) {
            200 -> "OK"
            400 -> "Bad Request"
            404 -> "Not Found"
            405 -> "Method Not Allowed"
            500 -> "Internal Server Error"
            505 -> "HTTP Version Not Supported"
            else -> throw IllegalArgumentException("Unsupported status code: $status")
        }

    data class Success(
        override val status: Int,
        override val contentType: String,
        override val content: String,
    ) : HttpResponse()

    data class Fail(
        override val status: Int,
        override val content: String,
    ) : HttpResponse() {

        override val contentType: String
            get() = "text/html"
    }
}