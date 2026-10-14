package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.CharacterClass

class Converters {
    @TypeConverter
    fun fromCharacterClass(value: CharacterClass?): String? {
        return value?.name
    }

    @TypeConverter
    fun toCharacterClass(value: String?): CharacterClass {
        return value?.let {
            try {
                CharacterClass.valueOf(it)
            } catch (e: Exception) {
                CharacterClass.WARRIOR
            }
        } ?: CharacterClass.WARRIOR
    }
}
