package com.sambath.shoppingassistant.data

import com.sambath.shoppingassistant.data.api.PriceApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PriceAgent(
    private val apiClient: PriceApiClient = PriceApiClient()
) {
    suspend fun findBestOptions(item: ShoppingItem): ShoppingItem = withContext(Dispatchers.IO) {
        val result = apiClient.searchPrices(item.name, limit = 3)
        item.copy(
            imageUrl = result.imageUrl ?: imageFor(item.name),
            options = result.options
        )
    }

    suspend fun findBestOptionsSafe(item: ShoppingItem): Result<ShoppingItem> = runCatching {
        findBestOptions(item)
    }
}
