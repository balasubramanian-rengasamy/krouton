package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.oneeyedmen.minutest.rootContext

private val route = int where { it > 10 }

fun restriction() = rootContext<Unit> {
    test("restricted route") {
        assertThat(route.parse("/0"), absent())
        assertThat(route.parse("/10"), absent())
        assertThat(route.parse("/11"), equalTo(11))
        assertThat(route.parse("/999"), equalTo(999))
    }
    
    test("reverse routing does not enforce restriction") {
        assertThat(route.path(0), equalTo("/0"))
        assertThat(route.path(10), equalTo("/10"))
        assertThat(route.path(11), equalTo("/11"))
        assertThat(route.path(999), equalTo("/999"))
    }
}
