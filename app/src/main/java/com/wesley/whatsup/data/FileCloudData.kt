package com.wesley.whatsup.data

data class FileCloudData(
  val id: String,
  val cid: String,
  val name: String,
  val size: Long,
  val url: String,
  val isExpanded: Boolean = false,
)