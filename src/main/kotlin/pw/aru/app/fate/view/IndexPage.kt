package pw.aru.app.fate.view

import io.javalin.Context
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import pw.aru.app.fate.data.provider.ServantProvider
import pw.aru.app.fate.tracker.OnlineTracker
import pw.aru.app.fate.view.base.View
import javax.servlet.http.HttpSession

class IndexPage(ctx: Context, online: OnlineTracker, servants: ServantProvider) : View(ctx, online, servants) {
    override fun Context.render() {
        val session: HttpSession? = req.getSession(false)

        contentType("text/html;charset=UTF-8").res.writer.appendHTML().html {
            head {
                styling()
                title("FATE/Oblivion")
            }
            body {
                navbar()

                div("container mb-4") {
                    fastActions()

                    table("table table-striped") {
                        thead("thead-dark") {
                            tr {
                                th { +"FateID" }
                                th { +"Nome" }
                                th { +"Origem" }
                                th { +"Classe" }
                                th(classes = "text-center") { i("fas fa-bars") }
                            }
                        }
                        tbody {
                            val list = servants.getAll().sortedBy { it.fateID }
                            for (servant in list) {
                                tr {
                                    th {
                                        +servant.fateID
                                        if (!servant.completed()) {
                                            +" "
                                            i("fas fa-exclamation-triangle")
                                        }
                                        if (servant.imageExt.isNotBlank()) {
                                            +" "
                                            i("fas fa-image")
                                        }
                                    }
                                    td { +servant.name }
                                    td { +servant.origin }
                                    td { +(servant.type?.name?.toLowerCase()?.capitalize() ?: "<Not set>") }
                                    td(classes = "text-center") {
                                        if (session == null) {
                                            a(classes = "btn btn-sm btn-primary", href = "/view/${servant.fateID}") {
                                                i("fas fa-eye")
                                            }
                                        } else {
                                            div("btn-group btn-group-sm") {
                                                a(classes = "btn btn-primary", href = "/view/${servant.fateID}") {
                                                    i("fas fa-eye")
                                                }
                                                a(classes = "btn btn-success", href = "/edit?id=${servant.fateID}") {
                                                    i("fas fa-pencil-alt")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                footer()
            }
        }.flush()
    }
}