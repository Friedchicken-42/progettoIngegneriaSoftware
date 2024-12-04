package com.example.ecodigify

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.example.ecodigify.api.IngredientApi
import com.example.ecodigify.api.RecipeApi
import com.example.ecodigify.dataclass.Ingredient
import com.example.ecodigify.dataclass.Recipe
import com.example.ecodigify.dataclass.RecipeFull
import com.example.ecodigify.db.AppDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

// Mainly glue between `Api, Database` and `UI`
object Manager {
    val recipeApi = RecipeApi()
    val ingredientApi = IngredientApi()
    var db: AppDatabase? = null

    fun init(db: AppDatabase) {
        this.db = db
    }

    suspend fun search(ing: String): List<Recipe> {
        return recipeApi.search(ing)
    }

    suspend fun inflate(recipe: Recipe): RecipeFull {
        return recipeApi.inflate(recipe)
    }

    suspend fun recipeGetAll(): List<RecipeFull> {
        return db!!.recipesDao.getAll()
    }

    suspend fun recipeGet(recipe: RecipeFull): RecipeFull? {
        return db!!.recipesDao.get(recipe.id)
    }

    suspend fun recipeRemove(recipe: RecipeFull) {
        db!!.recipesDao.remove(recipe)
    }

    suspend fun recipeAdd(recipe: RecipeFull) {
        db!!.recipesDao.add(recipe)
    }

    suspend fun ingredientGetAll(): List<Ingredient> {
        return db!!.ingredientsDao.getAll()
    }
}

// Callback-based async executor
// calls `done` with the return value of `function`
// if `function` throws, pass exception to `error` if set
// if set, calls `cleanup` always
fun <T> run(
    lifecycle: Lifecycle,
    function: suspend () -> T,
    done: (data: T) -> Unit,
    error: ((Exception) -> Unit)? = null,
    cleanup: (() -> Unit)? = null,
) {
    lifecycle.coroutineScope.launch {
        try {
            done(function())
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
