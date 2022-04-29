package pw.aru.app.fate.view

import io.javalin.Context
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import pw.aru.app.fate.data.provider.ServantProvider
import pw.aru.app.fate.exports.servantProvider
import pw.aru.app.fate.tracker.OnlineTracker
import pw.aru.app.fate.view.base.View
import pw.aru.core.extensions.nullIfBlank
import javax.servlet.http.HttpSession

class ViewPage(ctx: Context, online: OnlineTracker, servants: ServantProvider) : View(ctx, online, servants) {
    override fun Context.render() {
        val session: HttpSession? = req.getSession(false)

        val id = pathParam("id")
        val servant = servantProvider.getById(id)
        if (servant == null) {
            redirect("/")
            return
        }
        val effectiveName = servant.name.nullIfBlank() ?: servant.fateID

        contentType("text/html;charset=UTF-8").res.writer.appendHTML().html {
            head {
                styling()
                title("FATE/Oblivion - $effectiveName")
            }
            body {
                navbar()

                div("container mb-5") {
                    fastActions()

                    hr()

                    div("d-sm-flex py-2") {
                        div("pr-3 flex-grow-1") {
                            p("mb-1") { +"Nome" }
                            h5("mb-1") { +(servant.name.nullIfBlank() ?: "<Not set>") }
                            small { +"FateID: ${servant.fateID}" }
                        }

                        if (session != null) {
                            div("px-3 mt-4 mb-3") {
                                a(classes = "btn btn-success", href = "/edit?id=${servant.fateID}") {
                                    +"Edit "
                                    i("fas fa-pencil-alt")
                                }
                            }
                        }

                        div("pl-3") {
                            val stars = servant.stars
                            p("my-4 h4 text-warning") {
                                +if (stars == 0) "★0" else List(stars) { "★" }.joinToString("")
                            }
                        }
                    }

                    div("d-sm-flex py-2") {
                        div("pr-3 flex-grow-1") {
                            div("d-flex") {
                                div("pr-3 flex-fill") {
                                    p("mb-1") { +"Raça" }
                                    p("h5 mb-3") { +(servant.race.nullIfBlank() ?: "<Not set>") }
                                }
                                div("px-3 flex-fill") {
                                    p("mb-1") { +"Origem" }
                                    p("h5 mb-3") { +(servant.origin.nullIfBlank() ?: "<Not set>") }
                                }
                                div("pl-3 flex-fill") {
                                    p("mb-1") { +"Classe" }
                                    p("h5 mb-3") { +(servant.type?.name?.toLowerCase()?.capitalize() ?: "<Not set>") }
                                }
                            }

                            p("mb-1") { +"Descrição" }
                            div("alert alert-secondary text-body mb-3") {
                                val iterator = (servant.description.nullIfBlank() ?: "<Not set>")
                                    .splitToSequence("\n").iterator()

                                while (iterator.hasNext()) {
                                    val it = iterator.next()
                                    p(if (iterator.hasNext()) "h6 mb-2" else "h6 mb-0") { +it }
                                }
                            }

                            if (queryParam("showCatalyst", "false") != "false") {
                                p("mb-1") { +"Catalisador" }
                                p("h5 mb-3") { +(servant.catalyst.nullIfBlank() ?: "<Not set>") }
                            }
                        }

                        div("pl-3 text-center") {
                            img(
                                src = if (servant.imageExt.isEmpty()) "/img/no_avatar.png" else "/imgdb/$id",
                                classes = "img-fluid large-avatar"
                            )
                        }
                    }

                    hr()

                    div("row") {
                        div("col-sm") {
                            p("mb-1") { +"Arma 1" }
                            div("alert alert-secondary text-body mb-3") {
                                p("h5 mb-0") { +(servant.weapon1.nullIfBlank() ?: "<Not set>") }
                                hr("my-2")

                                val iterator = (servant.weapon1Desc.nullIfBlank() ?: "<Not set>")
                                    .splitToSequence("\n").iterator()

                                while (iterator.hasNext()) {
                                    val it = iterator.next()
                                    p(if (iterator.hasNext()) "h6 mb-2" else "h6 mb-0") { +it }
                                }
                            }
                        }

                        if (servant.weapon2.isNotBlank()) {
                            div("col-sm") {
                                p("mb-1") { +"Arma 2" }
                                div("alert alert-secondary text-body mb-3") {
                                    p("h5 mb-0") { +servant.weapon2 }
                                    hr("my-2")

                                    val iterator = (servant.weapon2Desc.nullIfBlank() ?: "<Not set>")
                                        .splitToSequence("\n").iterator()

                                    while (iterator.hasNext()) {
                                        val it = iterator.next()
                                        p(if (iterator.hasNext()) "h6 mb-2" else "h6 mb-0") { +it }
                                    }
                                }
                            }
                        }
                    }

                    div("row") {
                        div("col-sm") {
                            p("mb-1") { +"Habilidade 1" }
                            div("alert alert-secondary text-body mb-3") {
                                p("h5 mb-0") { +(servant.skill1.nullIfBlank() ?: "<Not set>") }
                                hr("my-2")

                                val iterator = (servant.skill1Desc.nullIfBlank() ?: "<Not set>")
                                    .splitToSequence("\n").iterator()

                                while (iterator.hasNext()) {
                                    val it = iterator.next()
                                    p(if (iterator.hasNext()) "h6 mb-2" else "h6 mb-0") { +it }
                                }
                            }
                        }

                        if (servant.skill2.isNotBlank()) {
                            div("col-sm") {
                                p("mb-1") { +"Habilidade 2" }
                                div("alert alert-secondary text-body mb-3") {
                                    p("h5 mb-0") { +servant.skill2 }
                                    hr("my-2")

                                    val iterator = (servant.skill2Desc.nullIfBlank() ?: "<Not set>")
                                        .splitToSequence("\n").iterator()

                                    while (iterator.hasNext()) {
                                        val it = iterator.next()
                                        p(if (iterator.hasNext()) "h6 mb-2" else "h6 mb-0") { +it }
                                    }
                                }
                            }
                        }
                    }


                    div("row") {
                        div("col-sm") {
                            p("mb-1") { +"Montaria" }
                            if (servant.mount.isBlank()) {
                                p("h5 mb-3") { +"Nenhuma" }
                            } else {
                                div("alert alert-secondary text-body mb-3") {
                                    p("h5 mb-0") { +servant.mount }
                                    hr("my-2")

                                    val iterator = (servant.mountDesc.nullIfBlank() ?: "<Not set>")
                                        .splitToSequence("\n").iterator()

                                    while (iterator.hasNext()) {
                                        val it = iterator.next()
                                        p(if (iterator.hasNext()) "h6 mb-2" else "h6 mb-0") { +it }
                                    }
                                }
                            }
                        }

                        if (servant.extras.isNotBlank()) {
                            div("col-sm") {
                                p("mb-1") { +"Extras" }
                                div("alert alert-secondary text-body mb-3") {
                                    val iterator = servant.extras.splitToSequence("\n").iterator()

                                    while (iterator.hasNext()) {
                                        val it = iterator.next()
                                        p(if (iterator.hasNext()) "h6 mb-2" else "h6 mb-0") { +it }
                                    }
                                }
                            }
                        }
                    }

                    p("mb-1") { +"Fantasma Nobre" }
                    div("alert alert-secondary text-body mb-3") {
                        p("h5 mb-0") { +(servant.noblePhantasm.nullIfBlank() ?: "<Not set>") }
                        hr("my-2")

                        val iterator = (servant.noblePhantasmDesc.nullIfBlank() ?: "<Not set>")
                            .splitToSequence("\n").iterator()

                        while (iterator.hasNext()) {
                            val it = iterator.next()
                            p(if (iterator.hasNext()) "h6 mb-2" else "h6 mb-0") { +it }
                        }
                    }
                }
                footer()
            }
        }.flush()
    }
}