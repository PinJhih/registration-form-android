package com.example.registration_form.database

import androidx.room.*
import com.example.registration_form.model.Table

@Dao
interface TableDao {
    @Insert
    fun insert(table: Table)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(table: List<Table>)

    fun insertAll(tables: ArrayList<Table>) = insertList(tables.toList())

    @Query("SELECT * FROM Tables WHERE id LIKE :id")
    fun getTableByID(id: Long): Table

    @Query("SELECT * FROM Tables ORDER BY :orderBy")
    fun getTableList(orderBy: String): List<Table>

    fun getTables(orderBy: String) = getTableList(orderBy).toArrayList()

    @Delete
    fun delete(table: Table)

    fun deleteById(id: Long) = delete(getTableByID(id))

    fun deleteAll() {
        val tables = getTableList("date")
        for (i in tables) {
            delete(i)
        }
    }

    @Update
    fun update(table: Table)

    private fun ArrayList<Table>.toList(): List<Table> {
        val tables: MutableList<Table> = mutableListOf()
        for (i in this)
            tables.add(i)
        return tables
    }

    private fun List<Table>.toArrayList(): ArrayList<Table> {
        val tables = ArrayList<Table>()
        for (i in this)
            tables.add(i)
        return tables
    }
}
