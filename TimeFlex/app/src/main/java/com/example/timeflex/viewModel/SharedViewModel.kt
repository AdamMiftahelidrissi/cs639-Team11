package com.example.timeflex.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.timeflex.data.Class

class SharedViewModel : ViewModel() {
    private val _selectedClass = MutableStateFlow<Class?>(null)
    val selectedClass: StateFlow<Class?> = _selectedClass

    fun selectClass(classItem: Class) {
        _selectedClass.value = classItem
    }

    fun clearSelectedClass() {
        _selectedClass.value = null
    }
}
