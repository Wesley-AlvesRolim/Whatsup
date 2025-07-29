package com.wesley.whatsup.ui.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.wesley.whatsup.Env
import com.wesley.whatsup.R
import com.wesley.whatsup.data.FileCloudData
import com.wesley.whatsup.services.fileUploader.FileUploaderResponseModel
import com.wesley.whatsup.services.fileUploader.FileUploaderService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

data class HomeUiState(
  val items: List<FileCloudData> = emptyList(),
)

class HomeViewModel(
  private val uploaderService: FileUploaderService,
  private val PREFS_NAME: Int = R.string.file_data_perf_name,
  private val KEY_OBJECT_LIST: String = "FILE_DATA"
) : ViewModel() {

  private val _homeUiState = MutableStateFlow(HomeUiState())
  val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

  fun uploadImage(context: Context, uri: Uri) {
    viewModelScope.launch {
      try {
        val imagePart = prepareImageFilePart(uri, context)
        val response = uploaderService.sendFile(
          "Bearer ${Env.API_KEY}",
          imagePart
        )
        if (response.isSuccessful) {
          Log.d("Upload", "Success!")
          val list = getModelList(context).toMutableList()
          val itemFromApi = response.body()!!
          val item =
            convertDataForView(
              itemFromApi,
              "${Env.PINATA_GATEWAY}/ipfs/${itemFromApi.data.cid}?download=true"
            )
          list.add(item)
          saveModelList(context, list)
          _homeUiState.value = _homeUiState.value.copy(items = list)
        } else {
          Log.e("Upload", "Failed: ${response.code()} ${response.message()} ${response.raw()}")
        }
      } catch (e: Exception) {
        Log.e("Upload", "Exception: ${e.message}")
      }
    }
  }

  fun prepareImageFilePart(uri: Uri, context: Context): MultipartBody.Part {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val filename = getFileNameAndExtension(context, uri)!!
    val file = File(context.cacheDir, filename)
    inputStream.use { input ->
      FileOutputStream(file).use { output ->
        input.copyTo(output)
      }
    }
    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("file", file.name, requestFile)
  }

  fun getFileNameAndExtension(context: Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
      val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
      if (it.moveToFirst() && nameIndex != -1) {
        val fullName = it.getString(nameIndex)
        return fullName
      }
    }
    return null
  }


  fun saveModelList(context: Context, modelList: List<FileCloudData>) {
    val sharedPreferences =
      context.getSharedPreferences(context.getString(PREFS_NAME), Context.MODE_PRIVATE)
    sharedPreferences.edit {
      val json = Gson().toJson(modelList)
      putString(KEY_OBJECT_LIST, json)
    }
  }

  fun getModelList(context: Context): List<FileCloudData> {
    val sharedPreferences =
      context.getSharedPreferences(context.getString(PREFS_NAME), Context.MODE_PRIVATE)
    val json = sharedPreferences.getString(KEY_OBJECT_LIST, null)
    if (json != null) {
      val type = object : TypeToken<List<FileCloudData>>() {}.type
      val theList: List<FileCloudData> = Gson().fromJson(json, type) ?: emptyList()
      _homeUiState.value = _homeUiState.value.copy(items = theList)
      return theList
    } else {
      return emptyList()
    }
  }

  fun shareLink(context: Context, link: String) {
    val sendIntent: Intent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, link)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
  }

  fun convertDataForView(
    model: FileUploaderResponseModel,
    url: String
  ): FileCloudData {
    return FileCloudData(
      model.data.id,
      model.data.cid,
      model.data.name,
      model.data.size,
      url
    )
  }


  companion object {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val okHttpClient = OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
          .writeTimeout(30, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS).build()
        val retrofit = Retrofit.Builder().client(okHttpClient)
          .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
          .baseUrl(Env.BASE_URL).build()
        val service: FileUploaderService =
          retrofit.create(FileUploaderService::class.java)
        HomeViewModel(service)
      }
    }
  }
}
