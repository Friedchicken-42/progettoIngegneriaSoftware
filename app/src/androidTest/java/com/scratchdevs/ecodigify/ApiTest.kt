package com.scratchdevs.ecodigify

import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.scratchdevs.ecodigify.dataclass.Ingredient
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class ApiTest {
    @Before
    fun initialization() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Manager.init(context)
    }

    @Test // S-07
    fun ingredientTest() = runBlocking {
        val ingredient = Manager.barcode("8715035110106")

        val date = LocalDate.parse(
            "02/03/2024",
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )

        assert(ingredient.id == 8715035110106)
        assert(ingredient.addDate == LocalDate.now())
        assert(ingredient.expirationDate == date)
        assert(ingredient.possibleNames == listOf("condiment", "sauce", "soy sauce"))
        assert(ingredient.quantity == "150")
    }

    @Test // S-08
    fun recipeTest() = runBlocking {
        val recipes = Manager.find("Romanoff")
        assert(!recipes.isEmpty())

        val recipe = recipes[0]
        assert(recipe.id == 53082)
        assert(recipe.name == "Strawberries Romanoff")
        assert(recipe.source == "https://natashaskitchen.com/strawberries-romanoff-recipe/".toUri())
        assert(recipe.thumbnail == "https://www.themealdb.com/images/media/meals/oe8rg51699014028.jpg".toUri())
        assert(recipe.ingredients.size == 5)
    }

    @Test // S-09
    fun filterRecipesTest() = runBlocking {
        Manager.ingredientAdd(
            Ingredient(
                id = 1,
                name = "Onion",
                addDate = LocalDate.now(),
                expirationDate = LocalDate.now().plusDays(10),
                possibleNames = listOf("Onion"),
                quantity = "1",
            )
        )

        val recipes = Manager.find("Fish")
        val filtered = Manager.filter("Fish", unwanted = listOf(0))
        assert(recipes.size != filtered.size)
    }
}
