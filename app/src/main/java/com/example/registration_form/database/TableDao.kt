package com.example.registration_form.database

import androidx.room.*
import com.example.registration_form.model.Table

@Dao
interface TableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(table: Table)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(table: List<Table>)

    @Query("SELECT * FROM Tables WHERE id LIKE :id")
    fun getTableByID(id: Long): List<Table>


    @Query("SELECT * FROM Tables ORDER BY :orderBy")
    fun getTables(orderBy: String): List<Table>

    @Delete
    fun delete(table: Table)

    @Update
    fun update(table: Table)
}
