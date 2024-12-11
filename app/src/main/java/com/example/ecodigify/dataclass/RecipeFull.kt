package com.example.ecodigify.dataclass

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Represents a full recipe in the application.
 *
 * This data class holds detailed information about a recipe, including its ID, name,
 * thumbnail image URI, instructions, ingredients, and source URI. It is used to
 * store and manage complete recipe data within the application.
 *
 * @property id The unique identifier for the recipe.
 * @property name The name of the recipe.
 * @property thumbnail The URI of the recipe's thumbnail image.
 * @property instructions The instructions for preparing the recipe.
 * @property ingredients A list of ingredient pairs, where each pair contains the
 * ingredient name and its quantity.
 * @property source The URI of the recipe's source (e.g., website URL).
 */
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