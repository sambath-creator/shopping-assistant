package com.sambath.shoppingassistant.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sambath.shoppingassistant.data.ShoppingRepository

class PriceRefreshWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val repository = ShoppingRepository.get(applicationContext)
        repository.refreshPricesNow(notifyOnComplete = true)
        return Result.success()
    }
}
