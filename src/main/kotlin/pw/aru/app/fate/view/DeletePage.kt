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

class DeletePage(ctx: Context, online: OnlineTracker, servants: ServantProvider) : View(ctx, online, servants) {
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
                            h4 { +"Excluir Servos" }

                            hr()

                            form(method = post, action = "/edit/delete") {
                                input(type = InputType.hidden, name = "id") {
                                    value = servantId
                                }
                                div("form-group") {
                                    p("label") { +"FateID do Servo" }
                                    p("h5") { +servantId }
                                }

                                p("lead") {
                                    +"Você tem certeza que deseja excluir esse Servo?"
                                }

                                button(type = submit, classes = "btn mt-4 btn-danger btn-block") { +"Excluir" }
                            }
                        }
                    }
                }

                footer()
            }
        }.flush()
    }
}