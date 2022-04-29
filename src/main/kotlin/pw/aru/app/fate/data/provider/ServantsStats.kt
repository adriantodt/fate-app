package pw.aru.app.fate.data.provider

import pw.aru.app.fate.data.ServantClass

data class ServantsStats(
    val count: Long,
    val imageCount: Long,
    val starCount: Map<Int, Int>,
    val typeCount: LinkedHashMap<ServantClass?, Int>,
    val originCount: Map<String, Int>,
    val raceCount: Map<String, Int>
)