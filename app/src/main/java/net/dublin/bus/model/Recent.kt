package net.dublin.bus.model

import androidx.room.Entity
import androidx.room.Ignore

@Entity(tableName = "recent", primaryKeys = ["number", "type"])
data class Recent(
        var number: String = "",

        var type: Int = 0,

        var date: Long? = null) {

    @Ignore constructor() : this("")

    fun isStop(): Boolean {
        return type == RecentType.STOP.ordinal
    }
}
