package net.dublin.bus.ui.view.route.detail.list

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_real_time.*
import kotlinx.android.synthetic.main.fragment_route_detail.*
import net.dublin.bus.R
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.utilities.Utility

class RouteDetailFragment : Fragment(), RouteDetailAdapter.ItemClickListener, RouteDetailContract.View {
    private lateinit var presenter: RouteDetailContract.Presenter
    private var mAdapter: RouteDetailAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_route_detail, container, false)
        setupRecyclerView(view)
        return view
    }

    private fun setupRecyclerView(view: View) {
        mAdapter = RouteDetailAdapter(this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mAdapter
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    private fun initialize() {
        presenter = RouteDetailPresenter(this)
        presenter.loadData()
    }

    override fun isNetworkAvailable(): Boolean {
        return Utility.isNetworkAvailable(activity)
    }

    override fun showData(data: List<Stop>) {
        mAdapter?.replaceData(data)
    }

    override fun showProgress() {
        route_detail_progress_bar?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        route_detail_progress_bar?.visibility = View.GONE
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

    override fun onItemClick(item: Stop) {
    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = RouteDetailFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}