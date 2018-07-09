package net.dublin.bus.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "routes")
data class Route(
        @PrimaryKey
        var number: String = "",
        var description: String? = null,
        var direction: String? = null,
        var from: String? = null,
        var inboundFrom: String? = null,
        var inboundPattern: String? = null,
        var inboundTowards: String? = null,
        var inboundVia: String? = null,
        var isMinimumFare: Boolean = false,
        var isNitelink: Boolean = false,
        var isStaged: Boolean = false,
        var isXpresso: Boolean = false,
        var outboundFrom: String? = null,
        var outboundPattern: String? = null,
        var outboundTowards: String? = null,
        var outboundVia: String? = null,
        var seqNumber: String? = null,
        var towards: String? = null,
        var code: String? = null) {
    @Ignore constructor() : this("")
}