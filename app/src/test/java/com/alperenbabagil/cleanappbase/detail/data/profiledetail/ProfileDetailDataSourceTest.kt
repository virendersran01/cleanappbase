package com.alperenbabagil.cleanappbase.detail.data.profiledetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.alperenbabagil.cabdata.ApiCallAdapter
import com.alperenbabagil.dataholder.DataHolder
import com.alperenbabagil.cleanappbase.core.data.CABDemoApiCallAdapter
import com.alperenbabagil.cleanappbase.core.data.CoreDataConstants
import com.alperenbabagil.cleanappbase.core.data.model.BaseRequest
import com.alperenbabagil.cleanappbase.core.data.model.CABDemoBaseApiResponse
import com.alperenbabagil.cleanappbase.core.data.model.ResponseTemplate
import com.alperenbabagil.cleanappbase.core.data.model.ServerError
import com.alperenbabagil.cleanappbase.detail.data.ProfileService
import com.alperenbabagil.cleanappbase.detail.data.profiledetail.model.ProfileDetailDataTemplate
import com.alperenbabagil.cleanappbase.detail.data.profiledetail.model.ProfileDetailNetworkDTO
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response


class ProfileDetailDataSourceTest {

    @get:Rule
    val instantExecutorRole = InstantTaskExecutorRule()

    @MockK
    lateinit var profileService: ProfileService

    @MockK
    lateinit var  apiCallAdapter: ApiCallAdapter

    @MockK
    lateinit var response: Response<CABDemoBaseApiResponse<ProfileDetailNetworkDTO>>

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `get profile detail test`() {
        val request = BaseRequest(
            ResponseTemplate(data= ProfileDetailDataTemplate(userName = "")))

        val expectedProfileDetailNetworkDTO = ProfileDetailNetworkDTO("nameT",
            "titleT",
            "skillT",
            "2000-11-10",
            "colorT",
            "martialT",
            "langT",
            "companyT"
            )

        coEvery { apiCallAdapter.adapt<ProfileDetailNetworkDTO>(any())  } coAnswers {
            DataHolder.Success(expectedProfileDetailNetworkDTO)
        }

        var profileDetailDataSource = spyk(ProfileDetailDataSource(profileService,apiCallAdapter))

        var resultDH: DataHolder<ProfileDetailNetworkDTO>

        runBlocking {
            resultDH=profileDetailDataSource.getDataSourceResult(request)
        }

        // Testing against adapter
        assertTrue(resultDH is DataHolder.Success)
        assertEquals(expectedProfileDetailNetworkDTO,(resultDH as DataHolder.Success).data)

        val expectedError = DataHolder.Fail(errStr = "expectedErr",error = ServerError("servErr",7))

        coEvery { apiCallAdapter.adapt<ProfileDetailNetworkDTO>(any())  } coAnswers {
            expectedError
        }

        runBlocking {
            resultDH=profileDetailDataSource.getDataSourceResult(request)
        }

        // Testing against adapter
        assertTrue(resultDH is DataHolder.Fail)
        assertEquals(expectedError.errStr,(resultDH as DataHolder.Fail).errStr)
        assertEquals(expectedError.error,(resultDH as DataHolder.Fail).error)


        val adapterInstance = spyk(CABDemoApiCallAdapter())

        coEvery {
            profileService.getProfileDetail(any())} coAnswers {
            response
        }

        coEvery { response.code() } coAnswers {200}

        coEvery { response.isSuccessful } coAnswers {true}

        coEvery { response.body() } coAnswers {
            CABDemoBaseApiResponse(CoreDataConstants.SERVER_STATUS_SUCCESS,
                expectedProfileDetailNetworkDTO,null)}

        profileDetailDataSource = spyk(ProfileDetailDataSource(profileService,adapterInstance))

        runBlocking {
            resultDH=profileDetailDataSource.getDataSourceResult(request)
        }

        assertEquals(expectedProfileDetailNetworkDTO,(resultDH as DataHolder.Success).data)

        val expectedServerError = ServerError("profileDetailError",7)
        coEvery { response.body() } coAnswers {
            CABDemoBaseApiResponse(CoreDataConstants.SERVER_STATUS_FAIL,
                expectedProfileDetailNetworkDTO,ServerError("profileDetailError",7))}

        runBlocking {
            resultDH=profileDetailDataSource.getDataSourceResult(request)
        }

        assertTrue(resultDH is DataHolder.Fail)
        assertEquals(expectedServerError,(resultDH as DataHolder.Fail).error)

    }
}