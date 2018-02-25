package net.dublin.bus.ui.view.route.detail.map

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.fragment_route_detail_map.*
import net.dublin.bus.R
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.utilities.*
import net.dublin.bus.ui.view.realtime.RealTimeActivity
import java.util.*

class RouteDetailMapFragment : Fragment(), OnMapReadyCallback, LocationRequestWrapper.OnNewLocationListener {
    private var mSupportMapFragment: SupportMapFragment? = null
    private var mDescriptionSubCategoryView: TextView? = null
    private var mLocalView: RelativeLayout? = null
    private var mMap: GoogleMap? = null
    private var mDescriptionView: TextView? = null
    private var mPhotoView: ImageView? = null
    //private var mMarkersId: HashMap<String, Local>? = null
    private var mMarkersId: HashMap<String, Stop>? = HashMap()
    private var markerAux: Marker? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_route_detail_map, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMapIfNeeded()
        setupViewProperties()
    }

    private fun setupViewProperties() {
        detail_map_stop_view?.setOnClickListener { v ->
            Log.d("teste", "teste")

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

            if (LocationUtil.requestLocationOrShowMessage(this, 0)) {
                connectLocation()
            }
        }
    }

    private fun connectLocation() {
        val locationRequestWrapper = LocationRequestWrapper(this.activity, this)
        locationRequestWrapper.connect()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 0 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            connectLocation()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        mMap = map

        if (PermissionsUtils.Location.hasPermission(activity)) {
            mMap?.isMyLocationEnabled = true
            mMap?.uiSettings?.isMyLocationButtonEnabled = false
        }

//        val center = LatLng(Constants.City.LATITUDE, Constants.City.LONGITUDE)
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 12f))
        map.setOnMarkerClickListener { marker ->
            markerAux?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map))
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map_selected))
            markerAux = marker

            showLocalSummary(mMarkersId?.get(marker.id))
            false
        }

        //mPresenter?.loadLocals(activity.supportLoaderManager)
    }

    fun showLocals(locals: List<Stop>) {
        var latLng: LatLng
        var marker: Marker?
        mMarkersId?.clear()

        for (local in locals) {
//            latLng = LatLng(local.latitude.toDouble(), local.longitude.toDouble())
//
//            val markerOptions = MarkerOptions().position(latLng)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_yellow_map))
//
//            marker = mMap?.addMarker(markerOptions)
//            marker?.let { m -> mMarkersId?.put(m.id, local) }
        }
    }


    private fun showLocalSummary(local: Stop?) {
//        mMap?.setPadding(0, 0, 0, 220)
        detail_map_description_aux_view.text = local?.stopNumber
        detail_map_description_view.text = local?.description
        detail_map_stop_view.let { ViewUtil.showViewLayout(context, it) }
        detail_map_stop_view.tag = local
    }

    //    override fun showLocalDetailUi(local: Local, view: ImageView) {
//        LocalDetailActivity.navigate(this.activity, view, local.id!!, local.idCategory)
//    }
    override fun onNewLocation(location: Location) {
        //        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 12f))

        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location.latitude?.let { LatLng(it, location.longitude) }, 12f))
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
