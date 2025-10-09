package com.example.models

import org.jetbrains.exposed.sql.Table

object Sets : Table() {
    val id = varchar("id", 50)
    val name = varchar("name", 100)
    val series = varchar("series", 100).nullable()
    val total = integer("total").nullable()
    override val primaryKey = PrimaryKey(id)
}

object Images : Table() {
    val id = integer("id").autoIncrement()
    val small = varchar("small", 255).nullable()
    val large = varchar("large", 255).nullable()
    override val primaryKey = PrimaryKey(id)
}

object Cards : Table() {
    val id = varchar("id", 50)
    val name = varchar("name", 100).nullable()
    val setId = varchar("set_id", 50).references(Sets.id).nullable()
    val number = varchar("number", 20).nullable()
    val rarity = varchar("rarity", 50).nullable()
    val nationalPokedexNumbers = integer("national_pokedex_number").nullable()
    val imageId = integer("image_id").references(Images.id).nullable()
    override val primaryKey = PrimaryKey(id)
}
