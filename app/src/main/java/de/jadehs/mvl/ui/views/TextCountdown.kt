package de.jadehs.mvl.ui.views

import android.content.Context
import android.util.AttributeSet
import kotlinx.coroutines.Runnable
import org.joda.time.*
import org.joda.time.format.PeriodFormatterBuilder

class TextCountdown : androidx.appcompat.widget.AppCompatTextView {


    private var isUpdating = false

    var countDownDestination: Instant = Instant.now()
    var periodFormatter =
        PeriodFormatterBuilder().minimumPrintedDigits(1).appendHours().appendLiteral(":")
            .minimumPrintedDigits(2)
            .appendMinutes().toFormatter()


    private val updateTime: Runnable = object : Runnable {
        override fun run() {
            removeCallbacks(this)

            if (!isUpdating) {
                return
            }

            onTimeChanged()



            val delay =
                Duration(null,
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


    init {

    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        if (isVisible && !isUpdating) {
            isUpdating = true
            updateTime.run()
        } else if (isVisible && isUpdating) {
            isUpdating = false
            removeCallbacks(updateTime)
        }
        super.onVisibilityAggregated(isVisible)
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun onTimeChanged() {
        val leftDuration = Period(null as? ReadableInstant, countDownDestination)
        text = periodFormatter.print(leftDuration)
    }
}