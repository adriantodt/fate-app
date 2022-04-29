package pw.aru.core.extensions

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

inline fun <R> Connection.createStatement(block: Statement.() -> R): R {
    return createStatement().use(block)
}

inline fun <R> Connection.prepareStatement(sql: String, block: PreparedStatement.() -> R): R {
    return prepareStatement(sql).use(block)
}

inline fun <R> Statement.executeQuery(sql: String, block: (ResultSet) -> R) : R {
    return executeQuery(sql).use(block)
}

inline fun <R> PreparedStatement.executeQuery(block: (ResultSet) -> R) : R {
    return executeQuery().use(block)
}

inline fun <R> Connection.executeStatementQuery(sql: String, block: (ResultSet) -> R): R {
    return createStatement { executeQuery(sql, block) }
}
