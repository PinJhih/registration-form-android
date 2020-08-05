package com.example.registration_form.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tables")
data class Table(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Long = 0L,
    @ColumnInfo(name = "title")
    var title: String = "",
    @ColumnInfo(name = "date")
    var date: String = "",
    @ColumnInfo(name = "member")
    var members: String = "",
    @ColumnInfo(name = "status")
    var status: String = "",
    @ColumnInfo(name = "paidCount")
    var paidCount: Int = 0,
    @ColumnInfo(name = "unpaidCount")
    var unpaidCount: Int = 0,
    @ColumnInfo(name = "organization")
    var organization: String = "",
    @ColumnInfo(name = "owner")
    var owner: String = ""
)
