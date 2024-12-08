package com.example.ecodigify.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecodigify.dataclass.Ingredient

@Dao
interface IngredientsDao {
    @Query("SELECT * FROM ingredient")
    suspend fun getAll(): List<Ingredient>

    @Query("SELECT * FROM ingredient WHERE id = :id")
    suspend fun get(id: Int): Ingredient

    @Query("SELECT * FROM ingredient WHERE name = :name")
    suspend fun get(name: String): Ingredient

    /**
     * Add one or more Ingredient class to the database
     *
     * You can either use the vararg as is:
     * <pre>
     * add(ingredient1, ingredient2)
     * </pre>
     * or use the spread operator (*) on a list of array:
     * <pre>
     * // With an array
     * add(*myArray)
     * // With a list
     * add(*myList.toTypedArray())
     * <pre>
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(vararg ingredient: Ingredient)

    /**
     * Remove one or more Ingredient class from the database
     *
     * You can either use the vararg as is:
     * <pre>
     * remove(ingredient1, ingredient2)
     * </pre>
     * or use the spread operator (*) on a list of array:
     * <pre>
     * // With an array
     * remove(*myArray)
     * // With a list
     * remove(*myList.toTypedArray())
     * <pre>
     */
    @Delete
    suspend fun remove(vararg ingredient: Ingredient)

    @Query("DELETE FROM ingredient WHERE id = :id")
    suspend fun remove(id: Int)

    @Query("DELETE FROM ingredient")
    suspend fun clear()
}