package com.example.ecodigify.dataclass

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    @PrimaryKey
    val id: Int,
    val name: String,
    val thumbnail: Uri,
    val instructions: String? = null,
    val ingredients: List<Pair<String, Int>>? = null,
    val source: Uri? = null,
)