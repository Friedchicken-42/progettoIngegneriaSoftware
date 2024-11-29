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
    val possible_names: List<String>,
    val quantity: String,
)