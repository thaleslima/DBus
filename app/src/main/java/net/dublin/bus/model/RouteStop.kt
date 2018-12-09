package net.dublin.bus.model

import androidx.room.Entity
import androidx.room.Ignore

@Entity(tableName = "route_stop",
        primaryKeys = ["numberRoute", "numberStop"])
data class RouteStop(
        var numberRoute: String = "",
        var numberStop: String = "") {
    @Ignore constructor() : this("", "")
}
