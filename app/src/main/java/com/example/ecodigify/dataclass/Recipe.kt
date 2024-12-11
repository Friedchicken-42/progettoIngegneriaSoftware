package com.example.ecodigify.dataclass

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents a recipe in the application.
 *
 * This data class holds basic information about a recipe, including its ID, name,
 * and thumbnail image URI. It is used to store and manage recipe data within
 * the application.
 *
 * @property id The unique identifier for the recipe.
 * @property name The name of the recipe.
 * @property thumbnail The URI of the recipe's thumbnail image.
 */
@Parcelize
data class Recipe(
    val id: Int,
    val name: String,
    val thumbnail: Uri,
) : Parcelable
