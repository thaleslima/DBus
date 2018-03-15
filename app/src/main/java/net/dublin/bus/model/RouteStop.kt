package net.dublin.bus.model

import android.arch.persistence.room.Entity

@Entity(tableName = "route_stop",
        primaryKeys = ["numberRoute", "numberStop"])
data class RouteStop(
        var numberRoute: String = "",
        var numberStop: String = ""
)
