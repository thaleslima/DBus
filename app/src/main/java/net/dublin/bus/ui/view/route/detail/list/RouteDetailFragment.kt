package net.dublin.bus.ui.view.route.detail.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_route_detail.*
import net.dublin.bus.R
import net.dublin.bus.common.Analytics
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.view.realtime.RealTimeActivity

class RouteDetailFragment : Fragment(), RouteDetailAdapter.ItemClickListener, RouteDetailContract.View {
    private lateinit var presenter: RouteDetailContract.Presenter
    private var mAdapter: RouteDetailAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_route_detail, container, false)
    }

    private fun setupRecyclerView() {
        mAdapter = RouteDetailAdapter(this)
        route_detail_recycler_view.layoutManager = LinearLayoutManager(context)
        route_detail_recycler_view.adapter = mAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
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

    override fun showData(data: List<Stop>) {
        mAdapter?.replaceData(data)
    }

    override fun showProgress() {
        route_detail_progress_bar_view?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        route_detail_progress_bar_view?.visibility = View.GONE
    }

    override fun showSnackBarNoConnection() {

    }

    override fun showSnackBarError() {

    }

    override fun onItemClick(item: Stop) {
        Analytics.sendRouteDetailListEvent(context)
        RealTimeActivity.navigate(requireContext(), item)
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