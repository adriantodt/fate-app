package pw.aru.app.fate.data.provider

import org.sqlite.SQLiteConnection
import pw.aru.app.fate.data.Servant
import pw.aru.app.fate.data.ServantClass
import pw.aru.app.fate.data.ServantField
import pw.aru.app.fate.data.ServantMatch
import pw.aru.core.extensions.*
import java.io.File
import java.sql.ResultSet
import kotlin.text.RegexOption.IGNORE_CASE

class SQLiteServantProvider : ServantProvider {
    private val connection = SQLiteConnection(File(".").absolutePath, "fate.sqlite")
    private val lock = Object()

    init {
        connection.createStatement { execute(tableCreation) }
    }

    override fun getById(id: String): Servant? {
        synchronized(lock) {
            connection.prepareStatement(getRow) {
                setString(1, id)
                executeQuery {
                    if (!it.next()) return null
                    return newServant(it)
                }
            }
        }
    }

    override fun getOrCreate(id: String): Servant {
        synchronized(lock) {
            val servant = getById(id)
            if (servant != null) return servant

            val insert = Servant(id)
            update(insert)
            return insert
        }
    }

    override fun getAll(): List<Servant> {
        synchronized(lock) {
            connection.createStatement {
                executeQuery(getAllRows) {
                    val list = ArrayList<Servant>()
                    while (it.next()) list += newServant(it)
                    return list
                }
            }
        }
    }

    override fun update(servant: Servant) {
        synchronized(lock) {
            connection.prepareStatement(updateRow) {
                setString(1, servant.fateID)
                setString(2, servant.name)
                setBytes(3, servant.imageData)
                setString(4, servant.imageExt)
                setString(5, servant.origin)
                setString(6, servant.race)
                setInt(7, servant.type?.ordinal ?: -1)
                setString(8, servant.description)
                setInt(9, servant.stars)
                setString(10, servant.catalyst)
                setString(11, servant.weapon1)
                setString(12, servant.weapon1Desc)
                setString(13, servant.weapon2)
                setString(14, servant.weapon2Desc)
                setString(15, servant.skill1)
                setString(16, servant.skill1Desc)
                setString(17, servant.skill2)
                setString(18, servant.skill2Desc)
                setString(19, servant.mount)
                setString(20, servant.mountDesc)
                setString(21, servant.extras)
                setString(22, servant.noblePhantasm)
                setString(23, servant.noblePhantasmDesc)
                execute()
            }
        }
    }

    override fun remove(id: String) {
        synchronized(lock) {
            connection.prepareStatement(deleteRow) {
                setString(1, id)
                execute()
            }
        }
    }

    override fun count(): Long {
        synchronized(lock) {
            connection.createStatement {
                executeQuery(countRows) {
                    it.next()
                    return it.getLong(1)
                }
            }
        }
    }

    override fun stats(): ServantsStats {
        synchronized(lock) {
            val count = connection.executeStatementQuery(countRows) {
                it.next()
                it.getLong(1)
            }

            val imageCount = connection.executeStatementQuery(countImgs) {
                it.next()
                it.getLong(1)
            }

            val starCount = LinkedHashMap<Int, Int>()
            connection.executeStatementQuery(groupByQuery("stars")) {
                while (it.next()) {
                    starCount[it.getInt(1)] = it.getInt(2)
                }
            }

            val typeCount = LinkedHashMap<ServantClass?, Int>()
            connection.executeStatementQuery(groupByQuery("type")) {
                while (it.next()) {
                    val type = it.getInt(1)
                    typeCount[if (type < 0) null else ServantClass.values()[type]] = it.getInt(2)
                }
            }

            val originCount = LinkedHashMap<String, Int>()
            connection.executeStatementQuery(groupByQuery("origin")) {
                while (it.next()) {
                    originCount[it.getString(1)] = it.getInt(2)
                }
            }

            val raceCount = LinkedHashMap<String, Int>()
            connection.executeStatementQuery(groupByQuery("race")) {
                while (it.next()) {
                    raceCount[it.getString(1)] = it.getInt(2)
                }
            }

            return ServantsStats(count, imageCount, starCount, typeCount, originCount, raceCount)
        }
    }

