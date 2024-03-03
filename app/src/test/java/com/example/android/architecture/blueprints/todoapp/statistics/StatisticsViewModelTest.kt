package com.example.android.architecture.blueprints.todoapp.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.source.FakeTestRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.core.Is.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var tasksRepository: FakeTestRepository


    @Before
    fun setupStatisticsViewModel() {
        // Initialise the repository with no tasks.
        tasksRepository = FakeTestRepository()

        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }


    //Upon a first run of this test, the test is failing because the dataLoading falue is false, so the
    //first assertThan line fails. This is happening because the tested code is being run immediately
    //and deterministically, which means refresh() actually finished completely including getting to the
    //last line of code _dataLoading.value = false. Since the assertion doesn't happen until the method has
    //completely finished, the tested value is showing false when we're testing for it to be true.
    @Test
    fun loadTasks_loading() {

        //Remember we are running this code on a test thread, separate from the main UI thread.

        //If I call pauseDispatcher() here, it won't automatically execute the whole coroutine and because
        //mainCoroutineRule here is a test coroutine scope, I can just directly call this method
        //This pauses the underlying test coroutine scope that's being used here. However since we're running
        //this block of code (fun loadTasks_loading() inside a now-paused dspatcher, the code in refresh() will
        //run until it hits the coroutine that is launched, this coroutine will NOT RUN, but

        //So pausing the test dispatcher here runs this block of code (represented by loadTasks_loading()) without
        //running any coroutines that may appear inside the code. Note, a new coroutine is launcched inside refresh()
        //method. Since the dispatcher has been paused, the code in this loadTasks_loading method (and also by default
        //the code in refresh() since it is also contained within this codeblock) is all run except the coroutine code
        //which is skipped.
        //Therefore, the code in refresh is run until the coroutine is launched, then the rest of the code in this
        //code block after the coroutine code is run (in this case there is no additional code here).
        //Then after refresh() code is run, the first assertThat() function is run (while the coroutine is stil
        //on hold.
        //Then when the test dispatcher is resumed, it resumes autoadvancing so it goes back and picks up the coroutine
        //in refresh() since this has been idling. If there was, hypothetically, another coroutine launched before
        //the second assrt statement below, then that coroutine would also be run. However, since there are no other
        //coroutines to run either in this code block or inside any other functions run inside this codeblock, the
        //second assert statement is run.
        //Since the code inside the coroutine in refresh() has updated the value of dataLoading, this assertion will
        //also now pass (it wont since pauseDispatcher is now depracated).

        mainCoroutineRule.pauseDispatcher()

        statisticsViewModel.refresh()

        //Basically what I'm checking here is that, when I'm refreshing, the data loading icon is going
        //to pop up. But then I also need to test that, sometime later, that the data loading icon
        //disappears once the data has been refreshed.
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is` (true))

        //Here the test coroutine is immediately executed again. Once the coroutine is finished, I can
        //check that the loading indicator disappeared.
        mainCoroutineRule.resumeDispatcher()

        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is` (false))


    }
}