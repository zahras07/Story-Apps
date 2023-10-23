package com.aya.storyapps2.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aya.storyapps2.Dummy
import com.aya.storyapps2.getOrAwaitValue
import com.aya.storyapps2.paging.PagingRepository
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
class SetViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var pagingRepository: PagingRepository

    private lateinit var settingViewModel: SettingViewModel

    @Before
    fun setup(){
        settingViewModel = SettingViewModel(pagingRepository)
    }

    @Test
    fun `when user in preferences is not null and return success`() = runTest {
        val user = Dummy.generateUser()

        Mockito.`when`(pagingRepository.getUser()).thenReturn(user)
        val result = settingViewModel.getUser().getOrAwaitValue()

        Mockito.verify(pagingRepository).getUser()
        assertNotNull(result.token)
        assertTrue(result.isLogin)
    }

    @Test
    fun `when user logout is true`() = runTest {
        settingViewModel.logout()
        Mockito.verify(pagingRepository).logout()
    }
}