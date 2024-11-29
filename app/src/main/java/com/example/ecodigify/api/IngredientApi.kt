package com.example.ecodigify.api

import com.example.ecodigify.dataclass.Ingredient
import io.ktor.client.call.body
import io.ktor.client.request.get
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
        println(out)

        return Ingredient(
            id = out.code.toLong(),
            name = "",
            possibleNames = out.product.categories_tags.map {
                it.removePrefix("en:").replace("-", " ")
            },
            addDate = LocalDate.now(),
            expirationDate = LocalDate.parse(
                out.product.expiration_date,
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            ),
            quantity = regex.find(out.product.quantity)?.value.orEmpty(),
        )
    }

    @Serializable
    private data class IngredientApiOutput(
        val code: String,
        val product: ProductApiOutput,
    )

    @Serializable
    private data class ProductApiOutput(
        val categories_tags: List<String>,
        val expiration_date: String,
        val quantity: String,
    )
}