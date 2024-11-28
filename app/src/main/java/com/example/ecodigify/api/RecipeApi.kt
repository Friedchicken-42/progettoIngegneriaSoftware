package com.example.ecodigify.api

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

class RecipeApi : Api() {
    private val url = "https://www.themealdb.com/api/json/v1/1"

    // Returns all the `Recipe` with a specific ingredient
    suspend fun search(ingredient: String): List<Recipe> {
        val out: ApiList<RecipeApiOutput> = client.get("$url/filter.php?i=$ingredient").body()
        return out.meals.map { recipe ->
            Recipe(
                id = recipe.idMeal,
                name = recipe.strMeal,
                thumb = recipe.strMealThumb
            )
        }
    }

    // Returns all the `RecipeFull` that maches `name`
    suspend fun find(name: String): List<RecipeFull> {
        val out: ApiList<RecipeFullApiOutput> = client.get("$url/search.php?s=$name").body()
        return out.meals.map { recipe ->
            RecipeFull(
                id = recipe.idMeal,
                name = recipe.strMeal,
                thumb = recipe.strMealThumb,
                instructions = recipe.strInstructions,
                ingredients = recipe.ingredients,
                source = recipe.source
            )
        }
    }

    // Convert a `Recipe` into a `RecipeFull` by doing an id lookup
    suspend fun inflate(recipe: Recipe): RecipeFull {
        val out: ApiList<RecipeFullApiOutput> = client.get("$url/lookup.php?i=${recipe.id}").body()
        val recipe = out.meals[0]

        return RecipeFull(
            id = recipe.idMeal,
            name = recipe.strMeal,
            thumb = recipe.strMealThumb,
            instructions = recipe.strInstructions,
            ingredients = recipe.ingredients,
            source = recipe.source
        )
    }

    // Private classes that match the JSON format returned
    @Serializable
    private data class ApiList<E>(
        val meals: List<E>
    )

    @Serializable
    private data class RecipeApiOutput(
        val strMeal: String,
        val strMealThumb: String,
        val idMeal: Int,
    )

    @Serializable(with = RecipeFullSerializer::class)
    data class RecipeFullApiOutput(
        val idMeal: Int,
        val strMeal: String,
        val strMealThumb: String,
        val strInstructions: String,
        val source: String,
        val ingredients: List<Pair<String, String>>,
    )

    // Custom deserializer for `RecipeFullApiOutput`
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

                    val a = v.jsonPrimitive.content

                    // match based on `N` (`index`)
                    val b = jsonObj[key]?.jsonPrimitive?.content
                        ?: throw SerializationException("Missing '$key' for $k ($index)")

                    Pair(a, b)
                }

            return RecipeFullApiOutput(
                idMeal, strMeal, strMealThumb, strInstructions, source,
                ingredients = pairs
            )
        }

    }
}