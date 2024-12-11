package com.example.ecodigify

import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ecodigify.dataclass.Ingredient
import com.example.ecodigify.dataclass.RecipeFull
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    var ingredient = Ingredient(
        id = 1,
        name = "A",
        addDate = LocalDate.now().minusDays(4),
        expirationDate = LocalDate.now().plusDays(4),
        possibleNames = listOf("A", "B"),
        quantity = "1"
    )

    var recipe = RecipeFull(
        id = 1,
        name = "A",
        thumbnail = "http://example.org".toUri(),
        instructions = "something",
        ingredients = emptyList(),
        source = "http://example.org".toUri()
    )

    @Before
    fun initialization() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Manager.init(context)
    }

    @Test
    fun recipeTest() = runBlocking {
        // S-01
        Manager.recipeAdd(recipe)
        assert(Manager.recipeGet(recipe) == recipe)

        Manager.recipeRemove(recipe)
        assert(Manager.recipeGetAll() == listOf<RecipeFull>())
    }

    @Test
    fun ingredientTest() = runBlocking {
        // S-04
        Manager.ingredientAdd(ingredient)
        assert(Manager.ingredientGetAll() == listOf(ingredient))

        // S-02
        Manager.ingredientRemove(ingredient)
        assert(Manager.ingredientGetAll() == emptyList<Ingredient>())
    }

    @Test // S-06
    fun restartDatabase() = runBlocking {
        Manager.recipeAdd(recipe)
        Manager.ingredientAdd(ingredient)
        assert(Manager.recipeGet(recipe) == recipe)
        assert(Manager.ingredientGetAll() == listOf(ingredient))

        Manager.close()

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Manager.init(context)

        assert(Manager.recipeGet(recipe) == recipe)
        assert(Manager.ingredientGetAll() == listOf(ingredient))
    }
}