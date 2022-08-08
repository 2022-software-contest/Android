package com.example.ssu_contest_eighteen_pomise.network

import com.example.ssu_contest_eighteen_pomise.dto.*
import retrofit2.Response
import retrofit2.http.*

interface UserService {

//    @GET("v1/display")
//    suspend fun display(): Response<List<ProtegeDTO>>

    @GET("v1/getGuardian")
    suspend fun getGuardian(
        @Header("Authorization") headerToken: String
    ): Response<GuardianDTO>

    @GET("v1/getProtege")
    suspend fun getProtege(
        @Header("Authorization") headerToken: String
    ): Response<ProtegeDTO>

    @POST("v1/addProtege")
    suspend fun addProtege(
        @Header("Authorization") headerToken: String,
        @Body postDTO: PostAddProtegeModel
    ): Response<PostAddProtegeModelResponse>

    @POST("v1/deleteProtege")
    suspend fun deleteProtege(
        @Header("Authorization") headerToken: String,
        @Body postDTO: PostDeleteProtegeModel
    ): Response<Void>

    companion object {
        const val BASE_URL =
            "http://43.200.98.211/"
    }
}