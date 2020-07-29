package com.example.registration_form.model

data class Table(
    var id: Long,
    var title: String,
    var date: String,
    var memberList: ArrayList<String>,
    var status: ArrayList<Boolean>,
    var paid: Int,
    var organization: String,
    var owner: String
)
