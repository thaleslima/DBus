package net.dublin.bus.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils
import com.google.gson.annotations.SerializedName

@Entity(tableName = "stops")
data class Stop(

        @PrimaryKey
        @SerializedName("stopnumber")
        var stopNumber: String = "",

        var address: String? = null,
        var description: String? = null,
        var descriptionLower: String? = null,
        var direction: String? = null,
        var isStagePoint: Boolean = false,

        @SerializedName("lat")
        var latitude: String? = null,
        var location: String? = null,

        @SerializedName("lng")
        var longitude: String? = null,
        var route: String? = null,
        var stageNumber: String? = null,
        var type: String? = null) {

    fun descriptionOrAddress(): String? {
        return if (!TextUtils.isEmpty(description)) {
            description
        } else if (!TextUtils.isEmpty(address)) {
            "$address, $location"
        } else {
            ""
        }
    }

}