package pw.aru.core.extensions

import java.lang.Appendable

fun String?.nullIfBlank(): String? = if (isNullOrBlank()) null else this
fun String?.nullIfEmpty(): String? = if (isNullOrEmpty()) null else this

operator fun Appendable.plusAssign(s: CharSequence) {
    append(s)
}