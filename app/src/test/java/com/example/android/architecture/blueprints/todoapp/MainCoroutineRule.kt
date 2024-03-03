package com.example.android.architecture.blueprints.todoapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain


//So this rule is re-usable for any of your code you might want to add to it.
//It implements TestWatcher() which implements the TestRule interface. It is the use of this interface
//that makes MainCoroutineRule a JUnit Rule
//Notice that the starting and finished methods here that you're overriding from TestWatcher() look
//basically like the @Before and @After methods we defined previously. Your're swapping in the dispatcher,
//and then you're cleaning up.
//Also notice that we're implementing TestCoroutineScope to which you are passing a test coroutine dispatcher
//By implementing TestCoroutineScope, this gives MainCoroutineRule the ability to control coroutine timeing
//using the test disptacher. Rememeber that a coroutine scope controls the lifecycle of the coroutine.
// You're going to see an example of this in the next video.Dep

@ExperimentalCoroutinesApi
class MainCoroutineRule(val dispatcher: TestDispatcher = TestCoroutineDispatcher()):
    TestWatcher(),

    TestCoroutineScope by TestCoroutineScope(dispatcher) {

        override fun starting(description: Description?) {
            super.starting(description)
            //**Important note: This line of code is where Dispatcher.Main is swapped out for the rest dispatcher
            Dispatchers.setMain(dispatcher)}

        override fun finished(description: Description?) {
            super.finished(description)
//            cleanupTestCoroutines()
            Dispatchers.resetMain()
        }
}