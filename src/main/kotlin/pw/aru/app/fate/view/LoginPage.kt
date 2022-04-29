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

class LoginPage(ctx: Context, online: OnlineTracker, servants: ServantProvider) : View(ctx, online, servants) {
    override fun Context.render() {
        val session: HttpSession? = req.getSession(false)

        if (session != null) {
            redirect("/")
            return
        }

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
                            h4 { +"Login em FATE/Oblivion" }

                            hr()

                            form(method = post, action = "/login") {
                                div("form-group") {
                                    label {
                                        forInput(id = "inputUsername")
                                        +"Nome de Usuário"
                                    }
                                    input(type = InputType.text, classes = "form-control", name = "username") {
                                        id = "inputUsername"
                                    }
                                }
                                div("form-group") {
                                    label {
                                        forInput(id = "inputColor")
                                        +"Cor"
                                    }
                                    select(classes = "form-control") {
                                        style = "background: white; color: black; font-style: italic;"
                                        id = "inputColor"
                                        name = "color"

                                        option {
                                            style = "background: white; color: black; font-style: italic;"
                                            value = "random"
                                            +"Aleatório"
                                        }
                                        option {
                                            style = "background: #17ac86; color: white"
                                            value = "17ac86"
                                            +"Turquesa"
                                        }
                                        option {
                                            style = "background: #25c059; color: white"
                                            value = "25c059"
                                            +"Verde"
                                        }
                                        option {
                                            style = "background: #277ecd; color: white"
                                            value = "277ecd"
                                            +"Azul"
                                        }
                                        option {
                                            style = "background: #843da4; color: white"
                                            value = "843da4"
                                            +"Violeta"
                                        }
                                        option {
                                            style = "background: #da004e; color: white"
                                            value = "da004e"
                                            +"Rosa"
                                        }
                                        option {
                                            style = "background: #e4b400; color: white"
                                            value = "e4b400"
                                            +"Amarelo"
                                        }
                                        option {
                                            style = "background: #d6680e; color: white"
                                            value = "d6680e"
                                            +"Laranja"
                                        }
                                        option {
                                            style = "background: #d7342a; color: white"
                                            value = "d7342a"
                                            +"Vermelho"
                                        }
                                        option {
                                            style = "background: #4c6876; color: white"
                                            value = "4c6876"
                                            +"Cinza"
                                        }
                                        option {
                                            style = "background: #161719; color: white"
                                            value = "161719"
                                            +"Preto"
                                        }
                                    }
                                }
                                button(type = submit, classes = "btn mt-4 btn-primary btn-block") { +"Login" }
                            }
                        }
                    }
                }

                footer()

                script {
                    unsafe {
                        +"""
                        document.addEventListener("DOMContentLoaded", function(event) {
                            var e = document.getElementById("inputColor");
                            e.onchange = function() {
                                var opts = e.options;
                                for (var i = 0, n = opts.length; i < n; i++) {
                                    if (opts[i].value == e.value) {
                                        e.style.cssText = opts[i].style.cssText;
                                        return;
                                    }
                                }
                            };
                        });
                        """.trimIndent()
                    }
                }
            }
        }.flush()
    }
}