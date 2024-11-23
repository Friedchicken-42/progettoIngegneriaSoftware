package com.example.ecodigify.dataclass

import java.time.LocalDate

data class Ingredient(
    val id: Int,
    val name: String,
    val expiration: LocalDate,
    val quantity: String,
)
