package pw.aru.app.fate.view

import io.javalin.Context
import kotlinx.html.*
import kotlinx.html.ButtonType.submit
import kotlinx.html.FormMethod.post
import kotlinx.html.stream.appendHTML
import pw.aru.app.fate.data.provider.ServantProvider
import pw.aru.app.fate.forInput
import pw.aru.app.fate.tracker.OnlineTracker
import pw.aru.app.fate.view.base.View
import javax.servlet.http.HttpSession

class ChangeIDPage(ctx: Context, online: OnlineTracker, servants: ServantProvider) : View(ctx, online, servants) {
    override fun Context.render() {
        val session: HttpSession? = req.getSession(false)

        if (session == null) {
            redirect("/login")
            return
        }

        val servantId = pathParam("id")

        contentType("text/html;charset=UTF-8").res.writer.appendHTML().html {
            head {
                styling()
                title("FATE/Oblivion")
            }
            body {
                navbar()

                div("container my-5") {
                    div("row justify-content-md-center") {
                        div("col-lg-4 col-md-8") {
                            h4 { +"Mudar ID de Servos" }

                            hr()

                            form(method = post, action = "/edit/modify_id") {
                                input(type = InputType.hidden, name = "id") {
                                    value = servantId
                                }
                                div("form-group") {
                                    p("label") { +"ID Antigo" }
                                    p("h5") { +servantId }
                                }

                                div("form-group") {
                                    label {
                                        forInput(id = "inputNewID")
                                        +"Novo ID"
                                    }
                                    input(type = InputType.text, classes = "form-control", name = "new_id") {
                                        id = "inputNewID"
                                    }
                                }
                                button(type = submit, classes = "btn mt-4 btn-warning btn-block") { +"Mudar" }
                            }
                        }
                    }
                }

                footer()
            }
        }.flush()
    }
}