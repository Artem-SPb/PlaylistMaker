package com.artspb.playlistmaker.medialibrary.ui

import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.artspb.playlistmaker.R
import com.google.android.material.snackbar.Snackbar

/**
 * Утилита для показа стилизованного Snackbar в соответствии с макетами Figma:
 * - В светлой теме: темный фон (#1A1B22) и белый текст (#FFFFFF).
 * - В темной теме: белый фон (#FFFFFF) и темный текст (#1A1B22).
 * - Текст выровнен по центру, шрифт ys_display_regular 14sp.
 */
fun showPlaylistSnackbar(anchorView: View, message: String) {
    val context = anchorView.context
    val snackbar = Snackbar.make(anchorView, message, Snackbar.LENGTH_SHORT)
    snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.primary_text_color))
    snackbar.setTextColor(ContextCompat.getColor(context, R.color.screen_background_color))

    val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView?.apply {
        textAlignment = View.TEXT_ALIGNMENT_CENTER
        gravity = Gravity.CENTER
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        typeface = ResourcesCompat.getFont(context, R.font.ys_display_regular)
    }

    snackbar.show()
}
