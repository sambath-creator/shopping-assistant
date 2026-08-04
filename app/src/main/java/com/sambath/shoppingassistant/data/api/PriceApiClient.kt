package com.sambath.shoppingassistant.data.api

import com.sambath.shoppingassistant.BuildConfig
import com.sambath.shoppingassistant.data.PriceOption
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class PriceSearchResult(
    val imageUrl: String?,
    val options: List<PriceOption>
)

class PriceApiException(message: String, cause: Throwable? = null) : Exception(message, cause)

class PriceApiClient(
    private val baseUrl: String = BuildConfig.PRICE_API_BASE_URL,
    private val client: OkHttpClient = defaultClient
) {
    suspend fun searchPrices(query: String, limit: Int = 3): PriceSearchResult {
        val trimmed = query.trim()
        if (trimmed.isBlank()) throw PriceApiException("Item name cannot be blank")

        val payload = JSONObject()
            .put("query", trimmed)
            .put("limit", limit)
            .toString()

        val request = Request.Builder()
            .url("${baseUrl.trimEnd('/')}/v1/prices/search")
            .post(payload.toRequestBody(JSON_MEDIA))
            .header("Accept", "application/json")
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string().orEmpty()

        if (!response.isSuccessful) {
            throw PriceApiException("Price API returned ${response.code}: ${body.take(200)}")
        }

        return parseResponse(body)
    }

    private fun parseResponse(raw: String): PriceSearchResult {
        val json = runCatching { JSONObject(raw) }
            .getOrElse { throw PriceApiException("Invalid JSON from price API", it) }

        val optionsArray = json.optJSONArray("options") ?: JSONArray()
        val options = buildList {
            for (index in 0 until optionsArray.length()) {
                val option = optionsArray.getJSONObject(index)
                add(
                    PriceOption(
                        store = option.getString("store"),
                        price = option.getString("price"),
                        url = option.getString("url"),
                        note = option.optString("note", "")
                    )
                )
            }
        }

        if (options.isEmpty()) {
            throw PriceApiException("Price API returned no options")
        }

        return PriceSearchResult(
            imageUrl = json.optString("imageUrl").takeIf { it.isNotBlank() },
            options = options
        )
    }

    companion object {
        private val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()

        private val defaultClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}
