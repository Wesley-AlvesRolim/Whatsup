package com.wesley.whatsup.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wesley.whatsup.R
import com.wesley.whatsup.data.FileCloudData
import com.wesley.whatsup.services.fileUploader.FileUploaderResponseModel
import com.wesley.whatsup.ui.components.ImageLinkItem
import com.wesley.whatsup.ui.viewmodels.HomeViewModel


@Composable
fun Home(navController: NavHostController, viewModel: HomeViewModel) {
  val context = LocalContext.current
  val trigger = remember { mutableIntStateOf(0) }
  val result = remember { mutableStateOf<Uri?>(null) }
  val imagePickerLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
      result.value = it
      trigger.intValue++
    }

  viewModel.getModelList(context)
  val list by viewModel.homeUiState.collectAsState()

  fun openFile() {
    imagePickerLauncher.launch(arrayOf("image/*"))
  }

  LaunchedEffect(trigger.intValue) {
    val uri = result.value ?: return@LaunchedEffect
    viewModel.uploadImage(context, uri)
  }

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = { openFile() },
        shape = CircleShape
      ) {
        Icon(Icons.Filled.Add, contentDescription = context.getString(R.string.send_file))
      }
    }
  ) { paddingValues ->
    if (list.items.isEmpty()) {
      MessageForEmptyLinksList(paddingValues)
    } else {
      RenderImageLinksList(paddingValues, viewModel, list.items)
    }
  }
}

@Composable
fun MessageForEmptyLinksList(paddingValues: PaddingValues) {
  val context = LocalContext.current
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(paddingValues),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = context.getString(R.string.empty_images_list_message),
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
  }
}

@Composable
fun RenderImageLinksList(
  paddingValues: PaddingValues,
  homeViewModel: HomeViewModel,
  linksList: List<FileCloudData>
) {
  val context = LocalContext.current
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(paddingValues)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    items(linksList) { link ->
      var isExpanded by remember { mutableStateOf(link.isExpanded) }
      ImageLinkItem(
        link = link,
        isExpanded = isExpanded,
        onExpandToggle = { isExpanded = !isExpanded },
        onShareClick = { homeViewModel.shareLink(context, link.url) }
      )
    }
  }
}