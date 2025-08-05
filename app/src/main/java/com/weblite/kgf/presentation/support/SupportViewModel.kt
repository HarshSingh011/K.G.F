package com.weblite.kgf.presentation.support


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weblite.kgf.domain.model.SupportMessage
import com.weblite.kgf.domain.repository.SupportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val repository: SupportRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<SupportMessage>>(emptyList())
    val messages: StateFlow<List<SupportMessage>> = _messages

    private var fetchJob: Job? = null

    fun startFetchingMessages(userId: String) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            while (true) {
                fetchCustomerMessages(userId)
                delay(2000)
            }
        }
    }

    fun stopFetchingMessages() {
        fetchJob?.cancel()
    }

    private suspend fun fetchCustomerMessages(userId: String) {
        val result = repository.getCustomerMessages(userId)
        result.onSuccess { msgs ->
            _messages.value = msgs
        }
        // No error UI, just keep last state
    }

    fun sendMessage(userId: String, message: String) {
        // Add user message immediately
        val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val userMsg = SupportMessage(message, time, true)
        _messages.value = _messages.value + userMsg
        viewModelScope.launch {
            val result = repository.sendMessage(userId, message)
            result.onSuccess { adminMsg ->
                _messages.value = _messages.value + adminMsg
            }
            // Optionally handle error (show toast, etc.)
        }
    }
}
