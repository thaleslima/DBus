package net.dublin.bus.ui.view.search

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_list_search.view.*
import net.dublin.bus.R
import net.dublin.bus.ui.utilities.inflate
import net.dublin.bus.model.Stop
import kotlin.collections.ArrayList

internal class SearchStopAdapter(private val mListener: ItemClickListener) : RecyclerView.Adapter<SearchStopAdapter.LocalViewHolder>() {
    private val mDataSet: MutableList<Stop> = ArrayList()

    companion object {
        private const val ITEM_TOP = 1
        private const val ITEM_MIDDLE = 2
    }

    internal interface ItemClickListener {
        fun onItemClick(item: Stop)
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

    fun replaceData(dataSet: List<Stop>) {
        val dataSetAux = ArrayList<Stop>()
        dataSetAux.add(Stop())
        dataSetAux.addAll(dataSet)
        setList(dataSetAux)
        notifyDataSetChanged()
    }

    private fun setList(dataSet: List<Stop>) {
        mDataSet.clear()
        mDataSet.addAll(dataSet)
    }

    fun clean() {
        mDataSet.clear()
        notifyDataSetChanged()
    }

    internal inner class LocalViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder(inflate(parent, viewType)) {
        private var mItem: Stop? = null

        fun bind(item: Stop) = with(itemView) {
            mItem = if (item.stopNumber.isEmpty()) null else item
            stop_search_description_view?.text = item.description
            stop_search_description_aux_view?.text = item.stopNumber

            itemView?.setOnClickListener { mItem?.let { it1 -> mListener.onItemClick(it1) } }
        }
    }

    fun inflate(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            ITEM_TOP -> parent.inflate(R.layout.item_list_search_top)
            else -> parent.inflate(R.layout.item_list_search)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return ITEM_TOP
        }
        return ITEM_MIDDLE
    }
}