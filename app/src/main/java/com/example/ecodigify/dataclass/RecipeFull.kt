package com.example.ecodigify.dataclass

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class RecipeFull(
    @PrimaryKey
    val id: Int,
    val name: String,
    val thumbnail: Uri,
    val instructions: String,
    val ingredients: List<Pair<String, String>>,
    val source: Uri,
) : Parcelable