package com.sambath.shoppingassistant.data

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sambath.shoppingassistant.worker.PriceRefreshWorker
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object ShoppingScheduler {
    private const val WORK_NAME = "weekly_price_refresh"

    fun schedule(context: Context, settings: ScheduleSettings) {
        val repeatDays = (settings.frequencyWeeks * 7L).coerceAtLeast(7L)
        val request = PeriodicWorkRequestBuilder<PriceRefreshWorker>(repeatDays, TimeUnit.DAYS)
            .setInitialDelay(initialDelay(settings).toMillis(), TimeUnit.MILLISECONDS)
            .addTag(WORK_NAME)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun initialDelay(settings: ScheduleSettings): Duration {
        val now = LocalDateTime.now()
        val runDay = DayOfWeek.of(settings.dayOfWeek.coerceIn(1, 7))
        val runTime = LocalTime.of(settings.hour.coerceIn(0, 23), settings.minute.coerceIn(0, 59))
        var next = now.with(runTime).with(java.time.temporal.TemporalAdjusters.nextOrSame(runDay))
        if (!next.isAfter(now)) {
            next = next.plusWeeks(settings.frequencyWeeks.toLong().coerceAtLeast(1))
        }
        return Duration.between(now, next)
    }
}
