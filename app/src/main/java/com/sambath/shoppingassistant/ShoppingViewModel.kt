package com.sambath.shoppingassistant

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sambath.shoppingassistant.data.ScheduleSettings
import com.sambath.shoppingassistant.data.ShoppingRepository

class ShoppingViewModel(private val repository: ShoppingRepository) : ViewModel() {
    val state = repository.state

    fun addItem(name: String) = repository.addItem(name)
    fun removeItem(itemId: Long) = repository.removeItem(itemId)
    fun renameItem(itemId: Long, name: String) = repository.renameItem(itemId, name)
    fun updateSchedule(schedule: ScheduleSettings) = repository.updateSchedule(schedule)
    fun refreshPrices() = repository.refreshPrices()

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ShoppingViewModel(ShoppingRepository.get(context)) as T
            }
        }
    }
}
