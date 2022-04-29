package pw.aru.app.fate.data

data class Servant(
    val fateID: String,
    var name: String,
    var imageData: ByteArray = EMPTY_BYTE_ARRAY,
    var imageExt: String = "",
    var origin: String,
    var race: String,
    var type: ServantClass?,
    var description: String,
    var stars: Int,
    var catalyst: String,
    var weapon1: String,
    var weapon1Desc: String,
    var weapon2: String,
    var weapon2Desc: String,
    var skill1: String,
    var skill1Desc: String,
    var skill2: String,
    var skill2Desc: String,
    var mount: String,
    var mountDesc: String,
    var extras: String,
    var noblePhantasm: String,
    var noblePhantasmDesc: String
) {
    constructor(fateID: String) : this(
        fateID, "", EMPTY_BYTE_ARRAY, "", "", "", null,
        "", 0, "", "", "", "", "",
        "", "", "", "", "", "",
        "", "", ""
    )

    fun patchClone(clone: Servant): Servant {
        return clone.copy(imageData = imageData, imageExt = imageExt)
    }

    fun completed(): Boolean {
        if (name.isBlank()) return false
        if (origin.isBlank()) return false
        if (race.isBlank()) return false
        if (type == null) return false
        if (description.isBlank()) return false
        if (weapon1.isBlank() || weapon1Desc.isBlank()) return false
        if (weapon2.isNotBlank() && weapon2Desc.isBlank()) return false
        if (skill1.isBlank() || skill1Desc.isBlank()) return false
        if (skill2.isNotBlank() && skill2Desc.isBlank()) return false
        if (type == ServantClass.RIDER && (mount.isBlank() || mountDesc.isBlank())) return false
        if (noblePhantasm.isBlank() || noblePhantasmDesc.isBlank()) return false
        return true
    }

    companion object : JsonPatcher {
        val EMPTY_BYTE_ARRAY = ByteArray(0)
        override fun patchFromJson(data: MutableMap<String, Any?>) {
            data.remove("imageData")
            data.remove("imageExt")
            data.remove("img")

            if (data.containsKey("type")) {
                data["type"].let {
                    if (it == "null") {
                        data["type"] = null
                    } else {
                        data["type"] = it.toString().toUpperCase()
                    }
                }
            }
            data.computeIfPresent("stars") { _, v -> v.toString().toIntOrNull() }
        }

        override fun patchToJson(data: MutableMap<String, Any?>) {
            data.remove("imageData")
            val ext = data.remove("imageExt")?.toString()

            if (ext.isNullOrEmpty()) {
                data["img"] = "/img/no_avatar.png"
            } else {
                data["img"] = "/imgdb/${data["fateID"]}"
            }

            if (data.containsKey("type")) {
                data.compute("type") { _, v -> v.toString().toLowerCase() }
            }
            data.computeIfPresent("stars") { _, v -> v.toString() }
        }
    }
}
