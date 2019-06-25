package com.gorillamoa.routines.core.data

import androidx.room.TypeConverter
import java.util.*

class TypeConverters {

    /**
     * A task carries a "Type" which we use to schedule things.
     */
    @TypeConverter
    fun IntToType(value: Int?): TaskType? {

        return when (value) {

            0 -> TaskType.TYPE_UNKNOWN
            1 -> TaskType.TYPE_HABIT
            2 -> TaskType.TYPE_GOAL
            3 -> TaskType.TYPE_SPECIAL
            else -> TaskType.TYPE_UNKNOWN
        }
    }

    @TypeConverter
    fun TypeToInt(type: TaskType): Int? {
        return when (type) {

            TaskType.TYPE_UNKNOWN -> 0
            TaskType.TYPE_GOAL -> 2
            TaskType.TYPE_HABIT -> 1
            TaskType.TYPE_SPECIAL -> 3
        }
    }

    @TypeConverter
    fun toDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun toLong(value: Date?): Long? {
        return (if (value == null) 0L else value!!.getTime()).toLong()
    }

}