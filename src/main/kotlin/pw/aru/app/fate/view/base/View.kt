package pw.aru.app.fate.view.base

import io.javalin.Context
import kotlinx.html.*
import pw.aru.app.fate.data.provider.ServantProvider
import pw.aru.app.fate.tracker.OnlineTracker
import pw.aru.exported.version

abstract class View(
    private val ctx: Context,
    protected val online: OnlineTracker,
    protected val servants: ServantProvider
) {
    fun render() {
        ctx.render()
    }

    protected abstract fun Context.render()

    protected fun HtmlBlockTag.fastActions(queryString: String? = null) {
        val session = ctx.req.getSession(false)

        div("d-flex bg-light py-2 mb-3") {
            div("px-3") { div("navbar-brand") { +"Fast Menu" } }

            div("flex-grow-1") {
                form(classes = "m-0", method = FormMethod.get, action = "/") {
                    div("d-flex") {
                        div("pr-1 flex-grow-1") {
                            input(classes = "form-control", type = InputType.text, name = "q") {
                                placeholder = "Pesquisar"
                                if (queryString != null) {
                                    value = queryString
                                }
                            }
                        }
                        div("pl-1") {
                            button(type = ButtonType.submit, classes = "btn btn-lg btn-secondary") {
                                i("fas fa-search")
                            }
                        }
                    }
                }
            }

            if (session != null) {
                val name = session.getAttribute("username").toString()
                val color = session.getAttribute("color").toString()

                div("px-3") {
                    span(classes = "navbar-text") {
                        +"Logged in as "
                        b {
                            style = "color: #$color;"
                            +name
                        }
                    }
                }
                div("px-3") {
                    div("btn-group") {
                        a(classes = "btn btn-warning", href = "/logout") { +"Logout" }
                        a(classes = "btn btn-primary", href = "/") { +"Home" }
                        a(classes = "btn btn-info", href = "/info") { +"Info" }
                        a(classes = "btn btn-success", href = "/edit") { +"New" }
                    }
                }
            } else {
                div("px-3") {
                    a(classes = "btn btn-info", href = "/login") { +"Login" }
                }
            }
        }
    }

    protected fun HEAD.styling() {
        link(
            rel = "stylesheet",
            href = "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
        )
        link(
            rel = "stylesheet",
            href = "/css/fixstrap4.css"
        )
        link(
            rel = "stylesheet",
            href = "https://use.fontawesome.com/releases/v5.7.2/css/all.css"
        )
    }

    protected fun BODY.navbar() {
        nav("navbar navbar-expang-lg navbar-dark bg-dark mb-4") {
            div("container") {
                a(classes = "navbar-brand", href = "/") {
                    +"FATE/Oblivion"
                }
            }
        }
    }

    protected fun BODY.footer() {
        div("jumbotron mb-0") {
            div("container") {
                h3 {
                    +"FATE/Oblivion - Servants Database"
                    small { small { small { +" v$version" } } }
                }
                p("lead") { +"By AdrianTodt" }
                p {
                    +"Made with "
                    a("https://getbootstrap.com") { +"Bootstrap" }
                    +", "
                    a("https://kotlinlang.org") { +"Kotlin" }
                    +", "
                    a("https://javalin.io/") { +"Javalin" }
                    +", "
                    a("https://www.sqlite.org/") { +"SQLite" }
                    +" and "
                    a("https://fontawesome.com/") { +"FontAwesome" }
                    +"."
                }

                val iterator = online.onlineAtPage(ctx.req.requestURI).iterator()

                if (iterator.hasNext()) {
                    hr()

                    h5 { +"Nesta Página:" }

                    p {

                        var first = true
                        while (iterator.hasNext()) {
                            val (name, color) = iterator.next()
                            if (!first) {
                                if (iterator.hasNext()) +", " else +" and "
                            }
                            first = false
                            b {
                                style = "color: #$color;"
                                +name
                            }
                        }
                    }
                }
            }
        }
    }
}
