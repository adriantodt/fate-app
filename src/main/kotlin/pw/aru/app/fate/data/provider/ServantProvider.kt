package pw.aru.app.fate.data.provider

import pw.aru.app.fate.data.Servant
import pw.aru.app.fate.data.ServantMatch

interface ServantProvider {
    fun getById(id: String): Servant?

    fun getOrCreate(id: String): Servant

    fun getAll(): List<Servant>

    fun update(servant: Servant)

    fun remove(id: String)

    fun count(): Long

    fun stats(): ServantsStats

    fun query(s: String): List<ServantMatch>
}
