package pw.aru.app.fate.tracker

import io.javalin.Context
import javax.servlet.http.*

class SessionBasedTracker : OnlineTracker, FootprintListener,
    HttpSessionListener, HttpSessionAttributeListener, HttpSessionIdListener {

    override fun footprint(ctx: Context) {
        val session = ctx.req.getSession(false) ?: return
        session.setAttribute("last_page", ctx.req.requestURI)
    }

    override fun onlineUsers(): List<OnlineTracker.User> {
        return sessions.asSequence()
            .filter { it.name != null && it.color != null && it.lastPage != null }
            .map { OnlineTracker.User(it.name!!, it.color!!, it.lastPage!!, it.lastEdit) }
            .toList()
    }

    override fun onlineAtPage(uri: String): List<OnlineTracker.User> {
        return sessions.asSequence()
            .filter { it.name != null && it.color != null && it.lastPage == uri }
            .map { OnlineTracker.User(it.name!!, it.color!!, it.lastPage!!, it.lastEdit) }
            .toList()
    }

    override fun onlineEditing(id: String): List<OnlineTracker.User> {
        return sessions.asSequence()
            .filter { it.name != null && it.color != null && it.lastEdit == id }
            .map { OnlineTracker.User(it.name!!, it.color!!, it.lastPage!!, it.lastEdit!!) }
            .toList()
    }

    private data class TrackedSession(
        var id: String,
        var name: String?,
        var color: String?,
        var lastPage: String?,
        var lastEdit: String?
    ) {
        override fun equals(other: Any?): Boolean {
            if (other is TrackedSession) {
                return id == other.id
            }
            return false
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }
    }

    private val sessions = LinkedHashSet<TrackedSession>()

    private fun findSession(id: String) = sessions.find { it.id == id }

    override fun sessionCreated(event: HttpSessionEvent) {
        sessions += TrackedSession(event.session.id, null, null, null, null)
    }

    override fun attributeAdded(event: HttpSessionBindingEvent) {
        when (event.name) {
            "username" -> {
                findSession(event.session.id)?.name = event.value.toString()
            }
            "color" -> {
                findSession(event.session.id)?.color = event.value.toString()
            }
            "last_page" -> {
                findSession(event.session.id)?.lastPage = event.value.toString()
            }
            "last_ajax_id" -> {
                findSession(event.session.id)?.lastEdit = event.value.toString()
            }
        }
    }

    override fun attributeReplaced(event: HttpSessionBindingEvent) {
        when (event.name) {
            "username" -> {
                findSession(event.session.id)?.name = event.session.getAttribute("name").toString()
            }
            "color" -> {
                findSession(event.session.id)?.color = event.session.getAttribute("color").toString()
            }
            "last_page" -> {
                findSession(event.session.id)?.lastPage = event.session.getAttribute("last_page").toString()
            }
            "last_ajax_id" -> {
                findSession(event.session.id)?.lastEdit = event.session.getAttribute("last_ajax_id").toString()
            }
        }
    }

    override fun attributeRemoved(event: HttpSessionBindingEvent) {
        when (event.name) {
            "username" -> {
                findSession(event.session.id)?.name = null
            }
            "color" -> {
                findSession(event.session.id)?.color = null
            }
            "last_page" -> {
                findSession(event.session.id)?.lastPage = null
            }
            "last_ajax_id" -> {
                findSession(event.session.id)?.lastEdit = null
            }
        }
    }

    override fun sessionIdChanged(event: HttpSessionEvent, oldSessionId: String) {
        findSession(oldSessionId)?.id = event.session.id
    }

    override fun sessionDestroyed(event: HttpSessionEvent) {
        sessions.removeIf { it.id == event.session.id }
    }
}