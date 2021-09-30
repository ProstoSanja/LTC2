package com.thatguyalex.ltc2.api

import com.thatguyalex.ltc2.api.classes.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface SocsApi {

    @POST("/attendance/rest/login")
    fun login(@Body body: LoginRequest): Single<LoginResponse>

    @POST("/attendance/rest/getAllGroups")
    fun getAllGroups(@Body body: GroupRequest): Single<List<GroupResponse>>

    @POST("/attendance/rest/getStudentsInGroup")
    fun getStudentsInGroup(@Body body: StudentRequest): Single<List<StudentResponse>>

    @POST("/attendance/rest/submitAttendance")
    fun submitAttendance(@Body body: SubmitAttendanceRequest): Completable

    @POST("/attendance/rest/deleteAttendance")
    fun deleteAttendance(@Body body: DeleteAttendanceRequest): Completable

    @POST("/attendance/rest/getHistory")
    fun getHistory(@Body body: GetHistoryRequest): Single<Nothing>

}