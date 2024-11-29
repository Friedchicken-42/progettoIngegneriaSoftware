package com.example.ecodigify.dataclass

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeFull(
    @PrimaryKey
    val id: Int,
    val name: String,
    val thumbnail: Uri,
    val instructions: String,
    val ingredients: List<Pair<String, String>>,
    val source: Uri,
)