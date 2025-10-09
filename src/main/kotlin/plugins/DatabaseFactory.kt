package com.example.plugins

import com.example.models.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(
            url = "jdbc:mysql://localhost:3306/pokemontcg",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root", // à adapter
            password = "SQLmdp610?happy" // à adapter
        )

        transaction {
            SchemaUtils.create(Sets, Images, Cards)
        }
    }
}
