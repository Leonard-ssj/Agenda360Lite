package config

import env.Env
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import tables.Clients
import tables.Services
import tables.Appointments
import tables.Users

object DatabaseConfig {
    fun connect() {
        val url = Env.get("DB_URL", "jdbc:mysql://localhost:3306/agenda360lite?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true")!!
        val user = Env.get("DB_USER", "agenda_user")!!
        val pass = Env.get("DB_PASS", "agenda_pass")!!

        val dbName = Regex("jdbc:mysql://[^/]+/([^?]+)").find(url)?.groupValues?.get(1) ?: "agenda360lite"
        val baseUrl = url.replace("/$dbName", "/")

        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            java.sql.DriverManager.getConnection(baseUrl, user, pass).use { conn ->
                conn.createStatement().use { st ->
                    st.executeUpdate("CREATE DATABASE IF NOT EXISTS `$dbName` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
                }
            }
        } catch (_: Exception) {
        }

        val hikari = HikariConfig().apply {
            jdbcUrl = url
            username = user
            password = pass
            driverClassName = "com.mysql.cj.jdbc.Driver"
            maximumPoolSize = 5
            addDataSourceProperty("allowPublicKeyRetrieval", "true")
        }
        Database.connect(HikariDataSource(hikari))
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Users, Clients, Services, Appointments)
        }
    }
}
