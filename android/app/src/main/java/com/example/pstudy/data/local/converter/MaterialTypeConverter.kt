package com.example.pstudy.data.local.converter

import androidx.room.TypeConverter
import com.example.pstudy.data.model.MaterialType

class MaterialTypeConverter {
    @TypeConverter
    fun fromMaterialType(materialType: MaterialType): String {
        return materialType.name
    }
    
    @TypeConverter
    fun toMaterialType(value: String): MaterialType {
        return MaterialType.valueOf(value)
    }
}