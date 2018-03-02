package net.dublin.bus.common

object Constants {
    const val CONTENT_TYPE = "Content-Type"
    const val CONTENT_TYPE_XML = "text/xml; charset=utf-8"
    const val SOAP_ACTION = "soapaction"

    const val NAMESPACE = "http://dublinbus.ie/"
    //const val API_URL_BASE = "http://rtpi.dublinbus.ie/"
    const val API_URL_BASE = "http://localhost:2543/"

    const val API_URL_BASE_SERVICE = "${API_URL_BASE}DublinBusRTPIService.asmx?op="

    const val API_URL_REAL_TIME_SOAP_METHOD = "GetRealTimeStopData_ForceLineNoteVisit"
    const val API_URL_REAL_TIME_CONTENT = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetRealTimeStopData_ForceLineNoteVisit xmlns=\"http://dublinbus.ie/\"><stopId>%s</stopId><forceRefresh>true</forceRefresh><forceLineNoteVisit>true</forceLineNoteVisit></GetRealTimeStopData_ForceLineNoteVisit></soap:Body></soap:Envelope>"

    const val API_URL_STOP_METHOD = "GetAllDestinations"
    const val API_URL_ROUTE_METHOD = "GetRoutesIncNiteLink_MobileFareCalc"

    const val API_URL_ROUTE_DETAIL_METHOD = "GetStopDataByRouteAndDirection"
    const val API_URL_ROUTE_DETAIL_CONTENT = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetStopDataByRouteAndDirection  xmlns=\"http://dublinbus.ie/\"><route>%s</route><direction>%s</direction></GetStopDataByRouteAndDirection ></soap:Body></soap:Envelope>"

    const val API_URL_STOP_NEAR = "${NAMESPACE}Templates/Public/RoutePlannerService/RTPIMapHandler.ashx?ne=53.650000,-6.000000&sw=53.050000,-6.700000&zoom=13&czoom=16&rjson=true"
}
