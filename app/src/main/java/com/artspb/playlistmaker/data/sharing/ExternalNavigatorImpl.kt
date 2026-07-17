package com.artspb.playlistmaker.data.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.artspb.playlistmaker.R
import com.artspb.playlistmaker.domain.sharing.ExternalNavigator

/**
 * Моя реализация навигатора во внешние приложения в слое Data.
 *
 * Почему я сделал именно так (Clean Architecture):
 * 1. Вся работа с Android SDK (`Context`, `Intent`, `Uri` и строковыми ресурсами `R.string...`) находится в слое Data.
 * 2. Использование флага `Intent.FLAG_ACTIVITY_NEW_TASK` гарантирует корректный запуск внешних Intent из любого контекста.
 */
class ExternalNavigatorImpl(
    private val context: Context
) : ExternalNavigator {

    override fun shareLink() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.course_link))
        }
        val chooser = Intent.createChooser(shareIntent, context.getString(R.string.share_app))
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    override fun openEmail() {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_message))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(supportIntent)
    }

    override fun openTerms() {
        val agreementIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(context.getString(R.string.practicum_offer_link))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(agreementIntent)
    }
}
