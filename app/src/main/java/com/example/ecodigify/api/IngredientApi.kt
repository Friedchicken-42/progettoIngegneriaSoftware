package com.example.ecodigify.api

import com.example.ecodigify.dataclass.Ingredient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class IngredientApi : Api() {
    private val url = "https://world.openfoodfacts.net/api/v2/product"

    // Use only the first number found
    private val regex = "\\d+".toRegex()

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

        return Ingredient(
            id = out.code.toLong(),
            name = "",
            possibleNames = out.product.categoriesTags.map {
                if (it.contains(":")) it.drop(3)
                else it
            }.map { it.replace("-", " ") },
            addDate = LocalDate.now(),
            expirationDate = expirationDate,
            quantity = quantity,
        )
    }

    @Serializable
    private data class IngredientApiOutput(
        val code: String,
        val product: ProductApiOutput,
    )

    @Serializable
    private data class ProductApiOutput(
        @SerialName("categories_tags") val categoriesTags: List<String>,
        @SerialName("expiration_date") val expirationDate: String? = null,
        val quantity: String? = null,
    )
}