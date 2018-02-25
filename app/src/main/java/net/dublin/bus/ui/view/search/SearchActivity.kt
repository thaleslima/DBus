package net.dublin.bus.ui.view.search

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search.*
import net.dublin.bus.R
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop

class SearchActivity : AppCompatActivity(), SearchStopAdapter.ItemClickListener, SearchRouteAdapter.ItemClickListener {
    override fun onItemClick(item: Route) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemClick(item: Stop) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mAdapter: SearchStopAdapter? = null
    private var mSearchRouteAdapter: SearchRouteAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        search_reset_view.setOnClickListener { }
        search_close_view.setOnClickListener { }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        mAdapter = SearchStopAdapter(this)
        mSearchRouteAdapter = SearchRouteAdapter(this)

        search_stops_view.layoutManager = LinearLayoutManager(this)
        search_stops_view.adapter = mAdapter
        search_stops_view.isNestedScrollingEnabled = false

        search_routes_view.layoutManager = LinearLayoutManager(this)
        search_routes_view.adapter = mAdapter
        search_routes_view.isNestedScrollingEnabled = false

        var stops = ArrayList<Stop>()
        stops.add(Stop())
        stops.add(Stop())
        stops.add(Stop())
        stops.add(Stop())
        stops.add(Stop())
        stops.add(Stop())
        stops.add(Stop())
        mAdapter?.replaceData(stops)

        search_routes_view.adapter = mSearchRouteAdapter
        var routes = ArrayList<Route>()
        routes.add(Route())
        routes.add(Route())
        routes.add(Route())
        routes.add(Route())
        routes.add(Route())
        routes.add(Route())
        routes.add(Route())
        mSearchRouteAdapter?.replaceData(routes)
    }

    companion object {
        fun navigate(activity: Activity) {
            val intent = Intent(activity, SearchActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val opts = ActivityOptions.makeSceneTransitionAnimation(activity)
                activity.startActivity(intent, opts.toBundle())
            } else {
                val intent = Intent(activity, SearchActivity::class.java)
                activity.startActivity(intent)
            }
        }
    }
}
