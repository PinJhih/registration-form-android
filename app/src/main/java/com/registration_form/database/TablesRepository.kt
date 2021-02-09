package com.registration_form.database

import androidx.lifecycle.LiveData
import com.registration_form.model.Table

class TablesRepository(private val tableDao: TableDao) {
    var tables: LiveData<List<Table>> = tableDao.getTables()
    suspend fun insert(table: Table) =
        tableDao.insert(table)

    suspend fun delete(table: Table) =
        tableDao.delete(table)

    suspend fun deleteAll() =
        tableDao.deleteAll()

    suspend fun get(id: Long) =
        tableDao.getTableByID(id)
}
