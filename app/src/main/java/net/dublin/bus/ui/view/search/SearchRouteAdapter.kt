package net.dublin.bus.ui.view.search

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_list_search_route.view.*
import net.dublin.bus.R
import net.dublin.bus.model.Route
import net.dublin.bus.ui.utilities.inflate
import java.util.*

internal class SearchRouteAdapter(private val mListener: ItemClickListener) : RecyclerView.Adapter<SearchRouteAdapter.LocalViewHolder>() {
    private val mDataSet: MutableList<Route> = ArrayList()

    companion object {
        private const val ITEM_TOP = 1
        private const val ITEM_MIDDLE = 2
    }

    internal interface ItemClickListener {
        fun onItemClick(item: Route)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        return LocalViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        holder.bind(mDataSet[position])
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    fun replaceData(dataSet: List<Route>) {
        val dataSetAux = ArrayList<Route>()
        if (!dataSet.isEmpty()) {
            dataSetAux.add(Route())
        }
        dataSetAux.addAll(dataSet)
        setList(dataSetAux)
        notifyDataSetChanged()
    }

    private fun setList(dataSet: List<Route>) {
        mDataSet.clear()
        mDataSet.addAll(dataSet)
    }

    fun clean() {
        mDataSet.clear()
        notifyDataSetChanged()
    }

    internal inner class LocalViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder(inflate(parent, viewType)) {
        private var mItem: Route? = null

        fun bind(item: Route) = with(itemView) {
            mItem = if (item.number.isEmpty()) null else item
            search_route_number_view?.text = item.number
            itemView.setOnClickListener { mItem?.let { it1 -> mListener.onItemClick(it1) } }
        }
    }

    fun inflate(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            ITEM_TOP -> parent.inflate(R.layout.item_list_search_route_top)
            else -> parent.inflate(R.layout.item_list_search_route)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return ITEM_TOP
        }
        return ITEM_MIDDLE
    }
}