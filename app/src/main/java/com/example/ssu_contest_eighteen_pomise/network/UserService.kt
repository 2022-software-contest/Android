package com.example.ssu_contest_eighteen_pomise.network

import com.example.ssu_contest_eighteen_pomise.dto.*
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body
import retrofit2.http.Header
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

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

    @POST("v1/getProtegePillRecord")
    suspend fun postGetProtegePillRecord(
        @Header("Authorization") headerToken: String,
        @Body protege: Email
    ): Response<PillRecord>

    @GET("v1/getPillProtege")
    suspend fun getMyPillRecord(
        @Header("Authorization") headerToken: String
    ): Response<List<Pill>>

    @POST("v1/deletePill")
    suspend fun deleteMyPillTime(
        @Header("Authorization") headerToken: String,
        @Body ids: List<Int>
    ): Response<Void>

    @POST("v1/changePassword")
    suspend fun updatePassword(
        @Header("Authorization") headerToken: String,
        @Body postDTO:UpdatePwDTO
    ): Response<Void>

    @POST("v1/report")
    suspend fun laterEatPostToGuardian(
        @Header("Authorization") headerToken: String,
        @Body pillNames:ReportLaterEatPillNames
    ):Response<Void>

//    @Headers("Content-Type: application/json")
//    @GET("getDrugPrdtPrmsnDtlInq01")
//    suspend fun getPillDetailInfo(
//        @Query("serviceKey") key: String,
//        @Query("item_name") name: String,
//        @Query("numOfRows") rows: Int,
//        @Query("pageNo") pn: Int,
//        @Query("type") type: String
//    ): Response<String>


    @POST("v1/17741/f745e9bba758a0fe5be6478f0e80d7a94d1de79c75b701ddb794160f2a122d7e/infer")
    suspend fun postOcrImage(
        @Header("X-OCR-SECRET") x_ocr_secret:String,
        @Header("Content-Type") content_type:String,
        @Body ocrBody:OcrBody
    ):Response<OcrDTO>


    companion object {
        const val BASE_URL =
            "https://taewoon.kro.kr/"
        const val PILL_OPEN_SOURCE_URL =
            "http://apis.data.go.kr/1471000/DrugPrdtPrmsnInfoService02/"
        const val OCR_URL =
            "https://w9stkrjgx4.apigw.ntruss.com/custom/"

        fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

                }

                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { hostname, session -> true }

            return builder
        }
    }
}