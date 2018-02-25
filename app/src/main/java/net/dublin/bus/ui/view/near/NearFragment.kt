package net.dublin.bus.ui.view.near

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
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
import com.google.android.gms.maps.model.LatLng
import net.dublin.bus.ui.utilities.LocationUtil
import net.dublin.bus.ui.utilities.PermissionsUtils
import net.dublin.bus.R
import net.dublin.bus.ui.utilities.LocationRequestWrapper

class NearFragment : Fragment(), OnMapReadyCallback, LocationRequestWrapper.OnNewLocationListener {


    private var mSupportMapFragment: SupportMapFragment? = null
    private var mDescriptionSubCategoryView: TextView? = null
    private var mLocalView: RelativeLayout? = null
    private var mMap: GoogleMap? = null
    private var mDescriptionView: TextView? = null
    private var mPhotoView: ImageView? = null
    //private var mMarkersId: HashMap<String, Local>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_near, container, false)
        setupViews(view)
        setupViewProperties()
        //mMarkersId = HashMap<String, Local>()
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMapIfNeeded()
    }

    private fun setupViews(view: View) {
        //mLocalView = view.findViewById(R.id.local_view) as RelativeLayout
        //mDescriptionSubCategoryView = mLocalView?.findViewById(R.id.descriptions_sub_category) as TextView
        //mDescriptionView = mLocalView?.findViewById(R.id.local_description) as TextView
        //mPhotoView = mLocalView?.findViewById(R.id.local_picture) as ImageView
    }

    private fun setupViewProperties() {
        // mLocalView?.setOnClickListener { v -> mPhotoView?.let { mPresenter?.openLocalDetails(v.tag as Local, it) } }
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

    fun connectLocation() {
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
//        map.setOnMarkerClickListener { marker ->
//            mPresenter?.openLocalSummary(mMarkersId?.get(marker.id))
//            false
//        }

        //mPresenter?.loadLocals(activity.supportLoaderManager)
    }

//    override fun showLocals(locals: List<Local>) {
//        var latLng: LatLng
//        var marker: Marker?
//        mMarkersId?.clear()
//
//        for (local in locals) {
//            latLng = LatLng(local.latitude, local.longitude)
//
//            val markerOptions = MarkerOptions().position(latLng)
//                    .icon(BitmapDescriptorFactory.fromResource(Utility.getIdImageCategory(local.idCategory)))
//
//            marker = mMap?.addMarker(markerOptions)
//            marker?.let { m -> mMarkersId?.put(m.id, local) }
//        }
//    }

//    override fun showLocalSummary(local: Local) {
//        mMap?.setPadding(0, 0, 0, 250)
//        mDescriptionView?.text = local.description
//        mDescriptionSubCategoryView?.text = local.descriptionSubCategories
//
//        Glide.with(this.context)
//                .load(local.imagePath)
//                .placeholder(R.color.placeholder)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(mPhotoView)
//
//        mLocalView?.let { ViewUtil.showViewLayout(context, it) }
//        mLocalView?.tag = local
//    }

    //    override fun showLocalDetailUi(local: Local, view: ImageView) {
//        LocalDetailActivity.navigate(this.activity, view, local.id!!, local.idCategory)
//    }
    override fun onNewLocation(location: Location) {
        //        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 12f))

        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location?.latitude?.let { LatLng(it, location.longitude) }, 12f))
    }


    companion object {
        fun newInstance(): Fragment {
            val fragment = NearFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }
}
