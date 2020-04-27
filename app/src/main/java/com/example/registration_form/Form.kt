package com.example.registration_form

data class Form(
    var id: Long,
    var title:String,
    var memberList:ArrayList<Int>,
    var status:ArrayList<Boolean>
)