package com.example.android.architecture.blueprints.todoapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

class TasksActivityTest {

    @RunWith(AndroidJUnit4::class)
    @LargeTest
    class TasksActivityTest {

        private lateinit var repository: TasksRepository

        @Before
        fun init() {
            //Note that we're not using any sort of fake repository here.
            repository = ServiceLocator.provideTasksRepository(
                getApplicationContext()
            )
            //Using runBlocking here because this itself is not a test, so you don't really need
            //access to a test coroutine dispatcher.
            runBlocking {
                //To make sure there's no state remaining from previous tests.
                repository.deleteAllTasks()
            }
        }

        @After
        fun reset() {
            //Does some additional cleanup.
            ServiceLocator.resetRepository()
        }


        @Test
        fun editTask() = runBlocking {
            // Set initial state. repository.saveTask(Task("TITLE1", "DESCRIPTION"))

            repository.saveTask(Task("TITLE1", "DESCRIPTION"))

            // Start up Tasks screen.
            //ActivityScenario is like FragmentScenario. It's an AndroidX testing library that wraps
            //around an activity and gives you direct control over the activity's lifecycle for
            //testing.
            val activityScenario = ActivityScenario.launch(TasksActivity::class.java)


            // Espresso code will go here.

            // Click on the task on the list and verify that all the data is correct.
            onView(withText("TITLE1")).perform(click())
            onView(withId(R.id.task_detail_title_text)).check(matches(withText("TITLE1")))
            onView(withId(R.id.task_detail_description_text)).check(matches(withText("DESCRIPTION")))
            onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

            // Click on the edit button, edit, and save.
            onView(withId(R.id.edit_task_fab)).perform(click())
            onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
            onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
            onView(withId(R.id.save_task_fab)).perform(click())

            // Verify task is displayed on screen in the task list.
            onView(withText("NEW TITLE")).check(matches(isDisplayed()))
            // Verify previous task is not displayed.
            onView(withText("TITLE1")).check(doesNotExist())

            // Make sure the activity is closed before resetting the db:
            activityScenario.close()
        }
    }

}