package net.dublin.bus.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import android.text.TextUtils
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import net.dublin.bus.common.Haversine

@Entity(tableName = "stops")
data class Stop(
        @PrimaryKey
        @SerializedName("stopnumber")
        var stopNumber: String = "",

        @Ignore
        var address: String? = null,

        var description: String? = null,

        @Ignore
        var descriptionLower: String? = null,

        @Ignore
        var direction: String? = null,

        @Ignore
        var isStagePoint: Boolean = false,

        @NonNull
        @SerializedName("lat")
        var latitude: Double ? = null,

        @NonNull
        @SerializedName("lng")
        var longitude: Double ? = null,

        @Ignore
        var location: String? = null,

        @Ignore
        var route: String? = null,

        @Ignore
        var distance: Double = 0.0,

        @Ignore
        var stageNumber: String? = null,

        @Ignore
        var type: String? = null) {


    fun latLng(): LatLng? {
        if(latitude != null && longitude != null) {
            return LatLng(latitude!!, longitude!!)
        }

        return null
    }

    fun descriptionOrAddress(): String? {
        return if (!TextUtils.isEmpty(description)) {
            description
        } else if (!TextUtils.isEmpty(address)) {
            "$address, $location"
        } else {
            ""
        }
    }

    fun calculateDistance(latitudeCurrent: Double, longitudeCurrent: Double) {
        if(latitude != null && longitude != null) {
            distance = Haversine.distance(latitude!!, longitude!!, latitudeCurrent, longitudeCurrent)
        }
    }
}