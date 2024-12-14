package com.scratchdevs.ecodigify.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.scratchdevs.ecodigify.dataclass.Ingredient
import com.scratchdevs.ecodigify.dataclass.RecipeFull

/**
 * The main database for the application.
 *
 * This abstract class represents the Room database for the application. It defines
 * the database version, entities, and type converters. It also provides access
 * to the data access objects (DAOs) for interacting with the database.
 */
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
    Converters.PossibleNames::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract val ingredientsDao: IngredientsDao
    abstract val recipesDao: RecipeFullDao
}
