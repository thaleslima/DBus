package net.dublin.bus.ui.view.route

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_route.*
import net.dublin.bus.R
import net.dublin.bus.common.Analytics
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.model.Route
import net.dublin.bus.ui.view.route.detail.RouteDetailActivity
import net.dublin.bus.ui.view.timetable.TimetablesActivity

class RouteFragment : Fragment(), RouteAdapter.ItemClickListener {
    private lateinit var model: RouteViewModel
    private lateinit var mAdapter: RouteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_route, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        initialize()
    }

    private fun setupRecyclerView() {
        mAdapter = RouteAdapter(this)
        route_recycler_view.layoutManager = LinearLayoutManager(context)
        route_recycler_view.adapter = mAdapter
    }

    private fun initialize() {
        val factory = RouteViewModelFactory(RouteRepository(requireContext()))
        model = ViewModelProviders.of(requireActivity(), factory).get(RouteViewModel::class.java)
        model.getStops().observe(requireActivity(), Observer<List<Route>> { it ->
            it?.let {
                showData(it)
            }
            hideProgress()
        })
    }

    fun showData(data: List<Route>) {
        mAdapter.replaceData(data)
    }

    fun showProgress() {
        route_progress_bar_view?.visibility = View.VISIBLE
    }

    fun hideProgress() {
        route_progress_bar_view?.visibility = View.GONE
    }

    override fun onItemClick(item: Route) {
        RouteDetailActivity.navigate(requireContext(), item)
    }

    override fun onLongItemClick(item: Route) {
        TimetablesActivity.navigate(requireContext(), item.number, item.code)
        Analytics.sendClickLongTimetablesEvent(requireContext())
    }

    companion object {
        fun newInstance(): androidx.fragment.app.Fragment {
            val fragment = RouteFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}