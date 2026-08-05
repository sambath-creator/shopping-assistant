package com.sambath.shoppingassistant.data

import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

class PriceAgent {
    private val stores = listOf(
        "Sainsbury's" to "https://www.sainsburys.co.uk/gol-ui/SearchResults/%s",
        "Asda" to "https://groceries.asda.com/search/%s",
        "Aldi" to "https://groceries.aldi.co.uk/en-GB/Search?keywords=%s",
        "Lidl" to "https://www.lidl.co.uk/search?query=%s",
        "Morrisons" to "https://groceries.morrisons.com/search?entry=%s",
        "Amazon" to "https://www.amazon.co.uk/s?k=%s",
        "Vanilla Valley" to "https://www.vanillavalley.co.uk/catalogsearch/result/?q=%s"
    )

    suspend fun findBestOptions(item: ShoppingItem): ShoppingItem {
        delay(300)
        val query = item.name.trim().replace(" ", "+")
        val seed = item.name.lowercase().hashCode().absoluteValue
        val results = stores.mapIndexed { index, store ->
            val pence = 120 + ((seed / (index + 3)) % 620)
            PriceOption(
                store = store.first,
                price = "GBP %.2f".format(pence / 100.0),
                url = store.second.format(query),
                note = if (index < 5) "UK supermarket result" else "Online result"
            )
        }.sortedBy { it.price.removePrefix("GBP ").toDouble() }

        return item.copy(
            imageUrl = imageFor(item.name),
            options = results.take(3)
        )
    }

    suspend fun findBestOptionsSafe(item: ShoppingItem): Result<ShoppingItem> = runCatching {
        findBestOptions(item)
    }
}
