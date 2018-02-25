package net.dublin.bus.ui.view.route.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_route.*
import net.dublin.bus.R
import net.dublin.bus.ui.view.main.MainActivity
import net.dublin.bus.ui.view.route.detail.list.RouteDetailFragment
import net.dublin.bus.ui.view.route.detail.map.RouteDetailMapFragment

class RouteDetailActivity : AppCompatActivity() {
    private var number: String? = null
    private var outbound: String? = null
    private var inbound: String? = null
    private var map: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route)
        initExtra()
        setupToolbar()
        setupView()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Route " + number
    }

    private fun setupView() {
        switchContent(RouteDetailFragment.newInstance())
        route_name_towards_view.text = inbound

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
        popup.menu.add(0, 0, 0, inbound)
        popup.menu.add(0, 1, 1, outbound)
        popup.show()
    }


    private fun initExtra() {
        number = intent.getStringExtra(EXTRA_ROUTE_NUMBER)
        outbound = intent.getStringExtra(EXTRA_ROUTE_OUT_TOWARDS)
        inbound = intent.getStringExtra(EXTRA_ROUTE_IN_TOWARDS)
    }

    companion object {
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
