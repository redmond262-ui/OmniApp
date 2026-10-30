package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** Cliente HTTP mínimo (sin dependencias extra). Las APIs de OpenStreetMap exigen un User-Agent propio. */
internal object Http {
    private const val USER_AGENT = "OmniHub/1.0 (app Android de prueba)"

    suspend fun get(url: String, timeoutMs: Int = 12_000): String = withContext(Dispatchers.IO) {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "GET"
            conn.connectTimeout = timeoutMs
            conn.readTimeout = timeoutMs
            conn.setRequestProperty("User-Agent", USER_AGENT)
            conn.setRequestProperty("Accept", "application/json")
            val code = conn.responseCode
            if (code !in 200..299) throw IOException("HTTP $code en $url")
            conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }

    suspend fun postForm(url: String, field: String, value: String, timeoutMs: Int = 25_000): String =
        withContext(Dispatchers.IO) {
            val conn = URL(url).openConnection() as HttpURLConnection
            try {
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.connectTimeout = timeoutMs
                conn.readTimeout = timeoutMs
                conn.setRequestProperty("User-Agent", USER_AGENT)
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                val body = field + "=" + URLEncoder.encode(value, "UTF-8")
                conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                val code = conn.responseCode
                if (code !in 200..299) throw IOException("HTTP $code en $url")
                conn.inputStream.bufferedReader().use { it.readText() }
            } finally {
                conn.disconnect()
            }
        }

    fun enc(text: String): String = URLEncoder.encode(text, "UTF-8")
}
