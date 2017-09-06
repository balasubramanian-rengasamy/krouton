package com.natpryce.krouton.http4k

import com.natpryce.krouton.PathTemplate
import com.natpryce.krouton.monitoredPath
import com.natpryce.krouton.parse
import com.natpryce.krouton.splitPath
import com.natpryce.krouton.toUrlTemplate
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response


fun <T> router(routes: MutableList<Request.(T) -> Response?>, handlerIfNoMatch: Request.(T)->Response) =
    fun Request.(t: T): Response =
        routes.firstNonNull { this.it(t) } ?: this.handlerIfNoMatch(t)


typealias RequestMonitor = (Request, Response, String)->Unit

class PathRouter(
    private val routes: List<PathMatchingHttpHandler<*>>,
    private val handlerIfNoMatch: HttpHandler
): HttpHandler {
    override fun invoke(request: Request): Response {
        val pathElements = splitPath(request.uri.path)
        return routes.firstNonNull { it(request, pathElements) } ?: handlerIfNoMatch(request)
    }
    
    fun templates(): List<String> = routes.map { it.urlTemplate() }
}


class PathMatchingHttpHandler<T>(
    private val pathTemplate: PathTemplate<T>,
    private val handler: Request.(T) -> Response,
    private val monitor: RequestMonitor?
): (Request, List<String>) -> Response? {
    override fun invoke(request: Request, path: List<String>): Response? {
        val parsed = pathTemplate.parse(path)
        if (parsed != null) {
            val response = request.handler(parsed)
            monitor?.invoke(request, response, pathTemplate.monitoredPath(parsed))
            return response
        }
        else {
            return null
        }
    }
    
    fun urlTemplate(): String = pathTemplate.toUrlTemplate()
}

private inline fun <T, U> List<T>.firstNonNull(f: (T) -> U?): U? {
    forEach { t -> f(t)?.let { return it } }
    return null
}


