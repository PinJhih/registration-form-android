package com.example.registration_form.database

import androidx.lifecycle.LiveData
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

    @Query("SELECT * FROM Tables ORDER BY date ASC")
    fun getTablesASC(): LiveData<List<Table>>

    @Query("SELECT * FROM Tables ORDER BY date DESC")
    fun getTablesDESC(): LiveData<List<Table>>

    fun getTables(orderBy: String): LiveData<List<Table>> =
        if (orderBy == "DESC") getTablesDESC() else getTablesASC()

    @Delete
    fun delete(table: Table)

    @Query("DELETE FROM Tables")
    fun deleteAll()

    @Update
    fun update(table: Table)

    private fun ArrayList<Table>.toList(): List<Table> {
        val tables: MutableList<Table> = mutableListOf()
        for (i in this)
            tables.add(i)
        return tables
    }
}
