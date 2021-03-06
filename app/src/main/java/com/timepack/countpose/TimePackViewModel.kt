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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimePackViewModel @Inject constructor() : ViewModel() {

    private var job: Job? = null

    val timeState = MutableLiveData(0L)
    val alertState = MutableLiveData(false)
    val alertMessage = MutableLiveData(false)

    fun setupInitTime(time: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            timeState.postValue(time)
            alertState.postValue(false)
            alertMessage.postValue(false)
        }
    }

    // Time in second
    fun startTimer() {
        stop()
        job = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                if (timeState.value!! <= 0L) {
                    job?.cancel()
                    return@launch
                }
                alertMessage.postValue(timeState.value!! - 1 == 0L)
                alertState.postValue(timeState.value!! - 1 <= 5)
                timeState.postValue((timeState.value!! - 1).coerceAtLeast(0L))
                delay(1_000)
            }
        }
    }

    fun stop(time: Long = 0) {
        job?.cancel()
        setupInitTime(time)
    }

    fun addTime(time: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            timeState.postValue(timeState.value!! + time)
        }
    }
}
