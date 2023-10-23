package com.aya.storyapps2.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.aya.storyapps2.Dummy
import com.aya.storyapps2.MainDispatcherRule
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
import com.aya.storyapps2.getOrAwaitValue
import com.aya.storyapps2.paging.PagingRepository
import com.aya.storyapps2.responses.MapsList

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var pagingRepository: PagingRepository
    private lateinit var mapsViewModel: MapsViewModel
    private val auth = "auth"
    @Before
    fun setup(){
        mapsViewModel = MapsViewModel(pagingRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when maps is not null and Return Success`() = runTest {
        val dummy = Dummy.generateDummyMaps()
        val expected = MutableLiveData<Result<List<MapsList>>>()
        expected.value = Result.Success(dummy)

        Mockito.`when`(pagingRepository.getMaps("Bearer $auth")).thenReturn(expected)

        val actualMaps = mapsViewModel.getMaps("Bearer $auth").getOrAwaitValue()

        Mockito.verify(pagingRepository).getMaps("Bearer $auth")
        assertNotNull(actualMaps)
        assertTrue(actualMaps is Result.Success)
    }

    @Test
    fun `when network error Return Error`() = runTest {
        val expected = MutableLiveData<Result<List<MapsList>>>()
        expected.value = Result.Error("Error")

        Mockito.`when`(pagingRepository.getMaps("Bearer $auth")).thenReturn(expected)
        val actualMaps = mapsViewModel.getMaps("Bearer $auth").getOrAwaitValue()

        Mockito.verify(pagingRepository).getMaps("Bearer $auth")
        assertNotNull(actualMaps)
        assertTrue(actualMaps is Result.Error)
    }

}