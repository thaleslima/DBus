package net.dublin.bus.data.route

import net.dublin.bus.model.Route

import java.util.Comparator

class RouteComparator : Comparator<Route> {
    override fun compare(paramRoute1: Route, paramRoute2: Route): Int {
        return Integer.valueOf(getNumbersFromString(paramRoute1.number))!!.compareTo(Integer.valueOf(getNumbersFromString(paramRoute2.number)))
    }

    companion object {
        fun getNumbersFromString(paramString: String?): String {
            var str = ""
            (0 until paramString!!.length)
                    .asSequence()
                    .filter { Character.isDigit(paramString[it]) }
                    .forEach { str += paramString[it] }
            return str
        }
    }
}
