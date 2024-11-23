package com.example.ecodigify.dataclass

data class RecipeFull(
    val id: Int,
    val name: String,
    val thumb: String,
    val instructions: String,
    val ingredients: List<Pair<String, String>>,
    val source: String,
)
