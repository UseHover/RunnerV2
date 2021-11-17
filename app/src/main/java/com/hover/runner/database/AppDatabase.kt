package com.hover.runner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hover.runner.transaction.repo.RunnerTransactionDao
import com.hover.runner.transaction.model.RunnerTransaction
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Database(entities = [RunnerTransaction::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun runnerTransactionDao(): RunnerTransactionDao

    companion object {
        private const val NUMBER_OF_THREADS = 8
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        @Volatile
        var INSTANCE: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "runner.db"
                        )
                            .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)
                            .build()
                    }
                }
            }
            return INSTANCE
        }
    }

}