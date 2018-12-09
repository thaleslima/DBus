package net.dublin.bus.ui.view.search

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_list_search_recent.view.*
import net.dublin.bus.R
import net.dublin.bus.model.Recent
import net.dublin.bus.ui.utilities.inflate
import java.util.*

internal class SearchRecentAdapter(private val mListener: ItemClickListener) : RecyclerView.Adapter<SearchRecentAdapter.LocalViewHolder>() {
    private val mDataSet: MutableList<Recent> = ArrayList()

    companion object {
        private const val ITEM_TOP = 1
        private const val ITEM_MIDDLE = 2
    }

    internal interface ItemClickListener {
        fun onStopClick(stop: String)

        fun onRouteClick(route: String)
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

    fun replaceData(dataSet: List<Recent>) {
        val dataSetAux = ArrayList<Recent>()
        if (!dataSet.isEmpty()) {
            dataSetAux.add(Recent())
        }
        dataSetAux.addAll(dataSet)
        setList(dataSetAux)
        notifyDataSetChanged()
    }

    private fun setList(dataSet: List<Recent>) {
        mDataSet.clear()
        mDataSet.addAll(dataSet)
    }

    fun clean() {
        mDataSet.clear()
        notifyDataSetChanged()
    }

    internal inner class LocalViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder(inflate(parent, viewType)) {
        private var mItem: Recent? = null

        fun bind(item: Recent) = with(itemView) {
            mItem = if (item.number.isEmpty()) null else item

            search_recent_description_view?.text = if (item.isStop())
                context.getString(R.string.search_stop_item, item.number)
            else
                context.getString(R.string.search_route_item, item.number)

            itemView.setOnClickListener {
                mItem?.let {
                    if (it.isStop()) {
                        mListener.onStopClick(it.number)
                    } else {
                        mListener.onRouteClick(it.number)
                    }
                }
            }
        }
    }

    fun inflate(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            ITEM_TOP -> parent.inflate(R.layout.item_list_search_recent_top)
            else -> parent.inflate(R.layout.item_list_search_recent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return ITEM_TOP
        }
        return ITEM_MIDDLE
    }
}