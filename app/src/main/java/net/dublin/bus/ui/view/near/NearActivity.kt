package net.dublin.bus.ui.view.near

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_near.*
import net.dublin.bus.*
import net.dublin.bus.common.Constants
import net.dublin.bus.model.Stop
import net.dublin.bus.model.Stops
import net.dublin.bus.ui.utilities.*
import net.dublin.bus.ui.view.realtime.RealTimeActivity
import okhttp3.*
import java.io.IOException
import java.util.*


class NearActivity : AppCompatActivity(), OnMapReadyCallback, LocationRequestWrapper.OnNewLocationListener, NearAdapter.ItemClickListener {
    override fun onItemClick(item: Stop) {
        RealTimeActivity.navigate(this, item)
    }

    private var mSupportMapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var mMarkersId: HashMap<String, Stop>? = HashMap()
    private var markerAux: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near)
        setUpMapIfNeeded()
        initRecyclerVideos()
    }

    private val nearAdapter = NearAdapter(this)

    private fun initRecyclerVideos() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        near_recycler_view.layoutManager = layoutManager
        near_recycler_view.isNestedScrollingEnabled = false
        near_recycler_view.adapter = nearAdapter

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(near_recycler_view)

        near_recycler_view.viewTreeObserver.addOnGlobalLayoutListener({
            try {
                val padding = Sizes.getDip(this@NearActivity, 10)
                mMap?.setPadding(0, near_recycler_view.height - padding, 0, near_recycler_view.height - padding)
            } catch (e: Exception) {
                e.fillInStackTrace()
            }
        })

//        detail_map_search_view.setOnClickListener({
//
//        })
    }


    fun run() {
        val body = FormBody.Builder().build()
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(Constants.API_URL_STOP_NEAR)
                .post(body)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val objectList = Gson().fromJson(response?.body()?.string(), Stops::class.java)

                this@NearActivity.runOnUiThread(java.lang.Runnable {
                    //showLocals(objectList.points.subList(1, 200))
                })
            }

            override fun onFailure(call: Call?, e: IOException?) {
            }
        })
    }


    fun showLocals(locals: List<Stop>) {
        var latLng: LatLng
        var marker: Marker?
        mMarkersId?.clear()

        for (local in locals) {
//            latLng = LatLng(local.latitude.toDouble(), local.longitude.toDouble())
//
//
//
//            val markerOptions = MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map))
//            marker = mMap?.addMarker(markerOptions)
//            marker?.let { m -> mMarkersId?.put(m.id, local) }
        }

        nearAdapter.replaceData(locals)
    }


    private fun setUpMapIfNeeded() {
        if (mSupportMapFragment == null) {
            val fm = supportFragmentManager
            mSupportMapFragment = fm.findFragmentById(R.id.map) as SupportMapFragment
            mSupportMapFragment?.getMapAsync(this)

            if (LocationUtil.requestLocationOrShowMessage(this, 0)) {
                connectLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        mMap = map

        if (PermissionsUtils.Location.hasPermission(this)) {
            mMap?.isMyLocationEnabled = true
            mMap?.uiSettings?.isMyLocationButtonEnabled = false
        }

        map.setOnMarkerClickListener { marker ->
            markerAux?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map))
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map_selected))
            markerAux = marker
            false
        }

        run()
    }

    private fun connectLocation() {
        val locationRequestWrapper = LocationRequestWrapper(this, this)
        locationRequestWrapper.connect()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 0 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            connectLocation()
        }
    }

    override fun onNewLocation(location: Location) {
        //        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 12f))

        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location.latitude.let { LatLng(it, location.longitude) }, 12f))
    }

    companion object {
        fun navigate(context: Context) {
            val intent = Intent(context, NearActivity::class.java)
            context.startActivity(intent)
        }
    }
}
