package com.natpryce.krouton.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.unaryPlus
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.Test

class RouterCompositionTests {
    val routeX = +"x"
    val appX = resources {
        routeX methods {
            GET { Response(OK).body("x") }
        }
    }
    
    val routeY = +"y"
    val appY = resources {
        routeY methods {
            GET { Response(OK).body("y") }
        }
    }
    
    @Test
    fun `you can add Krouton apps together`() {
        val composedApp = appX + appY
        
        val monolithicApp = resources {
            routeX methods {
                GET { Response(OK).body("x") }
            }
        
            routeY methods {
                GET { Response(OK).body("y") }
            }
        }
        
        assertThat(composedApp.urlTemplates(), equalTo(monolithicApp.urlTemplates()))
    }
}

operator fun ResourceRouter.plus(that: ResourceRouter) =
    ResourceRouter(this.router + that.router)

operator fun <T, ROUTE: Route<T>> Router<T,ROUTE>.plus(that: Router<T,ROUTE>) =
    Router(this.routes + that.routes, that.handlerIfNoMatch)