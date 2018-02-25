package net.dublin.bus.ui.view.stop

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_real_time.*
import kotlinx.android.synthetic.main.fragment_stop.*
import net.dublin.bus.R
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.utilities.Utility
import net.dublin.bus.ui.view.realtime.RealTimeActivity

class StopFragment : Fragment(), StopAdapter.ItemClickListener, StopContract.View {
    private var mAdapter: StopAdapter? = null
    private var presenter: StopContract.Presenter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_stop, container, false)
        setupRecyclerView(view)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun setupRecyclerView(view: View) {
        mAdapter = StopAdapter(this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mAdapter
    }

    private fun initialize() {
        presenter = StopPresenter(this, StopRepository())
        presenter?.loadData()
    }

    override fun onPause() {
        super.onPause()
        presenter?.unsubscribe()
    }

    override fun isNetworkAvailable(): Boolean {
        return Utility.isNetworkAvailable(activity)
    }

    override fun showData(data: List<Stop>) {
        mAdapter?.replaceData(data)
    }

    override fun showProgress() {
        stop_progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        stop_progress_bar.visibility = View.GONE
    }

    override fun showSnackBarNoConnection() {
        Snackbar.make(
                stop_swipe_refresh_layout,
                R.string.title_no_connection,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.title_retry) { presenter?.loadData() }
    }

    override fun showSnackBarError() {
        Snackbar.make(
                stop_swipe_refresh_layout,
                R.string.error_message,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.title_retry) { presenter?.loadData() }
    }

    override fun onItemClick(item: Stop) {
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
