package pw.aru.app.fate.tracker

interface OnlineTracker {
    data class User(val name: String, val color: String, val currentPage: String, val lastId: String?)

    fun onlineUsers(): List<User>

    fun onlineAtPage(uri: String) : List<User>

    fun onlineEditing(id: String): List<User>
}