    override fun query(s: String): List<ServantMatch> {
        val regex = regexRegex.replace(s, "\\$&").replace("\\*", ".*").toRegex(IGNORE_CASE)
        val sqlPattern = "%" + sqlpatternRegex.replace(s, "\\$&").replace("*", "%") + "%"

        val i = s.toIntOrNull()
        val types = ServantClass.values().filter { regex.find(it.name) != null }
        val q = StringBuilder()

        q += "SELECT " +
                "fateID, name, origin, " +
                "race, type, description, stars, catalyst, " +
                "weapon1, weapon1Desc, weapon2, weapon2Desc, " +
                "skill1, skill1Desc, skill2, skill2Desc, " +
                "mount, mountDesc, extras, noblePhantasm, noblePhantasmDesc" +
                " FROM servants " +
                "WHERE fateID LIKE ?" +
                " OR name LIKE ?" +
                " OR origin LIKE ?" +
                " OR race LIKE ?" +
                " OR description LIKE ?" +
                " OR catalyst LIKE ?" +
                " OR weapon1 LIKE ?" +
                " OR weapon1Desc LIKE ?" +
                " OR weapon2 LIKE ?" +
                " OR weapon2Desc LIKE ?" +
                " OR skill1 LIKE ?" +
                " OR skill1Desc LIKE ?" +
                " OR skill2 LIKE ?" +
                " OR skill2Desc LIKE ?" +
                " OR mount LIKE ?" +
                " OR mountDesc LIKE ?" +
                " OR extras LIKE ?" +
                " OR noblePhantasm LIKE ?" +
                " OR noblePhantasmDesc LIKE ?"

        for (type in types) {
            q += " OR type = ?"
        }

        if (i != null) {
            q += " OR stars = ?"
        }

        q += ";"


        synchronized(lock) {
            connection.prepareStatement(q.toString()) {
                for (n in 1..19) {
                    setString(n, sqlPattern)
                }

                var n = 20
                for (type in types) {
                    setInt(n, type.ordinal)
                    n++
                }

                if (i != null) {
                    setInt(n, i)
                }

                executeQuery {
                    val results = ArrayList<ServantMatch>()

                    while (it.next()) {
                        val fieldsMatched = ArrayList<ServantField>()

                        for (field in stringFields) {
                            val value = it.getString(field)
                            if (regex.find(value) != null) {
                                fieldsMatched += ServantField.Text(field, value)
                            }
                        }

                        if (types.isNotEmpty()) {
                            val value = it.getInt("type")
                            for (type in types) {
                                if (type.ordinal == value) {
                                    fieldsMatched += ServantField.Type("type", type)
                                    break
                                }
                            }
                        }

                        results += ServantMatch(
                            it.getString("fateID"),
                            it.getString("name"),
                            it.getInt("stars"),
                            regex,
                            fieldsMatched
                        )
                    }

                    return results
                }
            }
        }
    }

    companion object {
        private val regexRegex = Regex("[\\-\\[\\]/{}()*+?.\\\\^$|]")
        private val sqlpatternRegex = Regex("[%_]")

        const val tableCreation = "CREATE TABLE IF NOT EXISTS servants (" +
                "fateID TEXT PRIMARY KEY, name TEXT, imageData BLOB, " +
                "imageExt TEXT, origin TEXT, race TEXT, type INTEGER, " +
                "description TEXT, stars INTEGER, catalyst STRING, " +
                "weapon1 TEXT, weapon1Desc TEXT, weapon2 TEXT, weapon2Desc TEXT, " +
                "skill1 TEXT, skill1Desc TEXT, skill2 TEXT, skill2Desc TEXT, " +
                "mount TEXT, mountDesc TEXT, extras TEXT, noblePhantasm TEXT, noblePhantasmDesc TEXT" +
                ");"

        const val getAllRows = "SELECT * FROM servants;"

        const val getRow = "SELECT * FROM servants WHERE fateID=?;"

        const val deleteRow = "DELETE FROM servants WHERE fateID=?;"

        const val updateRow = "INSERT OR REPLACE INTO servants (" +
                "fateID, name, imageData, imageExt, origin, " +
                "race, type, description, stars, catalyst, " +
                "weapon1, weapon1Desc, weapon2, weapon2Desc, " +
                "skill1, skill1Desc, skill2, skill2Desc, " +
                "mount, mountDesc, extras, noblePhantasm, noblePhantasmDesc" +
                ") VALUES (" +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
                ");"

        const val countRows = "SELECT count(*) FROM servants;"

        const val countImgs = "SELECT count(*) FROM servants WHERE imageExt != '';"

        fun groupByQuery(row: String) = "SELECT $row, count($row) FROM servants GROUP BY $row"

    }

    private val stringFields = listOf(
        "name", "origin", "type", "description", "catalyst",
        "weapon1", "weapon1Desc", "weapon2", "weapon2Desc",
        "skill1", "skill1Desc", "skill2", "skill2Desc",
        "mount", "mountDesc", "extras", "noblePhantasm", "noblePhantasmDesc"
    )

    private fun newServant(results: ResultSet): Servant {
        val fateID: String = results.getString("fateID")
        val name: String = results.getString("name")
        val imageData = results.getBytes("imageData") ?: Servant.EMPTY_BYTE_ARRAY
        val imageExt: String = results.getString("imageExt")
        val origin: String = results.getString("origin")
        val race: String = results.getString("race")
        val typeOrdinal = results.getInt("type")
        val type: ServantClass? = if (typeOrdinal < 0) null else ServantClass.values()[typeOrdinal]
        val description: String = results.getString("description")
        val stars: Int = results.getInt("stars")
        val catalyst: String = results.getString("catalyst")
        val weapon1: String = results.getString("weapon1")
        val weapon1Desc: String = results.getString("weapon1Desc")
        val weapon2: String = results.getString("weapon2")
        val weapon2Desc: String = results.getString("weapon2Desc")
        val skill1: String = results.getString("skill1")
        val skill1Desc: String = results.getString("skill1Desc")
        val skill2: String = results.getString("skill2")
        val skill2Desc: String = results.getString("skill2Desc")
        val mount: String = results.getString("mount")
        val mountDesc: String = results.getString("mountDesc")
        val extras: String = results.getString("extras")
        val noblePhantasm: String = results.getString("noblePhantasm")
        val noblePhantasmDesc: String = results.getString("noblePhantasmDesc")

        return Servant(
            fateID, name, imageData, imageExt, origin,
            race, type, description, stars, catalyst,
            weapon1, weapon1Desc, weapon2, weapon2Desc,
            skill1, skill1Desc, skill2, skill2Desc,
            mount, mountDesc, extras, noblePhantasm, noblePhantasmDesc
        )
    }
}

