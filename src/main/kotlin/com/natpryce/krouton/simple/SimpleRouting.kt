package com.natpryce.krouton.simple

import com.natpryce.krouton.UrlScheme
import com.natpryce.krouton.parse
import com.natpryce.krouton.splitPath

infix fun <T> UrlScheme<T>.by(handler: (T) -> Unit) = fun(path: List<String>): Boolean {
    val parsed = parse(path)
    if (parsed == null) {
        return false
    } else {
        handler(parsed)
        return true
    }
}

fun routeOn(path: String, vararg routes: (List<String>) -> Boolean) = routeOn(splitPath(path), *routes)

fun <Criteria> routeOn(criteria: Criteria, vararg routes: (Criteria) -> Boolean): Boolean {
    for (route in routes) {
        if (route(criteria)) return true
    }
    return false
}

inline infix fun Boolean.otherwise(block: () -> Unit) {
    if (!this) block()
}