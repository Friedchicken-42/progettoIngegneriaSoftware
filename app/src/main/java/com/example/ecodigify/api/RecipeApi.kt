package com.example.ecodigify.api

import com.example.ecodigify.dataclass.Recipe
import com.example.ecodigify.dataclass.RecipeFull
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

class RecipeApi {
    private val url = "https://www.themealdb.com/api/json/v1/1"

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.BODY
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }

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
            TODO("Not necessary now")
        }

        override fun deserialize(decoder: Decoder): RecipeFullApiOutput {
            require(decoder is JsonDecoder)
            val jsonObj = decoder.decodeJsonElement() as JsonObject

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

            val pairs = jsonObj.entries
                .filter { it.key.startsWith("strIngredient") }
                .filter {
                    it.value.jsonPrimitive.content.isNotEmpty() && it.value.jsonPrimitive.content != "null"
                }
                .map { (k, v) ->
                    val index = k.substring(13)
                    val key = "strMeasure$index"

                    val a = v.jsonPrimitive.content
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