package net.dublin.bus.ui.view.route.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_real_time.*
import kotlinx.android.synthetic.main.activity_route.*
import net.dublin.bus.R
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.utilities.Utility
import net.dublin.bus.ui.view.main.MainActivity
import net.dublin.bus.ui.view.route.detail.list.RouteDetailFragment
import net.dublin.bus.ui.view.route.detail.map.RouteDetailMapFragment
import java.util.*

class RouteDetailActivity : AppCompatActivity() {
    private lateinit var number: String
    private lateinit var model: RouteDetailViewModel

    private var outbound: String? = null
    private var inbound: String? = null
    private var map: Boolean = false


    private val menu = TreeMap<Int, String>()
    private var directionCurrent: String = "I"
    private var routeNameTowards: String = ""

    companion object {
        private const val BUNDLE_DIRECTION = "bundle_direction"
        private const val BUNDLE_TOWARDS = "bundle_towards"
        const val EXTRA_ROUTE_NUMBER = "route_number"
        const val EXTRA_ROUTE_OUT_TOWARDS = "route_outbound_towards"
        const val EXTRA_ROUTE_IN_TOWARDS = "route_inbound_towards"

        fun navigate(context: Context, number: String?, outbound: String?, inbound: String?) {
            val intent = Intent(context, RouteDetailActivity::class.java)
            intent.putExtra(EXTRA_ROUTE_NUMBER, number)
            intent.putExtra(EXTRA_ROUTE_OUT_TOWARDS, outbound)
            intent.putExtra(EXTRA_ROUTE_IN_TOWARDS, inbound)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route)
        initExtra(savedInstanceState)
        setupToolbar()
        setupView()
        setupLoadData()
        loadData()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putString(BUNDLE_DIRECTION, directionCurrent)
        outState?.putString(BUNDLE_TOWARDS, routeNameTowards)
    }


    private fun setupLoadData() {
        val factory = RouteDetailViewModelFactory(RouteRepository(this))
        model = ViewModelProviders.of(this, factory).get(RouteDetailViewModel::class.java)
        model.getStops().observe(this, Observer<List<Stop>> {
            if (it != null) {
                route_count_view.text = getString(R.string.route_size_stops, it?.size.toString())
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

    fun loadData() {
        model.loadStops(number, directionCurrent)
    }

    fun showSnackBarNoConnection() {
        Snackbar.make(container, R.string.title_no_connection,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.title_retry) { loadData() }.show()
    }

    fun showSnackBarError() {
        Snackbar.make(container, R.string.error_message,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.title_retry) { loadData() }.show()
    }

    fun isNetworkAvailable(): Boolean {
        return Utility.isNetworkAvailable(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.route_title, number)
    }

    private fun setupView() {
        switchContent(RouteDetailFragment.newInstance())
        route_name_towards_view.text = routeNameTowards

        route_change_map_list_view.setOnClickListener({
            if (map) {
                route_change_map_list_view.setImageResource(R.drawable.ic_map_white_24dp)
                switchContent(RouteDetailFragment.newInstance())
            } else {
                route_change_map_list_view.setImageResource(R.drawable.ic_view_list_white_24dp)
                switchContent(RouteDetailMapFragment.newInstance())
            }
            map = !map
        })

        route_change_direction_view.setOnClickListener { v -> showFilterPopup(v) }
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
        routeNameTowards = inbound ?: ""
        directionCurrent = "I"
        loadData()
    }

    private fun changeToOutbound() {
        routeNameTowards = outbound ?: ""
        directionCurrent = "O"
        loadData()
    }

    private fun initExtra(savedInstanceState: Bundle?) {
        number = intent.getStringExtra(EXTRA_ROUTE_NUMBER)
        outbound = intent.getStringExtra(EXTRA_ROUTE_OUT_TOWARDS)
        inbound = intent.getStringExtra(EXTRA_ROUTE_IN_TOWARDS)

        if (outbound != null) {
            menu[2] = outbound!!
            directionCurrent = "O"
            routeNameTowards = outbound!!
        }

        if (inbound != null) {
            menu[1] = inbound!!
            directionCurrent = "I"
            routeNameTowards = inbound!!
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
