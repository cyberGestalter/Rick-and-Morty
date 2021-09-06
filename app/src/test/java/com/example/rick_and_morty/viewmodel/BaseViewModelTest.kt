package com.example.rick_and_morty.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.data.repository.Repository
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
open class BaseViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    protected val dispatcher = TestCoroutineDispatcher()

    @MockK
    protected lateinit var repository: Repository

    @Before
    fun init() {
        Dispatchers.setMain(dispatcher)
        MockKAnnotations.init(this)
    }

    @After
    fun makeLastCheckAndTearDown() {
        confirmVerified(repository)
        Dispatchers.resetMain()
    }
}