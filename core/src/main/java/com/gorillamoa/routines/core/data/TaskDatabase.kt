package com.gorillamoa.routines.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope

/**
 * A way to access the Data in our app is through the Task Database. We have several entities:
 *
 * Task: Defines general tasks information. We have 1 entry per task.
 *
 * ID | name | description | type | date | frequency
 *
 * TaskHistory: Contains the history of every task ever completed by the user
 *
 * taskId | date&time | additional info | skipped count | completed
 *
 * DayHistory: Contains a log of the Day's progress
 *
 * date | time started | time ended | score | points | task list
 *
 *
 */
@Database(entities = [
    Task::class,
    TaskHistory::class,
    DayHistory::class], version = 4)
@TypeConverters(com.gorillamoa.routines.core.data.TypeConverters::class)
abstract class TaskDatabase:RoomDatabase(){

    abstract fun taskDao():TaskDao

    abstract fun taskHistoryDao():TaskHistoryDao

    abstract fun dayHistoryDao():DayHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

//        fun getDatabase(context: Context, scope:CoroutineScope): TaskDatabase {
        fun getDatabase(context: Context): TaskDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        TaskDatabase::class.java,
                        "Task_database"
                )
//                        .addCallback(TaskDatabaseCallback(scope))
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    class TaskDatabaseCallback(private val scope:CoroutineScope):RoomDatabase.Callback(){
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
/*
            INSTANCE?.let {
                scope.launch(Dispatchers.IO) {
                    //STUFF is happening when we open
                  */
/*
                    wordDao.deleteAll()

                    var word = Word("Hello")
                    wordDao.insert(word)
                    word = Word("World!")
                    wordDao.insert(word)
 *//*

                }
            }
*/
        }

    }
}