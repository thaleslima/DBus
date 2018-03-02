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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_route_detail_map.*
import net.dublin.bus.R
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.utilities.*
import net.dublin.bus.ui.view.realtime.RealTimeActivity
import net.dublin.bus.ui.view.route.detail.RouteDetailViewModel
import net.dublin.bus.ui.view.route.detail.RouteDetailViewModelFactory
import java.util.*

class RouteDetailMapFragment : Fragment(), OnMapReadyCallback, LocationRequestWrapper.OnNewLocationListener {
    private var mSupportMapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var mMarkersId: HashMap<String, Stop>? = HashMap()
    private var markerAux: Marker? = null
    private lateinit var model: RouteDetailViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_route_detail_map, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMapIfNeeded()
        setupViewProperties()
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
    }

    private fun setupViewProperties() {
        detail_map_stop_view?.setOnClickListener { v ->
            detail_map_stop_view?.let {
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
            mSupportMapFragment = fm.findFragmentById(R.id.map) as SupportMapFragment
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
            markerAux?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map))
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map_selected))
            markerAux = marker

            showLocalSummary(mMarkersId?.get(marker.id))
            true
        }

        loadData()
    }

    private fun showStops(stops: List<Stop>) {
        var latLng: LatLng?
        var marker: Marker?
        mMarkersId?.clear()

        for (stop in stops) {
            latLng = stop.latLng()
            latLng?.let {
                val markerOptions = MarkerOptions().position(it)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map))

                marker = mMap?.addMarker(markerOptions)
                marker?.let { m -> mMarkersId?.put(m.id, stop) }
            }
        }
    }

    private fun moveCameraToFirstStop(stops: List<Stop>) {
        var latLng: LatLng?

        for (stop in stops) {
            latLng = stop.latLng()
            latLng?.let {
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 12f))
                return
            }
        }
    }

    private fun showLocalSummary(local: Stop?) {
        detail_map_description_aux_view.text = local?.stopNumber
        detail_map_description_view.text = local?.descriptionOrAddress()
        detail_map_stop_view.let { ViewUtil.showViewLayout(context, it) }
        detail_map_stop_view.tag = local
    }

    override fun onNewLocation(location: Location) {
    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = RouteDetailMapFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }
}
