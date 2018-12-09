package net.dublin.bus.data.route

import androidx.room.Room
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import net.dublin.bus.data.BusDatabase
import net.dublin.bus.model.Route
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RouteDaoTest {

    private lateinit var database: BusDatabase

    private val stop = Route(
            number = "1",
            seqNumber = "0",
            description = "Santry (Shanard Rd.) To Sandymount (St. John's Church)",
            isStaged = true,
            outboundPattern = "10001",
            outboundFrom = "Santry",
            outboundTowards = "Sandymount",
            outboundVia = "O'Connell St",
            inboundPattern = "11001",
            inboundFrom = "Sandymount",
            inboundTowards = "Shanard Road",
            inboundVia = "O'Connell Street",
            isXpresso = false,
            isNitelink = false,
            isMinimumFare = false)

    private val stops = listOf(stop)
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
    fun insertStopAndGetById() {
        // When inserting a task
        database.getRouteDao().saveAllRoutes(stops)

        // When getting the task by id from the database
        val loaded = database.getRouteDao().getRouteByNumber(stop.number)

        // The loaded data contains the expected values
        assertThat<Boolean>(loaded?.equals(stop), `is`<Boolean>(true))
    }
}