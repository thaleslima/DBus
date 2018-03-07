package net.dublin.bus.data.stop.remote

import io.reactivex.Observable
import net.dublin.bus.common.Constants
import net.dublin.bus.model.Stop
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.PropertyInfo
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.io.IOException
import java.util.*

class RemoteStopDataSource {
    fun getData(): Observable<List<Stop>> {
        val soapObject = SoapObject(Constants.NAMESPACE, Constants.API_URL_STOP_METHOD)

        val intA = PropertyInfo()
        intA.setName("filter")
        intA.value = ""
        soapObject.addProperty(intA)

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = true
        envelope.setOutputSoapObject(soapObject)
        val httpTransportSE = HttpTransportSE(Constants.API_URL_BASE_SERVICE + Constants.API_URL_STOP_METHOD)

        return Observable.create { subscriber ->
            try {
                httpTransportSE.call(Constants.NAMESPACE + Constants.API_URL_STOP_METHOD, envelope)
                val soapPrimitive = envelope.response as SoapObject
                val result = soapPrimitive.getProperty(0) as SoapObject
                val list = ArrayList<Stop>()
                val i = result.propertyCount

                for (j in 0 until i) {
                    val o = result.getProperty(j) as SoapObject
                    val stop = Stop()
                    stop.stopNumber = o.getProperty("StopNumber").toString()
                    stop.type = o.getProperty("Type").toString()
                    stop.latitude = o.getProperty("Longitude").toString().toDoubleOrNull() ?: 0.0
                    stop.longitude = o.getProperty("Latitude").toString().toDoubleOrNull() ?: 0.0
                    stop.description = o.getProperty("Description").toString()
                    stop.descriptionLower = o.getProperty("Description").toString().toLowerCase()
                    list.add(stop)
                }

                subscriber.onNext(list)
                subscriber.onComplete()
            } catch (e: IOException) {
                e.localizedMessage
                if (!subscriber.isDisposed) {
                    subscriber.onError(e)
                }
            }
        }
    }


}
