package com.example.registration_form

data class Table(
    var id: Long,
    var title: String,
    var date: String,
    var memberList: ArrayList<Int>,
    var status: ArrayList<Boolean>,
    var organization: String,
    var owner: String
)
