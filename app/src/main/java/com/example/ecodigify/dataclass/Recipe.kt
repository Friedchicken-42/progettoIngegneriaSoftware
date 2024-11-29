package com.example.ecodigify.dataclass

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Int,
    val name: String,
    val thumbnail: Uri,
) : Parcelable
