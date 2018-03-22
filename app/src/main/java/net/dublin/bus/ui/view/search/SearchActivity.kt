package net.dublin.bus.ui.view.search

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_search.*
import net.dublin.bus.R
import net.dublin.bus.common.AnalyticsUtil
import net.dublin.bus.data.recent.repository.RecentRepository
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Recent
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.view.realtime.RealTimeActivity
import net.dublin.bus.ui.view.route.detail.RouteDetailActivity

class SearchActivity : AppCompatActivity(), SearchStopAdapter.ItemClickListener, SearchRouteAdapter.ItemClickListener, SearchContract.View, SearchRecentAdapter.ItemClickListener {
    private lateinit var presenter: SearchContract.Presenter
    private lateinit var mAdapter: SearchStopAdapter
    private lateinit var mSearchRouteAdapter: SearchRouteAdapter
    private lateinit var mSearchRecentAdapter: SearchRecentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupView()
        setupRecyclerView()
        initialize()
    }

    private fun setupView() {
        search_reset_view.setOnClickListener { presenter.reset() }
        search_close_view.setOnClickListener { onSupportNavigateUp() }

        search_text_view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                presenter.loadSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        search_text_view.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun initialize() {
        presenter = SearchPresenter(this,
                RouteRepository(application),
                StopRepository(application),
                RecentRepository(application))
        presenter.loadRecent()
    }

    private fun setupRecyclerView() {
        mAdapter = SearchStopAdapter(this)
        mSearchRouteAdapter = SearchRouteAdapter(this)
        mSearchRecentAdapter = SearchRecentAdapter(this)

        search_stops_view.layoutManager = LinearLayoutManager(this)
        search_stops_view.adapter = mAdapter
        search_stops_view.isNestedScrollingEnabled = false

        search_routes_view.layoutManager = LinearLayoutManager(this)
        search_routes_view.adapter = mSearchRouteAdapter
        search_routes_view.isNestedScrollingEnabled = false

        search_recent_view.layoutManager = LinearLayoutManager(this)
        search_recent_view.adapter = mSearchRecentAdapter
        search_recent_view.isNestedScrollingEnabled = false
    }

    override fun showStops(data: List<Stop>) {
        mAdapter.replaceData(data)
    }

    override fun showRoutes(data: List<Route>) {
        mSearchRouteAdapter.replaceData(data)
    }

    override fun showRecent(data: List<Recent>) {
        mSearchRecentAdapter.replaceData(data)
    }

    override fun onItemClick(item: Route) {
        presenter.loadRoute(item)
    }

    override fun onItemClick(item: Stop) {
        presenter.loadStop(item)
    }

    override fun showRoute(item: Route) {
        RouteDetailActivity.navigate(this, item)
    }

    override fun showStop(item: Stop) {
        AnalyticsUtil.sendRouteSearchEvent(this)
        RealTimeActivity.navigate(this, item)
    }

    override fun resetSearch() {
        search_text_view.setText("")
    }

    override fun cleanRecentData() {
        mSearchRecentAdapter.clean()
    }

    override fun cleanData() {
        mAdapter.clean()
        mSearchRouteAdapter.clean()
    }

    override fun onStopClick(stop: String) {
        presenter.loadRecentStop(stop)
    }

    override fun onRouteClick(route: String) {
        presenter.loadRecentRoute(route)
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }
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
