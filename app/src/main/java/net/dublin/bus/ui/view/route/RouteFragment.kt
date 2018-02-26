package net.dublin.bus.ui.view.route

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_real_time.*
import kotlinx.android.synthetic.main.fragment_route.*
import net.dublin.bus.R
import net.dublin.bus.model.Route
import net.dublin.bus.ui.view.route.detail.RouteDetailActivity
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.ui.utilities.Utility

class RouteFragment : Fragment(), RouteAdapter.ItemClickListener, RouteContract.View {
    private var presenter: RouteContract.Presenter? = null
    private var mAdapter: RouteAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_route, container, false)
        setupRecyclerView(view)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun setupRecyclerView(view: View) {
        mAdapter = RouteAdapter(this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mAdapter
    }

    private fun initialize() {
        presenter = RoutePresenter(this, RouteRepository())
        presenter?.loadData()
    }

    override fun isNetworkAvailable(): Boolean {
        return Utility.isNetworkAvailable(activity)
    }

    override fun showData(data: List<Route>) {
        mAdapter?.replaceData(data)
    }

    override fun showProgress() {
        route_progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        route_progress_bar.visibility = View.GONE
    }

    override fun onItemClick(item: Route) {
        RouteDetailActivity.navigate(context, item.number, item.outboundTowards, item.inboundTowards)
    }

    override fun showSnackBarNoConnection() {
        Snackbar.make(
                real_swipe_refresh_layout,
                R.string.title_no_connection,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.title_retry) { presenter?.loadData() }
    }

    override fun showSnackBarError() {
        Snackbar.make(
                real_swipe_refresh_layout,
                R.string.error_message,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.title_retry) { presenter?.loadData() }
    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = RouteFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}