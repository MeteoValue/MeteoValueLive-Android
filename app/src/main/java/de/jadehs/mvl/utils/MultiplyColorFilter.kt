package de.jadehs.mvl.utils

import android.graphics.Color
import android.graphics.ColorMatrixColorFilter

class MultiplyColorFilter(color: Int) : ColorMatrixColorFilter(
    floatArrayOf(
        Color.red(color) / 255F, 0F, 0F, 0F, 0F,
        0F, Color.green(color) / 255F, 0F, 0F, 0F,
        0F, 0F, Color.blue(color) / 255F, 0F, 0F,
        0F, 0F, 0F, Color.alpha(color) / 255F, 0F
    )
) {
}