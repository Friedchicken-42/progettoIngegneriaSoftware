package com.example.ecodigify.dataclass

import java.time.LocalDate

data class Ingredient(
    val id: String,
    val name: String,
    val possible_names: List<String>,
    val add_date: LocalDate,
    val expiration: LocalDate,
    val quantity: String,
)
