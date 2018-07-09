package net.dublin.bus.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore

@Entity(tableName = "route_stop",
        primaryKeys = ["numberRoute", "numberStop"])
data class RouteStop(
        var numberRoute: String = "",
        var numberStop: String = "") {
    @Ignore constructor() : this("", "")
}
