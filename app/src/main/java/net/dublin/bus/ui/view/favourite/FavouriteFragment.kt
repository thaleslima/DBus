package net.dublin.bus.ui.view.favourite

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.dublin.bus.R
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Favourite
import net.dublin.bus.ui.utilities.Utility
import net.dublin.bus.ui.view.realtime.RealTimeActivity

class FavouriteFragment : Fragment(), FavouriteAdapter.ItemClickListener, FavouriteContract.View {
    private var mAdapter: FavouriteAdapter? = null
    private lateinit var presenter: FavouriteContract.Presenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_favorite, container, false)
        setupRecyclerView(view)
        initialize()

        return view
    }

    private fun setupRecyclerView(view: View) {
        mAdapter = FavouriteAdapter(this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mAdapter
    }

    private fun initialize() {
        presenter = FavouritePresenter(this, StopRepository(activity.application))
    }

    override fun onResume() {
        super.onResume()
        presenter.loadData()
    }

    override fun isNetworkAvailable(): Boolean {
        return Utility.isNetworkAvailable(activity)
    }

    override fun showData(data: List<Favourite>) {
        mAdapter?.replaceData(data)
    }

    override fun showNoData() {
    }

    override fun showSnackBarNoConnection() {
    }

    override fun showSnackBarError() {
    }

    override fun onItemClick(item: Favourite) {
        RealTimeActivity.navigate(activity, item)
    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = FavouriteFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}