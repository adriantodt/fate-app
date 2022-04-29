package pw.aru.app.fate.view

import io.javalin.Context
import kotlinx.html.*
import kotlinx.html.ButtonType.button
import kotlinx.html.stream.appendHTML
import pw.aru.app.fate.*
import pw.aru.app.fate.data.provider.ServantProvider
import pw.aru.app.fate.tracker.OnlineTracker
import pw.aru.app.fate.view.base.View
import javax.servlet.http.HttpSession

class EditPage(ctx: Context, online: OnlineTracker, servants: ServantProvider) : View(ctx, online, servants) {
    override fun Context.render() {
        val session: HttpSession? = req.getSession(false)

        if (session == null) {
            redirect("/login")
            return
        }

        contentType("text/html;charset=UTF-8")

        res.writer.appendHTML().html {
            head {
                styling()
                title("FATE/Oblivion")
            }
            body {
                navbar()

                div("container mb-4") {

                    fastActions()

                    h4 { +"Criar/Editar Servos" }

                    hr()

                    div("row") {
                        div("col-md-4") {
                            div("form-group") {
                                label {
                                    forInput(id = "inputFateID")
                                    +"FateID"
                                }
                                input(type = InputType.text, classes = "form-control js-ajax-key") {
                                    id = "inputFateID"
                                    name = "id"
                                    queryParam("id")?.let { value = it }
                                }
                                small("form-text text-muted") {
                                    +"Exemplos: AdvGuildeonArcher, npcZihiraRuler"
                                }
                            }
                        }
                        div("col-md-4") {
                            p("label") { +"Fast Actions" }
                            button(type = button, classes = "btn btn-success") {
                                id = "loadFateID"
                                onClick = "loadData()"
                                +"Editar"
                            }
                            +" "
                            button(type = button, classes = "btn btn-info") {
                                id = "saveAndView"
                                onClick = "viewData()"
                                +"Visualizar"
                            }
                            +" "
                            button(type = button, classes = "btn btn-warning") {
                                id = "renameFateID"
                                onClick = "renameData()"
                                +"Renomear"
                            }
                            +" "
                            button(type = button, classes = "btn btn-danger") {
                                id = "deleteFateID"
                                onClick = "deleteData()"
                                +"Excluir"
                            }
                        }
                    }

                    hr()

                    div("form-group") {
                        label {
                            forInput(id = "inputName")
                            +"Nome"
                        }
                        input(type = InputType.text, classes = "form-control js-ajax-input") {
                            id = "inputName"
                            name = "name"
                        }
                        small("form-text text-muted") {
                            +"De preferência o nome COMPLETO do servo."
                        }
                    }

                    div("row") {
                        div("col-md") {
                            div("row") {
                                div("col-md") {
                                    div("form-group") {
                                        label {
                                            forInput(id = "inputRace")
                                            +"Raça"
                                        }
                                        input(type = InputType.text, classes = "form-control js-ajax-input") {
                                            id = "inputRace"
                                            name = "race"
                                        }
                                        small("form-text text-muted") {
                                            +"Raça ou Espécie do servo."
                                        }
                                    }
                                }
                                div("col-md-3") {
                                    div("form-group") {
                                        label {
                                            forInput(id = "inputClass")
                                            +"Classe"
                                        }
                                        select(classes = "form-control js-ajax-input") {
                                            id = "inputClass"
                                            name = "type"
                                            option {
                                                value = "null"
                                                +"Selecione..."
                                            }
                                            option {
                                                value = "archer"
                                                +"Archer"
                                            }
                                            option {
                                                value = "assassin"
                                                +"Assassin"
                                            }
                                            option {
                                                value = "avenger"
                                                +"Avenger"
                                            }
                                            option {
                                                value = "berserker"
                                                +"Berserker"
                                            }
                                            option {
                                                value = "caster"
                                                +"Caster"
                                            }
                                            option {
                                                value = "lancer"
                                                +"Lancer"
                                            }
                                            option {
                                                value = "rider"
                                                +"Rider"
                                            }
                                            option {
                                                value = "ruler"
                                                +"Ruler"
                                            }
                                            option {
                                                value = "saber"
                                                +"Saber"
                                            }
                                        }
                                        small("form-text text-muted") {
                                            +"Pergunte a Nora."
                                        }
                                    }
                                }
                            }
                            div("row") {
                                div("col-md") {
                                    div("form-group") {
                                        label {
                                            forInput(id = "inputOrigin")
                                            +"Origem"
                                        }
                                        input(type = InputType.text, classes = "form-control js-ajax-input") {
                                            id = "inputOrigin"
                                            name = "origin"
                                        }
                                        small("form-text text-muted") {
                                            +"Mesa/Filme/Mangá/Anime de origem do servo."
                                        }
                                    }
                                }
                                div("col-md-4") {
                                    div("form-group") {
                                        label {
                                            forInput(id = "inputStars")
                                            +"Poder (★)"
                                        }
                                        select(classes = "form-control js-ajax-input") {
                                            id = "inputStars"
                                            name = "stars"
                                            option {
                                                value = "null"
                                                +"Selecione..."
                                            }
                                            option {
                                                value = "0"
                                                +"★0 - Inofensivo."
                                            }
                                            option {
                                                value = "1"
                                                +"★1 - Fracote"
                                            }
                                            option {
                                                value = "2"
                                                +"★2 - Fraco"
                                            }
                                            option {
                                                value = "3"
                                                +"★3 - Abaixo da Média"
                                            }
                                            option {
                                                value = "4"
                                                +"★4 - Mediano"
                                            }
                                            option {
                                                value = "5"
                                                +"★5 - Acima da Média"
                                            }
                                            option {
                                                value = "6"
                                                +"★6 - Relativamente Forte"
                                            }
                                            option {
                                                value = "7"
                                                +"★7 - Noras e Guildeons"
                                            }
                                            option {
                                                value = "8"
                                                +"★8 - Semi-Deuses"
                                            }
                                            option {
                                                value = "9"
                                                +"★9 - Deuses"
                                            }
                                            option {
                                                value = "10"
                                                +"★10 - \"Deuses são fracos.\""
                                            }
                                        }
                                        small("form-text text-muted") {
                                            +"Pense que é YuGiOh."
                                        }
                                    }
                                }
                            }
                        }
                        div("col-md-3") {
                            p("label") { +"Imagem" }
                            div("text-center") {
                                img(src = "/img/no_avatar.png", classes = "img-fluid avatar") { id = "avatarImg" }
                            }
                            div("progress mb-3") {
                                id = "img-progressbar"
                                style = "height: 0px;"
                                div("progress-bar progress-bar-striped progress-bar-animated") {
                                    id = "img-progress"
                                    style = "width: 0%;"
                                }
                            }
                            p("text-center") {
                                button(type = button, classes = "btn btn-sm btn-success") {
                                    id = "addImage"
                                    onClick = "uploadImage()"
                                    +"Adicionar"
                                }
                                +" "
                                button(type = button, classes = "btn btn-sm btn-danger") {
                                    id = "removeImage"
                                    onClick = "removeImage()"
                                    +"Remover"
                                }
                            }
                        }
                    }

                    hr()

                    div("row") {
                        div("col-md") {
                            div("form-group") {
                                label {
                                    forInput(id = "inputDescription")
                                    +"Descrição"
                                }
                                textArea(classes = "form-control js-ajax-input", rows = "5") {
                                    id = "inputDescription"
                                    name = "description"
                                }
                                small("form-text text-muted") {
                                    +"Descrição física, Roupas, Companions, entre outras descrições."
                                }
                            }
                        }
                        div("col-md-5") {
                            div("form-group") {
                                label {
                                    forInput(id = "inputExtras")
                                    +"Extras"
                                }
                                textArea(classes = "form-control js-ajax-input", rows = "5") {
                                    id = "inputExtras"
                                    name = "extras"
                                }
                                small("form-text text-muted") {
                                    +"Passivas, Anulações, entre outras informações extras."
                                }
                            }
                        }
                    }

                    hr()

                    div("row") {
                        div("col-md") {
                            div("form-group") {
                                label {
                                    forInput(id = "inputWeapon1")
                                    +"Arma 1"
                                }
                                small { i { +" (Espíritos e Invocações permanentes são armas)" } }
                                input(type = InputType.text, classes = "form-control js-ajax-input") {
                                    id = "inputWeapon1"
                                    name = "weapon1"
                                }
                                small("form-text text-muted") {
                                    +"A arma principal do servo."
                                }
                            }

                            div("form-group") {
                                label {
                                    forInput(id = "inputWeapon1Desc")
                                    small { +"Descrição" }
                                }
                                textArea(classes = "form-control js-ajax-input", rows = "4") {
                                    id = "inputWeapon1Desc"
                                    name = "weapon1Desc"
                                }
                                small("form-text text-muted") {
                                    +"Descrição da arma principal."
                                }
                            }
                        }
                        div("col-md") {
                            div("form-group") {
                                label {
                                    forInput(id = "inputWeapon1")
                                    +"Arma 2"
                                }
                                small { i { +" (Espíritos e Invocações permanentes são armas)" } }
                                input(type = InputType.text, classes = "form-control js-ajax-input") {
                                    id = "inputWeapon2"
                                    name = "weapon2"
                                }
                                small("form-text text-muted") {
                                    +"A arma reserva do servo."
                                }
                            }

                            div("form-group") {
                                label {
                                    forInput(id = "inputWeapon2Desc")
                                    small { +"Descrição" }
                                }
                                textArea(classes = "form-control js-ajax-input", rows = "4") {
                                    id = "inputWeapon2Desc"
                                    name = "weapon2Desc"
                                }
                                small("form-text text-muted") {
                                    +"Descrição da arma reserva."
                                }
                            }
                        }
                    }

                    hr()

                    div("row") {
                        div("col-md") {
                            div("form-group") {
                                label {
                                    forInput(id = "inputSkill1")
                                    +"Habilidade 1"
                                }
                                small { i { +" (Magia \"Signature\" ou Habilidade principal)" } }
                                input(type = InputType.text, classes = "form-control js-ajax-input") {
                                    id = "inputSkill1"
                                    name = "skill1"
                                }
                                small("form-text text-muted") {
                                    +"A habilidade principal do servo."
                                }
                            }

                            div("form-group") {
                                label {
                                    forInput(id = "inputSkill1Desc")
                                    small { +"Descrição" }
                                }
                                textArea(classes = "form-control js-ajax-input", rows = "4") {
                                    id = "inputSkill1Desc"
                                    name = "skill1Desc"
                                }
                                small("form-text text-muted") {
                                    +"Descrição da habilidade principal."
                                }
                            }
                        }
                        div("col-md") {
                            div("form-group") {
                                label {
                                    forInput(id = "inputSkill2")
                                    +"Habilidade 2"
                                }
                                small { i { +" (Magia \"Signature\" ou Habilidade secondária)" } }
                                input(type = InputType.text, classes = "form-control js-ajax-input") {
                                    id = "inputSkill2"
                                    name = "skill2"
                                }
                                small("form-text text-muted") {
                                    +"A arma reserva do servo."
                                }
                            }

                            div("form-group") {
                                label {
                                    forInput(id = "inputSkill2Desc")
                                    small { +"Descrição" }
                                }
                                textArea(classes = "form-control js-ajax-input", rows = "4") {
                                    id = "inputSkill2Desc"
                                    name = "skill2Desc"
                                }
                                small("form-text text-muted") {
                                    +"Descrição da habilidade secundária."
                                }
                            }
                        }
                    }

                    hr()

                    div("row") {
                        div("col-md") {
                            div("form-group") {
                                label {
                                    forInput(id = "inputMount")
                                    +"Montaria"
                                }
                                small { i { +" (Supostamente exclusivo de Riders)" } }
                                input(type = InputType.text, classes = "form-control js-ajax-input") {
                                    id = "inputMount"
                                    name = "mount"
                                }
                                small("form-text text-muted") {
                                    +"A montaria do servo, se ele tiver."
                                }
                            }

                            div("form-group") {
                                label {
                                    forInput(id = "inputMountDesc")
                                    small { +"Descrição" }
                                }
                                textArea(classes = "form-control js-ajax-input", rows = "4") {
                                    id = "inputMountDesc"
                                    name = "mountDesc"
                                }
                                small("form-text text-muted") {
                                    +"Descrição da montaria."
                                }
                            }
                        }
                        div("col-md") {
                            div("form-group") {
                                label {
                                    forInput(id = "inputNoblePhantasm")
                                    +"Fantasma Nobre"
                                }
                                small { i { +" (Habilidade suprema do Servo)" } }
                                input(type = InputType.text, classes = "form-control js-ajax-input") {
                                    id = "inputNoblePhantasm"
                                    name = "noblePhantasm"
                                }
                                small("form-text text-muted") {
                                    +"O Fantasma Nobre do servo."
                                }
                            }

                            div("form-group") {
                                label {
                                    forInput(id = "inputNoblePhantasmDesc")
                                    small { +"Descrição" }
                                }
                                textArea(classes = "form-control js-ajax-input", rows = "4") {
                                    id = "inputNoblePhantasmDesc"
                                    name = "noblePhantasmDesc"
                                }
                                small("form-text text-muted") {
                                    +"Descrição da Fantasma Nobre."
                                }
                            }
                        }
                    }

                    div("progress mt-3 mb-3") {
                        id = "ajax-progressbar"
                        style = "height: 2px;"
                        div("progress-bar progress-bar-striped progress-bar-animated") {
                            id = "ajax-progress"
                            style = "width: 100%;"
                        }
                    }

                    div("d-flex py-2") {
                        div("px-2 flex-grow-1") { }
                        div("px-2") {
                            p("label") { +"Fast Actions" }
                            button(type = button, classes = "btn btn-info") {
                                id = "saveAndViewBottom"
                                onClick = "viewData()"
                                +"Visualizar"
                            }
                            +" "
                            button(type = button, classes = "btn btn-success") {
                                id = "saveAndNew"
                                onClick = "reloadPage()"
                                +"Novo"
                            }
                            +" "
                            button(type = button, classes = "btn btn-warning") {
                                id = "renameFateIDBottom"
                                onClick = "renameData()"
                                +"Renomear"
                            }
                            +" "
                            button(type = button, classes = "btn btn-danger") {
                                id = "deleteFateIDBottom"
                                onClick = "deleteData()"
                                +"Excluir"
                            }
                        }
                    }
                }

                footer()

                script {
                    unsafe {
                        +"""
                        var buffer = {};
                        var formFields = {};
                        var isAjaxRunning = false;
                        var finishedCallbacks = [];

                        document.addEventListener("DOMContentLoaded", function(event) {
                            document.querySelectorAll(".js-ajax-input").forEach(input => {
                                formFields[input.getAttribute("name")] = input;
                                input.addEventListener("focusout", function() {
                                    sendPatch(input.getAttribute("name"), input.value);
                                }, true);
                            });
                        });

                        function isEmpty(obj) {
                            for(var prop in obj) {
                                if(obj.hasOwnProperty(prop))
                                    return false;
                            }

                            return true;
                        }

                        function _loadJson(str) {
                            var obj = JSON.parse(str);
                            if (obj.hasOwnProperty("redirect")) {
                                window.location.href = obj.redirect;
                                return;
                            }
                            var data = obj.data;
                            for(var k in data) {
                                if(formFields.hasOwnProperty(k)) {
                                    formFields[k].value = data[k];
                                }
                            }
                            if (data.hasOwnProperty("img")) {
                                document.getElementById("avatarImg").src = data.img;
                            }
                        }

                        function _runAjax() {
                            var id = document.querySelector(".js-ajax-key").value;
                            if(id == "") {
                                isAjaxRunning = false;
                                reloadPage();
                                return;
                            }
                            var toSend = buffer;
                            buffer = {};
                            toSend["fateID"] = id;
                            const ajax = new XMLHttpRequest();
                            ajax.onreadystatechange = function() {
                                document.getElementById("ajax-progress").style.width = (this.readyState * 25) + "%";
                                if (this.readyState == 4) {
                                    if (this.status == 200) {
                                        _loadJson(this.responseText);
                                    }

                                    if (isEmpty(buffer)) {
                                        isAjaxRunning = false;
                                        var toRun = finishedCallbacks;
                                        if (isEmpty(buffer)) {
                                            finishedCallbacks = [];
                                            toRun.forEach(it => it());
                                        } else {
                                            isAjaxRunning = true;
                                            _runAjax();
                                        }
                                    } else {
                                        _runAjax();
                                    }
                                }
                            };
                            ajax.open("PATCH", "/ajax/json", true);
                            ajax.setRequestHeader("Content-Type", "application/json");
                            ajax.send(JSON.stringify(toSend));
                        }

                        /**
                         * Envia o AJAX PATCH para /ajax/json.
                         * Caso já tenha uma AJAX executando, coloca em um buffer.
                         */
                        function sendPatch(key, value) {
                            buffer[key] = value;
                            if (isAjaxRunning) return;
                            isAjaxRunning = true;
                            _runAjax();
                        }

                        /**
                         * Event Hook para quando TODOS os AJAX PATCH requests terminarem.
                         * Se não estiver nenhum executando, execute imediatamente.
                         */
                        function onSendFinished(callback) {
                            if(isAjaxRunning) {
                                finishedCallbacks.push(callback);
                            } else {
                                callback();
                            }
                        }

                        /**
                         * Executa um AJAX GET para /ajax/json?id=<inputFateID.value> e
                         * preenche os campos desta página com os valores do JSON retornado.
                         */
                        function loadData() {
                            var id = document.querySelector(".js-ajax-key").value;
                            if (id == "") {
                                reloadPage();
                                return;
                            }
                            onSendFinished(function() {
                                const ajax = new XMLHttpRequest();
                                ajax.onreadystatechange = function() {
                                    document.getElementById("ajax-progress").style.width = (this.readyState * 25) + "%";
                                    if (this.readyState == 4 && this.status == 200) {
                                        console.log(this.responseText);
                                        _loadJson(this.responseText);
                                    }
                                };
                                ajax.open("GET", "/ajax/json?id=" + id, true);
                                ajax.send();
                            });
                        }

                        /**
                         * Redirecione para /edit/rename?id=<inputFateID.value>
                         */
                        function renameData() {
                            var id = document.querySelector(".js-ajax-key").value;
                            onSendFinished(function() {
                                if (id == "") {
                                    window.location.href = "/";
                                } else {
                                    window.location.href = "/edit/modify_id/" + id;
                                }
                            });
                        }

                        /**
                         * Redirecione para /edit/delete?id=<inputFateID.value>
                         */
                        function deleteData() {
                            var id = document.querySelector(".js-ajax-key").value;
                            onSendFinished(function() {
                                if (id == "") {
                                    window.location.href = "/";
                                } else {
                                    window.location.href = "/edit/delete/" + id;
                                }
                            });
                        }

                        /**
                         * Redirecione para /view/<inputFateID.value>
                         */
                        function viewData() {
                            var id = document.querySelector(".js-ajax-key").value;
                            onSendFinished(function() {
                                if (id == "") {
                                    window.location.href = "/";
                                } else {
                                    window.location.href = "/view/" + id;
                                }
                            });
                        }

                        /**
                         * Redirecione para /edit
                         */
                        function reloadPage() {
                            onSendFinished(function() {
                                window.location.href = "/edit";
                            });
                        }

                        /**
                         * Abra um FileSelectionDialog e envie por AJAX POST para /ajax/image
                         */
                        function uploadImage() {
                            var id = document.querySelector(".js-ajax-key").value;
                            if (id == "") return;
                            var fileInput = document.createElement("INPUT");
                            fileInput.setAttribute("type", "file");
                            fileInput.setAttribute("accept", "image/*");
                            fileInput.onchange = function(e) {
                                var data = new FormData();
                                data.append("id", id);
                                data.append("file", this.files[0]);
                                var ajax = new XMLHttpRequest();
                                ajax.addEventListener("progress", function(e) {
                                    var done = e.position || e.loaded, total = e.totalSize || e.total;
                                    var n = done/total;
                                    document.getElementById("img-progress").style.width = (n * 100) + "%";
                                }, false);
                                ajax.onreadystatechange = function() {
                                    if (this.readyState == 4) {
                                        document.getElementById("img-progressbar").style.height = "0px";
                                        if (this.status == 200) {
                                            document.getElementById("avatarImg").src = this.responseText;
                                        }
                                    } else {
                                        document.getElementById("img-progressbar").style.height = "3px";
                                    }
                                };
                                document.getElementById("img-progress").style.width = "0%";
                                ajax.open("POST", "/ajax/image", true);
                                ajax.send(data);
                            };
                            fileInput.click();
                        }

                        /**
                         * Envie por AJAX DELETE para /ajax/image
                         */
                        function deleteImage() {
                            var id = document.querySelector(".js-ajax-key").value;
                            if (id == "") return;
                            var ajax = new XMLHttpRequest();
                            ajax.onreadystatechange = function() {
                                if (this.readyState == 4) {
                                    document.getElementById("img-progressbar").style.height = "0px";
                                    if (this.status == 200) {
                                        document.getElementById("avatarImg").src = this.responseText;
                                    }
                                } else {
                                    document.getElementById("img-progressbar").style.height = "3px";
                                    document.getElementById("img-progress").style.width = (this.readyState * 25) + "%";
                                }
                            };
                            document.getElementById("img-progress").style.width = "0%";
                            ajax.open("DELETE", "/ajax/image", true);
                            ajax.setRequestHeader("Content-Type", "application/json");
                            ajax.send(JSON.stringify({"fateID": id}));
                        }

                        """.trimIndent()
                        +"\n"

                        val hasID = queryParam("id") != null

                        if (!hasID) +"\n// onReady()\n/*\n"

                        +"""
                        document.addEventListener("DOMContentLoaded", function(event) {
                            loadData();
                        });
                        """.trimIndent()

                        if (!hasID) +"\n*/"
                        +"\n"
                    }
                }
            }
        }.flush()
    }
}