package de.jadehs.mvl.utils

import android.app.PendingIntent
import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import de.jadehs.mvl.R
import de.jadehs.mvl.provider.ReportsFileProvider
import de.jadehs.mvl.reciever.ReportSharedReceiver
import java.io.File

class ReportsPublisher(private val context: Context) {


    fun getEmailIntent(reportsFile: File): Intent {
        val reportsUri = FileProvider.getUriForFile(
            context,
            ReportsFileProvider.AUTHORITY,
            reportsFile
        )
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, reportsUri)
            putExtra(
                Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.report_email))
            )
            putExtra(Intent.EXTRA_SUBJECT, "Reports ")
            putExtra(
                Intent.EXTRA_TEXT,
                """
                Sehr geehrtes MeteoValueLive-Team,
                im Anhang finden Sie die Parkplatz- und ETA-Berichte die bisher angefallen sind.
                
                Mit freundlichen Grüßen
                
                """.trimIndent()
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            clipData = ClipData.newRawUri("Report Data", reportsUri)
            type = "application/zip"
        }
        return emailIntent
    }

    fun getChooserPendingIntent(
        requestCode: Int,
        routeId: Long,
        reportsFile: File
    ): PendingIntent {
        return PendingIntent.getActivity(
            context,
            requestCode,
            getChooserIntent(routeId, reportsFile),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

    }

    fun getChooserIntent(routeId: Long, reportsFile: File): Intent {
        return ReportSharedReceiver.newChooserIntent(
            context,
            routeId,
            getEmailIntent(reportsFile)
        )
    }
}