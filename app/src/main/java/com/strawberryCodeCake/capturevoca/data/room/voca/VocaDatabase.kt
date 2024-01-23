package com.strawberryCodeCake.capturevoca.data.room.voca

import android.content.Context
import androidx.room.*
import com.strawberryCodeCake.capturevoca.data.room.voca.model.Voca

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Voca::class], version = 1, exportSchema = false)
abstract class VocaDatabase : RoomDatabase() {

    abstract fun vocaDao(): VocaDao

    companion object {
        @Volatile
        private var Instance: VocaDatabase? = null

        fun getDatabase(context: Context): VocaDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, VocaDatabase::class.java, "voca_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}