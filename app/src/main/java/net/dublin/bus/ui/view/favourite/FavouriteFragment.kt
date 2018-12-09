package net.dublin.bus.ui.view.favourite

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_favourite.*
import net.dublin.bus.R
import net.dublin.bus.common.Analytics
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Favourite
import net.dublin.bus.ui.view.realtime.RealTimeActivity

class FavouriteFragment : Fragment(), FavouriteAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var model: FavouriteViewModel

    private lateinit var mAdapter: FavouriteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        setupRecyclerView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        initialize()
    }

    private fun setupRecyclerView(view: View) {
        mAdapter = FavouriteAdapter(this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.favourite_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mAdapter
    }

    private fun initialize() {
        val factory = FavouriteViewModelFactory(StopRepository(requireContext()))
        model = ViewModelProviders.of(requireActivity(), factory).get(FavouriteViewModel::class.java)
        model.getStops().observe(requireActivity(), Observer<List<Favourite>> { it ->
            it?.let {
                onData(it)
            }
        })
    }

    private fun setupView() {
        favourite_swipe_refresh_layout.setOnRefreshListener(this)
    }

    private fun onData(data: List<Favourite>) {
        enableProgressSwipe()
        hideNoData()
        showData(data)
        if (data.isEmpty()) {
            disableProgressSwipe()
            showNoData()
        }
    }

    private fun hideProgressSwipe() {
        favourite_swipe_refresh_layout?.isRefreshing = false
    }

    private fun enableProgressSwipe() {
        favourite_swipe_refresh_layout?.visibility = View.VISIBLE
    }

    private fun disableProgressSwipe() {
        favourite_swipe_refresh_layout?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        mAdapter.updateRealData()
    }

    override fun onRefresh() {
        mAdapter.updateRealData()
        Handler().postDelayed({ hideProgressSwipe() }, 500)
    }

    fun showData(data: List<Favourite>) {
        mAdapter.replaceData(data)
    }

    private fun showNoData() {
        favorite_message_empty?.visibility = View.VISIBLE
    }

    private fun hideNoData() {
        favorite_message_empty?.visibility = View.GONE
    }

    override fun onItemClick(item: Favourite) {
        Analytics.sendRouteFavouriteEvent(requireContext())
        RealTimeActivity.navigate(requireActivity(), item)
    }

    companion object {
        fun newInstance(): androidx.fragment.app.Fragment {
            val fragment = FavouriteFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}