package com.wesley.whatsup.services.fileUploader

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileUploaderService {
  @Multipart
  @POST("files")
  suspend fun sendFile(
    @Header("Authorization") apiKey: String,
    @Part file: MultipartBody.Part,
    @Part("network") network: RequestBody = RequestBody.create(okhttp3.MultipartBody.FORM, "public")
  ): Response<FileUploaderResponseModel>
}