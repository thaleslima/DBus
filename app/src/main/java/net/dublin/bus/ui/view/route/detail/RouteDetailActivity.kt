package net.dublin.bus.ui.view.route.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_route_detail.*
import net.dublin.bus.R
import net.dublin.bus.common.Analytics
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.utilities.*
import net.dublin.bus.ui.view.main.MainActivity
import net.dublin.bus.ui.view.route.detail.list.RouteDetailFragment
import net.dublin.bus.ui.view.route.detail.map.RouteDetailMapFragment
import net.dublin.bus.ui.view.timetable.TimetablesActivity
import java.util.*

class RouteDetailActivity : AppCompatActivity() {
    private lateinit var number: String
    private lateinit var code: String
    private lateinit var model: RouteDetailViewModel

    private lateinit var outbound: String
    private lateinit var inbound: String
    private var map: Boolean = false

    private val menu = TreeMap<Int, String>()
    private var directionCurrent: String = "I"
    private var routeNameTowards: String = ""
    private var menuAssignment: MenuItem? = null

    companion object {
        private const val BUNDLE_DIRECTION = "bundle_direction"
        private const val BUNDLE_TOWARDS = "bundle_towards"
        const val EXTRA_ROUTE_NUMBER = "route_number"
        const val EXTRA_ROUTE_CODE = "route_code"
        const val EXTRA_ROUTE_OUT_TOWARDS = "route_outbound_towards"
        const val EXTRA_ROUTE_IN_TOWARDS = "route_inbound_towards"

        fun navigate(context: Context, item: Route) {
            val intent = Intent(context, RouteDetailActivity::class.java)
            intent.putExtra(EXTRA_ROUTE_NUMBER, item.number)
            intent.putExtra(EXTRA_ROUTE_CODE, item.code)
            intent.putExtra(EXTRA_ROUTE_OUT_TOWARDS, item.outboundTowards)
            intent.putExtra(EXTRA_ROUTE_IN_TOWARDS, item.inboundTowards)

            if (!TextUtils.isEmpty(item.outboundTowards) || !TextUtils.isEmpty(item.inboundTowards)) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, R.string.route_detail_no_items, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_detail)
        initExtra(savedInstanceState)
        setupToolbar()
        setupView()
        initContainer(savedInstanceState)
        setupLoadData()
        loadData()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putString(BUNDLE_DIRECTION, directionCurrent)
        outState?.putString(BUNDLE_TOWARDS, routeNameTowards)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.route_detail, menu)
        menuAssignment = menu.findItem(R.id.menu_assignment)
        menuAssignment?.isVisible = !TextUtils.isEmpty(code)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_assignment -> {
                Analytics.sendMenuTimetablesEvent(this)
                TimetablesActivity.navigate(this, number, code)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupLoadData() {
        val factory = RouteDetailViewModelFactory(RouteRepository(this))
        model = ViewModelProviders.of(this, factory).get(RouteDetailViewModel::class.java)
        model.getStops().observe(this, Observer<List<Stop>> {
            if (it != null) {
                route_count_view.text = getString(R.string.route_detail_size_stops, it?.size.toString())
                route_name_towards_view.text = routeNameTowards
            } else {
                onError()
            }
        })
    }

    private fun onError() {
        if (isNetworkAvailable()) {
            showSnackBarError()
        } else {
            showSnackBarNoConnection()
        }
    }

    private fun loadData() {
        model.loadStops(number, directionCurrent)
    }

    private fun reloadData() {
        model.reloadStops(number, directionCurrent)
    }

    private fun showSnackBarNoConnection() {
        snackBarNoConnection(container, { loadData() }).show()
    }

    private fun showSnackBarError() {
        snackBarErrorMessage(container, { loadData() }).show()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.route_detail_title, number)
    }

    private fun setupView() {
        route_name_towards_view.text = routeNameTowards
        map = getShowMapAtRouteDetail()

        route_change_map_list_view.setOnClickListener({
            map = !map
            sendEvent()
            changeMapOrList()
            changeIcoMapOrList()
            RouteDetailActivity@this.saveShowMapAtRouteDetail(map)
        })

        route_change_direction_view.setOnClickListener { v -> showFilterPopup(v) }
    }

    private fun initContainer(savedInstanceState: Bundle?) {
        val fragment = supportFragmentManager.findFragmentByTag("fragmentBase")

        if (savedInstanceState == null || fragment == null) {
            changeMapOrList()
        }

        changeIcoMapOrList()
    }

    private fun sendEvent() {
        Analytics.sendMapOrListProperty(this, map)
        Analytics.sendListOrMapEvent(this, map)
    }

    private fun changeMapOrList() {
        if (map) {
            switchContent(RouteDetailMapFragment.newInstance())
        } else {
            switchContent(RouteDetailFragment.newInstance())
        }
    }

    private fun changeIcoMapOrList() {
        if (map) {
            route_change_map_list_view.setImageResource(R.drawable.ic_view_list_white_24dp)
        } else {
            route_change_map_list_view.setImageResource(R.drawable.ic_map_white_24dp)
        }
    }

    private fun showFilterPopup(v: View) {
        val popup = PopupMenu(this, v)

        menu.entries.forEach {
            popup.menu.add(0, it.key, 0, it.value)
        }

        popup.show()
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    changeInbound()
                    true
                }
                2 -> {
                    changeToOutbound()
                    true
                }
                else -> false
            }
        }
    }

    private fun changeInbound() {
        routeNameTowards = inbound
        directionCurrent = "I"
        reloadData()
    }

    private fun changeToOutbound() {
        routeNameTowards = outbound
        directionCurrent = "O"
        reloadData()
    }

    private fun initExtra(savedInstanceState: Bundle?) {
        number = intent.getStringExtra(EXTRA_ROUTE_NUMBER)
        code = intent.getStringExtra(EXTRA_ROUTE_CODE)

        outbound = intent.getStringExtra(EXTRA_ROUTE_OUT_TOWARDS)
        inbound = intent.getStringExtra(EXTRA_ROUTE_IN_TOWARDS)

        if (!outbound.isEmpty()) {
            menu[2] = outbound
            directionCurrent = "O"
            routeNameTowards = outbound
        }

        if (!inbound.isEmpty()) {
            menu[1] = inbound
            directionCurrent = "I"
            routeNameTowards = inbound
        }

        if (savedInstanceState != null) {
            directionCurrent = savedInstanceState.getString(BUNDLE_DIRECTION)
            routeNameTowards = savedInstanceState.getString(BUNDLE_TOWARDS)
        }
    }

    private fun switchContent(fragment: Fragment?) {
        if (fragment != null) {
            try {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, fragment, "fragmentBase")
                fragmentTransaction.commitAllowingStateLoss()
            } catch (e: Exception) {
                Log.e(MainActivity::class.java.name, "Error in show view")
            }
        }
    }
}
