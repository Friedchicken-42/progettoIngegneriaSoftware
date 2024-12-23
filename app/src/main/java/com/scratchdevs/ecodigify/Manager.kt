package com.scratchdevs.ecodigify

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.room.Room
import com.scratchdevs.ecodigify.api.IngredientApi
import com.scratchdevs.ecodigify.api.RecipeApi
import com.scratchdevs.ecodigify.dataclass.Ingredient
import com.scratchdevs.ecodigify.dataclass.Recipe
import com.scratchdevs.ecodigify.dataclass.RecipeFull
import com.scratchdevs.ecodigify.db.AppDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Manages data interactions between the UI, API, and database.
 *
 * This object provides methods for accessing and manipulating data related to
 * recipes and ingredients. It acts as a bridge between the UI components and
 * the underlying data sources, handling API calls, database operations, and
 * data transformations.
 */
object Manager {
    private val recipeApi = RecipeApi()
    private val ingredientApi = IngredientApi()
    private lateinit var db: AppDatabase

    /**
     * Initializes the Manager with the application context.
     *
     * This method should be called once during application startup to
     * initialize the database connection.
     *
     * @param applicationContext The application context.
     */
    fun init(applicationContext: Context) {
        this.db =
            Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "Database"
            ).build()
    }

    /**
     * This method should be called after `init` to close the database connection
     */
    fun close() {
        db.close()
    }


    /**
     * Searches for an ingredient by barcode.
     *
     * @param code The barcode to search for.
     * @return The ingredient matching the barcode, or null if not found.
     */
    suspend fun barcode(code: String): Ingredient {
        return ingredientApi.search(code)
    }

    /**
     * Searches for recipes by ingredient name.
     *
     * @param ing The ingredient name to search for.
     * @return A list of recipes containing the ingredient.
     */
    suspend fun search(ing: String): List<Recipe> {
        return recipeApi.search(ing)
    }

    /**
     * Finds recipes containing a specific ingredient.
     *
     * @param name The recipe name to search for.
     * @return A list of full recipe where the title matches `name`.
     */
    suspend fun find(name: String): List<RecipeFull> {
        return recipeApi.find(name)
    }

    /**
     * Retrieves full details for a recipe.
     *
     * @param recipe The recipe to retrieve details for.
     * @return The full recipe details.
     */
    suspend fun inflate(recipe: Recipe): RecipeFull {
        return recipeApi.inflate(recipe)
    }

    /**
     * Search recipes by name and filters them based on a list
     * of index correlated with `ingredientGetAll`
     *
     * @param name The recipe name to search for.
     * @param unwanted The ingredients to exclude.
     * @return A list of full recipe.
     */
    suspend fun filter(
        name: String,
        unwanted: List<Int>
    ): List<RecipeFull> {
        val unwantedNames = ingredientGetAll()
            .filterIndexed { i, _ -> unwanted.contains(i) }
            .map { i -> i.name.lowercase() }

        return find(name)
            .filter { recipe ->
                recipe.ingredients.all { (name, _) -> !unwantedNames.contains(name.lowercase()) }
            }
    }

    /**
     * Retrieves all recipes from the database.
     *
     * @return A list of all recipes.
     */
    suspend fun recipeGetAll(): List<RecipeFull> {
        return db.recipesDao.getAll()
    }

    /**
     * Retrieves a specific recipe from the database.
     *
     * @param recipe The recipe to retrieve.
     * @return The recipe if found, null otherwise.
     */
    suspend fun recipeGet(recipe: RecipeFull): RecipeFull? {
        return db.recipesDao.get(recipe.id)
    }

    /**
     * Removes a recipe from the database.
     *
     * @param recipe The recipe to remove.
     */
    suspend fun recipeRemove(recipe: RecipeFull) {
        db.recipesDao.remove(recipe)
    }

    /**
     * Adds a recipe to the database.
     *
     * @param recipe The recipe to add.
     */
    suspend fun recipeAdd(recipe: RecipeFull) {
        db.recipesDao.add(recipe)
    }

    /**
     * Retrieves all ingredients from the database.
     *
     * @return A list of all ingredients.
     */
    suspend fun ingredientGetAll(): List<Ingredient> {
        return db.ingredientsDao.getAll()
    }

    /**
     * Removes an ingredient from the database.
     *
     * @param ingredient The ingredient to remove.
     */
    suspend fun ingredientRemove(ingredient: Ingredient) {
        db.ingredientsDao.remove(ingredient)
    }

    /**
     * Adds an ingredient to the database.
     *
     * @param ingredient The ingredient to add.
     */
    suspend fun ingredientAdd(ingredient: Ingredient) {
        db.ingredientsDao.add(ingredient)
    }
}

/**
 * Executes a callback function asynchronously and handles the result.
 *
 * This function launches a coroutine to execute the provided callback function.
 * It then handles the result of the callback, invoking the `done` function
 * with the result if successful, or the `error` function with any exceptions
 * that occur. The `cleanup` function is always invoked after the callback
 * execution, regardless of the outcome.
 *
 * @param lifecycle The lifecycle to scope the coroutine to.
 * @param callback The suspend function to execute.
 * @param done The function to invoke with the result of the callback if successful.
 * @param error The function to invoke with any exceptions that occur during callback execution.
 * @param cleanup The function to invoke after callback execution, regardless of the outcome.
 * @return The Job representing the launched coroutine.
 */
fun <T> run(
    lifecycle: Lifecycle,
    callback: suspend () -> T,
    done: ((data: T) -> Unit)? = null,
    error: ((Exception) -> Unit)? = null,
    cleanup: (() -> Unit)? = null,
): Job {
    return lifecycle.coroutineScope.launch {
        try {
            val out = callback()
            done?.invoke(out)
        } catch (_: CancellationException) {
        } catch (e: Exception) {
            error?.invoke(e)
        }

        try {
            cleanup?.invoke()
        } catch (_: Exception) {
        }
    }
}
