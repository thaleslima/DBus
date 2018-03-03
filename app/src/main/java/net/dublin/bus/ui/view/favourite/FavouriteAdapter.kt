package net.dublin.bus.ui.view.favourite

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_list_favorite.view.*
import net.dublin.bus.R
import net.dublin.bus.model.Favourite
import net.dublin.bus.ui.utilities.inflate
import java.util.ArrayList

internal class FavouriteAdapter(private val mListener: ItemClickListener) : RecyclerView.Adapter<FavouriteAdapter.LocalViewHolder>() {
    private val mDataSet: MutableList<Favourite> = ArrayList()

    companion object {
        private const val ITEM_TOP = 1
        private const val ITEM_MIDDLE = 2
        private const val ITEM_BOTTOM = 3
    }

    internal interface ItemClickListener {
        fun onItemClick(item: String)
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

    fun replaceData(dataSet: List<Favourite>) {
        setList(dataSet)
        notifyDataSetChanged()
    }

    private fun setList(dataSet: List<Favourite>) {
        mDataSet.clear()
        mDataSet.addAll(dataSet)
    }

    internal inner class LocalViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_favorite)) {
        private var mItem: Favourite? = null

        fun bind(item: Favourite) = with(itemView) {
            mItem = item
            favorite_description_view.text = item.description
            favorite_description_aux_view.text = item.stopNumber
            itemView.setOnClickListener { mItem?.let { it1 -> mListener.onItemClick(it1.stopNumber) } }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return ITEM_TOP
        }
        if (position == mDataSet.size - 1) {
            return ITEM_BOTTOM
        }
        return ITEM_MIDDLE
    }
}