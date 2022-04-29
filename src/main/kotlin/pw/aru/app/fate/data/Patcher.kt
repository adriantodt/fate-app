package pw.aru.app.fate.data

interface JsonPatcher {
    fun patchFromJson(data: MutableMap<String, Any?>)
    fun patchToJson(data: MutableMap<String, Any?>)
}