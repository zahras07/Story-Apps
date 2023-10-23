package com.aya.storyapps2.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.aya.storyapps2.MainDispatcherRule
import com.aya.storyapps2.getOrAwaitValue
import com.aya.storyapps2.paging.PagingRepository
import com.aya.storyapps2.responses.RegisterResponses
import com.aya.storyapps2.dataclass.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var pagingRepository: PagingRepository

    private lateinit var registerViewModel: RegisterViewModel

    val dummyUsername = "testing"
    val dummyEmail = "testing@gmail.com"
    var dummyPassword = "testing"

    @Before
    fun setup() {
        registerViewModel = RegisterViewModel(pagingRepository)
    }

    @Test
    fun `when create user return success`() = runTest {
        val resultMutable = MutableLiveData<Result<RegisterResponses>>()
        resultMutable.value = Result.Success(RegisterResponses())

        Mockito.lenient().`when`(
            pagingRepository.registerUser(
                dummyUsername,
                dummyEmail,
                dummyPassword
            )
        ).thenReturn(resultMutable)
        val result =
            registerViewModel.createUserRegister(dummyUsername, dummyEmail, dummyPassword).getOrAwaitValue()

        assertNotNull(result)
        assertTrue(result is Result.Success)
    }

    @Test
    fun `when create user return error`() = runTest {
        dummyPassword = "1"
        val resultMutable = MutableLiveData<Result<RegisterResponses>>()
        resultMutable.value = Result.Error("failed")

        Mockito.lenient().`when`(
            pagingRepository.registerUser(
                dummyUsername,
                dummyEmail,
                dummyPassword
            )
        ).thenReturn(resultMutable)
        val result =
            registerViewModel.createUserRegister(dummyUsername, dummyEmail, dummyPassword).getOrAwaitValue()

        assertNotNull(result)
        assertTrue(result is Result.Error)
    }
}
