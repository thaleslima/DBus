package net.dublin.bus.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore

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
