package com.example.ecodigify.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

import com.example.ecodigify.dataclass.Ingredient
import java.time.LocalDate

class LocalDateConverter {
    @TypeConverter
    fun dateToString(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun stringToDate(date: String): LocalDate {
        return LocalDate.parse(date)
    }
}

@Database(entities = [Ingredient::class], version = 1)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientsDao(): IngredientsDao
}