package com.alionur.quizzy.util

import android.os.Build
import android.text.Html

object Constants {

    const val BASE_URL = "https://opentdb.com/"
    const val RETROFIT = "retrofit"

    fun String.decodeHtml(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(this).toString()
        }
    }
}