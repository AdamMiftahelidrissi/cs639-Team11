package com.example.timeflex.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.timeflex.data.Class
import java.time.LocalTime

class SharedViewModel : ViewModel() {
    private val _selectedClass = MutableStateFlow<Class?>(null)
    val selectedClass: StateFlow<Class?> = _selectedClass

    private val _clockInTime = MutableStateFlow<LocalTime?>(null)
    val clockInTime: StateFlow<LocalTime?> = _clockInTime

    fun selectClass(classItem: Class) {
        _selectedClass.value = classItem
    }

    fun clearSelectedClass() {
        _selectedClass.value = null
    }

    fun setClockInTime(time: LocalTime) {
        _clockInTime.value = time
    }

    fun clearClockInTime() {
        _clockInTime.value = null
    }
}

