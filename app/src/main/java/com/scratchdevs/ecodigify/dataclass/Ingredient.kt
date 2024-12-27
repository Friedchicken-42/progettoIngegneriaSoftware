package com.scratchdevs.ecodigify.dataclass

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Custom serializer for [LocalDate] that uses the ISO 8601 date format.
 *
 * This serializer converts [LocalDate] objects to and from strings in the
 * ISO 8601 format (YYYY-MM-DD). It is used to serialize and deserialize
 * [LocalDate] values when working with JSON or other data formats.
 */
object LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    /**
     * Serializes a [LocalDate] value to a string in the ISO 8601 format.
     *
     * @param encoder The encoder to use for serialization.
     * @param value The [LocalDate] value to serialize.
     */
    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    /**
     * Deserializes a [LocalDate] value from a string in the ISO 8601 format.
     *
     * @param decoder The decoder to use for deserialization.
     * @return The deserialized [LocalDate] value.
     */
    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}

/**
 * Represents an ingredient in the application.
 *
 * This data class holds information about an ingredient, including its ID, name,
 * add date, expiration date, possible names, and quantity. It is used to store
 * and manage ingredient data within the application.
 *
 * @property id The unique identifier for the ingredient.
 * @property name The primary name of the ingredient.
 * @property addDate The date when the ingredient was added.
 * @property expirationDate The date when the ingredient will expire.
 * @property possibleNames A list of alternative names for the ingredient.
 * @property quantity The quantity of the ingredient.
 */
@Parcelize
@Entity
data class Ingredient(
    @PrimaryKey
    val id: Long,
    var name: String,
    @Serializable(with = LocalDateSerializer::class)
    @ColumnInfo(name = "add_date")
    val addDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    @ColumnInfo(name = "expiration_date")
    val expirationDate: LocalDate,
    val quantity: String,
    @ColumnInfo(name = "possible_names")
    var possibleNames: List<String> = listOf(name),
    @Serializable(with = LocalDateSerializer::class)
    @ColumnInfo(name = "last_notified")
    val lastNotified: LocalDate? = null,
) : Parcelable
