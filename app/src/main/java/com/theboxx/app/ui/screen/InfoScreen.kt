package com.theboxx.app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theboxx.app.R

@Composable
fun InfoScreen(padding: PaddingValues) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.boxx_single),
                    contentDescription = "The Boxx Icon",
                    modifier = Modifier.padding(24.dp)
                )
                Text(
                    text = "The Boxx",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                )
                Text(
                    text = "Technology to control your technology",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Row(
            modifier = Modifier
                .height(100.dp)
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Column(
//                modifier = Modifier.padding(36.dp)
//            ) {
                Icon(
                    imageVector = Icons.Filled.Build,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Source Code",
                    modifier = Modifier.padding(12.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        withLink(
                            LinkAnnotation.Url(
                                "https://github.com/the-boxx/the-boxx"
                            )
                        ) {
                            append("Source Code")
                        }
                    },
                    color = MaterialTheme.colorScheme.primary
                )
//            }
        }
    }

}