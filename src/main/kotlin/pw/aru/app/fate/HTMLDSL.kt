package pw.aru.app.fate

import kotlinx.html.HtmlTagMarker
import kotlinx.html.LABEL

@HtmlTagMarker
fun LABEL.forInput(id: String) {
    attributes["for"] = id
}
