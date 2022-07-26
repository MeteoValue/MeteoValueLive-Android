package de.jadehs.mvl.ui.views

import android.content.Context
import android.util.AttributeSet
import kotlinx.coroutines.Runnable
import org.joda.time.*
import org.joda.time.format.PeriodFormatterBuilder

class TextCountdown : androidx.appcompat.widget.AppCompatTextView {


    private var isUpdating = false

    var countDownDestination: Instant = Instant.now()
        set(value) {
            field = value
            updateText()
        }
    var periodFormatter =
        PeriodFormatterBuilder().printZeroIfSupported().minimumPrintedDigits(1).appendHours()
            .appendLiteral(":")
            .minimumPrintedDigits(2)
            .appendMinutes().toFormatter()


    private val updateTime: Runnable = object : Runnable {
        override fun run() {
            removeCallbacks(this)

            if (!isUpdating || !isEnabled) {
                return
            }
            updateText()


            val delay =
                Duration(
                    null,
                    DateTime.now().plusMinutes(1)
                        .withSecondOfMinute(countDownDestination.get(DateTimeFieldType.secondOfMinute()))
                        .withMillisOfSecond(countDownDestination.get(DateTimeFieldType.millisOfSecond()))
                )

            postDelayed(this, delay.millis)
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setUpdating(enabled)
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        setUpdating(isVisible)
    }

    private fun setUpdating(shouldUpdate: Boolean) {
        if (shouldUpdate && !isUpdating) {
            isUpdating = true
            updateTime.run()
        } else if (shouldUpdate && isUpdating) {
            isUpdating = false
            removeCallbacks(updateTime)
        }
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun updateText() {
        val leftDuration = Period(null as? ReadableInstant, countDownDestination)
        text = periodFormatter.print(leftDuration)
    }
}