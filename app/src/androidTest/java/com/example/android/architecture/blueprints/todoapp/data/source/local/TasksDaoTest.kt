package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase

    @Before
    fun initDb()  {
        //Remember, this applicatoinContext is from AndroidX testing libraries
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        ).build()

    }

    @After
    fun closeDb() = database.close()

    //Because the DAO has suspend functions in it, I use runTest here, which launches a
    //new coroutine
    @Test
    fun insertTaskAndGetId() = runTest {
        // Given - Insert a task
        val task = Task("title", "description")

        //Get the dao from the database
        database.taskDao().insertTask(task)

        //When - get the task by ID from the database

        val loaded = database.taskDao().getTaskById(task.id)

        //Then - the loaded data contains the expected values

        assertThat<Task>(loaded as Task, notNullValue())
        assertThat(loaded.id, `is`(task.id))
        assertThat(loaded.title, `is`(task.title))
        assertThat(loaded.description, `is`(task.description))
        assertThat(loaded.isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun updateTaskAndGetById() =  runTest{
        // 1. Insert a task into the DAO.

        val task = Task("title", "description")

        //Get the dao from the database
        database.taskDao().insertTask(task)

        // 2. Update the task by creating a new task with the same ID but different attributes.

        val newTask = Task("title_new", "description_new", false, task.id)
        database.taskDao().updateTask(newTask)


        // 3. Check that when you get the task by its ID, it has the updated values.

        val sameTask = database.taskDao().getTaskById(task.id)

        assertThat<Task>(sameTask as Task, notNullValue())
        assertThat(sameTask.id, `is`(task.id))
        assertThat(sameTask.title, `is`(newTask.title))
        assertThat(sameTask.description, `is`(newTask.description))
        assertThat(sameTask.isCompleted, `is`(newTask.isCompleted))



    }

}