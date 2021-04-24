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

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SleepDatabaseDao {

    @Insert
    suspend fun insert(sleepNight: SleepNight)

    @Update
    suspend fun update(sleepNight: SleepNight)

    @Query("SELECT * from sleep_night WHERE nightId=:key")
   suspend fun get(key: Long): SleepNight

    @Query("DELETE FROM sleep_night")
    suspend fun clear()

    @Query("SELECT * from sleep_night order by nightId DESC")
    fun getAllSleepData(): LiveData<List<SleepNight>>

    @Query("SELECT * FROM sleep_night ORDER BY nightId DESC LIMIT 1")
    suspend fun getTonight(): SleepNight?
}
