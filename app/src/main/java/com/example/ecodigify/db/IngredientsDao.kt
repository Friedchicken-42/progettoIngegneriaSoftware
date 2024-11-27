package com.example.ecodigify.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecodigify.dataclass.Ingredient

@Dao
interface IngredientsDao {
    @Query("SELECT * FROM ingredient WHERE id = :id")
    fun get(id: Int): Ingredient

    @Query("SELECT * FROM ingredient WHERE name = :name")
    fun get(name: String): Ingredient

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(vararg ingredient: Ingredient)

    @Delete
    fun remove(vararg ingredient: Ingredient)

    @Query("DELETE FROM ingredient WHERE id = :id")
    fun remove(id: Int)

    @Query("DELETE FROM ingredient")
    fun clear()
}