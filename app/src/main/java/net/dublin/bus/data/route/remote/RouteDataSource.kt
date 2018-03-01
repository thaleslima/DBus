package net.dublin.bus.data.route.remote

import io.reactivex.Observable
import net.dublin.bus.data.route.mapper.RouteDetailMapper
import net.dublin.bus.data.route.RouteComparator
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop
import net.dublin.bus.common.Constants
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.PropertyInfo
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.io.IOException
import java.util.*

class RouteDataSource {
    fun getData(): Observable<List<Route>> {
        val soapObject = SoapObject(Constants.NAMESPACE, Constants.API_URL_ROUTE_METHOD)

        val intA = PropertyInfo()
        intA.setName("filter")
        intA.value = ""
        soapObject.addProperty(intA)

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = true
        envelope.setOutputSoapObject(soapObject)
        val httpTransportSE = HttpTransportSE(Constants.API_URL_BASE_SERVICE + Constants.API_URL_ROUTE_METHOD)

        return Observable.create { subscriber ->
            try {
                httpTransportSE.call(Constants.NAMESPACE + Constants.API_URL_ROUTE_METHOD, envelope)
                val soapPrimitive = envelope.response as SoapObject
                val result = soapPrimitive.getProperty(0) as SoapObject
                val list = ArrayList<Route>()
                val i = result.propertyCount

                for (j in 0 until i) {
                    val o = result.getProperty(j) as SoapObject
                    val route = Route()
                    route.number = o.getProperty("Number").toString()
                    route.inboundTowards = o.getProperty("InboundTowards").toString()
                    route.outboundTowards = o.getProperty("OutboundTowards").toString()
                    list.add(route)
                }

                subscriber.onNext(list)
                subscriber.onComplete()
            } catch (e: IOException) {
                subscriber.onError(e)
            }
        }
    }

    fun getDataDetail(route: String, direction: String): Observable<List<Stop>> {
        val content = String.format(Constants.API_URL_ROUTE_DETAIL_CONTENT, route, direction)
        val mediaType = MediaType.parse(Constants.CONTENT_TYPE_XML)
        val body = RequestBody.create(mediaType, content)

        val client = OkHttpClient()

        val request = Request.Builder()
                .addHeader(Constants.SOAP_ACTION, Constants.NAMESPACE + Constants.API_URL_ROUTE_DETAIL_METHOD)
                .addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_XML)
                .url(Constants.API_URL_BASE_SERVICE + Constants.API_URL_ROUTE_DETAIL_METHOD)
                .post(body)
                .build()

        return Observable.create { subscriber ->
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    subscriber.onNext(RouteDetailMapper.mapFrom( response.body()!!.string()))
                    subscriber.onComplete()
                } else {
                    subscriber.onError(IOException())
                }
            } catch (e: Exception) {
                subscriber.onError(e)
            }
        }
    }
}
