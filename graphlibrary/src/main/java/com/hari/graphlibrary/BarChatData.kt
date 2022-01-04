package com.hari.graphlibrary

import android.graphics.Color

data class BarChatData(
    val barValue: Int,
    val barColorRes: Int = Color.BLACK,
    val barTag: Any? = null,
    val barText: String
)
