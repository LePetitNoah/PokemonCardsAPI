package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class CardSend(
    var id: String? = null,
    var name: String? = null,
    var set: Set? = Set(),
    var number: String? = null,
    var rarity: String? = null,
    var nationalPokedexNumbers: Int? = null,
    var images: Image? = Image(),
)

@Serializable
data class CardReceive(
    var id: String? = null,
    var name: String? = null,
    var set: Set? = Set(),
    var number: String? = null,
    var rarity: String? = null,
    var nationalPokedexNumbers: List<Int>? = null,
    var images: Image? = Image(),
)
@Serializable
data class Set(
    var id: String? = null,
    var name: String? = null,
    var series: String? = null,
    var total: Int? = null
)

@Serializable
data class Image(
    var small: String? = null,
    var large: String? = null
)

@Serializable
data class CardsResponse(
    val data: List<CardSend>
)

@Serializable
data class CardsReceived(
    val data: List<CardReceive>
)

@Serializable
data class Test(
    val content: String
)