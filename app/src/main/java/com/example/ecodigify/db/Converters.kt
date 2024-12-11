package com.example.ecodigify.db

import android.net.Uri
import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * Provides type converters for Room database.
 *
 * This class contains nested classes that define type converters for various data types,
 * allowing them to be stored and retrieved from the Room database.
 */
class Converters {
    /**
     * Converts between [LocalDate] and [String].
     */
    class LocalDateToString {
        @TypeConverter
        fun localDateToString(date: LocalDate): String {
            return date.toString()
        }

        @TypeConverter
        fun stringToLocalDate(date: String): LocalDate {
            return LocalDate.parse(date)
        }
    }

    /**
     * Converts between [Uri] and [String].
     */
    class UriToString {
        @TypeConverter
        fun uriToString(uri: Uri): String {
            return uri.toString()
        }

        @TypeConverter
        fun stringToUri(uri: String): Uri {
            return Uri.parse(uri)
        }
    }

    /**
     * Converts between a list of ingredient pairs ([List]<[Pair]<[String], [String]>>)
     * and [String].
     */
    class IngredientListToString {
        @TypeConverter
        fun ingredientListToString(ingredients: List<Pair<String, String>>): String {
            val string = StringBuilder("[")

            for (i in ingredients.indices) {
                string.append("(")
                string.append("${ingredients[i].first} | ${ingredients[i].second}")
                string.append(")")
            }

            return string.toString()
        }

        @TypeConverter
        fun stringToListIngredient(ingredients: String): List<Pair<String, String>> {
            val list = mutableListOf<Pair<String, String>>()

            var i = 0
            while (i < ingredients.length) {
                if (ingredients[i] == '(') {
                    i += 1
                    val tmpString = StringBuilder()
                    while (ingredients[i] != ')') {
                        tmpString.append(ingredients[i])
                        i += 1
                    }

                    if (tmpString.isNotEmpty()) {
                        val splitedTmpString = tmpString.split(" | ")
                        list.add(Pair(splitedTmpString[0], splitedTmpString[1]))
                    }
                }
                i += 1
            }

            return list
        }
    }

    /**
     * Converts between a list of names ([List]<[String]>) and [String].
     */
    class PossibleNames {
        @TypeConverter
        fun namesListToString(names: List<String>): String {
            val string = StringBuilder("[")

            for (i in names.indices) {
                if (i != 0) string.append(" | ")
                string.append(names[i])
            }

            string.append("]")

            return string.toString()
        }

        @TypeConverter
        fun stringToNamesList(names: String): List<String> {
            return names.removePrefix("[").removeSuffix("]").split(" | ")
        }
    }
}