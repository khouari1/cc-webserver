package com.webserver

import arrow.core.Either
import arrow.core.left
import arrow.core.right

typealias HttpRequestParseError = Pair<String, Int>

data class HttpRequest(
    val method: HttpMethod,
    val path: String,
    val version: String,
) {
    companion object {
        fun from(request: List<String>): Either<HttpRequestParseError, HttpRequest> {
            val parts = request.first().split(" ")
            val (method, path, version) = when (parts.size) {
                3 -> {
                    if (parts[1] == "/") {
                        listOf(parts[0], "index.html", parts[2])
                    } else {
                        parts
                    }
                }

                else -> return ("Invalid request format. Expecting 2 or 3 parts." to 400).left()
            }
            if (version != "HTTP/1.1") {
                return ("Unsupported version $version. Only HTTP/1.1 supported." to 505).left()
            }
            if (path.contains("..")) {
                return ("Path cannot contain '..'" to 400).left()
            }
            val parsedMethod = try {
                HttpMethod.valueOf(method)
            } catch (e: IllegalArgumentException) {
                return ("Invalid HTTP method provided $method. Supported methods: ${HttpMethod.entries}" to 405).left()
            }
            return HttpRequest(
                parsedMethod,
                path,
                version,
            ).right()
        }
    }
}