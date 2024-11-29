package com.example.ecodigify.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecodigify.dataclass.RecipeFull

@Dao
interface RecipeFullDao {
    @Query("SELECT * FROM recipefull")
    fun getAll(): List<RecipeFull>

    @Query("SELECT * FROM recipefull WHERE id = :id")
    fun get(id: Int): RecipeFull

    @Query("SELECT * FROM recipefull WHERE name = :name")
    fun get(name: String): RecipeFull

    /**
     * Add one or more RecipeFull class to the database
     *
     * You can either use the vararg as is:
     * <pre>
     * add(recipe1, recipe2)
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
    fun add(vararg recipe: RecipeFull)

    /**
     * Remove one or more RecipeFull class from the database
     *
     * You can either use the vararg as is:
     * <pre>
     * remove(recipe1, recipe2)
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
    fun remove(vararg recipe: RecipeFull)

    @Query("DELETE FROM recipefull WHERE id = :id")
    fun remove(id: Int)

    @Query("DELETE FROM ingredient")
    fun clear()
}