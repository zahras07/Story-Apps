package com.aya.storyapps2.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.aya.storyapps2.getOrAwaitValue
import com.aya.storyapps2.paging.PagingRepository
import com.aya.storyapps2.responses.LoginResponse
import junit.framework.Assert
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
import com.aya.storyapps2.dataclass.Result

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var pagingRepository: PagingRepository

    private lateinit var loginViewModel: LoginViewModel
    val dummyEmail = "testing"
    var dummyPassword = "123456"


    @Before
    fun setup() {
        loginViewModel = LoginViewModel(pagingRepository)
    }

    @Test
    fun `When Login is Success`() = runTest {
        val result = MutableLiveData<Result<LoginResponse>>()
        result.value = Result.Success(LoginResponse())

        Mockito.lenient().`when`(pagingRepository.login(dummyEmail, dummyPassword))
            .thenReturn(result)
        val actual = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()

        Assert.assertNotNull(actual)
        Assert.assertTrue(actual is Result.Success)

    }

    @Test
    fun `When Login Failed`() = runTest {
        dummyPassword = "1"
        val result = MutableLiveData<Result<LoginResponse>>()
        result.value = Result.Error("failed")

        Mockito.lenient().`when`(pagingRepository.login(dummyEmail, dummyPassword))
            .thenReturn(result)
        val actual = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()

        Assert.assertNotNull(actual)
        Assert.assertTrue(actual is Result.Error)
    }
}