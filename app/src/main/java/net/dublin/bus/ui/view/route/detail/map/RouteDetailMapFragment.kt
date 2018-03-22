package net.dublin.bus.ui.view.route.detail.map

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_route_detail_map.*
import net.dublin.bus.R
import net.dublin.bus.common.AnalyticsUtil
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.utilities.*
import net.dublin.bus.ui.view.realtime.RealTimeActivity
import net.dublin.bus.ui.view.route.detail.RouteDetailViewModel
import net.dublin.bus.ui.view.route.detail.RouteDetailViewModelFactory
import java.util.*

class RouteDetailMapFragment : Fragment(), OnMapReadyCallback, LocationRequestWrapper.OnNewLocationListener {
    private lateinit var model: RouteDetailViewModel
    private var mSupportMapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var mMarkersId: HashMap<String, Stop> = HashMap()
    private var markerAux: Marker? = null
    private var mStopNumber: String? = null
    private var mStopNumberRestore: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_route_detail_map, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initExtra(savedInstanceState)
        setUpMapIfNeeded()
        setupViewProperties()
    }

    private fun initExtra(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mStopNumberRestore = savedInstanceState.getString(BUNDLE_MARKER_STOP)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        mStopNumber?.let {
            outState?.putString(BUNDLE_MARKER_STOP, it)
        }
    }

    fun loadData() {
        val factory = RouteDetailViewModelFactory(RouteRepository(activity))
        model = ViewModelProviders.of(activity, factory).get(RouteDetailViewModel::class.java)
        model.getStops().observe(activity, Observer<List<Stop>> {
            it?.let { it1 ->
                moveCameraToFirstStop(it1)
                showStops(it1)
            }
        })

        model.getRoutes().observe(activity, Observer<String> {
            it?.let { it1 ->
                route_detail_map_routes_view?.text = it1
            }
        })
    }

    private fun setupViewProperties() {
        detail_map_stop_view?.setOnClickListener { v ->
            detail_map_stop_view?.let {
                AnalyticsUtil.sendRouteDetailMapEvent(context)
                RealTimeActivity.navigate(context, v.tag as Stop)
            }
        }

        detail_map_stop_view.viewTreeObserver.addOnGlobalLayoutListener({
            try {
                val padding = Sizes.getDip(this@RouteDetailMapFragment.context, 10)
                mMap?.setPadding(0, detail_map_stop_view.height + padding, 0, detail_map_stop_view.height + padding)
            } catch (e: Exception) {
                e.fillInStackTrace()
            }
        })
    }

    private fun setUpMapIfNeeded() {
        if (mSupportMapFragment == null) {
            val fm = childFragmentManager
            mSupportMapFragment = fm.findFragmentById(R.id.route_detail_map_view) as SupportMapFragment
            mSupportMapFragment?.getMapAsync(this)
            LocationUtil.requestLocationOrShowMessage(this, 0)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        mMap = map

        if (PermissionsUtils.Location.hasPermission(activity)) {
            mMap?.isMyLocationEnabled = true
            mMap?.uiSettings?.isMyLocationButtonEnabled = false
        }

        map.setOnMarkerClickListener { marker ->
            model.cleanRoutes()
            chanceIcoMarker(marker)
            true
        }

        loadData()
    }

    private fun chanceIcoMarker(marker: Marker) {
        markerAux?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map))
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map_selected))
        markerAux = marker
        showLocalSummary(mMarkersId[marker.id])
        mStopNumber = mMarkersId[marker.id]?.stopNumber
    }

    private fun cleanMap() {
        mMarkersId.clear()
        mMap?.clear()
        markerAux = null
        mStopNumber = null
        hideLocalSummary()
    }

    private fun showStops(stops: List<Stop>) {
        var marker: Marker?
        cleanMap()

        for (stop in stops) {
            stop.latLng()?.let {
                val markerOptions = MarkerOptions()
                        .position(it)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map))
                        .title("stopNumber" + stop.stopNumber)

                marker = mMap?.addMarker(markerOptions)
                marker?.let {
                    mMarkersId[it.id] = stop

                    if (stop.stopNumber == mStopNumberRestore) {
                        chanceIcoMarker(it)
                    }
                }
            }
        }
    }

    private fun moveCameraToFirstStop(stops: List<Stop>) {
        for (stop in stops) {
            stop.latLng()?.let {
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 12f))
                return
            }
        }
    }

    private fun showLocalSummary(stop: Stop?) {
        stop?.let {
            route_detail_map_number_stop_view.text = it.stopNumber
            route_detail_map_description_stop_view.text = it.descriptionOrAddress()
            detail_map_stop_view.let { it1 -> ViewUtil.showViewLayout(context, it1) }
            detail_map_stop_view.tag = it
            route_detail_map_routes_view.text = null
            model.loadRoutesByStopNumber(activity, it.stopNumber)
        }
    }

    private fun hideLocalSummary() {
        detail_map_stop_view.let { ViewUtil.hideViewLayout(context, it) }
    }

    override fun onNewLocation(location: Location) {
    }

    companion object {
        private const val BUNDLE_MARKER_STOP = "bundle_marker_stop"

        fun newInstance(): Fragment {
            val fragment = RouteDetailMapFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }
}
