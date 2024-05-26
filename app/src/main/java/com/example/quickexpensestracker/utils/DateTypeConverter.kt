<<<<<<< HEAD:app/src/main/java/com/example/quickexpensestracker/utils/DateTypeConverter.kt
package com.example.quickexpensestracker.utils
=======
package com.example.quickexpensestracker
>>>>>>> 1a2242ef8a87d4cea63d1ee776d61ab7f794da5d:app/src/main/java/com/example/quickexpensestracker/DateTypeConverter.kt

import androidx.room.TypeConverter
import java.util.*

class DateTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}