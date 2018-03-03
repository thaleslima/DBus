package net.dublin.bus.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favourites")
data class Favourite(
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

        @Ignore
        @SerializedName("lat")
        var latitude: String? = null,

        @Ignore
        var location: String? = null,

        @Ignore
        @SerializedName("lng")
        var longitude: String? = null,

        @Ignore
        var route: String? = null,

        @Ignore
        var stageNumber: String? = null,

        @Ignore
        var type: String? = null) {


    fun latLng(): LatLng? {
        val lat = latitudeDoubleOrNull()
        val lng = longitudeDoubleOrNull()

        if (lat != null && lng != null) {
            return LatLng(lat, lng)
        }

        return null
    }

    fun latitudeDoubleOrNull(): Double? {
        latitude?.let {
            return it.toDoubleOrNull()
        }

        return null
    }

    fun longitudeDoubleOrNull(): Double? {
        longitude?.let {
            return it.toDoubleOrNull()
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
}