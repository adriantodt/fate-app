package pw.aru.app.fate.view

import io.javalin.Context
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import pw.aru.app.fate.data.ServantField
import pw.aru.app.fate.data.provider.ServantProvider
import pw.aru.app.fate.tracker.OnlineTracker
import pw.aru.app.fate.view.base.View
import pw.aru.core.extensions.nullIfBlank
import javax.servlet.http.HttpSession

class QueryPage(ctx: Context, online: OnlineTracker, servants: ServantProvider) : View(ctx, online, servants) {
    override fun Context.render() {
        val session: HttpSession? = req.getSession(false)

        val queryString = queryParam("q")!!
        val query = queryString.let(servants::query)

        contentType("text/html;charset=UTF-8").res.writer.appendHTML().html {
            head {
                styling()
                title("FATE/Oblivion")
            }
            body {
                navbar()

                div("container mb-4") {
                    fastActions(queryString)

                    h4 {
                        +"Resultados de '"
                        +queryString
                        +"'"
                    }
                    hr()

                    for (result in query.sortedBy { it.fateID }) {
                        div("d-flex") {
                            div("pr-1 flex-grow-1") {
                                h5 {
                                    +(result.name.nullIfBlank() ?: "(Name not set)")
                                    +" "
                                    small {
                                        small {
                                            i {
                                                +"FateID: "
                                                +result.fateID
                                            }
                                        }
                                    }
                                }
                            }

                            div("px-1") {
                                if (session == null) {
                                    a(classes = "mt-1 btn btn-sm btn-primary", href = "/view/${result.fateID}") {
                                        i("fas fa-eye")
                                    }
                                } else {
                                    div("mt-1 btn-group btn-group-sm") {
                                        a(classes = "btn btn-primary", href = "/view/${result.fateID}") {
                                            i("fas fa-eye")
                                        }
                                        a(classes = "btn btn-success", href = "/edit?id=${result.fateID}") {
                                            i("fas fa-pencil-alt")
                                        }
                                    }
                                }
                            }

                            div("pl-1") {
                                p("my-1 h6 text-warning") {
                                    +if (result.stars == 0) "★0" else List(result.stars) { "★" }.joinToString("")
                                }
                            }
                        }
                        for (match in result.matches) {
                            small { +(nomes[match.name] ?: throw AssertionError()) }
                            when (match) {
                                is ServantField.Type -> p { b { +match.value.name.toLowerCase().capitalize() } }
                                is ServantField.Text -> match.value.splitToSequence("\n").forEach {
                                    p {
                                        var regexMatch: MatchResult? = result.pattern.find(it)

                                        if (regexMatch == null) {
                                            +it
                                        } else {
                                            var lastStart = 0
                                            val length = it.length
                                            do {
                                                val foundMatch = regexMatch!!
                                                +it.substring(lastStart, foundMatch.range.start)
                                                b { +foundMatch.value }
                                                lastStart = foundMatch.range.endInclusive + 1
                                                regexMatch = foundMatch.next()
                                            } while (lastStart < length && regexMatch != null)

                                            if (lastStart < length) {
                                                +it.substring(lastStart, length)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        br("my-4")
                    }
                }

                footer()
            }
        }.flush()
    }

    companion object {
        private val nomes = mapOf(
            "name" to "Nome",
            "origin" to "Origem",
            "type" to "Classe",
            "description" to "Descrição",
            "catalyst" to "Catalisador",
            "weapon1" to "Arma 1",
            "weapon1Desc" to "Descrição da Arma 1",
            "weapon2" to "Arma 2",
            "weapon2Desc" to "Descrição da Arma 2",
            "skill1" to "Habilidade 1",
            "skill1Desc" to "Descrição da Habilidade 1",
            "skill2" to "Habilidade 2",
            "skill2Desc" to "Descrição da Habilidade 2",
            "mount" to "Montaria",
            "mountDesc" to "Descrição da Montaria",
            "extras" to "Extras",
            "noblePhantasm" to "Fantasma Nobre",
            "noblePhantasmDesc" to "Descrição do Fantasma Nobre"
        )
    }
}