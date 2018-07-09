package net.dublin.bus.common

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream

object NetworkUtils {
    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: String, gZip: Boolean): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        return if (gZip) {
            convertInputStreamToString(response.body()!!.byteStream())
        } else {
            response.body()!!.string()
        }
    }

    @Throws(IOException::class)
    private fun convertInputStreamToString(inputStream: InputStream): String {
        val r = BufferedReader(InputStreamReader(GZIPInputStream(inputStream)))
        return r.use { it.readText() }
    }
}
