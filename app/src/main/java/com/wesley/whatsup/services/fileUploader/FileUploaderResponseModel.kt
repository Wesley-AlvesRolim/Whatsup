package com.wesley.whatsup.services.fileUploader;

import com.google.gson.annotations.SerializedName

data class FileUploaderResponseModel(
  val data: FileInfo
)

data class FileInfo(
  val id: String,
  val name: String,
  val cid: String,
  val size: Long,
  @SerializedName("number_of_files") val numberOfFiles: Int,
  @SerializedName("mime_type") val mimeType: String,
  @SerializedName("group_id") val groupId: String?
)