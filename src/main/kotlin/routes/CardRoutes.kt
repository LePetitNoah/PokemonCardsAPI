package com.example.routes

import com.example.models.CardReceive
import com.example.models.CardSend

import com.example.models.Cards
import com.example.models.CardsReceived
import com.example.models.CardsResponse
import com.example.models.Image
import com.example.models.Images
import com.example.models.Set
import com.example.models.Sets
import com.example.models.Test
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
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
                        CardSend(
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
            val card = call.receive<CardReceive>()
            print(card)
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
                    stmt[Cards.nationalPokedexNumbers] = card.nationalPokedexNumbers?.firstOrNull()
                }
            }

            call.respondText("Card added successfully!")
        }

        delete {
            try {
                val deletedCount = transaction {
                    Cards.deleteAll() // Supprime toutes les cartes de la table Cards
                }
                call.respond(HttpStatusCode.OK, mapOf(
                    "message" to "Toutes les cartes ont été supprimées.",
                    "deletedCount" to deletedCount.toString()
                ))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Impossible de supprimer les cartes.",
                    "details" to e.localizedMessage
                ))
            }
        }
    }

    route("/cards/bulk") {
        post {

                // On récupère le corps JSON envoyé
                val request = call.receive<CardsReceived>()
                // On insère en base dans une transaction Exposed
                transaction {
                    request.data.forEach { rawCard ->
                        // Extraction et transformation pour coller à ton modèle
                        val cardId = rawCard.id ?: return@forEach
                        val set = rawCard.set
                        val image = rawCard.images

                        // --- SET ---
                        var setId: String? = null
                        if (set != null && set.id != null) {
                            // On vérifie si le set existe déjà
                            val existingSet = Sets.select { Sets.id eq set.id!! }.singleOrNull()
                            if (existingSet == null) {
                                Sets.insert {
                                    it[id] = set.id!!
                                    it[name] = set.name ?: ""
                                    it[series] = set.series
                                    it[total] = set.total
                                }
                            }
                            setId = set.id
                        }

                        // --- IMAGE ---
                        var imageId: Int? = null
                        if (image != null && (image.small != null || image.large != null)) {
                            imageId = Images.insert { stmt ->
                                stmt[Images.small] = image.small
                                stmt[Images.large] = image.large
                            } get Images.id
                        }

                        // --- NATIONAL POKEDEX ---
                        val pokedexNumber = rawCard.nationalPokedexNumbers?.firstOrNull()

                        // --- CARD ---
                        // On évite les doublons (si déjà existante)
                        val existingCard = Cards.select { Cards.id eq cardId }.singleOrNull()
                        if (existingCard == null) {
                            Cards.insert {
                                it[Cards.id] = cardId
                                it[Cards.name] = rawCard.name
                                it[Cards.setId] = setId
                                it[Cards.number] = rawCard.number
                                it[Cards.rarity] = rawCard.rarity
                                it[Cards.nationalPokedexNumbers] = pokedexNumber
                                it[Cards.imageId] = imageId
                            }
                        }
                    }
                }
                call.respond(HttpStatusCode.Created, mapOf("status" to "success", "count" to request.data.size.toString()))
        }
    }
}
