package com.example.routes

import com.example.models.Image
import com.example.models.Set
import kotlinx.serialization.Serializable

@Serializable
data class BulkCardsRequest(
    val data: List<RawCard>
)

@Serializable
data class RawCard(
    val id: String? = null,
    val name: String? = null,
    val set: Set? = null,
    val number: String? = null,
    val rarity: String? = null,
    val nationalPokedexNumbers: List<Int>? = null,
    val images: Image? = null
)