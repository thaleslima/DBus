package net.dublin.bus.ui.view.near

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_near.*
import net.dublin.bus.R
import net.dublin.bus.common.Constants
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.utilities.LocationRequestWrapper
import net.dublin.bus.ui.utilities.LocationUtil
import net.dublin.bus.ui.utilities.PermissionsUtils
import net.dublin.bus.ui.utilities.Sizes
import net.dublin.bus.ui.view.realtime.RealTimeActivity
import java.util.*

class NearActivity : AppCompatActivity(), OnMapReadyCallback, LocationRequestWrapper.OnNewLocationListener, NearAdapter.ItemClickListener {
    private val mapStateManager: MapStateManager = MapStateManager()

    private var mSupportMapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var mMarkersId: HashMap<String, Stop> = HashMap()
    private var mMarkers: HashMap<Int, Marker> = HashMap()
    private var markerAux: Marker? = null
    private val nearAdapter = NearAdapter(this)
    private var moveCamera = false
    private var smoothScroll = false
    private var latitudeSearch = 0.0
    private var longitudeSearch = 0.0
    private var stopNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near)
        mapStateManager.restoreInstanceState(savedInstanceState)

        setUpMapIfNeeded()
        initRecyclerView()
        setupViewProperties()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        mapStateManager.saveLocationCurrent(outState, mMap)
        mapStateManager.saveLocationSearch(outState, latitudeSearch, longitudeSearch)
        mapStateManager.saveStopNumberSelected(outState, stopNumber)
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        near_recycler_view.layoutManager = layoutManager
        near_recycler_view.isNestedScrollingEnabled = false
        near_recycler_view.adapter = nearAdapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(near_recycler_view)

        near_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!smoothScroll) {
                    val centerView = snapHelper.findSnapView(layoutManager)
                    val pos = layoutManager.getPosition(centerView)

                    mMarkers[pos]?.let {
                        chanceIcoMarker(it)
                    }
                }
            }
        })

        near_recycler_view.viewTreeObserver.addOnGlobalLayoutListener({
            try {
                val padding = Sizes.getDip(this@NearActivity, 10)
                mMap?.setPadding(0, near_recycler_view.height - padding, 0, near_recycler_view.height - padding)
            } catch (e: Exception) {
                e.fillInStackTrace()
            }
        })
    }

    private fun setupViewProperties() {
        near_search_button_view.setOnClickListener { search() }
        near_search_button_view.visibility = View.GONE
        near_back_view.setOnClickListener { onSupportNavigateUp() }
    }

    private fun search() {
        mMap?.cameraPosition?.target?.let {
            getStopsByLatLng(it.latitude, it.longitude)
        }
    }

    private fun chanceIcoMarker(marker: Marker) {
        markerAux?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map))
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map_selected))
        markerAux = marker
        stopNumber = mMarkersId[marker.id]?.stopNumber
    }

    private fun cleanMap() {
        mMarkersId.clear()
        mMarkers.clear()
        mMap?.clear()
        markerAux = null
        moveCamera = false

        near_search_button_view.visibility = View.GONE
        nearAdapter.clear()
    }

    private fun setUpMapIfNeeded() {
        if (mSupportMapFragment == null) {
            val fm = supportFragmentManager
            mSupportMapFragment = fm.findFragmentById(R.id.near_map) as SupportMapFragment
            mSupportMapFragment?.getMapAsync(this)

            if (!mapStateManager.restored && LocationUtil.requestLocationOrShowMessage(this, 0)) {
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
            chanceIcoMarker(marker)
            scrollToPosition(marker.tag as Int)
            true
        }

        map.setOnCameraMoveListener {
            moveCamera = true
        }
        map.setOnCameraIdleListener {
            if (moveCamera) near_search_button_view.visibility = View.VISIBLE
        }

        if (!mapStateManager.restored) {
            moveCamera(Constants.getLatLngDefault())
            getStopsByLatLng(Constants.LATITUDE, Constants.LONGITUDE)
        } else {
            moveCamera(mapStateManager.locationCurrent!!)
            getStopsByLatLng(mapStateManager.locationSearch!!.latitude, mapStateManager.locationSearch!!.longitude)
        }
    }

    private fun scrollToPosition(position: Int) {
        smoothScroll = true
        near_recycler_view.scrollToPosition(position)
        near_recycler_view.smoothScrollToPosition(position)



        Handler().postDelayed({ smoothScroll = false }, 1000)
    }

    private fun smoothScrollToPosition(position: Int) {
        near_recycler_view.scrollToPosition(position)
    }

    private fun moveCamera(latLng: LatLng) {
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun getStopsByLatLng(latitude: Double, longitude: Double) {
        latitudeSearch = latitude
        longitudeSearch = longitude

        StopRepository(application).getStopsByLatLng(latitude, longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    showStops(data)
                }, {

                })
    }

    private fun showStops(stops: List<Stop>) {
        var position = 0
        cleanMap()

        nearAdapter.replaceData(stops)

        for ((index, stop) in stops.withIndex()) {
            createMarker(stop)?.let {
                mMarkersId[it.id] = stop
                mMarkers[index] = it
                it.tag = index
                if (index == 0) {
                    chanceIcoMarker(it)
                }

                if (stop.stopNumber == mapStateManager.stopNumberSelected) {
                    position = index
                }
            }
        }

        restoredPosition(position)
    }

    private fun createMarker(stop: Stop): Marker? {
        return stop.latLng()?.let {
            val markerOptions = MarkerOptions().position(it)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map))
                    .title("stopNumber" + stop.stopNumber)

            mMap?.addMarker(markerOptions)
        }
    }

    private fun restoredPosition(position: Int) {
        if (mapStateManager.restored) {
            mMarkers[position]?.let {
                chanceIcoMarker(it)
                smoothScrollToPosition(position)
            }
            mapStateManager.restored = false
        }
    }

    override fun onItemClick(item: Stop) {
        RealTimeActivity.navigate(this, item)
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
        location.let {
            moveCamera(LatLng(it.latitude, it.longitude))
            getStopsByLatLng(it.latitude, it.longitude)
        }
    }

    companion object {
        fun navigate(context: Context) {
            val intent = Intent(context, NearActivity::class.java)
            context.startActivity(intent)
        }
    }
}
