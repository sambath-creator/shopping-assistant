import java.util.UUID

package com.sambath.shoppingassistant.data

data class ShoppingItem(
    val id: Long = UUID.randomUUID().toString(),
    val name: String,
    val imageUrl: String = imageFor(name),
    val options: List<PriceOption> = emptyList()
)

data class PriceOption(
    val store: String,
    val price: String,
    val url: String,
    val note: String
)

data class ScheduleSettings(
    val frequencyWeeks: Int = 1,
    val dayOfWeek: Int = 1,
    val hour: Int = 7,
    val minute: Int = 30
) {
    val display: String
        get() {
            val day = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                .getOrElse(dayOfWeek - 1) { "Mon" }
            return "Every $frequencyWeeks week${if (frequencyWeeks == 1) "" else "s"} on $day at %02d:%02d"
                .format(hour, minute)
        }
}

data class ShoppingState(
    val items: List<ShoppingItem> = defaultItems,
    val schedule: ScheduleSettings = ScheduleSettings(),
    val isRefreshing: Boolean = false,
    val lastRunSummary: String = "Not run yet"
)

val defaultItems = listOf(
    ShoppingItem(name = "Strong white bread flour"),
    ShoppingItem(name = "Unsalted butter"),
    ShoppingItem(name = "Caster sugar"),
    ShoppingItem(name = "Free range eggs"),
    ShoppingItem(name = "Dark chocolate chips")
)

fun imageFor(itemName: String): String {
    val query = itemName.trim().ifBlank { "baking ingredient" }.replace(" ", "%20")
    return "https://source.unsplash.com/600x420/?$query,baking"
}
