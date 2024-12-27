package com.scratchdevs.ecodigify.api

import com.scratchdevs.ecodigify.dataclass.Ingredient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * API that retrieves ingredients from the OpenFoodFacts API
 */
class IngredientApi : Api() {
    private val url = "https://world.openfoodfacts.net/api/v2/product"

    // Use only the first number found
    private val regex = "\\d+".toRegex()

    /**
     * Retrieves an ingredient from the API given a specific barcode number
     *
     * @param code The barcode number of the ingredient.
     */
    suspend fun search(code: String): Ingredient {
        // HTTP request
        val out: IngredientApiOutput = client.get("$url/$code?product_type=food").body()

        val expirationDate = if (out.product.expirationDate.isNullOrEmpty()) {
            LocalDate.now().plusDays(7)
        } else {
            val pattern = if (out.product.expirationDate.contains("-")) {
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
            } else {
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            }

            LocalDate.parse(
                out.product.expirationDate,
                pattern
            )
        }

        val quantity = if (!out.product.quantity.isNullOrEmpty()) {
            regex.find(out.product.quantity)?.value.orEmpty()
        } else {
            "1"
        }

        fun endsWith(word: String, vararg seq: String): Boolean {
            return seq.filter { word.endsWith(it) }.any()
        }

        val possibleNames = out.product.categoriesTags
            .map {
                if (it.contains(":")) it.drop(3)
                else it
            }
            .map { it.replace("-", " ") }
            .map { word ->
                when {
                    word.endsWith("ies") -> word.dropLast(3) + "y"
                    endsWith(word, "xes", "ses", "ches", "shes") -> word.dropLast(2)
                    word.endsWith("ves") -> word.dropLast(3) + "f"
                    word.endsWith("s") && !endsWith(word, "ss", "us", "is") -> word.dropLast(1)
                    else -> word
                }
            }

        return Ingredient(
            id = out.code.toLong(),
            name = "",
            possibleNames = possibleNames,
            addDate = LocalDate.now(),
            expirationDate = expirationDate,
            quantity = quantity,
        )
    }

    /**
     * Output of the Api with the original barcode
     */
    @Serializable
    private data class IngredientApiOutput(
        val code: String,
        val product: ProductApiOutput,
    )

    /**
     * Output of the Api without the original barcode and a quantity
     */
    @Serializable
    private data class ProductApiOutput(
        @SerialName("categories_tags") val categoriesTags: List<String>,
        @SerialName("expiration_date") val expirationDate: String? = null,
        val quantity: String? = null,
    )
}