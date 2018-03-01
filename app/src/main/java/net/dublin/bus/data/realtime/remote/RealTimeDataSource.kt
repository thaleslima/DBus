package net.dublin.bus.data.realtime.remote

import io.reactivex.Observable
import net.dublin.bus.data.realtime.mapper.RealTimeMapper
import net.dublin.bus.model.StopData
import net.dublin.bus.common.Constants
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

class RealTimeDataSource {
    fun getData(stopNumber: String): Observable<List<StopData>> {
        val content = String.format(Constants.API_URL_REAL_TIME_CONTENT, stopNumber)
        val mediaType = MediaType.parse(Constants.CONTENT_TYPE_XML)
        val body = RequestBody.create(mediaType, content)

        val client = OkHttpClient()

        val request = Request.Builder()
                .addHeader(Constants.SOAP_ACTION, Constants.NAMESPACE + Constants.API_URL_REAL_TIME_SOAP_METHOD)
                .addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_XML)
                .url(Constants.API_URL_BASE_SERVICE +  Constants.API_URL_REAL_TIME_SOAP_METHOD)
                .post(body)
                .build()

        return Observable.create { subscriber ->
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    subscriber.onNext(RealTimeMapper.mapFrom(response.body()!!.string()))
                    subscriber.onComplete()
                } else {
                    subscriber.onError(IOException())
                }
            } catch (e: IOException) {
                subscriber.onError(e)
            }
        }
    }
}
