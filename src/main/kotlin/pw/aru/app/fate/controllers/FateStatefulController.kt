package pw.aru.app.fate.controllers

import ch.qos.logback.core.helpers.ThrowableToStringArray
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.BadRequestResponse
import io.javalin.Context
import pw.aru.app.fate.data.provider.CachedServantProvider
import pw.aru.app.fate.data.provider.SQLiteServantProvider
import pw.aru.app.fate.data.Servant
import pw.aru.app.fate.data.provider.ServantProvider
import java.io.ByteArrayInputStream
import javax.servlet.http.HttpSession
import kotlin.random.Random

class FateStatefulController(private val servantProvider: ServantProvider) {

    private val colors = listOf(
        "17ac86", "25c059", "277ecd", "843da4", "da004e", "e4b400", "d6680e", "d7342a", "4c6876", "161719"
    )

    fun doLogin(ctx: Context) {
        val name = ctx.formParam("username")
        val color = ctx.formParam("color")
        if (name.isNullOrBlank() || color == null) {
            ctx.redirect("/login")
            return
        }

        val session = ctx.req.session
        session.setAttribute("username", name)
        if (color == "random") {
            session.setAttribute("color", colors[Random.nextInt(colors.size)])
        } else {
            session.setAttribute("color", color)
        }

        ctx.redirect("/")
    }

    fun doLogout(ctx: Context) {
        ctx.req.session.invalidate()
        ctx.redirect("/")
    }

    fun servantImage(ctx: Context) {
        val servant = servantProvider.getById(ctx.pathParam("id")) ?: throw BadRequestResponse("No Servant")
        ctx.contentType(servant.imageExt)
        ctx.result(ByteArrayInputStream(servant.imageData))
    }

    fun doModifyID(ctx: Context) {
        val id = ctx.formParam("id") ?: throw BadRequestResponse("No fateID")
        val newID = ctx.formParam("new_id") ?: throw BadRequestResponse("No new fateID")
        val servant = servantProvider.getById(id) ?: throw BadRequestResponse("No Servant")

        servantProvider.update(servant.copy(fateID = newID))
        servantProvider.remove(servant.fateID)

        ctx.redirect("/edit?id=$newID")
    }

    fun doDelete(ctx: Context) {
        val id = ctx.formParam("id") ?: throw BadRequestResponse("No fateID")
        val servant = servantProvider.getById(id) ?: throw BadRequestResponse("No Servant")

        servantProvider.remove(servant.fateID)

        ctx.redirect("/")
    }
}