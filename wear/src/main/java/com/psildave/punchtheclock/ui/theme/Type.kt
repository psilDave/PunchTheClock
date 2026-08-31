package com.psildave.punchtheclock.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Typography


private val RobotoFamily = FontFamily.Default
/**
 * Typography configurations for the Wear OS application using Material 3 styles.
 */
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Light,
        fontSize = 40.sp,
        lineHeight = 46.sp,
        letterSpacing = (-1).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.3.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
)
