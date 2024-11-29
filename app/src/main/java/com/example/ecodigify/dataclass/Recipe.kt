package com.example.ecodigify.dataclass

import android.net.Uri

data class Recipe(
    val id: Int,
    val name: String,
    val thumbnail: Uri,
)
