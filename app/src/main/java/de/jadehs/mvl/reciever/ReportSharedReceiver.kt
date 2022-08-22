package de.jadehs.mvl.reciever

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import de.jadehs.mvl.MeteoApplication
import de.jadehs.mvl.R
import de.jadehs.mvl.data.models.ReportArchive

class ReportSharedReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ROUTE_ID = "de.jadehs.mvl.ReportSharedReceiver.route_id"

        @JvmStatic
        fun newExtras(routeId: Long): Bundle {
            return Bundle().apply {
                putLong(EXTRA_ROUTE_ID, routeId)
            }
        }

        @JvmStatic
        fun newPendingIntent(context: Context, routeId: Long): PendingIntent {
            return PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, ReportSharedReceiver::class.java).apply {
                    putExtras(newExtras(routeId))
                },
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        @JvmStatic
        fun newChooserIntent(
            context: Context,
            routeId: Long,
            emailIntent: Intent,
            chooserTitle: Int = R.string.report_send
        ): Intent {


            val chooserReceiver = newPendingIntent(context, routeId)
            return Intent.createChooser(
                emailIntent,
                context.getString(chooserTitle),
                chooserReceiver.intentSender
            )
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        val clickedComponent: ComponentName? =
            intent.getParcelableExtra(Intent.EXTRA_CHOSEN_COMPONENT)
        val routeId = intent.extras?.getLong(EXTRA_ROUTE_ID)

        clickedComponent?.let {
            routeId?.let {
                val reportArchive = ReportArchive.fromContext(context, routeId)
                reportArchive.clearArchive()
            }
        }
    }
}