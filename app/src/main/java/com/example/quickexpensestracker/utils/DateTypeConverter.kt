package com.example.quickexpensestracker.utils

import androidx.room.TypeConverter // Import TypeConverter annotation from Room library for defining type conversion methods.
import java.util.* // Import java.util package for Date class.

class DateTypeConverter {
    @TypeConverter // Annotate fromTimestamp method with TypeConverter to specify it as a type conversion method.
    fun fromTimestamp(value: Long?): Date? { // Define fromTimestamp method to convert Long value to Date.
        return if (value == null) null else Date(value) // Convert Long value to Date or return null if the value is null.
    }

    @TypeConverter // Annotate dateToTimestamp method with TypeConverter to specify it as a type conversion method.
    fun dateToTimestamp(date: Date?): Long? { // Define dateToTimestamp method to convert Date to Long value.
        return date?.time // Return the time value of the Date object as Long, or return null if the date is null.
    }
}
