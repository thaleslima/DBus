package net.dublin.bus.data.realtime.mapper

import android.util.Log
import net.dublin.bus.model.Stop
import org.w3c.dom.Element
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

object RouteDetailMapper {
    private const val TAG = "GetStopDataByRoute"

    fun mapFrom(from: String?): List<Stop> {
        val list = ArrayList<Stop>()
        val builder: DocumentBuilder
        val factory: DocumentBuilderFactory

        if (from != null) {
            try {
                factory = DocumentBuilderFactory.newInstance()
                builder = factory.newDocumentBuilder()
                builder.isValidating
                val localDocument = builder.parse(InputSource(StringReader(from)))
                localDocument.documentElement.normalize()
                val localNodeList1 = localDocument.getElementsByTagName("Stops")
                val i = localNodeList1.length
                var j = 0

                if (i > 0) {
                    do {
                        var route = ""
                        var direction = ""
                        var stopNumber = ""
                        var address = ""
                        var location = ""
                        var longitude = ""
                        var latitude = ""
                        //var isStagePoint = false
                        var stageNumber = ""
                        val localNode = localNodeList1.item(j)

                        if (localNode.nodeType == 1.toShort()) {
                            val localElement = localNode as Element
                            val localNodeList2 = (localElement.getElementsByTagName("Route").item(0) as Element).childNodes
                            if (localNodeList2.length > 0) {
                                route = localNodeList2.item(0).nodeValue.trim()
                            }
                            val localNodeList3 = (localElement.getElementsByTagName("Direction").item(0) as Element).childNodes
                            if (localNodeList3.length > 0) {
                                direction = localNodeList3.item(0).nodeValue.trim()
                            }
                            val localNodeList4 = (localElement.getElementsByTagName("StopNumber").item(0) as Element).childNodes
                            if (localNodeList4.length > 0) {
                                stopNumber = localNodeList4.item(0).nodeValue.trim()
                            }
                            val localNodeList5 = (localElement.getElementsByTagName("SeqNumber").item(0) as Element).childNodes
                            if (localNodeList5.length > 0) {
                                localNodeList5.item(0).nodeValue.trim()
                            }
                            val localNodeList6 = (localElement.getElementsByTagName("Address").item(0) as Element).childNodes
                            if (localNodeList6.length > 0) {
                                address = localNodeList6.item(0).nodeValue.trim()
                            }
                            val localNodeList7 = (localElement.getElementsByTagName("Location").item(0) as Element).childNodes
                            if (localNodeList7.length > 0) {
                                location = localNodeList7.item(0).nodeValue.trim()
                            }
                            val localNodeList8 = (localElement.getElementsByTagName("Latitude").item(0) as Element).childNodes
                            if (localNodeList8.length > 0) {
                                latitude = localNodeList8.item(0).nodeValue.trim()
                            }
                            val localNodeList9 = (localElement.getElementsByTagName("Longitude").item(0) as Element).childNodes
                            if (localNodeList9.length > 0) {
                                longitude = localNodeList9.item(0).nodeValue.trim()
                            }
//                            val isStagePointNodes = localElement.getElementsByTagName("IsStagePoint")
//                            if (isStagePointNodes.length > 0 && isStagePointNodes.item(0).childNodes.length > 0) {
//                                //isStagePoint = java.lang.Boolean.valueOf(isStagePointNodes.item(0).textContent.trim())!!.booleanValue()
//                            }
                            val stageNumberNodes = localElement.getElementsByTagName("StageNumber")
                            if (stageNumberNodes.length > 0 && stageNumberNodes.item(0).childNodes.length > 0) {
                                stageNumber = stageNumberNodes.item(0).textContent.trim()
                            }
                        }

                        list.add(Stop(
                                stopNumber = stopNumber,
                                latitude = latitude,
                                longitude = longitude,
                                route = route,
                                direction = direction,
                                address = address,
                                location = location,
                                stageNumber = stageNumber)
                        )

                        j++
                    } while (j < i)
                }
            } catch (localIOException: IOException) {
                Log.e(TAG, localIOException.message)
            } catch (localParserConfigurationException: ParserConfigurationException) {
                Log.e(TAG, localParserConfigurationException.message)
            } catch (localException: Exception) {
                Log.e(TAG, localException.message)
            }
        }

        return list
    }
}