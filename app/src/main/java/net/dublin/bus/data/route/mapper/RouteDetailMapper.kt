package net.dublin.bus.data.route.mapper

import net.dublin.bus.model.Stop
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

object RouteDetailMapper {
    fun mapFrom(from: String?): List<Stop> {
        val list = ArrayList<Stop>()
        val builder: DocumentBuilder
        val factory: DocumentBuilderFactory

        if (from != null) {
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
                    var longitude: String? = null
                    var latitude: String? = null
                    var isStagePoint = false
                    var stageNumber = ""
                    val localNode = localNodeList1.item(j)
                    if (localNode.nodeType == 1.toShort()) {

                        val localElement = localNode as Element
                        val localNodeList2 = (localElement.getElementsByTagName("Route").item(0) as Element).childNodes
                        if (localNodeList2.length > 0) {
                            route = localNodeList2.item(0).nodeValue.trim { it <= ' ' }
                        }
                        val localNodeList3 = (localElement.getElementsByTagName("Direction").item(0) as Element).childNodes
                        if (localNodeList3.length > 0) {
                            direction = localNodeList3.item(0).nodeValue.trim { it <= ' ' }
                        }
                        val localNodeList4 = (localElement.getElementsByTagName("StopNumber").item(0) as Element).childNodes
                        if (localNodeList4.length > 0) {
                            stopNumber = localNodeList4.item(0).nodeValue.trim { it <= ' ' }
                        }
                        val localNodeList5 = (localElement.getElementsByTagName("SeqNumber").item(0) as Element).childNodes
                        if (localNodeList5.length > 0) {
                            localNodeList5.item(0).nodeValue.trim { it <= ' ' }
                        }
                        val localNodeList6 = (localElement.getElementsByTagName("Address").item(0) as Element).childNodes
                        if (localNodeList6.length > 0) {
                            address = localNodeList6.item(0).nodeValue.trim { it <= ' ' }
                        }
                        val localNodeList7 = (localElement.getElementsByTagName("Location").item(0) as Element).childNodes
                        if (localNodeList7.length > 0) {
                            location = localNodeList7.item(0).nodeValue.trim { it <= ' ' }
                        }
                        val localNodeList8 = (localElement.getElementsByTagName("Latitude").item(0) as Element).childNodes
                        if (localNodeList8.length > 0) {
                            latitude = localNodeList8.item(0).nodeValue.trim { it <= ' ' }
                        }
                        val localNodeList9 = (localElement.getElementsByTagName("Longitude").item(0) as Element).childNodes
                        if (localNodeList9.length > 0) {
                            longitude = localNodeList9.item(0).nodeValue.trim { it <= ' ' }
                        }
//                        val isStagePointNodes = localElement.getElementsByTagName("IsStagePoint")
//                        if (isStagePointNodes.length > 0 && isStagePointNodes.item(0).childNodes.length > 0) {
//                            isStagePoint = java.lang.Boolean.valueOf(isStagePointNodes.item(0).textContent.trim { it <= ' ' })!!.booleanValue()
//                        }
                        val stageNumberNodes = localElement.getElementsByTagName("StageNumber")
                        if (stageNumberNodes.length > 0 && stageNumberNodes.item(0).childNodes.length > 0) {
                            stageNumber = stageNumberNodes.item(0).textContent.trim { it <= ' ' }
                        }
                    }

                    val lat = latitude?.toDoubleOrNull()
                    val lon = longitude?.toDoubleOrNull()

                    list.add(Stop(
                            stopNumber = stopNumber,
                            latitude = lat ?: 0.0,
                            longitude = lon ?: 0.0,
                            routes = route,
                            direction = direction,
                            address = address,
                            location = location,
                            stageNumber = stageNumber)
                    )
                    j++
                } while (j < i)
            }
        }

        return list
    }
}