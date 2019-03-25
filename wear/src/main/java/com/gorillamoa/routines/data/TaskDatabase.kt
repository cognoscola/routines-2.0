package com.gorillamoa.routines.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Task::class], version = 2)
@TypeConverters(com.gorillamoa.routines.data.TypeConverters::class)
abstract class TaskDatabase:RoomDatabase(){

    abstract fun taskDao():TaskDao

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