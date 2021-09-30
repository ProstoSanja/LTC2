package com.thatguyalex.ltc2.api.classes

data class DeleteAttendanceRequest(
    val barcode : String,
    val year : Int,
    val month : Int,
    val date : Int,
    val course : String,
    val courseId : Int,
    val guid : String
)
