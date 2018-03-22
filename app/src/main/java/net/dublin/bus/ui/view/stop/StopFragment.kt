package net.dublin.bus.ui.view.stop

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_stop.*
import net.dublin.bus.R
import net.dublin.bus.common.AnalyticsUtil
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.utilities.Utility
import net.dublin.bus.ui.view.realtime.RealTimeActivity

class StopFragment : Fragment(), StopAdapter.ItemClickListener {
    private lateinit var model: StopViewModel
    private var mAdapter: StopAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_stop, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        initialize()
    }

    private fun setupRecyclerView() {
        mAdapter = StopAdapter(this)
        stop_recycler_view.layoutManager = LinearLayoutManager(context)
        stop_recycler_view.adapter = mAdapter
    }

    private fun initialize() {
        showProgress()

        val factory = StopViewModelFactory(StopRepository(activity.application))
        model = ViewModelProviders.of(activity, factory).get(StopViewModel::class.java)
        model.getStops().observe(activity, Observer<List<Stop>> {
            it?.let {
                showData(it)
            }
            hideProgress()
        })
    }

    fun isNetworkAvailable(): Boolean {
        return Utility.isNetworkAvailable(activity)
    }

    fun showData(data: List<Stop>) {
        mAdapter?.replaceData(data)
    }

    fun showProgress() {
        stop_progress_bar_view?.visibility = View.VISIBLE
    }

    fun hideProgress() {
        stop_progress_bar_view?.visibility = View.GONE
    }

    override fun onItemClick(item: Stop) {
        AnalyticsUtil.sendRouteEvent(context)
        RealTimeActivity.navigate(context, item)
    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = StopFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
