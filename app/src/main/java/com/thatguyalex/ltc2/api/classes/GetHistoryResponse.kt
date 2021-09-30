package com.thatguyalex.ltc2.api.classes

data class GetHistoryResponse(
    val course : String,
    val date : Int,
    val month : Int,
    val year : Int,
)
