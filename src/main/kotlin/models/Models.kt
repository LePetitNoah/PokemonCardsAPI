package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Card(
    var id: String? = null,
    var name: String? = null,
    var set: Set? = Set(),
    var number: String? = null,
    var rarity: String? = null,
    var nationalPokedexNumbers: Int? = null,
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
data class CardsWrapper(
    val data: List<Card>
)