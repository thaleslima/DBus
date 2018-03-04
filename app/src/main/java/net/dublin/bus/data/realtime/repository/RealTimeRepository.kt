package net.dublin.bus.data.realtime.repository

import io.reactivex.Observable
import net.dublin.bus.data.realtime.remote.RealTimeDataSource
import net.dublin.bus.model.StopData
import java.util.*

class RealTimeRepository {
    fun getData(stopNumber: String): Observable<List<StopData>> {
        return RealTimeDataSource().getData(stopNumber)
//        return RealTimeDataSource().getData(stopNumber).map {
//            val list: ArrayList<StopData> = arrayListOf()
//            val n = rand(0, 1000)
//            list.add(StopData(destinationName = "Terminal $n"))
//            list.add(StopData(destinationName = "Terminal $n"))
//            list.add(StopData(destinationName = "Terminal $n"))
//            list
//        }
    }

    fun rand(from: Int, to: Int) : Int {
        return Random().nextInt(to - from) + from
    }
}
