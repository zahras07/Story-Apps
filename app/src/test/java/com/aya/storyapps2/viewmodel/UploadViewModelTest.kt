package com.aya.storyapps2.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.aya.storyapps2.MainDispatcherRule
import com.aya.storyapps2.getOrAwaitValue
import com.aya.storyapps2.paging.PagingRepository
import com.aya.storyapps2.responses.StoryAddResponses
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
class UploadViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var pagingRepository: PagingRepository

    private lateinit var uploadViewModel: UploadViewModel

    @Mock
    private lateinit var file: MultipartBody.Part
    @Mock
    private lateinit var desc : RequestBody
    private val auth = "testi"
    private val lat = 1F
    private val lon = 1F


    @Before
    fun setup(){
        uploadViewModel = UploadViewModel(pagingRepository)
    }

    @Test
    fun `when upload is success`() = runTest {
        val resultMutable = MutableLiveData<Result<StoryAddResponses>>()
        resultMutable.value = Result.Success(StoryAddResponses())

        Mockito.lenient().`when`(
            pagingRepository.uploadStory(
                "Bearer $auth",desc,file,lat,lon
            )
        ).thenReturn(resultMutable)
        val result = uploadViewModel.uploadToServer("Bearer $auth",desc,file,lat,lon).getOrAwaitValue()

        assertNotNull(result)
        assertTrue(result is Result.Success)
    }

    @Test
    fun `when upload is return error`() = runTest {
        val resultMutable = MutableLiveData<Result<StoryAddResponses>>()
        resultMutable.value = Result.Error("Error")

        Mockito.lenient().`when`(
            pagingRepository.uploadStory(
                "Bearer $auth",desc,file,lat,lon
            )
        ).thenReturn(resultMutable)
        val result = uploadViewModel.uploadToServer("Bearer $auth",desc,file,lat,lon).getOrAwaitValue()

        assertNotNull(result)
        assertTrue(result is Result.Error)
    }
}
