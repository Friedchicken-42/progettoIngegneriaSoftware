package com.example.ecodigify.dataclass

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.format.DateTimeFormatter

object LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}

@Parcelize
@Entity
data class Ingredient(
    @PrimaryKey
    val id: Long,
    val name: String,
    @Serializable(with = LocalDateSerializer::class)
    @ColumnInfo(name = "add_date")
    val addDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    @ColumnInfo(name = "expiration_date")
    val expirationDate: LocalDate,
    @ColumnInfo(name = "possible_names")
    val possibleNames: List<String>,
    val quantity: String
) : Parcelable
