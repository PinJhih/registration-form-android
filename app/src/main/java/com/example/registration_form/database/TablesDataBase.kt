package com.example.registration_form.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.registration_form.model.Table

@Database(entities = [(Table::class)], version = 1)
abstract class TablesDataBase : RoomDatabase() {
    abstract fun tableDao(): TableDao
    companion object {
        private var INSTANCE: TablesDataBase? = null

        fun getInstance(context: Context): TablesDataBase {
            if (INSTANCE == null) {
                synchronized(TablesDataBase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        TablesDataBase::class.java,
                        TablesDataBase::class.java.simpleName
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}
