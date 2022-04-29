package pw.aru.app.fate.tracker

import io.javalin.Context

interface FootprintListener {
    fun footprint(ctx: Context)
}
