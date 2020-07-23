package com.example.registration_form

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TablesDB(
    context: Context, name: String = database,
    factory: SQLiteDatabase.CursorFactory? = null, version: Int = v
) :

    SQLiteOpenHelper(context, name, factory, version) {
    companion object {
        private const val database = "Tables.db"
        private const val v = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE tables (id text PRIMARY KEY,title text NOT NULL,date text NOT NULL,members text NOT NULL,status text NOT NULL,paid number NOT NULL,organization text NOT NULL,owner text NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tables")
        onCreate(db)
    }
}
