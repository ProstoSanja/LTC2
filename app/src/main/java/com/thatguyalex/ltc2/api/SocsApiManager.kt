package com.thatguyalex.ltc2.api

import com.thatguyalex.ltc2.api.classes.*
import com.thatguyalex.ltc2.viewmodel.GroupsState
import com.thatguyalex.ltc2.viewmodel.MainViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.ZoneOffset

class SocsApiManager(private val model: MainViewModel) {

    private val socsApi: SocsApi

    init {
        val retrofitFactory = Retrofit.Builder()
            .baseUrl("http://www.dcs.gla.ac.uk")
            .client(
                OkHttpClient.Builder()
                    .cookieJar(SocsCookieJar())
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
        socsApi = retrofitFactory.create(SocsApi::class.java)
    }

    fun login(guid:String, password:String) {
        socsApi.login(LoginRequest(guid, password))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(model::handleApiLogin, model::handleApiError)
    }

    fun fetchGoups() {
        socsApi.getAllGroups(GroupRequest(model.guid.value!!))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(model::handleGroupFetch, model::handleApiError)
    }

    fun fetchGroup(groupsState: GroupsState) {
        socsApi.getStudentsInGroup(StudentRequest(model.guid.value!!, groupsState.courseName, groupsState.groupId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(model::handleStudentGroup, model::handleApiError)
    }

    fun submitAttendance(barcode: String, courseName: String, courseId: Int, reportError : Boolean, callback: () -> Unit) {
        val time = Instant.now().atZone(ZoneOffset.UTC)

        socsApi.submitAttendance(SubmitAttendanceRequest(barcode, time.year, time.monthValue, time.dayOfMonth, time.hour, time.minute, courseName, courseId, model.guid.value!!))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(callback, if (reportError) model::handleApiError else { _  -> })
    }

    fun deleteAttendance(barcode: String, courseName: String, courseId: Int, callback: () -> Unit) {
        val time = Instant.now().atZone(ZoneOffset.UTC)

        socsApi.deleteAttendance(DeleteAttendanceRequest(barcode, time.year, time.monthValue, time.dayOfMonth, courseName, courseId, model.guid.value!!))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(callback, model::handleApiError)
    }
}