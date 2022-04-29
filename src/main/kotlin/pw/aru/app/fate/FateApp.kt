package pw.aru.app.fate

import io.javalin.Javalin
import org.eclipse.jetty.server.session.DefaultSessionCache
import org.eclipse.jetty.server.session.FileSessionDataStore
import org.eclipse.jetty.server.session.SessionHandler
import pw.aru.app.fate.controllers.FatePageController
import pw.aru.app.fate.controllers.FateRestController
import pw.aru.app.fate.controllers.FateStatefulController
import pw.aru.app.fate.data.provider.CachedServantProvider
import pw.aru.app.fate.data.provider.SQLiteServantProvider
import pw.aru.app.fate.tracker.SessionBasedTracker
import java.nio.file.Files

fun main() {
    val servants = CachedServantProvider(SQLiteServantProvider())
    val tracker = SessionBasedTracker()

    val pages = FatePageController(tracker, servants, tracker)
    val rest = FateRestController(servants)
    val stateful = FateStatefulController(servants)

    Javalin.create()
        .disableStartupBanner()
        .sessionHandler {
            SessionHandler().apply {
                addEventListener(tracker)
                sessionCache = DefaultSessionCache(this)
                sessionCache.sessionDataStore = FileSessionDataStore().apply {
                    storeDir = Files.createTempDirectory("javalin-").toFile()
                }
            }
        }
        .enableStaticFiles("/static")

        .get("/", pages::indexPage)
        .get("/login", pages::loginPage)
        .get("/view/:id", pages::viewPage)
        .get("/edit", pages::editPage)
        .get("/edit/modify_id/:id", pages::changeIDPage)
        .get("/edit/delete/:id", pages::deletePage)
        .get("/info", pages::infoPage)

        .get("/imgdb/:id", stateful::servantImage)
        .post("/login", stateful::doLogin)
        .get("/logout", stateful::doLogout)
        .post("/edit/modify_id", stateful::doModifyID)
        .post("/edit/delete", stateful::doDelete)

        .get("/ajax/json", rest::ajaxGet)
        .patch("/ajax/json", rest::ajaxPatch)
        .post("/ajax/image", rest::postImg)
        .delete("/ajax/image", rest::deleteImg)


        .start(25999)
}