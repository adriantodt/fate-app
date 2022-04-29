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
import java.util.concurrent.atomic.AtomicInteger
import javax.servlet.http.HttpSession
import kotlin.random.Random

class FateRestController(private val servantProvider: ServantProvider) {

    private val mapper = jacksonObjectMapper()

    fun ajaxGet(ctx: Context) {

        val session: HttpSession? = ctx.req.getSession(false)

        if (session == null) {
            ctx.contentType("application/json")
            ctx.result(mapper.writeValueAsString(mapOf("redirect" to "/login")))
            return
        }

        val id = ctx.queryParam("id") ?: throw BadRequestResponse("No fateID")

        session.setAttribute("last_ajax_id", id)

        val servant = servantProvider.getOrCreate(id)
        val servantData = mapper.convertValue<MutableMap<String, Any?>>(servant)
        Servant.patchToJson(servantData)
        ctx.contentType("application/json")
        ctx.result(mapper.writeValueAsString(mapOf("data" to servantData)))
    }

    fun ajaxPatch(ctx: Context) {
        try {
            val session: HttpSession? = ctx.req.getSession(false)

            if (session == null) {
                ctx.contentType("application/json")
                ctx.result(mapper.writeValueAsString(mapOf("redirect" to "/login")))
                return
            }

            val patchData = runCatching {
                mapper.readValue<MutableMap<String, Any?>>(ctx.body())
            }.getOrNull() ?: throw BadRequestResponse("Can't read patchData")

            val id = patchData["fateID"] as? String ?: throw BadRequestResponse("No fateID")

            session.setAttribute("last_ajax_id", id)

            val servant = servantProvider.getOrCreate(id)
            val servantData = mapper.convertValue<MutableMap<String, Any?>>(servant)
            Servant.patchToJson(servantData)
            servantData += patchData
            ctx.contentType("application/json")
            ctx.result(mapper.writeValueAsString(mapOf("data" to servantData)))
            Servant.patchFromJson(servantData)
            servantProvider.update(servant.patchClone(mapper.convertValue(servantData)))

        } catch (e: Exception) {
            ctx.status(400).result(ThrowableToStringArray.convert(e).joinToString("\n"))
        }
    }

    val counter = AtomicInteger()

    fun postImg(ctx: Context) {
        val session: HttpSession = ctx.req.getSession(false) ?: return

        val id = ctx.formParam("id") ?: throw BadRequestResponse("No fateID")

        session.setAttribute("last_ajax_id", id)

        val file = ctx.uploadedFile("file") ?: throw BadRequestResponse("No file uploaded")
        val ext = file.contentType
        val img = file.content.readBytes()

        val servant = servantProvider.getOrCreate(id)
        servant.imageData = img
        servant.imageExt = ext

        servantProvider.update(servant)

        ctx.result("/imgdb/$id?v=${counter.getAndIncrement()}")
    }

    fun deleteImg(ctx: Context) {
        val session: HttpSession? = ctx.req.getSession(false)

        if (session == null) {
            ctx.contentType("application/json")
            ctx.result(mapper.writeValueAsString(mapOf("redirect" to "/login")))
            return
        }

        val deleteData = runCatching {
            mapper.readValue<MutableMap<String, Any?>>(ctx.body())
        }.getOrNull() ?: throw BadRequestResponse("Can't read payload")

        val id = deleteData["fateID"] as? String ?: throw BadRequestResponse("No fateID")

        session.setAttribute("last_ajax_id", id)

        servantProvider.remove(id)
    }
}