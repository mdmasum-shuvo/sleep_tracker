/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {



    private var tonight = MutableLiveData<SleepNight>()
    private var nights = database.getAllSleepData()
    private val _navigateToSleepQuality=MutableLiveData<SleepNight>()
    val navigateToSleepQuality:LiveData<SleepNight>
        get() = _navigateToSleepQuality

    val nightString = Transformations.map(nights) {
        formatNights(it, application.resources)
    }

    val startButtonVisible = Transformations.map(tonight) {
        null == it
    }
    val stopButtonVisible = Transformations.map(tonight) {
        null != it
    }
    val clearButtonVisible = Transformations.map(nights) {
        it?.isNotEmpty()
    }

    private var _showSnackbarEvent = MutableLiveData<Boolean>()

    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent
    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        viewModelScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }
    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }
    private suspend fun getTonightFromDatabase(): SleepNight? {

            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }

          return  night

    }

    fun onStartTracking() {
        viewModelScope.launch {
            val night = SleepNight()
            insert(night)
            tonight.value = getTonightFromDatabase()

        }
    }

    fun onStopTracking() {
        viewModelScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli=System.currentTimeMillis()
            updateNight(oldNight)
            _navigateToSleepQuality.value=oldNight
        }
    }

    fun onClear() {
        viewModelScope.launch {
            clearData()
            tonight.value = null
            _showSnackbarEvent.value = true
        }
    }

    private suspend fun clearData() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    private suspend fun updateNight(oldNight: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(oldNight)
        }
    }

    private suspend fun insert(night: SleepNight) {
            database.insert(night)

    }

    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }
}

