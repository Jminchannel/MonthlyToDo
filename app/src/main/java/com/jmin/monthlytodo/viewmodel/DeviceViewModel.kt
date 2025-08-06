package com.jmin.monthlytodo.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmin.monthlytodo.repository.DeviceRepository
import kotlinx.coroutines.launch

class DeviceViewModel : ViewModel() {
    private val repository = DeviceRepository()
    private val TAG = "DeviceViewModel"
    
    var isRecording by mutableStateOf(false)
        private set
        
    var recordingError by mutableStateOf<String?>(null)
        private set
    
    fun recordDeviceInfo(context: Context) {
        viewModelScope.launch {
            try {
                isRecording = true
                recordingError = null
                Log.d(TAG, "Recording device info...")
                repository.recordDeviceInfo(context)
                Log.d(TAG, "Device info recorded successfully")
            } catch (e: Exception) {
                recordingError = "记录设备信息失败: ${e.message}"
                Log.e(TAG, "Failed to record device info", e)
            } finally {
                isRecording = false
            }
        }
    }
    
    fun clearError() {
        recordingError = null
    }
}