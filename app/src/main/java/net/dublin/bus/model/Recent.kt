package net.dublin.bus.model

import android.arch.persistence.room.Entity

@Entity(tableName = "recent", primaryKeys = ["number", "type"])
data class Recent(
        var number: String = "",

        var type: Int = 0,

        var date: Long? = null) {

    fun isStop(): Boolean {
        return type == RecentType.STOP.ordinal
    }

}
