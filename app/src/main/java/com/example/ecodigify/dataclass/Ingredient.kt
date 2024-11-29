package com.example.ecodigify.dataclass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Ingredient(
    @PrimaryKey
    val id: Long,
    val name: String,
    @ColumnInfo(name = "expiration_date")
    val expirationDate: LocalDate,
    @ColumnInfo(name = "possible_names")
    val possibleNames: List<String>,
    val quantity: String,
)