package com.example.ecodigify.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecodigify.dataclass.RecipeFull

/**
 * Data Access Object for interacting with the `recipefull` table in the database.
 *
 * This interface defines methods for performing CRUD (Create, Read, Update, Delete)
 * operations on the `recipefull` table. It uses Room annotations to map these
 * methods to SQL queries.
 */
@Dao
interface RecipeFullDao {
    /**
     * Retrieves all recipes from the database.
     *
     * @return A list of all recipes.
     */
    @Query("SELECT * FROM recipefull")
    suspend fun getAll(): List<RecipeFull>

    /**
     * Retrieves a recipe by its ID.
     *
     * @param id The ID of the recipe to retrieve.
     * @return The recipe with the specified ID, or null if not found.
     */
    @Query("SELECT * FROM recipefull WHERE id = :id")
    suspend fun get(id: Int): RecipeFull?

    /**
     * Retrieves a recipe by its name.
     *
     * @param name The name of the recipe to retrieve.
     * @return The recipe with the specified name, or null if not found.
     */
    @Query("SELECT * FROM recipefull WHERE name = :name")
    suspend fun get(name: String): RecipeFull?

    /**
     * Adds one or more recipes to the database.
     * Either vararg:
     * <pre>
     * add(recipe1, recipe2)
     * </pre>
     * or the spread operator (*) can be used:
     * <pre>
     * add(*myArray)
     * add(*myList.toTypedArray())
     * <pre>
     *
     * If a recipe with the same ID already exists, it will be ignored.
     *
     * @param recipe The recipe(s) to add.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(vararg recipe: RecipeFull)

    /**
     * Removes one or more recipes from the database.
     * Either vararg:
     * <pre>
     * remove(recipe1, recipe2)
     * </pre>
     * or the spread operator (*) can be used:
     * <pre>
     * remove(*myArray)
     * remove(*myList.toTypedArray())
     * <pre>
     *
     * @param recipe The recipe(s) to remove.
     */
    @Delete
    suspend fun remove(vararg recipe: RecipeFull)

    /**
     * Removes a recipe by its ID.
     *
     * @param id The ID of the recipe to remove.
     */
    @Query("DELETE FROM recipefull WHERE id = :id")
    suspend fun remove(id: Int)

    /**
     * Removes all recipes from the database.
     */
    @Query("DELETE FROM ingredient")
    suspend fun clear()
}