package com.gothwad.prepmate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [OfflineDraft::class, NotificationItem::class],
    version = 1,
    exportSchema = false
)
abstract class PrepmateDatabase : RoomDatabase() {
    abstract fun prepmateDao(): PrepmateDao

    companion object {
        @Volatile
        private var INSTANCE: PrepmateDatabase? = null

        fun getDatabase(context: Context): PrepmateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PrepmateDatabase::class.java,
                    "prepmate_database"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
