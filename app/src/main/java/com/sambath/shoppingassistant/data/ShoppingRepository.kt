package com.sambath.shoppingassistant.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ShoppingRepository private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("baker_basket", Context.MODE_PRIVATE)
    private val agent = PriceAgent()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _state = MutableStateFlow(loadState())

    val state: StateFlow<ShoppingState> = _state

    fun addItem(name: String) {
        val cleaned = name.trim()
        if (cleaned.isBlank()) return
        _state.update { it.copy(items = it.items + ShoppingItem(name = cleaned)) }
        persist()
    }

    fun removeItem(itemId: Long) {
        _state.update { it.copy(items = it.items.filterNot { item -> item.id == itemId }) }
        persist()
    }

    fun renameItem(itemId: Long, name: String) {
        val cleaned = name.trim()
        if (cleaned.isBlank()) return
        _state.update { state ->
            state.copy(items = state.items.map { if (it.id == itemId) it.copy(name = cleaned, imageUrl = imageFor(cleaned)) else it })
        }
        persist()
    }

    fun updateSchedule(schedule: ScheduleSettings) {
        _state.update { it.copy(schedule = schedule) }
        persist()
        ShoppingScheduler.schedule(appContext, schedule)
    }

    fun refreshPrices() {
        scope.launch {
            refreshPricesNow()
        }
    }

    suspend fun refreshPricesNow() {
        _state.update { it.copy(isRefreshing = true, lastRunSummary = "Researching latest options...") }
        val refreshed = _state.value.items.map { agent.findBestOptions(it) }
        _state.update {
            it.copy(
                items = refreshed,
                isRefreshing = false,
                lastRunSummary = "Checked ${refreshed.size} items"
            )
        }
        persist()
    }

    private fun persist() {
        val state = _state.value
        prefs.edit()
            .putString("items", encodeItems(state.items).toString())
            .putInt("frequencyWeeks", state.schedule.frequencyWeeks)
            .putInt("dayOfWeek", state.schedule.dayOfWeek)
            .putInt("hour", state.schedule.hour)
            .putInt("minute", state.schedule.minute)
            .putString("lastRunSummary", state.lastRunSummary)
            .apply()
    }

    private fun loadState(): ShoppingState {
        val schedule = ScheduleSettings(
            frequencyWeeks = prefs.getInt("frequencyWeeks", 1),
            dayOfWeek = prefs.getInt("dayOfWeek", 1),
            hour = prefs.getInt("hour", 7),
            minute = prefs.getInt("minute", 30)
        )
        val items = prefs.getString("items", null)?.let(::decodeItems).orEmpty().ifEmpty { defaultItems }
        return ShoppingState(
            items = items,
            schedule = schedule,
            lastRunSummary = prefs.getString("lastRunSummary", "Not run yet") ?: "Not run yet"
        )
    }

    private fun encodeItems(items: List<ShoppingItem>): JSONArray {
        val array = JSONArray()
        items.forEach { item ->
            val options = JSONArray()
            item.options.forEach {
                options.put(JSONObject().put("store", it.store).put("price", it.price).put("url", it.url).put("note", it.note))
            }
            array.put(
                JSONObject()
                    .put("id", item.id)
                    .put("name", item.name)
                    .put("imageUrl", item.imageUrl)
                    .put("options", options)
            )
        }
        return array
    }

    private fun decodeItems(raw: String): List<ShoppingItem> {
        return runCatching {
            val array = JSONArray(raw)
            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                val options = item.optJSONArray("options") ?: JSONArray()
                ShoppingItem(
                    id = item.getLong("id"),
                    name = item.getString("name"),
                    imageUrl = item.optString("imageUrl", imageFor(item.getString("name"))),
                    options = List(options.length()) { optionIndex ->
                        val option = options.getJSONObject(optionIndex)
                        PriceOption(
                            store = option.getString("store"),
                            price = option.getString("price"),
                            url = option.getString("url"),
                            note = option.getString("note")
                        )
                    }
                )
            }
        }.getOrDefault(defaultItems)
    }

    companion object {
        @Volatile private var instance: ShoppingRepository? = null

        fun get(context: Context): ShoppingRepository {
            return instance ?: synchronized(this) {
                instance ?: ShoppingRepository(context).also { instance = it }
            }
        }
    }
}
