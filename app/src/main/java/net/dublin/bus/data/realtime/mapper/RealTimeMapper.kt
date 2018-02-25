package net.dublin.bus.data.realtime.mapper

import android.util.Log
import net.dublin.bus.model.StopData
import org.w3c.dom.Element
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

object RealTimeMapper {
    private const val TAG = "GetRealTime"

    fun mapFrom(from: String?): List<StopData> {
        val list = ArrayList<StopData>()
        val builder: DocumentBuilder
        val factory: DocumentBuilderFactory

        if (from != null) {
            try {
                factory = DocumentBuilderFactory.newInstance()
                builder = factory.newDocumentBuilder()
                builder.isValidating
                val document = builder.parse(InputSource(StringReader(from)))
                document.documentElement.normalize()
                val nodeList = document.getElementsByTagName("StopData")
                val i = nodeList.length
                var j = 0

                if (i > 0) {
                    do {
                        var responseTimestamp = ""
                        var publishedLineName = ""
                        var destinationName = ""
                        var monitored = ""
                        var inCongestion = ""
                        var expectedDepartureTime = ""
                        var lineNote = ""
                        val localNode = nodeList.item(j)

                        if (localNode.nodeType == 1.toShort()) {
                            val element = localNode as Element
                            val nodeList2 = element.getElementsByTagName("ServiceDelivery_ResponseTimestamp").item(0).childNodes
                            if (nodeList2.length > 0) {
                                responseTimestamp = nodeList2.item(0).nodeValue.trim()
                            }
                            val nodeList3 = element.getElementsByTagName("MonitoredVehicleJourney_PublishedLineName").item(0).childNodes
                            if (nodeList3.length > 0) {
                                publishedLineName = nodeList3.item(0).nodeValue.trim().toLowerCase()
                            }
                            val nodeList4 = element.getElementsByTagName("MonitoredVehicleJourney_DestinationName").item(0).childNodes
                            if (nodeList4.length > 0) {
                                destinationName = nodeList4.item(0).nodeValue.trim()
                            }
                            val nodeList5 = element.getElementsByTagName("MonitoredVehicleJourney_Monitored").item(0).childNodes
                            if (nodeList5.length > 0) {
                                monitored = nodeList5.item(0).nodeValue.trim()
                            }
                            val nodeList6 = element.getElementsByTagName("MonitoredVehicleJourney_InCongestion").item(0).childNodes
                            if (nodeList6.length > 0) {
                                inCongestion = nodeList6.item(0).nodeValue.trim()
                            }
                            val nodeList7 = element.getElementsByTagName("MonitoredCall_ExpectedDepartureTime").item(0).childNodes
                            if (nodeList7.length > 0) {
                                expectedDepartureTime = nodeList7.item(0).nodeValue.trim()
                            }
                            val nodeList8 = element.getElementsByTagName("LineNote").item(0).childNodes
                            if (nodeList8.length > 0) {
                                lineNote = nodeList8.item(0).nodeValue.trim()
                            }
                        }
                        list.add(
                                StopData(
                                        lineNote,
                                        expectedDepartureTime,
                                        destinationName,
                                        inCongestion,
                                        monitored,
                                        publishedLineName,
                                        responseTimestamp
                                ))
                        j++
                    } while (j < i)
                }
            } catch (localSAXException: SAXException) {
                Log.e(TAG, localSAXException.localizedMessage)
            } catch (localIOException: IOException) {
                Log.e(TAG, localIOException.localizedMessage)
            } catch (localParserConfigurationException: ParserConfigurationException) {
                Log.e(TAG, localParserConfigurationException.localizedMessage)
            } catch (localException: Exception) {
                Log.e(TAG, localException.localizedMessage)
            }
        }

        return list
    }
}