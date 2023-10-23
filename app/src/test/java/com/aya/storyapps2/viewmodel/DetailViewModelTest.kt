package com.aya.storyapps2.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.aya.storyapps2.Dummy
import com.aya.storyapps2.MainDispatcherRule
import com.aya.storyapps2.getOrAwaitValue
import com.aya.storyapps2.paging.PagingRepository
import com.aya.storyapps2.responses.Story
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
class DetailViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var pagingRepository: PagingRepository
    private lateinit var detailViewModel: DetailViewModel
    private val auth = "auth"
    private val id = "id"

    @Before
    fun setup(){
        detailViewModel = DetailViewModel(pagingRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Test
    fun `when get detail is not null and Return Success`() = runTest{
        val dummy = Dummy.generateDummyDetail()
        val expected = MutableLiveData<Result<Story>>()
        expected.value = Result.Success(dummy)

        Mockito.`when`(pagingRepository.getStoryDetail("Bearer $auth", id)).thenReturn(expected)
        val result = detailViewModel.getDetail("Bearer $auth", id).getOrAwaitValue()

        Mockito.verify(pagingRepository).getStoryDetail("Bearer $auth", id)
        assertNotNull(result)
        assertTrue(result is Result.Success)
    }

    @Test
    fun `When Get Detail is Null and Return Failed`() = runTest {
        val expectedDetail = MutableLiveData<Result<Story>>()
        expectedDetail.value = Result.Error("failed")

        Mockito.lenient().`when`(pagingRepository.getStoryDetail("Bearer $auth", id)).thenReturn(expectedDetail)
        val result = detailViewModel.getDetail("Bearer $auth", id).getOrAwaitValue()

        Mockito.verify(pagingRepository).getStoryDetail("Bearer $auth", id)
        assertNotNull(result)
        assertTrue(result is Result.Error)
    }
}