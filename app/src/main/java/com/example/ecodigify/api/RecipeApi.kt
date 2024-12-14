package com.example.ecodigify.api

import androidx.core.net.toUri
import com.example.ecodigify.dataclass.Recipe
import com.example.ecodigify.dataclass.RecipeFull
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

/**
 * API that retrieves recipes from the Themealdb API
 */
class RecipeApi : Api() {
    private val url = "https://www.themealdb.com/api/json/v1/1"

    /**
     * Given an ingredient's name, find fitting recipes for it
     *
     * @param ingredient The name of the required ingredient
     * @return Returns a list of `Recipe` with the specific ingredient
     */
    suspend fun search(ingredient: String): List<Recipe> {
        val out: ApiList<RecipeApiOutput> = client.get("$url/filter.php?i=$ingredient").body()
        return out.meals.map { recipe ->
            Recipe(
                id = recipe.idMeal,
                name = recipe.strMeal,
                thumbnail = recipe.strMealThumb.toUri()
            )
        }
    }

    /**
     * Given a recipe's name, find fitting recipes for it
     *
     * @param name The partial/full name of the recipe
     * @return Returns a list of `RecipeFull` with the specific name
     */
    suspend fun find(name: String): List<RecipeFull> {
        val out: ApiList<RecipeFullApiOutput> = client.get("$url/search.php?s=$name").body()
        return out.meals.map { recipe ->
            RecipeFull(
                id = recipe.idMeal,
                name = recipe.strMeal,
                thumbnail = recipe.strMealThumb.toUri(),
                instructions = recipe.strInstructions,
                ingredients = recipe.ingredients,
                source = recipe.source.toUri()
            )
        }
    }

    /**
     * Given a recipe of type `Recipe`, retrieve all it's details by doing an id lookup
     *
     * @param recipe The recipe to add details to
     * @return Returns a `RecipeFull` of the same recipe, but with more details
     */
    suspend fun inflate(recipe: Recipe): RecipeFull {
        val out: ApiList<RecipeFullApiOutput> = client.get("$url/lookup.php?i=${recipe.id}").body()

        @Suppress("NAME_SHADOWING")
        val recipe = out.meals[0]

        return RecipeFull(
            id = recipe.idMeal,
            name = recipe.strMeal,
            thumbnail = recipe.strMealThumb.toUri(),
            instructions = recipe.strInstructions,
            ingredients = recipe.ingredients,
            source = recipe.source.toUri()
        )
    }

    /**
     * Represents a list of items received from the API.
     *
     * @param E The type of items in the list.
     * @property meals The list of items.
     */
    @Serializable
    private data class ApiList<E>(
        val meals: List<E>
    )

    /**
     * Represents a basic recipe output from the API.
     *
     * @property strMeal The name of the recipe.
     * @property strMealThumb The URL of the recipe's thumbnail image.
     * @property idMeal The ID of the recipe.
     */
    @Serializable
    private data class RecipeApiOutput(
        val strMeal: String,
        val strMealThumb: String,
        val idMeal: Int,
    )

    /**
     * Represents a full recipe output from the API.
     *
     * @property idMeal The ID of the recipe.
     * @property strMeal The name of the recipe.
     * @property strMealThumb The URL of the recipe's thumbnail image.
     * @property strInstructions The instructions for preparing the recipe.
     * @property source The source of the recipe (e.g., website URL).
     * @property ingredients A list of ingredient pairs, where each pair contains the ingredient name and its quantity.
     */
    @Serializable(with = RecipeFullSerializer::class)
    data class RecipeFullApiOutput(
        val idMeal: Int,
        val strMeal: String,
        val strMealThumb: String,
        val strInstructions: String,
        val source: String,
        val ingredients: List<Pair<String, String>>,
    )

    /**
     * A custom serializer for [RecipeFullApiOutput] that handles the specific JSON structure
     * received from the API.
     *
     * This serializer is responsible for converting the JSON data into a [RecipeFullApiOutput]
     * object and vice versa. It handles the simple conversion of string and integer fields
     * as well as the custom conversion of the ingredients list, which is stored in a
     * specific format in the JSON data.
     */
    object RecipeFullSerializer : KSerializer<RecipeFullApiOutput> {
        @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RecipeFull") {
            element("idMeal", buildSerialDescriptor("id", PrimitiveKind.INT))
            element("strMeal", buildSerialDescriptor("strMeal", PrimitiveKind.STRING))
            element("strMealThumb", buildSerialDescriptor("strMealThumb", PrimitiveKind.STRING))
            element(
                "strInstructions",
                buildSerialDescriptor("strInstructions", PrimitiveKind.STRING)
            )
            element("source", buildSerialDescriptor("source", PrimitiveKind.STRING))
        }

        override fun serialize(
            encoder: Encoder,
            value: RecipeFullApiOutput
        ) {
            // Only deserializer is needed
            TODO("Not necessary now")
        }

        /**
         * Deserializes a [RecipeFullApiOutput] object from the given [decoder].
         *
         * This function extracts the necessary data from the JSON object and creates
         * a [RecipeFullApiOutput] instance. It handles the simple conversion of string
         * and integer fields as well as the custom conversion of the ingredients list.
         *
         * @param decoder The decoder to use for deserialization.
         * @return The deserialized [RecipeFullApiOutput] object.
         * @throws SerializationException If any required data is missing or invalid.
         */
        override fun deserialize(decoder: Decoder): RecipeFullApiOutput {
            require(decoder is JsonDecoder)
            val jsonObj = decoder.decodeJsonElement() as JsonObject

            // Simple conversion for `string` and `int` fields
            val idMeal = jsonObj["idMeal"]?.jsonPrimitive?.int
                ?: throw SerializationException("Missing 'idMeal'")
            val strMeal = jsonObj["strMeal"]?.jsonPrimitive?.content
                ?: throw SerializationException("Missing 'strMeal'")
            val strMealThumb = jsonObj["strMealThumb"]?.jsonPrimitive?.content
                ?: throw SerializationException("Missing 'strMealThumb'")
            val strInstructions = jsonObj["strInstructions"]?.jsonPrimitive?.content
                ?: throw SerializationException("Missing 'strInstructions'")
            val source = jsonObj["strSource"]?.jsonPrimitive?.content
                ?: throw SerializationException("Missing 'source'")

            // Custom conversion for `List<Pair<String, String>>`
            // in the JSON data is stored as `{ "strIngredientN": x, "strMeasureN": y, ... }`,
            // so we match them based on `N`
            val pairs = jsonObj.entries
                .filter { it.key.startsWith("strIngredient") }
                .filter {
                    // remove empty values
                    it.value.jsonPrimitive.content.isNotEmpty() && it.value.jsonPrimitive.content != "null"
                }
                .map { (k, v) ->
                    val index = k.substring(13)
                    val key = "strMeasure$index"

                    val ingredient = v.jsonPrimitive.content.trim()

                    // match based on `N` (`index`)
                    val quantity = jsonObj[key]?.jsonPrimitive?.content?.trim()
                        ?: throw SerializationException("Missing '$key' for $k ($index)")

                    Pair(ingredient, quantity)
                }

            return RecipeFullApiOutput(
                idMeal, strMeal, strMealThumb, strInstructions, source,
                ingredients = pairs
            )
        }

    }
}