package net.dublin.bus.data.routestop

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

import net.dublin.bus.data.BusDatabase
import net.dublin.bus.model.Route
import net.dublin.bus.model.RouteStop
import net.dublin.bus.model.Stop
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RouteStopDaoTest {

    private lateinit var database: BusDatabase

    private val route1 = Route(number = "1", description = "1")
    private val route2 = Route(number = "2", description = "2")
    private val route3 = Route(number = "3", description = "3")
    private val route4 = Route(number = "4", description = "4")
    private val routes = listOf(route1, route2, route3, route4)

    private val stop1 = Stop(stopNumber = "1", description = "1")
    private val stop2 = Stop(stopNumber = "2", description = "2")
    private val stop3 = Stop(stopNumber = "3", description = "3")
    private val stop4 = Stop(stopNumber = "4", description = "4")
    private val stops = listOf(stop1, stop2, stop3, stop4)

    private val routeStop1 = RouteStop(numberRoute = "1", numberStop = "1")
    private val routeStop2 = RouteStop(numberRoute = "1", numberStop = "2")
    private val routeStop3 = RouteStop(numberRoute = "1", numberStop = "3")
    private val routeStop4 = RouteStop(numberRoute = "2", numberStop = "1")
    private val routeStop5 = RouteStop(numberRoute = "2", numberStop = "3")
    private val routeStop6 = RouteStop(numberRoute = "2", numberStop = "4")
    private val routeStop7 = RouteStop(numberRoute = "3", numberStop = "4")
    private val routeStop8 = RouteStop(numberRoute = "4", numberStop = "3")

    private val routeStops = listOf(
            routeStop1,
            routeStop2,
            routeStop3,
            routeStop4,
            routeStop5,
            routeStop6,
            routeStop7,
            routeStop8
    )


    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context,
                BusDatabase::class.java).build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertRouteStopsAndGetByRouteNumber() {
        database.getRouteDao().saveAllRoutes(routes)
        database.getStopDao().saveAllStops(stops)
        database.getRouteStopDao().saveAllRouteStops(routeStops)

        var loaded = database.getRouteStopDao().getStopsForRoutes(arrayOf(route1.number))
        assertThat<Int>(loaded.size, `is`(3))

        loaded = database.getRouteStopDao().getStopsForRoutes(arrayOf(route1.number, route4.number))
        assertThat<Int>(loaded.size, `is`(4))

        assertThat<Boolean>(loaded[0] == stop1, `is`<Boolean>(true))
        assertThat<Boolean>(loaded[3] == stop3, `is`<Boolean>(true))
    }

    @Test
    fun insertRouteStopsAndGetByStopNumber() {
        database.getRouteDao().saveAllRoutes(routes)
        database.getStopDao().saveAllStops(stops)
        database.getRouteStopDao().saveAllRouteStops(routeStops)

        val loaded = database.getRouteStopDao().getRoutesForStop(stop1.stopNumber)
        assertThat<Int>(loaded.size, `is`(2))
        assertThat<Boolean>(loaded[0] == route1, `is`<Boolean>(true))
        assertThat<Boolean>(loaded[1] == route2, `is`<Boolean>(true))
    }
}