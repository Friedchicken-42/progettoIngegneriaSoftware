package com.example.ecodigify

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.example.ecodigify.api.IngredientApi
import com.example.ecodigify.api.RecipeApi
import com.example.ecodigify.dataclass.Ingredient
import com.example.ecodigify.dataclass.Recipe
import com.example.ecodigify.dataclass.RecipeFull
import com.example.ecodigify.db.AppDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.room.Room

// Mainly glue between `Api, Database` and `UI`
object Manager {
    private val recipeApi = RecipeApi()
    private val ingredientApi = IngredientApi()
    private var db: AppDatabase? = null

    fun init(applicationContext: Context) {
        this.db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "Database").build()
    }

    suspend fun barcode(code: String): Ingredient {
        return ingredientApi.search(code)
    }

    suspend fun search(ing: String): List<Recipe> {
        return recipeApi.search(ing)
    }

    suspend fun find(ing: String): List<RecipeFull> {
        return recipeApi.find(ing)
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

    suspend fun ingredientRemove(ingredient: Ingredient) {
        db!!.ingredientsDao.remove(ingredient)
    }

    suspend fun ingredientAdd(ingredient: Ingredient) {
        db!!.ingredientsDao.add(ingredient)
    }
}

// Callback-based async executor
// calls `done` with the return value of `function`
// if `function` throws, pass exception to `error` if set
// if set, calls `cleanup` always
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
