package com.practicum.pexelsapp.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.practicum.pexelsapp.R


val Mulish = FontFamily(
    Font(R.font.mulish_regular, FontWeight.W400), // Обычный
    Font(R.font.mulish_medium, FontWeight.W500), // Medium
    Font(R.font.mulish_semibold, FontWeight.W600), // SemiBold
    Font(R.font.mulish_bold, FontWeight.W700)  // Bold
)

val Typography = Typography(
    // 14.sp 400 — Поиск и неактивные чипсы
    bodyLarge = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp
    ),

    // 14.sp 700 — Активные чипсы (Акцентная метка)
    labelLarge = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W700,
        fontSize = 14.sp
    ),

    // 18.sp 700 — Заголовки на страницах
    titleLarge = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W700,
        fontSize = 18.sp
    ),

    // 14.sp 600 — Кнопки
    labelMedium = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W600,
        fontSize = 14.sp
    )
)
