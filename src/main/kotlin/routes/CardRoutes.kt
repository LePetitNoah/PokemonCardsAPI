package com.example.routes

import com.example.models.Card

import com.example.models.Cards
import com.example.models.CardsResponse
import com.example.models.Image
import com.example.models.Images
import com.example.models.Set
import com.example.models.Sets
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


fun Route.cardRoutes() {

    route("/cards") {

        // GET all cards
        get {
            val cards = transaction {
                Cards
                    .selectAll()
                    .map { cardRow ->
                        val set = cardRow[Cards.setId]?.let { setId ->
                            Sets.selectAll().where { Sets.id eq setId }.firstOrNull()?.let {
                                Set(
                                    id = it[Sets.id],
                                    name = it[Sets.name],
                                    series = it[Sets.series],
                                    total = it[Sets.total]
                                )
                            }
                        }

                        val image = cardRow[Cards.imageId]?.let { imgId ->
                            Images.selectAll().where { Images.id eq imgId }.firstOrNull()?.let {
                                Image(
                                    small = it[Images.small],
                                    large = it[Images.large]
                                )
                            }
                        }
                        Card(
                            id = cardRow[Cards.id],
                            name = cardRow[Cards.name],
                            set = set,
                            number = cardRow[Cards.number],
                            rarity = cardRow[Cards.rarity],
                            nationalPokedexNumbers = cardRow[Cards.nationalPokedexNumbers],
                            images = image
                        )
                    }
            }
            call.respond(CardsResponse(cards))
        }

        // POST new card
        post {
            val card = call.receive<Card>()

            transaction {
                // Insert Set if not exists
                card.set?.let { s ->
                    if (!Sets.selectAll().where { Sets.id eq (s.id ?: "") }.any()) {
                        Sets.insert {
                            it[id] = s.id ?: ""
                            it[name] = s.name ?: ""
                            it[series] = s.series
                            it[total] = s.total
                        }
                    }
                }

                // Insert image
                val id = Images.insert { stmt ->
                    stmt[Images.small] = card.images?.small
                    stmt[Images.large] = card.images?.large
                } get Images.id

                // Insert card
                Cards.insert { stmt ->
                    stmt[Cards.id] = card.id ?: ""
                    stmt[Cards.name] = card.name
                    stmt[Cards.number] = card.number
                    stmt[Cards.rarity] = card.rarity
                    stmt[Cards.setId] = card.set?.id
                    stmt[Cards.imageId] = id
                    stmt[Cards.nationalPokedexNumbers] = card.nationalPokedexNumbers
                }
            }

            call.respondText("Card added successfully!")
        }
    }

//    route("/cards/bulk") {
//        post {
//            val wrapper = call.receive<CardsWrapper>()
//            val cards = wrapper.data
//
//            transaction {
//                cards.forEach { card ->
//                    // Insert Set if not exists
//                    card.set?.let { s ->
//                        if (!Sets.selectAll().where { Sets.id eq (s.id ?: "") }.any()) {
//                            Sets.insert {
//                                it[id] = s.id ?: ""
//                                it[name] = s.name ?: ""
//                                it[series] = s.series
//                                it[total] = s.total
//                            }
//                        }
//                    }
//
//                    // Insert Image
//                    val id = Images.insert { stmt ->
//                        stmt[Images.small] = card.images?.small
//                        stmt[Images.large] = card.images?.large
//                    } get Images.id
//
//                    // Insert Card
//                    if (!Cards.selectAll().where { Cards.id eq (card.id ?: "") }.any()) {
//                        Cards.insert {
//                            it[Cards.id] = card.id ?: ""
//                            it[name] = card.name
//                            it[number] = card.number
//                            it[rarity] = card.rarity
//                            it[setId] = card.set?.id
//                            it[imageId] = id
//                            it[nationalPokedexNumbers] = card.nationalPokedexNumbers?.firstOrNull()
//                        }
//                    }
//                }
//            }
//
//            call.respondText("${cards.size} cards added successfully!")
//        }
//    }
}
