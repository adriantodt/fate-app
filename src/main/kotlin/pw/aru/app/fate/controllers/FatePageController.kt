package pw.aru.app.fate.controllers

import io.javalin.Context
import pw.aru.app.fate.data.provider.ServantProvider
import pw.aru.app.fate.tracker.FootprintListener
import pw.aru.app.fate.tracker.OnlineTracker
import pw.aru.app.fate.view.*
import pw.aru.app.fate.view.DeletePage
import pw.aru.core.extensions.nullIfBlank

class FatePageController(
    private val listener: FootprintListener,
    private val servantProvider: ServantProvider,
    private val onlineTracker: OnlineTracker
) {
    fun indexPage(ctx: Context) {
        listener.footprint(ctx)
        if (ctx.queryParam("q","").nullIfBlank() != null) {
            QueryPage(ctx, onlineTracker, servantProvider).render()
        } else {
            IndexPage(ctx, onlineTracker, servantProvider).render()
        }
    }

    fun loginPage(ctx: Context) {
        listener.footprint(ctx)
        LoginPage(ctx, onlineTracker, servantProvider).render()
    }

    fun viewPage(ctx: Context) {
        listener.footprint(ctx)
        ViewPage(ctx, onlineTracker, servantProvider).render()
    }

    fun editPage(ctx: Context) {
        listener.footprint(ctx)
        EditPage(ctx, onlineTracker, servantProvider).render()
    }

    fun infoPage(ctx: Context) {
        listener.footprint(ctx)
        InfoPage(ctx, onlineTracker, servantProvider).render()
    }

    fun changeIDPage(ctx: Context) {
        listener.footprint(ctx)
        ChangeIDPage(ctx, onlineTracker, servantProvider).render()
    }

    fun deletePage(ctx: Context) {
        listener.footprint(ctx)
        DeletePage(ctx, onlineTracker, servantProvider).render()
    }
}