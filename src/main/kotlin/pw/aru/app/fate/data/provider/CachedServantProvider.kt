package pw.aru.app.fate.data.provider

import pw.aru.app.fate.data.Servant
import pw.aru.app.fate.data.ServantMatch
import java.util.Collections.newSetFromMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors.newSingleThreadScheduledExecutor
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class CachedServantProvider(private val src: ServantProvider) : ServantProvider {
    private val cache = ConcurrentHashMap<String, Servant>()
    private val touchedKeySet = newSetFromMap<String>(ConcurrentHashMap())

    init {
        executor.scheduleAtFixedRate(::cleanupUntouched, 1, 1, TimeUnit.MINUTES)
    }

    override fun getById(id: String): Servant? {
        return (cache[id] ?: src.getById(id))?.keySetTouch()
    }

    override fun getOrCreate(id: String): Servant {
        return (cache[id] ?: src.getOrCreate(id)).keySetTouch()
    }

    override fun getAll(): List<Servant> {
        return src.getAll()
    }

    override fun update(servant: Servant) {
        cache[servant.fateID] = servant.keySetTouch()
        src.update(servant)
    }

    override fun remove(id: String) {
        touchedKeySet.remove(id)
        cache.remove(id)
        src.remove(id)
    }

    override fun count(): Long {
        return src.count()
    }

    override fun stats(): ServantsStats {
        return src.stats()
    }

    override fun query(s: String): List<ServantMatch> {
        return src.query(s)
    }

    private fun Servant.keySetTouch() = apply {
        touchedKeySet += fateID
    }

    private fun cleanupUntouched() {
        cache.keys.retainAll(touchedKeySet)
        touchedKeySet.clear()
    }

    companion object {
        val executor: ScheduledExecutorService = newSingleThreadScheduledExecutor()
    }
}

