/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.timepack.countpose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimePackViewModel @Inject constructor() : ViewModel() {

    private var job: Job? = null

    val timeState = MutableStateFlow(0L)
    val alertState = MutableStateFlow(false)

    fun setupInitTime(time: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            timeState.emit(time)
            alertState.emit(false)
        }
    }

    // Time in second
    fun startTimer(time: Long) {
        stop()
        job = viewModelScope.launch(Dispatchers.IO) {
            timeState.emit(time)
            while (isActive) {
                if (timeState.value <= 0) {
                    cancel()
                    return@launch
                }
                alertState.emit(timeState.value <= 5)
                delay(1_000)
                timeState.emit(timeState.value - 1)
            }
        }
    }

    fun stop(time: Long = 0) {
        job?.cancel()
        setupInitTime(time)
    }
}
