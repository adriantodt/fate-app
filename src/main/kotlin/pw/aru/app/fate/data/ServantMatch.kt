package pw.aru.app.fate.data

data class ServantMatch(
    val fateID: String,
    val name: String,
    val stars: Int,
    val pattern: Regex,
    val matches: List<ServantField>
)

sealed class ServantField(val name: String) {
    class Type(name: String, val value: ServantClass) : ServantField(name)
    class Text(name: String, val value: String) : ServantField(name)
}