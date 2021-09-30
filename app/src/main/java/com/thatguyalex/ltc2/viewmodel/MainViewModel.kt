package com.thatguyalex.ltc2.viewmodel

import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thatguyalex.ltc2.api.SocsApiManager
import com.thatguyalex.ltc2.api.classes.GroupResponse
import com.thatguyalex.ltc2.api.classes.LoginResponse
import com.thatguyalex.ltc2.api.classes.StudentResponse

class MainViewModel : ViewModel() {

    val socsApiManager = SocsApiManager(this)

    val authentificated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val guid: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val groupsList: MutableLiveData<List<GroupsState>> by lazy {
        MutableLiveData<List<GroupsState>>()
    }

    val selectedGroup: MutableLiveData<GroupsState> by lazy {
        MutableLiveData<GroupsState>()
    }
    val selectedGroupStudentsList: MutableLiveData<List<StudentResponse>> by lazy {
        MutableLiveData<List<StudentResponse>>()
    }

    val error: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun handleApiLogin(loginResponse: LoginResponse) {
        if (loginResponse.isTutor) {
            authentificated.value = true
        }
    }

    fun handleGroupFetch(groups: List<GroupResponse>) {
        val groupsByCourse = HashMap<String, MutableList<GroupResponse>>()
        groups.forEach {
            val newGroup = groupsByCourse.getOrDefault(it.course, mutableListOf())
            newGroup += it
            groupsByCourse[it.course] = newGroup
        }

        val newGroupsList = mutableListOf<GroupsState>()
        for (newGroup in groupsByCourse) {
            newGroupsList.add(GroupsState(true, -1, newGroup.value[0].course, newGroup.value[0].course, -1))
            for (entry in newGroup.value) {
                newGroupsList.add(GroupsState(false, entry.id, entry.name, entry.course, entry.courseId))
            }
        }
        groupsList.value = newGroupsList
    }

    fun handleStudentGroup(students: List<StudentResponse>) {
        selectedGroupStudentsList.value = students
    }

    fun handleApiError(t: Throwable) {
        t.printStackTrace()
        Log.e("TAG", t.message.toString())
        error.value = t.message.toString()
    }

}