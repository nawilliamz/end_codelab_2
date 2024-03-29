/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.statistics

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.example.android.architecture.blueprints.todoapp.TodoApplication
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Error
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.DefaultTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel for the statistics screen.
 */
class StatisticsViewModel(private val tasksRepository: TasksRepository) : ViewModel() {

    //This tasksRepository is the fake repository we created for earlier use wth TasksViewModel and
    //TasksFragment
//    private val tasksRepository = (application as TodoApplication).taskRepository

    private val tasks: LiveData<Result<List<Task>>> = tasksRepository.observeTasks()
    private val _dataLoading = MutableLiveData<Boolean>(false)
    private val stats: LiveData<StatsResult?> = tasks.map {
        if (it is Success) {
            getActiveAndCompletedStats(it.data)
        } else {
            null
        }
    }

    val activeTasksPercent = stats.map {
        it?.activeTasksPercent ?: 0f }
    val completedTasksPercent: LiveData<Float> = stats.map { it?.completedTasksPercent ?: 0f }
    val dataLoading: LiveData<Boolean> = _dataLoading

    //**These variables are solely set up for testing purposes. It retrieves our tasks from the repository that is used in
    //the view model constructor and allows us to set the result of that "download" as either an Error a Success.
    //it is a Result<List<Task>>
    //This line of code sets our Result object returned from refreshing our repository (Result containt a List of tasks)
    //to type Error (which simulates a problem downloading our task from the repository).
    //Both of these variables error and empty should be set to an Error state when I refresh and there's
    //an error
    val error: LiveData<Boolean> = tasks.map { it is Error }
    val empty: LiveData<Boolean> = tasks.map { (it as? Success)?.data.isNullOrEmpty() }

    @SuppressLint("SuspiciousIndentation")
    fun refresh() {
        _dataLoading.value = true

            //pauseDispatcher() pauses the test coroutine dispatcher right here before the launch
            //This pauses the running of the code in this codeblock so that we can assert that _dataLoading.value is true here
            //and this will pass successfully (if true)
            viewModelScope.launch {
                tasksRepository.refreshTasks()
                _dataLoading.value = false
            }
            //resumeDispatcher() resumes the coroutine here.
    }

//    @Suppress("UNCHECKED_CAST")
//    class StatisticsViewModelFactory (
//        private val tasksRepository: TasksRepository
//    ) : ViewModelProvider.NewInstanceFactory() {
//        override fun <T : ViewModel> create(modelClass: Class<T>) =
//            (StatisticsViewModel(tasksRepository) as T)
//    }

}

@Suppress("UNCHECKED_CAST")
class StatisticsViewModelFactory (
    private val tasksRepository: TasksRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (StatisticsViewModel(tasksRepository) as T)
}
