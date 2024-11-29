package com.example.ecodigify.db

import android.net.Uri
import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
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

    class IngredientListToString {
        @TypeConverter
        fun ingredientListToString(ingredients: List<Pair<String, String>>): String {
            var string = StringBuilder("[")

            for (i in ingredients.indices) {
                string.append("(")
                string.append("${ingredients[i].first} | ${ingredients[i].second}")
                string.append(")")
            }

            return string.toString()
        }

        @TypeConverter
        fun stringToListIngredient(ingredients: String): List<Pair<String, String>> {
            var list = mutableListOf<Pair<String, String>>()

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

    class PossibleNames {
        @TypeConverter
        fun namesListToString(names: List<String>): String {
            var string = StringBuilder("[")

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