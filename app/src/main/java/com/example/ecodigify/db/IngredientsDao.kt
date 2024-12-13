package com.example.ecodigify.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecodigify.dataclass.Ingredient

/**
 * Data Access Object for interacting with the `ingredient` table in the database.
 *
 * This interface defines methods for performing CRUD (Create, Read, Update, Delete)
 * operations on the `ingredient` table. It uses Room annotations to map these
 * methods to SQL queries.
 */
@Dao
interface IngredientsDao {
    /**
     * Retrieves all ingredients from the database.
     *
     * @return A list of all ingredients.
     */
    @Query("SELECT * FROM ingredient")
    suspend fun getAll(): List<Ingredient>

    /**
     * Retrieves an ingredient by its ID.
     *
     * @param id The ID of the ingredient to retrieve.
     * @return The ingredient with the specified ID, or null if not found.
     */
    @Query("SELECT * FROM ingredient WHERE id = :id")
    suspend fun get(id: Int): Ingredient

    /**
     * Retrieves an ingredient by its name.
     *
     * @param name The name of the ingredient to retrieve.
     * @return The ingredient with the specified name, or null if not found.
     */
    @Query("SELECT * FROM ingredient WHERE name = :name")
    suspend fun get(name: String): Ingredient

    /**
     * Adds one or more ingredients to the database.
     * Either vararg:
     * <pre>
     * add(ingredient1, ingredient2)
     * </pre>
     * or the spread operator (*) can be used:
     * <pre>
     * add(*myArray)
     * add(*myList.toTypedArray())
     * <pre>
     *
     * @param ingredient The ingredient(s) to add.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(vararg ingredient: Ingredient)

    /**
     * Removes one or more ingredients from the database.
     * Either vararg:
     * <pre>
     * remove(ingredient1, ingredient2)
     * </pre>
     * or the spread operator (*) can be used:
     * <pre>
     * remove(*myArray)
     * remove(*myList.toTypedArray())
     * <pre>
     *
     * @param ingredient The ingredient(s) to remove.
     */
    @Delete
    suspend fun remove(vararg ingredient: Ingredient)

    /**
     * Removes an ingredient by its ID.
     *
     * @param id The ID of the ingredient to remove.
     */
    @Query("DELETE FROM ingredient WHERE id = :id")
    suspend fun remove(id: Int)

    /**
     * Removes all ingredients from the database.
     */
    @Query("DELETE FROM ingredient")
    suspend fun clear()
}