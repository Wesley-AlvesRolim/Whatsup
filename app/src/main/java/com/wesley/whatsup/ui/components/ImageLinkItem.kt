package com.wesley.whatsup.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wesley.whatsup.R
import com.wesley.whatsup.data.FileCloudData

@Composable
fun ImageLinkItem(
  link: FileCloudData,
  isExpanded: Boolean,
  onExpandToggle: () -> Unit,
  onShareClick: () -> Unit
) {
  val context = LocalContext.current
  Card(
    shape = RoundedCornerShape(12.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    modifier = Modifier
      .fillMaxWidth()
      .animateContentSize()
      .clickable(onClick = onExpandToggle)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(
          imageVector = Icons.Filled.Face,
          contentDescription = context.getString(R.string.image_icon_description),
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = link.name,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = link.size.toString(),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
          )
        }
      }

      ImageLinkItemFooterInformation(isExpanded, onShareClick)
    }
  }
}

@Composable
fun ImageLinkItemFooterInformation(
  isExpanded: Boolean,
  onShareClick: () -> Unit
) {
  val context = LocalContext.current
  AnimatedVisibility(visible = isExpanded) {
    Column {
      HorizontalDivider(
        modifier = Modifier.padding(vertical = 12.dp),
        thickness = DividerDefaults.Thickness,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        ActionButton(
          text = context.getString(R.string.share_message),
          icon = Icons.Default.Share,
          onClick = onShareClick
        )
        //ActionButton(
        //  text = link.expirationDate,
        //  icon = Icons.Default.DateRange,
        //  onClick = {}
        //)
      }
    }
  }
}
