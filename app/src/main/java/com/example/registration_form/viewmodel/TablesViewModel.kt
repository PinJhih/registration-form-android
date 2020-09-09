package com.example.registration_form.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.registration_form.database.TablesDataBase
import com.example.registration_form.database.TablesRepository
import com.example.registration_form.model.Table
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TablesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TablesRepository
    val tables: LiveData<List<Table>>

    init {
        val tablesDao = TablesDataBase.getInstance(application).tableDao()
        repository = TablesRepository(tablesDao)
        tables = repository.tables
    }

    fun insert(table: Table) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(table)
    }

    fun delete(table: Table) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(table)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

    fun sorb(orderBy: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.sorb(orderBy)
    }
}
