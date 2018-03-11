package net.dublin.bus.ui.view.utilities

import android.util.Log

import com.squareup.okhttp.mockwebserver.Dispatcher
import com.squareup.okhttp.mockwebserver.MockResponse
import com.squareup.okhttp.mockwebserver.MockWebServer
import com.squareup.okhttp.mockwebserver.RecordedRequest

import net.dublin.bus.common.Constants

import java.io.IOException

import android.support.test.InstrumentationRegistry.getInstrumentation

object MockServer {
    private val TAG = MockServer::class.java.name

    private const val FILE_NAME_REAL_TIME_RESPONSE = "real_time_response.xml"
    private const val FILE_NAME_WARNING_NO_ITEMS_REAL_TIME_RESPONSE = "real_time_warning_response.xml"
    private const val FILE_NAME_WARNING_REAL_TIME_RESPONSE = "real_time_line_note_response.xml"
    private const val FILE_NAME_NO_ITEMS_REAL_TIME_RESPONSE = "real_time_no_items_response.xml"

    private lateinit var server: MockWebServer

    @Throws(IOException::class)
    fun start() {
        server = MockWebServer()
        server.start(2543)
    }

    fun shutdown() {
        try {
            server.shutdown()
        } catch (e: IOException) {
            Log.d(TAG, e.message)
        }

    }

    fun setDispatcherRealTimeResponse200() {
        server.setDispatcher(createDispatcher200(Constants.API_URL_REAL_TIME_SOAP_METHOD, FILE_NAME_REAL_TIME_RESPONSE))
    }

    fun setDispatcherTimeWarningNoItemsResponse200() {
        server.setDispatcher(createDispatcher200(Constants.API_URL_REAL_TIME_SOAP_METHOD, FILE_NAME_WARNING_NO_ITEMS_REAL_TIME_RESPONSE))
    }

    fun setDispatcherTimeWarningWithItemsResponse200() {
        server.setDispatcher(createDispatcher200(Constants.API_URL_REAL_TIME_SOAP_METHOD, FILE_NAME_WARNING_REAL_TIME_RESPONSE))
    }

    fun setDispatcherNoItemsResponse200() {
        server.setDispatcher(createDispatcher200(Constants.API_URL_REAL_TIME_SOAP_METHOD, FILE_NAME_NO_ITEMS_REAL_TIME_RESPONSE))
    }

    fun setDispatcherResponse500() {
        server.setDispatcher(DispatcherResponse500())
    }

    private fun createDispatcher200(path: String, file: String): Dispatcher {
        return object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                if (request.path.contains(path)) {
                    return MockResponse()
                            .setResponseCode(200)
                            .setBody(StringUtil.getStringFromFile(getInstrumentation().context, file))
                }

                throw InterruptedException()
            }
        }
    }

    private class DispatcherResponse500 : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            return MockResponse()
                    .setResponseCode(500)
                    .setBody("")
        }
    }
}
