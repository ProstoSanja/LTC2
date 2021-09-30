package com.thatguyalex.ltc2.api.classes

data class StudentResponse(
    val attendedPastTwoWeeks : Boolean,
    val attendedToday : Boolean,
    val barcode : String,
    val id : Int,
    val login : String,
    val name : String,
)
