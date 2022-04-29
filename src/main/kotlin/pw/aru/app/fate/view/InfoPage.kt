package pw.aru.app.fate.view

import io.javalin.Context
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import pw.aru.app.fate.data.provider.ServantProvider
import pw.aru.app.fate.tracker.OnlineTracker
import pw.aru.app.fate.view.base.View
import javax.servlet.http.HttpSession

class InfoPage(ctx: Context, online: OnlineTracker, servants: ServantProvider) : View(ctx, online, servants) {
    override fun Context.render() {
        val servantsStats = servants.stats()

        contentType("text/html;charset=UTF-8").res.writer.appendHTML().html {
            head {
                styling()
                title("FATE/Oblivion")
            }
            body {
                navbar()

                div("container mb-4") {
                    fastActions()

                    h3("mb-1") { +"Informações" }

                    hr()

                    div("row") {
                        div("col") {
                            h5 { +"Usuários online" }
                            table("table table-striped") {
                                thead("thead-dark") {
                                    tr {
                                        th { +"Nome" }
                                        th { +"Página" }
                                    }
                                }
                                tbody {
                                    for (user in online.onlineUsers()) {
                                        tr {
                                            th {
                                                span {
                                                    style = "color: #${user.color};"
                                                    +user.name
                                                }
                                            }
                                            td { +calculateLastUserAction(user.currentPage, user.lastId) }
                                        }
                                    }
                                }
                            }
                        }
                        div("col") {
                            h5 { +"Estatísticas" }
                            table("table table-striped") {
                                thead("thead-dark") {
                                    tr {
                                        th { +"Tipo" }
                                        th { +"Quantidade" }
                                    }
                                }
                                tbody {
                                    tr {
                                        th { +"Servos" }
                                        td { text(servantsStats.count) }
                                    }
                                    tr {
                                        th { +"Imagens" }
                                        td { text(servantsStats.imageCount) }
                                    }
                                }
                            }
                        }
                    }

                    hr()

                    div("row") {
                        div("col") {
                            h5 { +"Estrelas" }
                            table("table table-striped") {
                                thead("thead-dark") {
                                    tr {
                                        th { +"Poder (★)" }
                                        th { +"Quantidade" }
                                    }
                                }
                                tbody {
                                    servantsStats.starCount.entries
                                        .sortedByDescending { it.value }.forEach { (amount, count) ->
                                            tr {
                                                th { +"★$amount" }
                                                td { text(count) }
                                            }
                                        }
                                }
                            }
                        }
                        div("col") {
                            h5 { +"Classes" }
                            table("table table-striped") {
                                thead("thead-dark") {
                                    tr {
                                        th { +"Classe" }
                                        th { +"Quantidade" }
                                    }
                                }
                                tbody {
                                    servantsStats.typeCount.entries
                                        .sortedByDescending { it.value }.forEach { (type, count) ->
                                            tr {
                                                th {
                                                    +(type?.name?.toLowerCase()?.capitalize() ?: "Not set")
                                                }
                                                td { text(count) }
                                            }
                                        }
                                }
                            }
                        }
                    }

                    hr()

                    div("row") {
                        div("col") {
                            h5 { +"Raças" }
                            table("table table-striped") {
                                thead("thead-dark") {
                                    tr {
                                        th { +"Raça" }
                                        th { +"Quantidade" }
                                    }
                                }
                                tbody {
                                    servantsStats.raceCount.entries
                                        .sortedByDescending { it.value }.forEach { (race, count) ->
                                            tr {
                                                th { +(if (race.isBlank()) "Not set" else race) }
                                                td { text(count) }
                                            }
                                        }
                                }
                            }
                        }
                        div("col") {
                            h5 { +"Origens" }
                            table("table table-striped") {
                                thead("thead-dark") {
                                    tr {
                                        th { +"Origem" }
                                        th { +"Quantidade" }
                                    }
                                }
                                tbody {
                                    servantsStats.originCount.entries
                                        .sortedByDescending { it.value }.forEach { (origin, count) ->
                                            tr {
                                                th { +(if (origin.isBlank()) "Not set" else origin) }
                                                td { text(count) }
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

    private fun calculateLastUserAction(currentPage: String, lastId: String?): String {
        return when (currentPage) {
            "", "/" -> "At the homepage"

            "/info" -> "Looking at this page"

            "/edit" -> {
                lastId?.let(servants::getById)?.let {
                    if (it.name.isNotBlank()) {
                        "Editing ${it.name} (${it.fateID})"
                    } else {
                        "Editing a unnamed servant (${it.fateID})"
                    }
                } ?: "Creating a new servant"
            }

            else -> when {

                currentPage.startsWith("/view/") -> {
                    currentPage.removePrefix("/view/").let(servants::getById)?.let {
                        if (it.name.isNotBlank()) {
                            "Viewing ${it.name}'s page (${it.fateID})"
                        } else {
                            "Viewing a unnamed servant's page (${it.fateID})"
                        }
                    } ?: "Viewing a unnamed servant's page"
                }

                currentPage.startsWith("/edit/modify_id/") -> {
                    currentPage.removePrefix("/edit/modify_id/").let(servants::getById)?.let {
                        if (it.name.isNotBlank()) {
                            "Changing ${it.name}'s ID (${it.fateID})"
                        } else {
                            "Editing a unnamed servant's ID (${it.fateID})"
                        }
                    } ?: "Editing a unnamed servant's ID"
                }

                currentPage.startsWith("/edit/delete/") -> {
                    currentPage.removePrefix("/edit/delete/").let(servants::getById)?.let {
                        if (it.name.isNotBlank()) {
                            "On ${it.name}'s delete screen (${it.fateID})"
                        } else {
                            "On a unnamed servant's delete screen (${it.fateID})"
                        }
                    } ?: "On a unnamed servant's delete screen"
                }

                else -> "Somewhere on the site"
            }
        }
    }
}
