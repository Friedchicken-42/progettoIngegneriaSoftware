package com.example.ecodigify.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.example.ecodigify.dataclass.Ingredient
import com.example.ecodigify.dataclass.RecipeFull

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        Ingredient::class,
        RecipeFull::class,
    ],
)
@TypeConverters(
    Converters.LocalDateToString::class,
    Converters.UriToString::class,
    Converters.IngredientListToString::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract val ingredientsDao: IngredientsDao
    abstract val recipesDao: RecipeFullDao
}