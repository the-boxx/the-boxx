package com.theboxx.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.theboxx.app.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */

)

val avenirNextFamily = FontFamily(
    Font(R.font.avenirnext_regular, FontWeight.Normal),
    Font(R.font.avenirnext_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.avenirnext_ultralight, FontWeight.ExtraLight),
    Font(R.font.avenirnext_ultralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.avenirnext_bold, FontWeight.Bold),
    Font(R.font.avenirnext_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.avenirnext_heavy, FontWeight.ExtraBold),
    Font(R.font.avenirnext_heavyitalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.avenirnext_demibold, FontWeight.SemiBold),
    Font(R.font.avenirnext_demibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.avenirnext_medium, FontWeight.Medium),
    Font(R.font.avenirnext_mediumitalic, FontWeight.Medium, FontStyle.Italic),


)