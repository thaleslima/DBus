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
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.view.realtime.RealTimeActivity
import net.dublin.bus.ui.view.route.detail.RouteDetailActivity

class SearchActivity : AppCompatActivity(), SearchStopAdapter.ItemClickListener, SearchRouteAdapter.ItemClickListener, SearchContract.View {
    private lateinit var presenter: SearchContract.Presenter
    private var mAdapter: SearchStopAdapter? = null
    private var mSearchRouteAdapter: SearchRouteAdapter? = null

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
                StopRepository(application))
    }

    private fun setupRecyclerView() {
        mAdapter = SearchStopAdapter(this)
        mSearchRouteAdapter = SearchRouteAdapter(this)

        search_stops_view.layoutManager = LinearLayoutManager(this)
        search_stops_view.adapter = mAdapter
        search_stops_view.isNestedScrollingEnabled = false

        search_routes_view.layoutManager = LinearLayoutManager(this)
        search_routes_view.adapter = mSearchRouteAdapter
        search_routes_view.isNestedScrollingEnabled = false
    }

    override fun showStops(data: List<Stop>) {
        mAdapter?.replaceData(data)
    }

    override fun showRoutes(data: List<Route>) {
        mSearchRouteAdapter?.replaceData(data)
    }

    override fun onItemClick(item: Route) {
        RouteDetailActivity.navigate(this, item)
    }

    override fun onItemClick(item: Stop) {
        RealTimeActivity.navigate(this, item)
    }

    override fun resetSearch() {
        search_text_view.setText("")
    }

    override fun cleanData() {
        mAdapter?.clean()
        mSearchRouteAdapter?.clean()
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
